package com.tracom.office_planner.User;

/*
Login for user management and notifications
 */

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.Notifcations.SendNotification;
import com.tracom.office_planner.Notifcations.SmsConfiguration;
import com.tracom.office_planner.Organization.Organization;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Transactional
@EnableScheduling
@Service
public class UserServiceClass implements SendNotification {
    public static final int MAX_ATTEMPTS = 4;

//    Time account is locked after failed attempts
    private static final long LOCK_TIME = 30 * 60 * 1000;

    private final JavaMailSender mailSender;

    private final SmsConfiguration smsConfiguration;

    private final UserRepository userRepository;

    private final UserPasswordRepository passwordRepository;

    @Autowired
    public UserServiceClass(JavaMailSender mailSender, SmsConfiguration smsConfiguration, UserRepository userRepository, UserPasswordRepository passwordRepository) {
        this.mailSender = mailSender;
        this.smsConfiguration = smsConfiguration;
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
    }

//    Send notification to user with a link to enable them to set up their new account
    @Override
    public void sendRegisterMail(User user, String reset) throws MessagingException, UnsupportedEncodingException {
        MimeMessage registerMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(registerMessage);

        helper.setFrom("eworkske@gmail.com","Planner Support");
        helper.setTo(user.getUserEmail());
        String subject = "Resister - Register and set Details";
        String content = "<h3 style=\"color: blue\"> "+ "Register to planner" + "</h3>"
                +"<p> You have received the link below to register and set password</p>"
                +"<p> Click below link to register</p>"
                +"<p><a href=\"" + reset + "\">Register</a></p>"
                +"<p>Regards</p>";
        helper.setSubject(subject);
        helper.setText(content,true);
        userRepository.save(user);
        mailSender.send(registerMessage);
    }

//    Email notification for user to enable them to reser their password
    @Override
    public void sendForgotMail(User user, String reset) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Forgot Password reset E-mail");
        message.setTo(user.getUserEmail());
        message.setText("This is a link to reset your password. Click to change the password " + reset);
        mailSender.send(message);
    }

//    Send a meeting notification to user about a meeting about to start
    @Override
    public void sendMeetingMail(User user, Meeting meeting) throws MessagingException, UnsupportedEncodingException {
        MimeMessage meetMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(meetMessage);

        helper.setFrom("eworkske@gmail.com","Planner Support");
        helper.setTo(user.getUserEmail());
        String subject = "Meeting Planner - Meeting about to start";
        List<User> userList = meeting.getUsers();
        List<String> users = new ArrayList<>();
        userList.forEach(u -> { users.add(u.getUserName());});
        String content = "<h3 style=\"color: blue\"> "+ meeting.getMeetName()+"</h3>"
                        +"<p> You have a meeting on " + meeting.getDescription() +"</p>"
                        +"<p> With "+ users +"</p>"
                        +"<p>Happening in the next fifteen Minutes at "+meeting.getMeetStart()+"</p>"
                        +"<p>Regards</p>";
        helper.setSubject(subject);
        helper.setText(content,true);
        mailSender.send(meetMessage);

    }

//    Send a sms notification to a user on a meeting about to start
    @Override
    public void sendMeetingSms(User user, Meeting meeting) {
        List<String> users = new ArrayList<>();
        meeting.getUsers().forEach(u -> users.add(u.getUserName()));
        PhoneNumber to = new PhoneNumber(user.getPhone());
        PhoneNumber from = new PhoneNumber(smsConfiguration.getPhoneNumber());
        String message = "Hello you have a meeting on " + meeting.getMeetName() +
                " at " + meeting.getBoardroom().getBoardName()+
                " with " + users +
                " in fifteen minutes";
        MessageCreator creator = Message.creator(to,from,message);
        creator.create();
    }

//    User requesting a reset link or a register link
    public void updateToken(String token, String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email);
        if(user != null){
            user.setToken(token);
            userRepository.save(user);
        }
        else{
            throw new UsernameNotFoundException("User with this email does not exist");
        }
    }

//    Retrieving a user based on their token
    public User getUserByToken(String token){
        return userRepository.findByToken(token);
    }

//    Update a users password after reset
    public void updatePassword(User user, String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(password);
        user.setUserPassword(encoded);
        user.setToken(null);
        userRepository.save(user);
    }

//    Function to update a user
    public void updateUser(User user, String username, String password, String phone) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(password);
        user.setUserPassword(encoded);
        user.setUserName(username);
        user.setPhone(phone);
        user.setToken(null);
        userRepository.save(user);
        userRepository.findById(user.getUserId());
        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);
        userPassword.setUserPassword(user.getUserPassword());
        passwordRepository.save(userPassword);
    }

//    Find all users bases on a search keyword

    public Page<User> listAll(String keyword, int pageNo, String sortDir, String field, Organization organization){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,
                sortDir.equals("asc")? Sort.by(field).ascending():Sort.by(field).descending());
        if (keyword != null){
            return userRepository.search(keyword, organization, pageable );
        }
        return userRepository.searchAll(organization,pageable);
    }


//    Failed Attempts Service
    public void increaseAttempts(User user){
        int currentAttempts = user.getFailedAttempt() + 1;
        userRepository.updateFailedAttempt(currentAttempts, user.getUserEmail());
        PlannerLogger.loginFailure(user);
    }

//    change the number of failed attempts after a user successfully logs in

    public void resetAttempts(String email){
        userRepository.updateFailedAttempt(0,email);
    }

//    Lock users account after maxing out their login attempts
    public void lockAccount(User user){
        user.setAccountUnlocked(false);
        user.setLockTime(LocalDateTime.now());
        userRepository.save(user);
        PlannerLogger.loginFailureLocked(user);
    }

//    Unlock account after lock time is elapsed

    public boolean unlockAccount(User user){
        long lockedTime = user.getLockTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long currentTime = System.currentTimeMillis();

        if(lockedTime + LOCK_TIME < currentTime){
            user.setAccountUnlocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepository.save(user);
            PlannerLogger.loginFailureUnLocked(user);
            return true;
        }
        return false;
    }
}
