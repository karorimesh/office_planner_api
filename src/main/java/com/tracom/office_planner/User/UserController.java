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
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserServiceClass userService;



//    Return all users in an organization
    @GetMapping("/list_users")
    public String viewUsers(HttpServletRequest request,Model model){
        return viewUsersList(request,model, null,1,"userName","asc");
    }

//    Return users based on search
    @GetMapping("/list_users/page/{page}")
    public String viewUsersList(HttpServletRequest request,Model model, @Param("keyword") String keyword,
                                @PathVariable(name = "page") int page,
                                @Param("field") String field, @Param("dir") String dir) {
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User currentUser = userRepo.findUserByName(name);
        Page<User> content = userService.listAll(keyword,page,dir,field, currentUser.getOrganization());
        List<User> listUser = content.getContent();
        List<User> listUsers = FluentIterable.from(listUser)
                        .filter(u -> u != currentUser)
                        .toList();
        model.addAttribute("userList", listUsers);
        model.addAttribute("keyword",keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", content.getTotalPages());
        model.addAttribute("totalUsers",content.getTotalElements());
        model.addAttribute("sortDir", dir);
        model.addAttribute("sortField",field);
        model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
        return "userManager";
    }

//    Admin function to delete a user
    @RequestMapping(value = "/delete_user/{user_id}")
    public String deleteUser(@PathVariable(name = "user_id") int id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        User deletedUser = userRepo.getById(id);
        PlannerLogger.deleteUser(deletedUser,user);
        userRepo.deleteById(id);
        return "redirect:/list_users";
    }

//    Admin function to add a user
    @GetMapping("/add_user")
    public String showAddUsersForm(Model model, User user) {

        model.addAttribute("user", user);
        return "createUser";
    }

    /*Create a @PostMapping function to save new user

     */
    @PostMapping("/save_user")
    public String saveNewUser(User us, HttpServletRequest request, Model model) {
        String token = RandomString.make(10);
        String resetlink = Utility.getSiteUrl(request)+"/register?token="+token;
        System.out.println(resetlink);
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        try {
            us.setToken(token);
            us.setOrganization(user.getOrganization());
            PlannerLogger.createUser(us);
            userService.sendRegisterMail(us,resetlink);
            throw new MessagingException("Could not send email please check to ensure it's valid");
        }
        catch ( MessagingException e){
            model.addAttribute("error","Could not send email please check to ensure it's valid");
            userRepo.delete(us);
        }
        catch (DataIntegrityViolationException e){
            model.addAttribute("error","Email already exists");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(userRepo.findById(user.getUserId()) != null){
            model.addAttribute("message", "Email sent successfully");
        }

        return showAddUsersForm(model,us);
    }

//  User to edit their profile
    @PostMapping("/edited_profile")
    public String saveProfile(User user){
        user.setOrganization(userRepo.findById(user.getUserId()).get().getOrganization());
        user.setUserPassword(userRepo.findById(user.getUserId()).get().getUserPassword());
        userRepo.save(user);
        PlannerLogger.updateUser(user);
        return "redirect:/?logout";
    }

//    Admin function to edit a users details
    @PostMapping("/edited_user")
    public String saveEdited(User user){
        user.setOrganization(userRepo.findById(user.getUserId()).get().getOrganization());
        user.setUserPassword(userRepo.findById(user.getUserId()).get().getUserPassword());
        userRepo.save(user);
        PlannerLogger.updateUser(user);
        return "redirect:/list_users";
    }

//    Admin function to edit a user
    @RequestMapping("/edit_user/{user_id}")
    public ModelAndView userProfile(@PathVariable(name = "user_id") Integer user_id){
        ModelAndView modelAndView = new ModelAndView("editUser");
        User user = userRepo.getById(user_id);
        modelAndView.addObject("profile",user);
        return modelAndView;
    }

//    Current logged in user to view their profile
    @RequestMapping("/my_profile")
    public ModelAndView currentUser(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("editProfile");
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        modelAndView.addObject("profile", user);
        return modelAndView;
        // TODO: 11/14/2021 Add nullpointerexception Handler here 
    }

}
