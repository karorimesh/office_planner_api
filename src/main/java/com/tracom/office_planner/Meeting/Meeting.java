package com.tracom.office_planner.Meeting;

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.CoOwners.CoOwners;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.User.User;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
/*import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;*/


@Entity
@Table(name = "meeting")
//Lombok
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int meetId;
    private String meetName;
    private String description;
    private int capacity;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_meetings",
            joinColumns = @JoinColumn(name = "meet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<User>();
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "room")
    private BoardRoom boardroom;
    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<CoOwners> coOwners;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "meeting")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private List<RepeatMeetings> repeatMeetings = new ArrayList<>();
//    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime meetStart;
//    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime meetEnd;
    @ManyToOne
    @JoinColumn(name = "organization")
    private Organization organization;


    //Constructor without Id


    @Override
    public String toString() {
        return "Meeting{" +
                "meetId=" + meetId +
                ", meetName='" + meetName + '\'' +
                ", description='" + description + '\'' +
                ", capacity=" + capacity +
                ", users=" + users +
                ", boardroom=" + boardroom +
                ", coOwners=" + coOwners +
                ", repeatMeetings=" + repeatMeetings +
                ", meetStart=" + meetStart +
                ", meetEnd=" + meetEnd +
                '}';
    }
}
