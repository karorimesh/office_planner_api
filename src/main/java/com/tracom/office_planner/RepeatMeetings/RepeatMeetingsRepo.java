package com.tracom.office_planner.RepeatMeetings;

import com.tracom.office_planner.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface RepeatMeetingsRepo extends JpaRepository<RepeatMeetings, Integer> {
@Query("FROM RepeatMeetings r WHERE r.meetDate = ?1")
    RepeatMeetings findByDate(String meetDate);
    @Query("SELECT r FROM RepeatMeetings r WHERE CONCAT(r.meetDate, ' ' ,r.meeting.meetName,' ',r.meeting.meetStart,' ',r.meeting.boardroom.boardName) LIKE %?1%")
    public Page<RepeatMeetings> search(String keyword, Pageable pageable);
}
