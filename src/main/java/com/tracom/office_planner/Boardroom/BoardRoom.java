package com.tracom.office_planner.Boardroom;

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Organization.Organization;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "boardRoom")
//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class BoardRoom {
    @Id
    private int board_id;
    private String board_name;
    @ManyToOne
    private Organization organization;
    @OneToMany
    private Set<Meeting> meetings;



    public BoardRoom(String board_name, Organization organization, Set<Meeting> meetings) {
        this.board_name = board_name;
        this.organization = organization;
        this.meetings = meetings;
    }


}
