package com.tracom.office_planner.User;

/*
Controller class for the users crud functions
 */

import com.azure.cosmos.implementation.guava25.collect.FluentIterable;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepo;
    private final UserServiceClass userService;

    @Autowired
    public UserController(UserRepository userRepo, UserServiceClass userService) {
        this.userRepo = userRepo;
        this.userService = userService;
    }

    //    Return all users in an organization
    @GetMapping("/list_users")
    public String viewUsers(HttpServletRequest request,Model model){
        return viewUsersList(request,model, null,1,"userName","asc");
    }

/*
Return users based on search
 */
    @GetMapping("/list_users/page/{page}")
    public String viewUsersList(HttpServletRequest request,Model model, @Param("keyword") String keyword,
                                @PathVariable(name = "page") int page,
                                @Param("field") String field, @Param("dir") String dir) {
        userService.viewUsersList(request,keyword,page,dir,field,model);
        return "userManager";
    }

/*
Admin function to delete a user
 */
    @RequestMapping(value = "/delete_user/{user_id}")
    public String deleteUser(@PathVariable(name = "user_id") int id, HttpServletRequest request) {
        userService.deleteUser(request,id);
        return "redirect:/list_users";
    }

/*
Admin function to add a user
 */
    @GetMapping("/add_user")
    public String showAddUsersForm(Model model, User user) {
        model.addAttribute("user", user);
        return "createUser";
    }

    /*
    Create a @PostMapping function to save new user
     */
    @PostMapping("/save_user")
    public String saveNewUser(User us, HttpServletRequest request, Model model) {
        userService.saveNewUser(request,us,model);
        return showAddUsersForm(model,us);
    }

/*
User to edit their profile
 */
    @PostMapping("/edited_profile")
    public String saveProfile(User user){
        userService.saveProfile(user);
        return "redirect:/?logout";
    }

/*
Admin function to edit a users details
 */
    @PostMapping("/edited_user")
    public String saveEdited(User user){
        userService.saveEdited(user);
        return "redirect:/list_users";
    }

/*
 Admin function to edit a user
 */
    @RequestMapping("/edit_user/{user_id}")
    public ModelAndView userProfile(@PathVariable(name = "user_id") Integer user_id){
        return userService.userProfile(user_id);
    }

/*
Current logged in user to view their profile
 */
    @RequestMapping("/my_profile")
    public ModelAndView currentUser(HttpServletRequest request){
        return userService.currentUser(request);
        // TODO: 11/14/2021 Add nullpointerexception Handler here 
    }

}
