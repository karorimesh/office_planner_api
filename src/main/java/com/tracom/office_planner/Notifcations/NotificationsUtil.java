package com.tracom.office_planner.Notifcations;

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import com.tracom.office_planner.User.UserServiceClass;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
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

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    private List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
    private User user;
    private Meeting meet;



    TimerTask notifications = new TimerTask() {

        @SneakyThrows
        @Override
        public void run() {
            serviceClass.sendMeetingMail(user,meet);
            serviceClass.sendMeetingSms(user,meet);
            PlannerLogger.meetingNotification(meet,user);
        }
    };

    public void notificationTimer(RepeatMeetings meeting, User user) throws ExecutionException, InterruptedException {
        LocalDateTime time = LocalDateTime.of(meeting.getMeetDate(),
                meeting.getMeeting().getMeetStart()).minusMinutes(15);
        long sendTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        System.out.println("Scheduling....");
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.
                schedule(new Notification(user,meeting.getMeeting()),sendTime, TimeUnit.MILLISECONDS);
        scheduledFutures.add(scheduledFuture);
    }

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
                        user = u;
                        meet = r.getMeeting();
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
