package com.tracom.office_planner.User;

//Controller to enable authentication o a user into the system


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
    @Autowired
    private UserServiceClass serviceClass;
    @Autowired
    private UserRepository userRepository;

//    Logged-out user requesting a password reset
    @GetMapping("/forgot")
    public String getPasswordForm(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "forgot";
        }
        return "redirect:/home";
    }

//    Sending the reset link to the user
    @PostMapping("/forgot")
    public String sendResetEmail(HttpServletRequest request, Model model){
        String email = request.getParameter("email");
        String token = RandomString.make(10);
        // TODO: 10/27/2021 Add try and catch method here to handle error
        User user = userRepository.findByEmail(email);
        if(user != null){
            serviceClass.updateToken(token,email);
            String resetlink = Utility.getSiteUrl(request) +"/reset?token="+token;
            serviceClass.sendForgotMail(user,resetlink);
            PlannerLogger.resetPasswordRequest(user);
            System.out.println(resetlink);
        }
        else{
            model.addAttribute("error", "Email does not exist");
        }

        return "forgot";
    }

//    Providing the password reset form based on the users link
    @GetMapping("/reset")
    public String getResetForm(@Param("token") String token, Model model){
        User user = serviceClass.getUserByToken(token);
        model.addAttribute("token",token);
        return "forgotForm";
    }

//    Resetting the users password
    @PostMapping("/reset")
    public String resetPassword(HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        try {
            User user = serviceClass.getUserByToken(token);
            List<UserPassword> userPasswords = user.getUserPasswords();
            List<String> passwords = new ArrayList<>();
            Collections.reverse(userPasswords);
            userPasswords.subList(1,4);
            userPasswords.forEach(up -> {
                passwords.add(up.getUserPassword());
            });
            if (passwords.contains(encodedPassword)){
                model.addAttribute("error", "Use a password you've not used before");
                return getResetForm(token,model);
            }else {
                serviceClass.updatePassword(user,password);
                PlannerLogger.resetSuccess(user);
            }
            throw new UsernameNotFoundException("User does not exist");
        } catch(UsernameNotFoundException e){
            model.addAttribute("error",e.getMessage());
        }
        // TODO: 10/27/2021 Add try catch for invalid token
        return "login";
    }

//    New user setting their password
    @GetMapping("/register")
    public String registerUser(@Param("token") String token, Model model){
        model.addAttribute("token",token);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "register";
        }
        return "redirect:/home";
    }

//    Saving a new registered user details
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

        return "register";
    }
}
