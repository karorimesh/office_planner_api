package com.tracom.office_planner.User;

import com.tracom.office_planner.Meeting.Meeting;

public interface SendMail {
    void sendRegisterMail(User user, String token);
    void sendForgotMail(User user, String token);
}
