package com.tracom.office_planner.Meeting;

import com.tracom.office_planner.User.User;

import javax.persistence.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "meeting")

public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int meet_id;
    private String meet_name;
    private String description;
    @Temporal(TemporalType.DATE)
    private LocalDateTime meet_date;
    @Temporal(TemporalType.TIME)
    private LocalDateTime meet_start;
    @Temporal(TemporalType.TIME)
    private  LocalDateTime meet_end;
    private int capacity;
    @OneToOne
    private User user;

}
