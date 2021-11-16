package com.tracom.office_planner.User;

import com.tracom.office_planner.Employee.Employee;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Organization.Organization;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    private int userId;
    @Column(unique = true)
    private String userName;

    private String userPassword;

    private String userRole;
    @Column(unique = true)
    private String userEmail;

    @Column(name = "token")
    private String token;

    //private boolean user_admin;
    @OneToOne(cascade = CascadeType.ALL)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToMany(mappedBy = "users")
    private List<Meeting> meetings = new ArrayList<Meeting>();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String user_password) {
        this.userPassword = user_password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String user_role) {
        this.userRole = user_role;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String user_email) {
        this.userEmail = user_email;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public User(String userName, String userPassword, String userRole, String userEmail, Employee employee) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRole = userRole;
        this.userEmail = userEmail;
        this.employee = employee;
    }
}
