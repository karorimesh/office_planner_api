package com.tracom.office_planner.MeetingsLog;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
