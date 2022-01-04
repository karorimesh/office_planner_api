package com.tracom.office_planner;

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserServiceClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class OfficePlannerApplicationTests {

    @Autowired
    UserServiceClass serviceClass;

//    @Test
    void contextLoads() {
    }

//    @Test
    void mailService() throws MessagingException, UnsupportedEncodingException {
        User user = new User();
        user.setUserEmail("karorimesh@gmail.com");
        serviceClass.sendRegisterMail(user,"link");
    }

    @Test
    public void sendSms(){
        User user = new User();
        Meeting meeting = new Meeting();
        BoardRoom boardRoom = new BoardRoom();
        boardRoom.setBoardName("Block Karori");
        user.setPhone("+254710896261");
        meeting.setMeetName("Sms test");
        meeting.setBoardroom(boardRoom);
        serviceClass.sendMeetingSms(user,meeting);
    }

}
