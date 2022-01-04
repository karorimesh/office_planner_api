package com.tracom.office_planner.MeetingsLog;

/* Logger class to implement various activities happening in the application
it covers all activites a user does wile interacting with the application logging goes to a file
 */
// TODO: 12/23/2021  make logs accessible to the admin

import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.User.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlannerLogger {
    private static final Logger logger = LogManager.getLogger();

//    Testing my loggers
    public static void loggingTask(){
        logger.info("This is an info message");
        logger.debug("This is a debug message");
        logger.warn("This is a warn message");
        logger.error("This is an error message");
        logger.fatal("This is a fatal message");
    }

//    Organization management
    public static void createOrganization(Organization organization){
        logger.info("Organization created with id" +
                organization.getOrganization_id()+
        " and name "+organization.getOrganization_name());
    }

//    User Management
    public static void createUser(User user){
        logger.info("User added with id "+user.getUserId()
        +" token given is "+user.getToken());
    }
    public static void updateUser(User user){
        logger.info("User edited with id "+user.getUserId()
        +" name "+user.getUserName());
    }
    public static void deleteUser(User user, User admin){
        logger.warn("User deleted with id "+user.getUserId()
        +" name "+user.getUserName()+
                " by admin "+ admin.getUserName());
    }
    public static void firstUserUpdate(User user){
        logger.warn("User first time login with id "+user.getUserId()
        +" name "+user.getUserName());
    }

//    Event Management
    public static void createMeeting(Meeting meeting, User user){
        logger.info("Meeting created by "+ user.getUserName()
        +" with topic "+meeting.getMeetName()
        +" happening at "+meeting.getBoardroom().getBoardName());
    }
    public static void updateMeeting(Meeting meeting, User user){
        logger.info("Meeting edited by "+ user.getUserName()
        +" with topic "+meeting.getMeetName()
        +" happening at "+meeting.getBoardroom().getBoardName());
    }
    public static void deleteMeeting(Meeting meeting, User user){
        logger.warn("Meeting deleted by "+ user.getUserName()
        +" with topic "+meeting.getMeetName()
        +" happening at "+meeting.getBoardroom().getBoardName());
    }

//    Password Management
    public static void resetPasswordRequest(User user){
        logger.warn("User with Id "+user.getUserId()
        +" requested for password reset. Email sent with token "
        + user.getToken());
    }
    public static void resetSuccess(User user){
        logger.info("User with id "+user.getUserId()+" successfully updated password");
    }

//    Logged in User
    public static void loggedInUser(User user){
        logger.info("User " +user.getUserId()+
                " has logged in");
    }

    // TODO: 12/23/2021 Logger for when a user logs out
    public static void loggedOutUser(User user){
        logger.info("User " +user.getUserId()+
                " has logged out");
    }
    public static void loginFailure(User user){
        logger.info("User " +user.getUserId()+
                " has failed to logged in");
    }
    public static void loginFailureLocked(User user){
        logger.warn("User " +user.getUserId()+
                " has failed to log in and account is locked");
    }
    public static void loginFailureUnLocked(User user){
        logger.warn("User " +user.getUserId()+
                " account is now unlocked");
    }

//    Boardroom Management
    public static void createBoardroom(BoardRoom boardRoom, User user){
        logger.info("User with id "+user.getUserId()
        +" has created a boardroom with id "+boardRoom.getBoardId()+
                " and name "+boardRoom.getBoardName());
    }
    public static void editBoardroom(BoardRoom boardRoom, User user){
        logger.warn("User with id "+user.getUserId()
        +" has edited a boardroom with id "+boardRoom.getBoardId()+
                " and name "+boardRoom.getBoardName());
    }
    public static void deleteBoardroom(BoardRoom boardRoom, User user){
        logger.warn("User with id "+user.getUserId()
        +" has deleted a boardroom with id "+boardRoom.getBoardId()+
                " and name "+boardRoom.getBoardName());
    }

//    Notifications manager
    public static void meetingNotification(Meeting meeting, User user){
        logger.info("Sending meeting notification to User with id " +user.getUserId()+
                " and name " +user.getUserName()+
                " on meeting with topic " +meeting.getMeetName()+
                " happening at " +
                meeting.getBoardroom().getBoardName());
    }

//    Application started
    public static void appStart(){
        logger.warn("App has started");
    }
}

