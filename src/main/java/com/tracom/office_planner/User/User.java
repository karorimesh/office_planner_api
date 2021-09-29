package com.tracom.office_planner.User;

import com.tracom.office_planner.Employee.Employee;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user")
//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int user_id;

    private String user_name;

    private String user_password;

    private String user_role;

    //private boolean user_admin;
    @OneToOne
    private Employee employee;

    public User(String user_name, String user_password,
                String user_role,
                Employee employee) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_role = user_role;
//        this.user_admin = user_admin;
        this.employee = employee;
    }

}
