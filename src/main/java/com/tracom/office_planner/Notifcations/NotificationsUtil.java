package com.tracom.office_planner.Notifcations;

/* This is a notification utility class to allow scheduling
 of meeting notifications to be sent to users
 15 minutes before the meeting time- a really nice piece of work
 */

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserServiceClass;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;

@EnableScheduling
@Component
@NoArgsConstructor
@Transactional
@Service
public class NotificationsUtil {

    @Autowired
    private RepeatMeetingsRepo meetingsRepo;
    @Autowired
    private UserServiceClass serviceClass;
    /*Create a pool of 100 threads to be able to run simultaneously,
     possibility of being higher depending on the expected traffic
     */
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);
    /*
    Store a list of the tasks already scheduled task to allow actions such as cancelling
     */
    private final List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();


    /*
    This method configures a timer to enable the scheduling 15 minutes before time
     */
    public void notificationTimer(RepeatMeetings meeting, User user) throws ExecutionException, InterruptedException {
        LocalDateTime time = LocalDateTime.of(meeting.getMeetDate(),
                meeting.getMeeting().getMeetStart()).minusMinutes(15);
        long sendTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println("Scheduling....");
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.
                schedule(new Notification(user,meeting.getMeeting()),sendTime, TimeUnit.MILLISECONDS);
        scheduledFutures.add(scheduledFuture);
    }

    /*
    This method retrieves all the meetings in the database at an interval
    The meetings are of a specified date
    17 minutes to compensate for lag time since it is in long tye and the action cannot be instant
     */
    @Scheduled(fixedDelay = 17 * 60 * 1000)
        public void sendNotifications(){
        try {
            scheduledFutures.forEach(f -> f.cancel(true));
        }catch (Exception e){
            e.getMessage();
        }
        List<RepeatMeetings> repeatMeetings = meetingsRepo.findByDate(LocalDate.now());
        // TODO: 12/1/2021 Filter to load current Dates
        repeatMeetings.forEach(r ->{
            if (r.getMeeting().getMeetStart().isAfter(LocalTime.now().plusMinutes(16))) {
                r.getMeeting().getUsers().forEach(u -> {
                    try {
                        notificationTimer(r,u);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    /*
    An internal class that creates the task to be run
     */

    public class Notification extends TimerTask {
        User user;
        Meeting meet;

        public Notification(User user, Meeting meet) {
            this.user = user;
            this.meet = meet;
        }


        @SneakyThrows
        @Override
        public void run() {
            System.out.println("Scheduling the meeting");
            try {
                serviceClass.sendMeetingMail(user,meet);
                System.out.println("Sent the mail");
            }finally
            {
                System.out.println("Failed to send mail  ");
                try {
                    serviceClass.sendMeetingSms(user,meet);
                    System.out.println("Sent the sms");
                }finally {
                    System.out.println("Failed to send the SMS");
                    PlannerLogger.meetingNotification(meet,user);
                }
            }
        }
    }

}
