//Organization Entity to allow addition of more than one organization
package com.tracom.office_planner.Organization;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tracom.office_planner.User.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;

//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "organization")

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "organization_id")
@JsonIncludeProperties({"organization_id","organization_name","organization_desc"})
public class Organization {
    @Id
    private String organization_id;
    private String organization_name;
    private String organization_desc;


    public Organization(String organization_name) {
        this.organization_name = organization_name;
    }
}
