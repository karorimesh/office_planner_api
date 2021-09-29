package com.tracom.office_planner.Meeting;

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.CoOwners.CoOwners;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.User.User;
import lombok.*;

import javax.persistence.*;
import java.util.List;
/*import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;*/


@Entity
@Table(name = "meeting")
//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int meet_id;
    private String meet_name;
    private String description;
    private int capacity;
    @ManyToOne
    private User user;
    @ManyToOne
    private BoardRoom boardroom;
    @OneToMany
    private List<CoOwners> coOwners;
    @OneToMany
    private List<RepeatMeetings> repeatMeetings;

    //Constructor without Id


    public Meeting(String meet_name,
                   String description, int capacity,
                   User user, BoardRoom boardroom,
                   List<CoOwners> coOwners,
                   List<RepeatMeetings> repeatMeetings) {
        this.meet_name = meet_name;
        this.description = description;
        this.capacity = capacity;
        this.user = user;
        this.boardroom = boardroom;
        this.coOwners = coOwners;
        this.repeatMeetings = repeatMeetings;
    }
}
