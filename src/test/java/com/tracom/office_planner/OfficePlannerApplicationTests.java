package com.tracom.office_planner;

import com.tracom.office_planner.Employee.Employee;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserServiceClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OfficePlannerApplicationTests {

    @Autowired
    UserServiceClass serviceClass;

    @Test
    void contextLoads() {
    }

    @Test
    void mailService(){
        User user = new User();
        user.setUser_email("karorimesh@gmail.com");
        serviceClass.sendMail(user);
    }

}
