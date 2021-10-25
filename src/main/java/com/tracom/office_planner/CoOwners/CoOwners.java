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
@Getter
@Setter
public class CoOwners {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int coOwner_id;
    private String coOwner;
    private String coOwnerPhone;
    @ManyToOne
    private Meeting meeting;

    public CoOwners(String coOwner, String coOwnerPhone, Meeting meeting) {
        this.coOwner = coOwner;
        this.coOwnerPhone = coOwnerPhone;
        this.meeting = meeting;
    }

    public CoOwners(int coOwner_id, String coOwner) {
        this.coOwner_id = coOwner_id;
        this.coOwner = coOwner;
    }

    public CoOwners(String coOwner) {
        this.coOwner = coOwner;
    }
}
