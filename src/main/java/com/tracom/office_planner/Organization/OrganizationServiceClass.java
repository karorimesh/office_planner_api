package com.tracom.office_planner.Organization;

import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import com.tracom.office_planner.User.UserServiceClass;
import com.tracom.office_planner.User.Utility;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;

// TODO: 12/23/2021 delete ts class if I wont use it
@Transactional
@Service
public class OrganizationServiceClass {
    private final OrganizationRepo organizationRepo;

    private final UserRepository userRepo;

    private final UserServiceClass userService;

    @Autowired
    public OrganizationServiceClass(OrganizationRepo organizationRepo, UserRepository userRepo, UserServiceClass userService) {
        this.organizationRepo = organizationRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    public void createOrg(Model model){
        Organization org = new Organization();
        User user = new User();
        model.addAttribute("org",org);
        model.addAttribute("user", user);
    }

    /*
    Save a new organization
     */
    public void saveOrg(HttpServletRequest request, User user, Organization organization, Model model){
        String token = RandomString.make(10);
        String resetLink = Utility.getSiteUrl(request)+"/register?token="+token;
        System.out.println(resetLink);
        try {
            user.setToken(token);
            user.setOrganization(organization);
            user.setUserRole("admin");
            organizationRepo.save(organization);
            userRepo.save(user);
            userService.sendRegisterMail(user,resetLink);
            PlannerLogger.createOrganization(organization);
            PlannerLogger.createUser(user);
            model.addAttribute("message", "Organization Added Successfully, Check Email to register Admin");
            throw new MessagingException("Details not valid or already exist");
        }  catch ( MessagingException e){
            model.addAttribute("error",e.getMessage());
        }catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Email already exists");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            model.addAttribute(e.getMessage());
        }
    }
}
