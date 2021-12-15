package com.tracom.office_planner.RepeatMeetings;

import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RepeatMeetingsRepo extends JpaRepository<RepeatMeetings, Integer> {
    @Query("SELECT r FROM RepeatMeetings r WHERE r.meetDate = ?1")
    List<RepeatMeetings> findByDate(LocalDate date);
    @Query("SELECT r FROM RepeatMeetings r WHERE CONCAT(r.meetDate, ' ' ,r.meeting.meetName,' ',r.meeting.meetStart,' ',r.meeting.boardroom.boardName) LIKE %?1% AND r.meeting.organization=?2")
    Page<RepeatMeetings> search(String keyword, Organization organization, Pageable pageable);
    @Query("SELECT r FROM RepeatMeetings r WHERE r.meeting.organization=?1")
    Page<RepeatMeetings> searchAll(Pageable pageable, Organization organization);
}
