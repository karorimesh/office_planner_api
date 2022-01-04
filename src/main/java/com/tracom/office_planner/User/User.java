package com.tracom.office_planner.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tracom.office_planner.Employee.Employee;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Organization.Organization;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
Entity class for a user
 */

@Entity
@Table(name = "user")
//Lombok
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property  = "meetId")
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private List<Meeting> meetings = new ArrayList<Meeting>();

    @Column(name = "account_unlocked", columnDefinition = "boolean default true")
    private boolean accountUnlocked = true;

    @Column(name = "attempts")
    private Integer failedAttempt = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;

    @OneToMany
    List<UserPassword> userPasswords = new ArrayList<>();

    @Column(name = "phone", length = 15)
    private String phone;

    public User(int userId) {
        this.userId = userId;
    }

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

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userRole='" + userRole + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", token='" + token + '\'' +
                ", employee=" + employee +
                ", organization=" + organization +
                '}';
    }
}
