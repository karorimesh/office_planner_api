package com.tracom.office_planner.CoOwners;

import com.tracom.office_planner.Meeting.Meeting;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "coOwner")
//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CoOwners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int coOwnerId;
    private String coOwner;

    public CoOwners(String coOwner) {
        this.coOwner = coOwner;
    }

}
