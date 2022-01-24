package com.tracom.office_planner.Api;

import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.Organization.OrganizationRepo;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import com.tracom.office_planner.User.UserServiceClass;
import com.tracom.office_planner.User.Utility;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;

@Transactional
@Service
public class ApiServiceClass {

    private final UserRepository userRepo;
    private final OrganizationRepo organizationRepo;
    private final UserServiceClass userServiceClass;

    @Autowired
    public ApiServiceClass(UserRepository userRepo, OrganizationRepo organizationRepo, UserServiceClass userServiceClass) {
        this.userRepo = userRepo;
        this.organizationRepo = organizationRepo;
        this.userServiceClass = userServiceClass;
    }

    //    Organization Logic
    /*
    Save an oranization logic
     */
    public void saveOrganization(HttpServletRequest request, User user, Organization organization) throws MessagingException, UnsupportedEncodingException {
        String token = RandomString.make(10);
        String registerLink = Utility.getSiteUrl(request)+"/register?token="+token;
        System.out.println(registerLink);
        user.setToken(token);
        organizationRepo.save(organization);
        user.setOrganization(organization);
        userRepo.save(user);
        PlannerLogger.createUser(user);
        userServiceClass.sendRegisterMail(user,registerLink);
    }


}
