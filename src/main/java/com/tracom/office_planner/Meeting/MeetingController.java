package com.tracom.office_planner.Meeting;

import com.azure.cosmos.implementation.guava25.collect.FluentIterable;
import com.tracom.office_planner.Boardroom.BoardRepository;
import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Boardroom.BoardServiceClass;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import com.tracom.office_planner.User.UserServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MeetingController {

        private MeetingRepository meetRepo;

        private RepeatMeetingsRepo meetingsRepo;

        private BoardServiceClass boardServiceClass;

        private MeetingServiceClass serviceClass;

        private UserServiceClass userServiceClass;

        private UserRepository userRepository;

        private BoardRepository boardRepository;

        @Autowired
        public MeetingController(MeetingRepository meetRepo, RepeatMeetingsRepo meetingsRepo, BoardServiceClass boardServiceClass, MeetingServiceClass serviceClass, UserServiceClass userServiceClass, UserRepository userRepository, BoardRepository boardRepository) {
        this.meetRepo = meetRepo;
        this.meetingsRepo = meetingsRepo;
        this.boardServiceClass = boardServiceClass;
        this.serviceClass = serviceClass;
        this.userServiceClass = userServiceClass;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    @GetMapping("/meeting")
        public String viewAllMeetings(HttpServletRequest request, Model model){
            return viewMeetList(request, model, null,1,"meetDate","asc");
        }

        @GetMapping("/my_meeting")
        public String viewAllMyMeetings(HttpServletRequest request, Model model){
            return viewMyMeetList(request, model, null,1,"meetDate","asc");
        }

        @GetMapping("/meeting/page/{page}")
        public String viewMeetList(HttpServletRequest request,Model model, @Param("keyword") String keyword,
                                    @PathVariable(name = "page") int page,
                                    @Param("field") String field, @Param("dir") String dir) {
            Principal principal = request.getUserPrincipal();
            User user = userRepository.findUserByName(principal.getName());
            Page<RepeatMeetings> content = serviceClass.listAll(keyword,page,dir,field, user.getOrganization());
            List<RepeatMeetings> listMeet = content.getContent();
            model.addAttribute("meetings", listMeet);
            model.addAttribute("keyword",keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", content.getTotalPages());
            model.addAttribute("totalUsers",content.getTotalElements());
            model.addAttribute("sortDir", dir);
            model.addAttribute("sortField",field);
            model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
            return "scheduled";
        }
        @GetMapping("/my_meeting/page/{page}")
        public String viewMyMeetList(HttpServletRequest request,Model model, @Param("keyword") String keyword,
                                    @PathVariable(name = "page") int page,
                                    @Param("field") String field, @Param("dir") String dir) {
            Principal principal = request.getUserPrincipal();
            User user = userRepository.findUserByName(principal.getName());
            Page<RepeatMeetings> content = serviceClass.listAll(keyword,page,dir,field, user.getOrganization());
            List<RepeatMeetings> listMeet = content.getContent();
            List<RepeatMeetings> listMeets = FluentIterable
                    .from(listMeet)
                            .filter(l -> l.getMeeting().getUsers().contains(user))
                    .toList();
            model.addAttribute("meetings", listMeets);
            model.addAttribute("keyword",keyword);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", content.getTotalPages());
            model.addAttribute("totalUsers",content.getTotalElements());
            model.addAttribute("sortDir", dir);
            model.addAttribute("sortField",field);
            model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
            return "scheduled";
        }


        @RequestMapping(value = "/delete_meet/{repeatId}")
        public String deleteMeet(@PathVariable(name = "repeatId") int id, HttpServletRequest request) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            Meeting meeting = meetingsRepo.getById(id).getMeeting();
            PlannerLogger.deleteMeeting(meeting, user);
            meetingsRepo.deleteById(id);
            return "redirect:/meeting";
        }

    // TODO: 11/17/2021 INIT binder to format the date and time 

//        @InitBinder
//        protected void initBinder(WebDataBinder binder) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//            binder.registerCustomEditor(LocalTime.class, new CustomDateEditor(
//                  dateFormat, true));
//        }

        @GetMapping("/new_meet")
        public String meetingForm(Model model, HttpServletRequest request) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            Meeting meeting = new Meeting();
            List<BoardRoom> boards = boardRepository.findBoards(user.getOrganization());
            List<User> usersList = userRepository.findUsers(user.getOrganization());
            List<User> users = FluentIterable.from(usersList)
                            .filter(u -> u != user)
                                    .toList();
            model.addAttribute("meet", meeting);
            model.addAttribute("board",boards);
            model.addAttribute("users",users);
            return "createMeeting";
        }


        @PostMapping("/save_meet")
        public String saveNewMeet(Meeting meet, HttpServletRequest request) {
            // TODO: 11/4/2021 Add Creators ID in the save
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            meet.setOrganization(user.getOrganization());
            meet.getUsers().add(user);
            meet.getRepeatMeetings().forEach(r->r.setMeeting(meet));
            meetRepo.save(meet);
            PlannerLogger.createMeeting(meet,user);
            return "redirect:meeting";
        }

        @PostMapping("/meet_edited")
        public String saveEditedMeet(Meeting meet, HttpServletRequest request) {
            // TODO: 11/4/2021 Add Creators ID in the save
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            meet.setOrganization(user.getOrganization());
            meet.getRepeatMeetings().forEach(r->r.setMeeting(meet));
            meetRepo.save(meet);
            PlannerLogger.updateMeeting(meet,user);
            return "redirect:meeting";
        }

        @RequestMapping("/reschedule/{meet_id}")
        public ModelAndView showEditUserForm(HttpServletRequest request,@PathVariable(name = "meet_id") Integer id, Model model) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            List<BoardRoom> boards = boardRepository.findBoards(user.getOrganization());
            List<User> users = userRepository.findUsers(user.getOrganization());
            ModelAndView mnv = new ModelAndView("editMeeting");
            Meeting m = (Meeting) meetRepo.getById(id);
            model.addAttribute("board",boards);
            model.addAttribute("users",users);
            mnv.addObject("meet", m);
            return mnv;
        }

        @PostMapping("/calendar")
        public String showCalendar( HttpServletRequest request, Model model){
        String keyword = request.getParameter("date");
            return viewMeetList(request, model, keyword,1,"meetDate","asc");
        }
}
