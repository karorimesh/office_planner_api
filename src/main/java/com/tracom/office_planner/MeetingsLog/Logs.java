package com.tracom.office_planner.MeetingsLog;

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Notifcations.Notifications;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.User.User;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "logs")
//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Logs {
    @Id
    private int log_id;
    @ManyToOne
    private Organization organization;
    @ManyToOne
    private User user;
    @OneToOne
    private Meeting meeting;
    @ManyToOne
    private BoardRoom boardroom;
    @OneToMany
    private Set<Notifications> notifications;

    public Logs(Organization organization,
                User user, Meeting meeting,
                BoardRoom boardroom,
                Set<Notifications> notifications) {
        this.organization = organization;
        this.user = user;
        this.meeting = meeting;
        this.boardroom = boardroom;
        this.notifications = notifications;
    }
}
