package com.tracom.office_planner.Notifcations;

import com.tracom.office_planner.Meeting.Meeting;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
//Secondary Table of recipients

@Entity
@Table(name = "notifications")
//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Notifications {
    @Id
    private int notification_id;
    private LocalDateTime notification_Date;
    private LocalDateTime notification_Time;
    @ManyToOne
    private Meeting meeting;
    private String phone;

    public Notifications(LocalDateTime notification_Date,
                         LocalDateTime notification_Time,
                         Meeting meeting, String phone) {
        this.notification_Date = notification_Date;
        this.notification_Time = notification_Time;
        this.meeting = meeting;
        this.phone = phone;
    }
}
