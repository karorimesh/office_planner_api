package com.tracom.office_planner.Meeting;


import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MeetingServiceClass {

    private RepeatMeetingsRepo repeatMeetingsRepo;

    @Autowired
    public MeetingServiceClass(RepeatMeetingsRepo repeatMeetingsRepo) {
        this.repeatMeetingsRepo = repeatMeetingsRepo;
    }

    public Page<RepeatMeetings> listAll(String keyword, int pageNo, String sortDir, String field, Organization organization){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,
                sortDir.equals("asc")? Sort.by(field).ascending():Sort.by(field).descending());
        if (keyword != null){
            return repeatMeetingsRepo.search(keyword, organization, pageable);
        }
        return repeatMeetingsRepo.searchAll(pageable,organization);
    }

}
