package com.tracom.office_planner;


import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Meeting.MeetingRepository;
import com.tracom.office_planner.Notifcations.NotificationsUtil;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Timer;

//@SpringBootTest
public class Notifications {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private NotificationsUtil notificationsUtil;


//    @Test
    public void notificationsTest(){
        Timer timer = new Timer(true);
        User user = new User();
        Meeting meet = new Meeting();
        meet.setMeetName("Notifications");
        user.setPhone("+254710896261");
        user.setUserEmail("karorimesh@gmail.com");
    }
}
