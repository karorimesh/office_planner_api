package com.tracom.office_planner.Meeting;

import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
        @Autowired
        private MeetingRepository meetRepo;
        @Autowired
        private RepeatMeetingsRepo meetingsRepo;

        @GetMapping("/meeting")
        public String viewUsers(Model model){
            List<Meeting> meetings = meetRepo.findAll();
            List<RepeatMeetings> repeatMeetings = meetingsRepo.findAll();
            model.addAttribute("meet", meetings);
            model.addAttribute("repeatMeet", repeatMeetings);
            return "meeting";
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
            model.addAttribute("meet", meeting);
            return "new_meet";
        }


        @PostMapping("/save_meet")
        public String saveNewMeet(Meeting meet) {
            meetRepo.save(meet);
            return "meeting";
        }

        @RequestMapping("/reschedule/{meet_id}")
        public ModelAndView showEditUserForm(@PathVariable(name = "meet_id") Integer id) {
            ModelAndView mnv = new ModelAndView("reschedule");
            Meeting m = (Meeting) meetRepo.getById(id);
            mnv.addObject("meet", m);
            return mnv;
        }


}
