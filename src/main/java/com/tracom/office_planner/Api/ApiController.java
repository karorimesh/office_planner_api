package com.tracom.office_planner.Api;

/*
Collection of all my controllers to create a Restful API
Brace yourself this is really long file
 */


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.azure.cosmos.implementation.HttpConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracom.office_planner.Boardroom.BoardRepository;
import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.Boardroom.BoardServiceClass;
import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Meeting.MeetingRepository;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.Organization.OrganizationRepo;
import com.tracom.office_planner.ProjectServiceClass;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.*;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
public class ApiController {
    private  final OrganizationRepo organizationRepo;
    private  final UserRepository userRepo;
    private final BoardRepository boardRepository;
    private final BoardServiceClass boardServiceClass;
    private final MeetingRepository meetRepo;
    private final RepeatMeetingsRepo meetingsRepo;
    private final ProjectServiceClass projectServiceClass;
    private final UserServiceClass userServiceClass;
    private final ApiServiceClass apiServiceClass;


    @Autowired
    public ApiController(ApiServiceClass apiServiceClass, UserServiceClass userServiceClass, OrganizationRepo organizationRepo, UserRepository userRepo, BoardRepository boardRepository, BoardServiceClass boardServiceClass, MeetingRepository meetRepo, RepeatMeetingsRepo meetingsRepo, ProjectServiceClass projectServiceClass) {
        this.userServiceClass = userServiceClass;
        this.organizationRepo = organizationRepo;
        this.userRepo = userRepo;
        this.boardRepository = boardRepository;
        this.boardServiceClass = boardServiceClass;
        this.meetRepo = meetRepo;
        this.meetingsRepo = meetingsRepo;
        this.projectServiceClass = projectServiceClass;
        this.apiServiceClass = apiServiceClass;
    }



    //    ORGANIZATION CONTROLLER
//    Get all organizations in the Database

//    Save a new organization
    @PostMapping("/org/save")
    ResponseEntity<String> saveOrganization( HttpServletRequest request,@RequestBody User user){

        try {
            apiServiceClass.saveOrganization(request,user,user.getOrganization());
            return ResponseEntity.status(HttpStatus.CREATED).body("Organization has been added admin has received registration email");
        }
        catch ( MessagingException e){
            userRepo.delete(user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not send mail, please ensure it's valid");
        }
        catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists!!");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//    END OF ORGANIZATION CONTROLLER

//    AUTHENTICATION CONTROLLER- SPRING SECURITY "/login" IS THE DEFAULT LOGIN URL
//    Get a refresh token if the current JWT access token has expired this takes a month to expire
    @GetMapping("/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username =  decodedJWT.getSubject();
                User user = userRepo.findUserByName(username);
                String[] roles = decodedJWT.getClaim("role").asArray(String.class);
                String accessToken = JWT.create()
                        .withSubject(user.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("role", Stream.of(user.getUserRole().split(",")).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            }catch (Exception e){
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpConstants.StatusCodes.FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error", e.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
        }else {
            throw new RuntimeException("Unable to find refresh token");
        }

    }

//    END OF AUTHENTICATION CONTROLLER

//    BOARDROOM CONTROLLER

    @GetMapping("/boardrooms")
    ResponseEntity<List<BoardRoom>> boardrooms(HttpServletRequest request){
        User user = projectServiceClass.findUser(request);
        return ResponseEntity.ok().body(boardRepository.findBoards(user.getOrganization()));
    }

    @GetMapping("/boardroom/{id}")
    ResponseEntity<BoardRoom> boardroom(HttpServletRequest request, @PathVariable Integer id){
        User user = projectServiceClass.findUser(request);
        try {
            BoardRoom boardroom = boardRepository.findBoard(id, user.getOrganization());
            return ResponseEntity.status(HttpStatus.FOUND).body(boardroom);
        }catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BoardRoom());
        }
    }

    @PostMapping("/boardroom/save")
    ResponseEntity<BoardRoom> createRoom(@RequestBody BoardRoom boardRoom, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        boardRoom.setOrganization(user.getOrganization());
        return ResponseEntity.status(HttpStatus.CREATED).body(boardRepository.save(boardRoom));
    }

    @PutMapping("/boardroom/edit/{id}")
    ResponseEntity<String> editRoom(@PathVariable(name = "id") Integer id,HttpServletRequest request,@RequestBody BoardRoom boardRoom){
        User user = projectServiceClass.findUser(request);
        if(boardRepository.findById(id).isPresent() && user.getOrganization() == boardRepository.getById(id).getOrganization()){
            boardRepository.updateRoom(boardRoom.getBoardName(),
                    boardRoom.getCapacity(), boardRoom.isTV(), boardRoom.isPhone(),
                    boardRoom.isWhiteboard(), boardRoom.getBoardLocation(), boardRoom.getOthers(), id);
            PlannerLogger.editBoardroom(boardRoom,user);
            return ResponseEntity.ok().body("Boardroom edited successfully");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such boardroom in your organization");
        }

    }

    @DeleteMapping("/boardroom/delete/{id}")
    ResponseEntity<String> deleteUser(@PathVariable(name = "id") Integer id, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        if(boardRepository.findById(id).isPresent() && user.getOrganization() == boardRepository.findById(id).get().getOrganization()){
            BoardRoom boardRoom = boardRepository.getById(id);
            PlannerLogger.deleteBoardroom(boardRoom,user);
            boardRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted boardroom with id " + id);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such boardroom in your organization");
        }

    }





//    END OF BOARDROOM CONTROLLER

//    USER MANAGEMENT CONTROLLER

