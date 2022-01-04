package com.tracom.office_planner.Security;

/* Handler for successful login
 */

import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.User.CustomUser;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import com.tracom.office_planner.User.UserServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class LoginSuccess extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserServiceClass serviceClass;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        User user = customUser.getUser();
        PlannerLogger.loggedInUser(user);
        if (user.getFailedAttempt() > 0){
            serviceClass.resetAttempts(user.getUserEmail());
        }
        super.onAuthenticationSuccess(request,response,authentication);
    }
}
