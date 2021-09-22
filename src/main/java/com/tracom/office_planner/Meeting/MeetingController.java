package com.tracom.office_planner.Meeting;

import com.tracom.office_planner.User.User;
import com.tracom.office_planner.Meeting.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
@Controller
public class MeetingController {
        @Autowired
        private MeetingRepository meetRepo;

        @GetMapping("/meeting")
        public String viewUsers(Model model){
            List<Meeting> meetings = meetRepo.findAll();
            model.addAttribute("meet", meetings);
            return "meeting";
        }

        @RequestMapping("/delete_meet/{meet_id}")
        public String deleteMeet(@PathVariable(name = "meet_id") int id) {
            meetRepo.deleteById(id);
            return "meeting";
        }

        @GetMapping("/new_meet")
        public String meetingForm(Model model) {

            model.addAttribute("meet", new Meeting());
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
