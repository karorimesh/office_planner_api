package com.tracom.office_planner.User;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "user_id", nullable = false)
    private int user_id;
    @Column(name = "user_name", length = 20, nullable = false)
    private String user_name;
    @Column(name = "password", length = 20, nullable = false)
    private String user_password;
    @Column(name = "user_admin")
    private boolean user_admin;

    public boolean isUser_admin() {
        return user_admin;
    }

    public void setUser_admin(boolean user_admin) {
        this.user_admin = user_admin;
    }

    public User() {
    }

    public User(String user_name, String user_password, boolean user_access) {
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_admin = user_admin;
    }

    public User(int user_id, String user_name, String user_password, boolean user_access) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_password = user_password;
        this.user_admin = user_admin;
    }

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

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                ", user_password='" + user_password + '\'' +
                ", user_admin=" + user_admin +
                '}';
    }
}
