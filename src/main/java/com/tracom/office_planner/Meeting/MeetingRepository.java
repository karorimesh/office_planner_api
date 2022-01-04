package com.tracom.office_planner.Meeting;
/*
Repository class to handle the meeting
 */

import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    /*
    Api Queries
     */
    @Query("SELECT m FROM Meeting m WHERE m.organization = ?1")
    List<Meeting> findMeets(Organization organization);
}
