package com.tracom.office_planner;

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Meeting.MeetingRepository;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class MeetRepoTest {

    @Autowired
    private MeetingRepository meetRepo;
    @Autowired
    private RepeatMeetingsRepo repo;

//    @Test
    public void testMeetCreate() {

//        List<LocalDate> date = new ArrayList<LocalDate>();
//        date.add(LocalDate.of(2020,11,11));
        Meeting meet = new Meeting();
        RepeatMeetings repeat = new RepeatMeetings(LocalDate.of(2020,12,12));
        List<RepeatMeetings> repeated = new ArrayList<>();
        repeated.add(repeat);
        meet.setMeetName("tracom test");
        meet.setMeetStart(LocalTime.of(15,15));
        meet.setMeetEnd(LocalTime.of(15,30));
        meet.setCapacity(4);
        meet.setRepeatMeetings(repeated);
//        repeat.setMeet_date(LocalDate.of(2021,12,12));



        Meeting saveMeet = meetRepo.save(meet);
//        RepeatMeetings saveRepeat = repo.save(repeat);

        Assertions.assertThat(saveMeet).isNotNull();
        Assertions.assertThat(saveMeet.getMeetId()).isGreaterThan(0);
//        Assertions.assertThat(saveRepeat).isNotNull();
//        Assertions.assertThat(saveRepeat.getRepeat_id()).isGreaterThan(0);
    }

    @Test
    public void deleteMeet(){
        if(meetRepo.existsById(30)){
            meetRepo.deleteById(30);
        }
        Assertions.assertThat(meetRepo.findById(30)).isNotPresent();
    }


}
