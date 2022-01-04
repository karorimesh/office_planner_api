package com.tracom.office_planner;

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserServiceClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SmsTest {
    @Autowired
    UserServiceClass userServiceClass;
    @Test
    public void sendSms(){
        User user = new User();
        Meeting meeting = new Meeting();
        BoardRoom boardRoom = new BoardRoom();
        boardRoom.setBoardName("Block Karori");
        user.setPhone("+254710896261");
        List<User> users = new ArrayList<User>();
        users.add(user);
        meeting.setMeetName("Sms test");
        meeting.setUsers(users);
        meeting.setBoardroom(boardRoom);
        userServiceClass.sendMeetingSms(user,meeting);
    }
}
