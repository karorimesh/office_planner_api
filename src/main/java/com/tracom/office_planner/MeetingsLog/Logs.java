package com.tracom.office_planner.MeetingsLog;

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Notifcations.Notifications;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.User.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int log_id;
    private LocalDateTime logDate;
    private String logger;
    private String level;
    private String message;

}