    @GetMapping("/user/{id}")
    ResponseEntity<User> findUser(HttpServletRequest request, @PathVariable Integer id){
        User user = projectServiceClass.findUser(request);
        try {
            User user1 = userRepo.findUserById(id, user.getOrganization());
            return ResponseEntity.status(HttpStatus.FOUND).body(user1);
        }catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new User());
        }
    }

    @GetMapping("/users")
    ResponseEntity<List<User>> findUsers(HttpServletRequest request){
        User user = projectServiceClass.findUser(request);
        return ResponseEntity.ok().body(userRepo.findUsers(user.getOrganization()));
    }

    @PostMapping("/user/save")
    ResponseEntity<String> adminSaveUser(HttpServletRequest request, @RequestBody User newUser){
        User currentUser = projectServiceClass.findUser(request);
        String token = RandomString.make(10);
        String registerLink = Utility.getSiteUrl(request)+"/register?token="+token;
        System.out.println(registerLink);
        try {
            newUser.setToken(token);
            newUser.setOrganization(currentUser.getOrganization());
            PlannerLogger.createUser(newUser);
            userRepo.save(newUser);
            userServiceClass.sendRegisterMail(newUser,registerLink);
            return ResponseEntity.status(HttpStatus.CREATED).body("User has been saved successfully");
        }
        catch ( MessagingException e){
            userRepo.delete(newUser);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not send mail, please ensure it's valid");
        }
        catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists!!");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
/** Still a work in progress
    @PutMapping("/user/edit/{id}")
    ResponseEntity<String> editUser(@PathVariable(name = "id") Integer id,HttpServletRequest request,@RequestBody User editedUser){
        User user = projectServiceClass.findUser(request);
        if(userRepo.findById(id).isPresent() && user.getOrganization() == userRepo.getById(id).getOrganization()){
            userRepo.save(editedUser);
            PlannerLogger.updateUser(editedUser);
            return ResponseEntity.ok().body("User edited successfully");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such User in your organization");
        }
    }
 */

    @DeleteMapping("/user/delete/{id}")
    ResponseEntity<String> deleteRoom(@PathVariable(name = "id") Integer id, HttpServletRequest request){
        User user = projectServiceClass.findUser(request);
        if(userRepo.findById(id).isPresent() && user.getOrganization() == userRepo.findById(id).get().getOrganization()){
            User deleteUser = userRepo.getById(id);
            PlannerLogger.deleteUser(deleteUser,user);
            userRepo.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Deleted User with id " + id);
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such User in your organization");
        }

    }

    @PostMapping("user/forgot")
    public ResponseEntity<String> sendResetEmail(HttpServletRequest request, @RequestBody User forgotUser){
        String email = forgotUser.getUserEmail();
        String token = RandomString.make(10);
        // TODO: 10/27/2021 Add try and catch method here to handle error
        User user = userRepo.findByEmail(email);
        if(user != null){
            userServiceClass.updateToken(token,email);
            String resetLink = Utility.getSiteUrl(request) +"/reset?token="+token;
            userServiceClass.sendForgotMail(user,resetLink);
            PlannerLogger.resetPasswordRequest(user);
            System.out.println(resetLink);
            return ResponseEntity.ok("Email sent successfully");
        }
        else{
            return ResponseEntity.ok("Email does not exist in the System");
        }
    }


    //    Resetting the users password
    @PostMapping("user/reset")
    public ResponseEntity<String> resetPassword(HttpServletRequest request, @RequestBody User user){
        String encodedPassword = new BCryptPasswordEncoder().encode(user.getUserPassword());
        try {
            User forgotUser = userServiceClass.getUserByToken(user.getToken());
//            List<UserPassword> userPasswords = user.getUserPasswords();
//            List<String> passwords = new ArrayList<>();
//            Collections.reverse(userPasswords);
//            userPasswords.subList(1,4);
//            userPasswords.forEach(up -> {
//                passwords.add(up.getUserPassword());
//            });
            if (userRepo.findUserByName(forgotUser.getUserName()) == null){
                return ResponseEntity.badRequest().body("Use a password you have never used before");
            }else {
                userServiceClass.updatePassword(forgotUser, user.getUserPassword());
                PlannerLogger.resetSuccess(user);
                return ResponseEntity.badRequest().body("User password changed successfully you can login now");
            }
        } catch(UsernameNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        // TODO: 10/27/2021 Add try catch for invalid token
    }


    //    Saving a new registered user details
    @PostMapping("user/register")
     ResponseEntity<String> registration(HttpServletRequest request, @RequestBody User user){
        User newUser = userServiceClass.getUserByToken(user.getToken());
        try{
            userServiceClass.updateUser(newUser, user.getUserName(), user.getUserPassword(), user.getPhone());
            PlannerLogger.firstUserUpdate(user);
            return ResponseEntity.ok("Registration successful");
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.ok("Registration username is taken");
        }
    }


//    END OF USER MANAGEMENT

//    MEETING MANAGEMENT

    @GetMapping("/meetings")
    ResponseEntity<List<Meeting>> meetings(HttpServletRequest request){
        User user = projectServiceClass.findUser(request);
        return ResponseEntity.ok().body(meetRepo.findMeets(user.getOrganization()));
    }

    @GetMapping("/meeting/{id}")
    ResponseEntity<Meeting> meeting(HttpServletRequest request, @PathVariable Integer id){
        User user = projectServiceClass.findUser(request);
        try {
            Meeting meeting = meetRepo.findOrgMeet(id,user.getOrganization());
            return ResponseEntity.status(HttpStatus.FOUND).body(meeting);
        }catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Meeting());
        }
    }

    @PostMapping("/meeting/save")
    ResponseEntity<String> createMeet( @RequestBody Meeting meeting, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        List<RepeatMeetings> meetings = meeting.getRepeatMeetings();
        meeting.setOrganization(user.getOrganization());
        meeting.getUsers().add(user);

        for (RepeatMeetings r : meetings){
            if(meetingsRepo.findConflictingMeet(boardRepository.getById(meeting.getBoardroom().getBoardId()), r.getMeetDate(),meeting.getMeetStart()) != null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This room won't be available at this time !!");
            }
        }
        meetRepo.save(meeting);
        PlannerLogger.createMeeting(meeting,user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Meeting created successfully");
    }

    /** Continue working on this still not working, use post mapping for now
    @PutMapping("/meeting/update/{id}")
    ResponseEntity<String> updateMeet( @RequestBody Meeting meeting, HttpServletRequest request, @PathVariable("id") int id){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        List<RepeatMeetings> meetings = meeting.getRepeatMeetings();
        BoardRoom boardRoom = boardRepository.getById(meeting.getBoardroom().getBoardId());
        meeting.setBoardroom(boardRoom);


        for (RepeatMeetings r : meetings){
            RepeatMeetings meeting1 = meetingsRepo.findConflictingMeet(boardRepository.getById(meeting.getBoardroom().getBoardId()), r.getMeetDate(),meeting.getMeetStart());
            if( meeting1 != null && meeting1.getMeeting().getMeetId() != meeting.getMeetId()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This room won't be available at this time !!");
            }
        }
        meetRepo.save(meeting);
        PlannerLogger.updateMeeting(meeting,user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Meeting created successfully");
    }
     */

    @DeleteMapping("/meeting/delete/{id}")
    ResponseEntity<String> deleteMeet(@PathVariable Integer id, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        Meeting meeting = meetRepo.findMeet(id);
//        PlannerLogger.deleteMeeting(meeting, user);
        try{
            if (meeting.getUsers().contains(user)){
                meetRepo.deleteById(id);
            }
            if(meetRepo.findById(id).isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Meeting not deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Meeting deleted successfully");
        }catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Meeting does not exist");
        }

    }

//    END OF MEETING MANAGEMENT

}
