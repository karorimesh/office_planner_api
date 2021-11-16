package com.tracom.office_planner.Meeting;

import com.tracom.office_planner.Boardroom.BoardRepository;
import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.CoOwners.CoOwnerRepo;
import com.tracom.office_planner.CoOwners.CoOwners;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MeetingController {

        private MeetingRepository meetRepo;

        private RepeatMeetingsRepo meetingsRepo;

        private BoardRepository boardRepository;

        private CoOwnerRepo coOwnerRepo;

        private MeetingServiceClass serviceClass;

        private UserRepository userRepository;

    @Autowired
    public MeetingController(MeetingRepository meetRepo, RepeatMeetingsRepo meetingsRepo, BoardRepository boardRepository, CoOwnerRepo coOwnerRepo, MeetingServiceClass serviceClass, UserRepository userRepository) {
        this.meetRepo = meetRepo;
        this.meetingsRepo = meetingsRepo;
        this.boardRepository = boardRepository;
        this.coOwnerRepo = coOwnerRepo;
        this.serviceClass = serviceClass;
        this.userRepository = userRepository;
    }




        @GetMapping("/meeting")
        public String viewAllMeetings(Model model){
            return viewMeetList(model, null,1,"meetDate","asc");
        }

        @GetMapping("/meeting/page/{page}")
        public String viewMeetList(Model model, @Param("keyword") String keyword,
                                    @PathVariable(name = "page") int page,
                                    @Param("field") String field, @Param("dir") String dir) {
            Page<RepeatMeetings> content = serviceClass.listAll(keyword,page,dir,field);
            List<RepeatMeetings> listMeets = content.getContent();
            model.addAttribute("meetings", listMeets);
            model.addAttribute("keyword",keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", content.getTotalPages());
            model.addAttribute("totalUsers",content.getTotalElements());
            model.addAttribute("sortDir", dir);
            model.addAttribute("sortField",field);
            model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
            return "scheduled/scheduled";
        }


        @RequestMapping("/delete_meet/{meet_id}")
        public String deleteMeet(@PathVariable(name = "meet_id") int id) {
            meetRepo.deleteById(id);
            return "meeting";
        }

//        @InitBinder
//        protected void initBinder(WebDataBinder binder) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//            binder.registerCustomEditor(LocalTime.class, new CustomDateEditor(
//                  dateFormat, true));
//        }

        @GetMapping("/new_meet")
        public String meetingForm(Model model) {
            Meeting meeting = new Meeting();
            List<BoardRoom> board = boardRepository.findAll();
            List<User> users = userRepository.findAll();
            model.addAttribute("meet", meeting);
            model.addAttribute("board",board);
            model.addAttribute("users",users);
            return "create.meeting/create.meeting";
        }


        @PostMapping("/save_meet")
        public String saveNewMeet(Meeting meet) {
            // TODO: 11/4/2021 Add Creators ID in the save
            meet.getRepeatMeetings().forEach(r->r.setMeeting(meet));
            meetRepo.save(meet);
            return "redirect:meeting";
        }

        @RequestMapping("/reschedule/{meet_id}")
        public ModelAndView showEditUserForm(@PathVariable(name = "meet_id") Integer id, Model model) {
            List<BoardRoom> board = boardRepository.findAll();
            ModelAndView mnv = new ModelAndView("edit.meeting/editMeeting");
            Meeting m = (Meeting) meetRepo.getById(id);
            model.addAttribute("board",board);
            mnv.addObject("meet", m);
            return mnv;
        }


}
