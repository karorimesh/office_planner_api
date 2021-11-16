package com.tracom.office_planner.User;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserServiceClass userService;



    @GetMapping("/list_users")
    public String viewUsers(Model model){
        return viewUsersList(model, null,1,"userName","asc");
    }

    @GetMapping("/list_users/page/{page}")
    public String viewUsersList(Model model, @Param("keyword") String keyword,
                                @PathVariable(name = "page") int page,
                                @Param("field") String field, @Param("dir") String dir) {
        Page<User> content = userService.listAll(keyword,page,dir,field);
        List<User> listUsers = content.getContent();
        model.addAttribute("userList", listUsers);
        model.addAttribute("keyword",keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", content.getTotalPages());
        model.addAttribute("totalUsers",content.getTotalElements());
        model.addAttribute("sortDir", dir);
        model.addAttribute("sortField",field);
        model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
        return "user.manager/userManager";
    }


    @RequestMapping(value = "/delete_user/{user_id}")
    public String deleteUser(@PathVariable(name = "user_id") int id) {
        userRepo.deleteById(id);
        return "redirect: user.manager/userManager";
    }

    @GetMapping("/add_user")
    public String showAddUsersForm(Model model) {

        model.addAttribute("user", new User());
        return "create.user/createUser";
    }

    /*Create a @PostMapping function to save new user

     */
    @PostMapping("/save_user")
    public String saveNewUser(User us, HttpServletRequest request, Model model) {
        String token = RandomString.make(10);
        String resetlink = Utility.getSiteUrl(request)+"/register?token="+token;
        System.out.println(resetlink);
        try {
            us.setToken(token);
            userRepo.save(us);
            userService.sendRegisterMail(us,resetlink);
            model.addAttribute("message", "Email sent Successfully");
            throw new MessagingException("Could not send email please check to ensure it's valid");
        }  catch ( MessagingException e){
            model.addAttribute("error",e.getMessage());
        }catch (DataIntegrityViolationException e){
            model.addAttribute("error","Email already exists");
        }

        System.out.println(resetlink);
        return "redirect: user.manager/userManager";
    }


    @PostMapping("/edited_user")
    public String saveEdited(User user){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(user.getUserPassword());
        user.setUserPassword(encoded);
        userRepo.save(user);
        return "homepage/homepage";
    }

    @RequestMapping("/edit_user/{user_id}")
    public ModelAndView userProfile(@PathVariable(name = "user_id") Integer user_id){
        ModelAndView modelAndView = new ModelAndView("edit.profile/editProfile");
        User user = userRepo.getById(user_id);
        modelAndView.addObject("profile",user);
        return modelAndView;
    }

    @RequestMapping("/my_profile")
    public ModelAndView currentUser(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("edit.profile/editProfile");
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        modelAndView.addObject("profile", user);
        return modelAndView;
        // TODO: 11/14/2021 Add nullpointerexception Handler here 
    }


}
