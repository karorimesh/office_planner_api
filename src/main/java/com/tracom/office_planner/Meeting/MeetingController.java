package com.tracom.office_planner.Meeting;

/*
Meeting controller class
 */

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
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class MeetingController {

        private final MeetingRepository meetRepo;

        private final RepeatMeetingsRepo meetingsRepo;

        private final BoardServiceClass boardServiceClass;

        private final MeetingServiceClass serviceClass;

        private final UserServiceClass userServiceClass;

        private final UserRepository userRepository;

        private final BoardRepository boardRepository;

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
        public String meetingForm(Model model, HttpServletRequest request, Meeting meeting) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
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
        public String saveNewMeet(Meeting meet, HttpServletRequest request, Model model) {
            // TODO: 11/4/2021 Add Creators ID in the save
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            meet.setOrganization(user.getOrganization());
            meet.getUsers().add(user);
            meet.getRepeatMeetings().forEach(r->r.setMeeting(meet));
            List<RepeatMeetings> meetings = meet.getRepeatMeetings();
//            Check for any conflicting meeting
            for (RepeatMeetings r : meetings){
                if(meetingsRepo.findConflictingMeet(meet.getBoardroom(), r.getMeetDate(),meet.getMeetStart()) != null){
                    model.addAttribute("error","This boardroom will be busy at this time! contact an admin for a reschedule or check the calendar for available time");
                    return meetingForm(model,request,meet);
                }
            }
            PlannerLogger.createMeeting(meet,user);
            meetRepo.save(meet);
            return "redirect:/my_meeting";

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
            Meeting m = meetRepo.getById(id);
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
