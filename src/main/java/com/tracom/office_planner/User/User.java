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

    private String user_email;

    //private boolean user_admin;
    @OneToOne(cascade = CascadeType.ALL)
    private Employee employee;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public User(String user_name, String user_password, String user_role, String user_email, Employee employee) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_role = user_role;
        this.user_email = user_email;
        this.employee = employee;
    }
}
