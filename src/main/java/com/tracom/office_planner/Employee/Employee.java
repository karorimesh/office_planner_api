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
}
