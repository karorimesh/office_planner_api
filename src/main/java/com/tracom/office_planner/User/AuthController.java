package com.tracom.office_planner.User;

/**
 * Controller to enable authentication o a user into the system
 */


import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class AuthController {

    private final UserServiceClass serviceClass;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserServiceClass serviceClass, UserRepository userRepository) {
        this.serviceClass = serviceClass;
        this.userRepository = userRepository;
    }

    /*
    Logged-out user requesting a password reset
    */
    @GetMapping("/forgot")
    public String getPasswordForm(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "forgot";
        }
        return "redirect:/home";
    }

/*
Sending the reset link to the user
 */
    @PostMapping("/forgot")
    public String sendResetEmail(HttpServletRequest request, Model model){
        // TODO: 1/24/2022 Update view to take a message
        serviceClass.sendResetMail(request,model);
        return getPasswordForm(model);
    }

/*
 Providing the password reset form based on the users link
 */
    @GetMapping("/reset")
    public String getResetForm(@Param("token") String token, Model model){
        model.addAttribute("token",token);
        return "forgotForm";
    }

/*
Resetting the users password
 */
    @PostMapping("/reset")
    public String resetPassword(HttpServletRequest request, Model model){
        serviceClass.resetPassword(request,model);
        return getResetForm(request.getParameter("token"),model);
    }

/*
 New user setting their password
 */
    @GetMapping("/register")
    public String registerUser(@Param("token") String token, Model model){
        model.addAttribute("token",token);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "register";
        }
        return "redirect:/home";
    }

/*
Saving a new registered user details
 */
    @PostMapping("/register")
    public String registration(HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        User user = serviceClass.getUserByToken(token);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        try{
            serviceClass.updateUser(user,username,password,phone);
            PlannerLogger.firstUserUpdate(user);
            model.addAttribute("message", "Successfully registered click Login to enter");
        }catch (DataIntegrityViolationException e){
            model.addAttribute("error","This user name is taken");
        }

        return registerUser(request.getParameter("token"),model);
    }
}
