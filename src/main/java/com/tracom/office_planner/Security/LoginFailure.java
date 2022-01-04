package com.tracom.office_planner.Security;

/*
Handler for failed login
 */

import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import com.tracom.office_planner.User.UserServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailure extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserServiceClass serviceClass;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        User user = userRepository.findUserByName(username);

        if(user != null){
            if(user.isEnabled() && user.isAccountUnlocked()){
                if(user.getFailedAttempt() < UserServiceClass.MAX_ATTEMPTS - 1){
                    serviceClass.increaseAttempts(user);
                    exception = new BadCredentialsException(" You have " + (3-user.getFailedAttempt()) + " login attempts left");
                }
                else {
                    serviceClass.lockAccount(user);
                    exception =  new LockedException("Your Account has been locked, Try again after 30 minutes");
                }
            }
            else if(!user.isAccountUnlocked()){
                if(serviceClass.unlockAccount(user)){
                    exception  = new LockedException("Your account has been unlocked Login again");
                }
            }
        }
        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request,response,exception);
    }
}
