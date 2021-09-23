package com.tracom.office_planner.Notifcations;

import com.tracom.office_planner.Meeting.Meeting;

import java.time.LocalDateTime;
//Secondary Table of recipients
public class Notifications {
    private int notification_id;
    private LocalDateTime notification_Date;
    private LocalDateTime notification_Time;
    private Meeting meeting;
}
