package com.tracom.office_planner.Employee;

/* Entity for employee - an incomplete concept
 to enable an organization with an
already existing database for employees
 */

import com.tracom.office_planner.Organization.Organization;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "employee")
@SecondaryTable(name = "employeeName", pkJoinColumns = @PrimaryKeyJoinColumn(name = "name_id"))
//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int emp_id;
    @Column(name = "First_Name", table = "employeeName")
    private String emp_firstName;
    @Column(name = "Last_Name", table = "employeeName")
    private String emp_LastName;
    private String gender;
    private String phone;
    private String email;
    @ManyToOne(cascade = CascadeType.ALL)
    private Organization organization;


    public Employee(String emp_firstName,
                    String emp_LastName, String gender
            , String phone, String email,
                    Organization organization) {
        this.emp_firstName = emp_firstName;
        this.emp_LastName = emp_LastName;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.organization = organization;
    }

    public Employee(String email) {
        this.email = email;
    }
}
