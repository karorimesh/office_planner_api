package com.tracom.office_planner.User;

/*
Login for user management and notifications
 */

import com.azure.cosmos.implementation.guava25.collect.FluentIterable;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.Notifcations.SendNotification;
import com.tracom.office_planner.Notifcations.SmsConfiguration;
import com.tracom.office_planner.Organization.Organization;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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
        mailSender.send(registerMessage);
    }

//    Email notification for user to enable them to reset their password
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

/*
     Forgot email
 */
    public void sendResetMail(HttpServletRequest request, Model model){
        String email = request.getParameter("email");
        String token = RandomString.make(10);
        User user = userRepository.findByEmail(email);
        if(user != null){
            updateToken(token,email);
            String resetLink = Utility.getSiteUrl(request) +"/reset?token="+token;
            sendForgotMail(user,resetLink);
            PlannerLogger.resetPasswordRequest(user);
            System.out.println(resetLink);
            model.addAttribute("message", "Check email for reset link");
        }
        else{
            model.addAttribute("error", "Email does not exist");
        }
    }

    /*
     Reset Password
     */
    public  void resetPassword( HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String encodedPassword = new BCryptPasswordEncoder().encode(password);
        try {
            User user =getUserByToken(token);
            List<UserPassword> userPasswords = user.getUserPasswords();
            List<String> passwords = new ArrayList<>();
            Collections.reverse(userPasswords);
            userPasswords.subList(1,4);
            userPasswords.forEach(up -> {
                passwords.add(up.getUserPassword());
            });
            if (passwords.contains(encodedPassword)){
                model.addAttribute("error", "Use a password you've not used before");
            }else {
                updatePassword(user,password);
                PlannerLogger.resetSuccess(user);
                model.addAttribute("message", "Password changed successfully Login" );
            }
            throw new UsernameNotFoundException("User does not exist");
        } catch(UsernameNotFoundException e){
            model.addAttribute("error",e.getMessage());
        }
    }
    /*
    Registration
     */
    public void registration(HttpServletRequest request, Model model){
        String token = request.getParameter("token");
        User user = getUserByToken(token);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        try{
            updateUser(user,username,password,phone);
            PlannerLogger.firstUserUpdate(user);
            model.addAttribute("message", "Successfully registered click Login to enter");
        }catch (DataIntegrityViolationException e){
            model.addAttribute("error","This user name is taken");
        }
    }

    /*
    A paginated users list
     */
    public void viewUsersList(HttpServletRequest request, String keyword, int page, String dir, String field, Model model){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User currentUser = userRepository.findUserByName(name);
        Page<User> content =listAll(keyword,page,dir,field, currentUser.getOrganization());
        List<User> listUser = content.getContent();
        List<User> listUsers = FluentIterable.from(listUser)
                .filter(u -> u != currentUser)
                .toList();
        model.addAttribute("userList", listUsers);
        model.addAttribute("keyword",keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", content.getTotalPages());
        model.addAttribute("totalUsers",content.getTotalElements());
        model.addAttribute("sortDir", dir);
        model.addAttribute("sortField",field);
        model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
    }

    /*
Admin function to delete a user
 */
    public void deleteUser(HttpServletRequest request, int id){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        User deletedUser = userRepository.getById(id);
        PlannerLogger.deleteUser(deletedUser,user);
        userRepository.deleteById(id);
    }

/*
    Create a new user function
*/
    public void saveNewUser(HttpServletRequest request, User us, Model model){
        String token = RandomString.make(10);
        String resetLink = Utility.getSiteUrl(request)+"/register?token="+token;
        System.out.println(resetLink);
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        try {
            us.setToken(token);
            us.setOrganization(user.getOrganization());
            PlannerLogger.createUser(us);
            sendRegisterMail(us,resetLink);
            throw new MessagingException("Could not send email please check to ensure it's valid");
        }
        catch ( MessagingException e){
            model.addAttribute("error","Could not send email please check to ensure it's valid");
            userRepository.delete(us);
        }
        catch (DataIntegrityViolationException e){
            model.addAttribute("error","Email already exists");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(userRepository.findById(user.getUserId()).isPresent()){
            model.addAttribute("message", "Email sent successfully");
        }
    }

/*
User to edit their profile
 */
    public void saveProfile( User user){
        user.setOrganization(userRepository.findById(user.getUserId()).get().getOrganization());
        user.setUserPassword(userRepository.findById(user.getUserId()).get().getUserPassword());
        userRepository.save(user);
        PlannerLogger.updateUser(user);
    }

/*
Admin function to edit a users details
 */
    public void saveEdited(User user){
        user.setOrganization(userRepository.findById(user.getUserId()).get().getOrganization());
        user.setUserPassword(userRepository.findById(user.getUserId()).get().getUserPassword());
        userRepository.save(user);
        PlannerLogger.updateUser(user);
    }

/*
 Admin function to edit a user
 */
    public ModelAndView userProfile( int userId){
        ModelAndView modelAndView = new ModelAndView("editUser");
        User user = userRepository.getById(userId);
        modelAndView.addObject("profile",user);
        return modelAndView;
    }

/*
Current logged in user to view their profile
 */
    public ModelAndView currentUser(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("editProfile");
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        modelAndView.addObject("profile", user);
        return modelAndView;
    }
}
