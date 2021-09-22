//Organization Entity
package com.tracom.office_planner.Organization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "organization")

public class Organization {
    @Id
    private int organization_id;
    @Column(length = 50)
    private String organization_name;

    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "organization_id=" + organization_id +
                ", organization_name='" + organization_name + '\'' +
                '}';
    }

    public Organization() {
    }

    public Organization(int organization_id, String organization_name) {
        this.organization_id = organization_id;
        this.organization_name = organization_name;
    }
}
