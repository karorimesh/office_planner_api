package com.tracom.office_planner.RepeatMeetings;

import com.tracom.office_planner.Meeting.Meeting;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

//Lombok
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


@Entity
@Table(name = "repeatMeetings")

public class RepeatMeetings {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int repeatId;
        private LocalDate meetDate;
        @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
        @JoinColumn(name = "meet_id")
        private Meeting meeting;

        public RepeatMeetings(LocalDate meet_date, Meeting meeting) {
                this.meetDate = meet_date;
                this.meeting = meeting;
        }

        public RepeatMeetings(int repeat_id, LocalDate meet_date) {
            this.repeatId = repeat_id;
            this.meetDate = meet_date;
        }

        public RepeatMeetings(LocalDate meet_date) {
              this.meetDate = meet_date;
        }

        @Override
        public String toString() {
                return "RepeatMeetings{" +
                        "repeatId=" + repeatId +
                        ", meetDate=" + meetDate +
                        '}';
        }
}
