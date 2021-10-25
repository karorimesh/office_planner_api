package com.tracom.office_planner.RepeatMeetings;

import com.tracom.office_planner.Meeting.Meeting;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

//Lombok
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString


@Entity
@Table(name = "repeatMeetings")

public class RepeatMeetings {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int repeat_id;
        private LocalDate meet_date;
        @ManyToOne(cascade = CascadeType.ALL )
        private Meeting meeting;

        public RepeatMeetings(LocalDate meet_date, Meeting meeting) {
                this.meet_date = meet_date;
                this.meeting = meeting;
        }

        public RepeatMeetings(int repeat_id, LocalDate meet_date) {
            this.repeat_id = repeat_id;
            this.meet_date = meet_date;
        }

        public RepeatMeetings(LocalDate meet_date) {
              this.meet_date = meet_date;
        }
}
