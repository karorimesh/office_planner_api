package com.tracom.office_planner.User;

//Utility to generate a link to send to the user

import javax.servlet.http.HttpServletRequest;

public class Utility {
    public static String getSiteUrl(HttpServletRequest request){
        String site = request.getRequestURL().toString();
        return site.replace(request.getServletPath(),"");
    }
}
