package com.tracom.office_planner.RepeatMeetings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface RepeatMeetingsRepo extends JpaRepository<RepeatMeetings, Integer> {
@Query("FROM RepeatMeetings r WHERE r.meet_date = ?1")
    RepeatMeetings findByDate(String meet_date);

}
