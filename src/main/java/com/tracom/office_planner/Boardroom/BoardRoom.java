package com.tracom.office_planner.Boardroom;

/* Entity class for the boardroom */

import com.fasterxml.jackson.annotation.*;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Organization.Organization;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boardRoom")
//Lombok
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "boardId")
@JsonIncludeProperties({"boardId", "boardName", "boardLocation", "Capacity", "TV", "Whiteboard", "Phone", "Others"})
public class BoardRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardId;
    private String boardName;
    private String boardLocation;
    private int Capacity;
    private boolean TV;
    private boolean Whiteboard;
    private boolean Phone;
    private String Others;
    @ManyToOne
    private Organization organization;
//    @JsonBackReference
    @OneToMany(mappedBy = "boardroom", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Meeting> meetings = new ArrayList<>();

    public BoardRoom(int board_id, String board_name) {
        this.boardId = board_id;
        this.boardName = board_name;
    }

    public BoardRoom(String board_name) {
        this.boardName = board_name;
    }

    @Override
    public String toString() {
        return "BoardRoom{" +
                "boardId=" + boardId +
                ", boardName='" + boardName + '\'' +
                ", boardLocation='" + boardLocation + '\'' +
                ", Capacity=" + Capacity +
                ", TV=" + TV +
                ", Whiteboard=" + Whiteboard +
                ", Phone=" + Phone +
                ", Others='" + Others + '\'' +
                ", organization=" + organization +
                '}';
    }
}
