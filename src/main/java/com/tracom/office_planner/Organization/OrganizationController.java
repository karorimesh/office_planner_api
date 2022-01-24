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

    private final OrganizationServiceClass serviceClass;

    @Autowired
    public OrganizationController(OrganizationServiceClass serviceClass,OrganizationRepo organizationRepo, UserRepository userRepo, UserServiceClass userService) {
        this.organizationRepo = organizationRepo;
        this.userRepo = userRepo;
        this.userService = userService;
        this.serviceClass = serviceClass;
    }

    @GetMapping("/new_org")
    public String createOrg(Model model){
        serviceClass.createOrg(model);
        /*
            Only a logged-out user can create an organization
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
            return "createOrganization";
        }
        return "redirect:/home";
    }

    @PostMapping("/save_org")
    public String saveOrg(Organization organization, User us, HttpServletRequest request, Model model){

        serviceClass.saveOrg(request,us,organization,model);
        return createOrg(model);
    }
}
