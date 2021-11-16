package com.tracom.office_planner.User;


import net.bytebuddy.utility.RandomString;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    @Autowired
    private UserServiceClass serviceClass;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/forgot")
    public String getPasswordForm(){
        return "login/forgot";
    }

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
            System.out.println(resetlink);
        }
        else{
            model.addAttribute("error", "Email does not exist");
        }

        return "login/forgot";
    }
    @GetMapping("/reset")
    public String getResetForm(@Param("token") String token, Model model){
        User user = serviceClass.getUserByToken(token);
        model.addAttribute("token",token);
        return "login/forgot_form";
    }
    @PostMapping("/reset")
    public String resetPassword(HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        try {
            User user = serviceClass.getUserByToken(token);
            serviceClass.updatePassword(user,password);
            throw new UsernameNotFoundException("User does not exist");
        } catch(UsernameNotFoundException e){
            model.addAttribute("error",e.getMessage());
        }
        // TODO: 10/27/2021 Add try catch for invalid token
        return "login/login";
    }

    @GetMapping("/register")
    public String registerUser(@Param("token") String token, Model model){
        model.addAttribute("token",token);
        return "login/register";
    }

    @PostMapping("/register")
    public String registration(HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        User user = serviceClass.getUserByToken(token);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try{
            serviceClass.updateUser(user,username,password);
            model.addAttribute("message", "Successfully registered click Login to enter");
        }catch (DataIntegrityViolationException e){
            model.addAttribute("error","This user name is taken");
        }

        return "login/register";
    }
}
