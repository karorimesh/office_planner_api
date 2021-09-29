//Organization Entity
package com.tracom.office_planner.Organization;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "organization")

public class Organization {
    @Id
    private int organization_id;
    private String organization_name;

    public Organization(String organization_name) {
        this.organization_name = organization_name;
    }
}
