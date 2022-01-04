package com.tracom.office_planner.Notifcations;

/*
Interface for all methods responsible to sending of the notifications
 */

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.User.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface SendNotification {
    void sendRegisterMail(User user, String token) throws MessagingException, UnsupportedEncodingException;

    void sendForgotMail(User user, String token);

    void sendMeetingMail(User user, Meeting meeting) throws MessagingException, UnsupportedEncodingException;

    void sendMeetingSms(User user, Meeting meeting);
}
