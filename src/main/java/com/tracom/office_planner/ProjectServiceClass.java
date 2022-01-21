package com.tracom.office_planner;
/*
Currently, not in use
 */

import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class ProjectServiceClass {

    private  final UserRepository userRepository;

    public User findUser(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        return user;
    }
}
