package com.tracom.office_planner.Boardroom;

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Organization.Organization;
import lombok.*;

import javax.persistence.*;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int board_id;
    private String board_name;
    @ManyToOne
    private Organization organization;
    @OneToMany
    private List<Meeting> meetings;

    public BoardRoom(int board_id, String board_name) {
        this.board_id = board_id;
        this.board_name = board_name;
    }

    public BoardRoom(String board_name) {
        this.board_name = board_name;
    }
}
