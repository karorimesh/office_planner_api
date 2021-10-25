package com.tracom.office_planner;

import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectController {
    @Autowired
    private UserRepository userRepo;
    @GetMapping("")
    public String landPage(){
        return "landing/landing";
    }


    @GetMapping("/landing")
    public String landingPage(){
        return "landing/landing";
    }


    @GetMapping("/home")
    public String viewHomePage()
    {
        return "homepage/homepage";
    }


    @GetMapping("/login")
    public String validateUser(){
        return"login/login";
    }


    @GetMapping("/403")
    public String returnError(){
        return "403";
    }


}
