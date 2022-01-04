package com.tracom.office_planner.RepeatMeetings;

/*
Crud handling o the meetings in the organization
 */

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RepeatMeetingsRepo extends JpaRepository<RepeatMeetings, Integer> {
//    Select all meetings in a specified date
    @Query("SELECT r FROM RepeatMeetings r WHERE r.meetDate = ?1")
    List<RepeatMeetings> findByDate(LocalDate date);
//    select all meetings based on a search word
    @Query("SELECT r FROM RepeatMeetings r WHERE CONCAT(r.meetDate, ' ' ,r.meeting.meetName,' ',r.meeting.meetStart,' ',r.meeting.boardroom.boardName) LIKE %?1% AND r.meeting.organization=?2")
    Page<RepeatMeetings> search(String keyword, Organization organization, Pageable pageable);
//    return all meetings of a specified organization
    @Query("SELECT r FROM RepeatMeetings r WHERE r.meeting.organization=?1")
    Page<RepeatMeetings> searchAll(Pageable pageable, Organization organization);
//    Find a conflicting meeting
    @Query("FROM RepeatMeetings r WHERE r.meeting.boardroom = ?1 AND r.meetDate = ?2 AND r.meeting.meetStart <= ?3 AND r.meeting.meetEnd > ?3")
    RepeatMeetings findConflictingMeet(BoardRoom boardRoom, LocalDate date, LocalTime startTime);

    /*
    Api Queries
     */
    @Query("SELECT r FROM RepeatMeetings r WHERE r.meeting.organization = ?1")
    List<RepeatMeetings> findMeets(Organization organization);
}
