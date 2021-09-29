package com.tracom.office_planner.RepeatMeetings;

import com.tracom.office_planner.Meeting.Meeting;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

//Lombok
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


@Entity
@Table(name = "repeatMeetings")

public class RepeatMeetings {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int repeat_id;
        private String meeting_name;
        private LocalDate meet_date;
        private LocalTime meet_start;
        private LocalTime meet_end;
        @ManyToOne
        private Meeting meeting;

}
