package com.tracom.office_planner.Meeting;

/*Entity to enable scheduling of a meeting */

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.User.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


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
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property  = "userId")
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_meetings",
            joinColumns = @JoinColumn(name = "meet_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<User>();
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "room")
    private BoardRoom boardroom;
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "repeatId")
    @OneToMany(cascade = { CascadeType.PERSIST},orphanRemoval = true,  fetch = FetchType.LAZY, mappedBy = "meeting")
    private List<RepeatMeetings> repeatMeetings ;
    private LocalTime meetStart;
    private LocalTime meetEnd;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "organization")
    private Organization organization;

    public void setRepeatMeetings(List<RepeatMeetings> repeatMeetings) {
        this.repeatMeetings = repeatMeetings;
        repeatMeetings.forEach(r->r.setMeeting(this));
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetId=" + meetId +
                ", meetName='" + meetName + '\'' +
                ", description='" + description + '\'' +
                ", capacity=" + capacity +
                ", users=" + users +
                ", boardroom=" + boardroom +
                ", repeatMeetings=" + repeatMeetings +
                ", meetStart=" + meetStart +
                ", meetEnd=" + meetEnd +
                '}';
    }
}
