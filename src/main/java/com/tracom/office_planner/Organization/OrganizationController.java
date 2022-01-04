package com.tracom.office_planner.Organization;



import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import com.tracom.office_planner.User.UserServiceClass;
import com.tracom.office_planner.User.Utility;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class OrganizationController {

    private final OrganizationRepo organizationRepo;

    private final UserRepository userRepo;

    private final UserServiceClass userService;

    @Autowired
    public OrganizationController(OrganizationRepo organizationRepo, UserRepository userRepo, UserServiceClass userService) {
        this.organizationRepo = organizationRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @GetMapping("/new_org")
    public String createOrg(Model model){
        Organization org = new Organization();
        User user = new User();
        model.addAttribute("org",org);
        model.addAttribute("user", user);
//        Only a logged out user can create an ganization
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "createOrganization";
        }
        return "redirect:/home";
    }

    @PostMapping("/save_org")
    public String saveOrg(Organization organization, User us, HttpServletRequest request, Model model){

        String token = RandomString.make(10);
        String resetlink = Utility.getSiteUrl(request)+"/register?token="+token;
        System.out.println(resetlink);
        try {
            us.setToken(token);
            us.setOrganization(organization);
            us.setUserRole("admin");
            organizationRepo.save(organization);
            userRepo.save(us);
            userService.sendRegisterMail(us,resetlink);
            PlannerLogger.createOrganization(organization);
            PlannerLogger.createUser(us);
            model.addAttribute("message", "Organization Added Successfully, Check Email to register Admin");
            throw new MessagingException("Details not valid or already exist");
        }  catch ( MessagingException e){
            model.addAttribute("error",e.getMessage());
        }catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Email already exists");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}
