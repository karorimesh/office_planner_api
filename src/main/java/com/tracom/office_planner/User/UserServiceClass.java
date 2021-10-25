package com.tracom.office_planner.User;

import com.tracom.office_planner.Meeting.Meeting;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceClass implements SendMail {
    private final JavaMailSender mailSender;

    public UserServiceClass(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Override
    public void sendMail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("New User Registration");
        message.setTo(user.getUser_email());
        message.setText("Below is a link to register to the office planner" +
                " click on to set up a profile and use our service, regards");

        mailSender.send(message);
    }

}
