package com.tracom.office_planner.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserServiceClass userService;



    @GetMapping("/list_users")
    public String viewUsers(Model model){
        List<User> usersList = userRepo.findAll();
        model.addAttribute("userList", usersList);
        return "list_users";
    }


    @RequestMapping("/delete_user/{user_id}")
    public String deleteUser(@PathVariable(name = "user_id") int id) {
        userRepo.deleteById(id);
        return "list_users";
    }

    @GetMapping("/add_user")
    public String showAddUsersForm(Model model) {

        model.addAttribute("user", new User());
        return "add_user";
    }

    /*Create a @PostMapping function to save new user

     */
    @PostMapping("/save_user")
    public String saveNewUser(User us) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(us.getUser_password());
        us.setUser_password(encoded);
        userRepo.save(us);
        userService.sendMail(us);
        return "list_users";
    }

    @RequestMapping("/edit_user/{user_id}")
//    public ModelAndView showEditUserForm(@PathVariable(name = "user_id") Integer id) {
//        ModelAndView mnv = new ModelAndView("edit_user");
//        User user = userRepo.getById(id);
//        mnv.addObject("User", user);
//        return mnv;
//    }
    public ModelAndView userProfile(@PathVariable(name = "user_id") Integer user_id){
        ModelAndView modelAndView = new ModelAndView("edit_user");
        User user = userRepo.getById(user_id);
        modelAndView.addObject("profile",user);
        return modelAndView;
    }


}
