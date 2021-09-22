package com.tracom.office_planner.Employee;

import com.tracom.office_planner.Organization.Organization;

import javax.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    private int emp_id;
    private String emp_name;
    private String gender;
    private String phone;
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    private Organization organization;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Employee(int emp_id, String emp_name, String gender, String phone, String email) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    public Employee() {
    }

    public int getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(int emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "emp_id=" + emp_id +
                ", emp_name='" + emp_name + '\'' +
                ", gender='" + gender + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", organization=" + organization +
                '}';
    }
}
