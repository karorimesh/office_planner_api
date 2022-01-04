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
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private  final OrganizationRepo organizationRepo;
    private  final UserRepository userRepo;
    private final BoardRepository boardRepository;
    private final BoardServiceClass serviceClass;
    private final MeetingRepository meetRepo;
    private final RepeatMeetingsRepo meetingsRepo;

//    ORGANIZATION CONTROLLER
//    Get all organizations in the Database
    @GetMapping("/org")
    ResponseEntity<List<Organization>> getOrganizations(){
        return ResponseEntity.ok().body(organizationRepo.findAll());
    }

//    Save a new organization
    @PostMapping("/org/save")
    ResponseEntity<Organization> saveOrganization( @RequestBody Organization organization, @RequestBody User user){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/org/save").toUriString());

        return ResponseEntity.created(uri).body(organizationRepo.save(organization));
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

    @GetMapping("/boardroom")
    ResponseEntity<List<String>> boardrooms(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        return ResponseEntity.ok().body(boardRepository.findRooms(user.getOrganization()));
    }

    @PostMapping("/boardroom/save")
    ResponseEntity<BoardRoom> createRoom(@RequestBody BoardRoom boardRoom, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        boardRoom.setOrganization(user.getOrganization());
        return ResponseEntity.status(HttpStatus.CREATED).body(boardRepository.save(boardRoom));
    }

    @DeleteMapping("/boardroom/delete/{id}")
    ResponseEntity<String> deleteRoom(@PathVariable(name = "id") Integer id, HttpServletRequest request){
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

    @PutMapping("/boardroom/edit/{id}")
    ResponseEntity<String> editRoom(@PathVariable(name = "id") Integer id,HttpServletRequest request,@RequestBody BoardRoom boardRoom){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
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

//    END OF BOARDROOM CONTROLLER

//    USER MANAGEMENT CONTROLLER

//    END OF USER MANAGEMENT

//    MEETING MANAGEMENT

    @GetMapping("/meeting")
    ResponseEntity<List<Meeting>> meetings(HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        return ResponseEntity.ok().body(meetRepo.findMeets(user.getOrganization()));
    }

    @PostMapping("/meeting/save")
    ResponseEntity<Meeting> createMeet(@RequestBody Meeting meeting, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepo.findUserByName(name);
        List<RepeatMeetings> meetings = meetingsRepo.findMeets(user.getOrganization());
        meeting.setOrganization(user.getOrganization());
        meeting.getUsers().add(user);
        for (RepeatMeetings r : meetings){
            if(meetingsRepo.findConflictingMeet(meeting.getBoardroom(), r.getMeetDate(),meeting.getMeetStart()) != null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(meeting);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(meetRepo.save(meeting));
    }

//    END OF MEETING MANAGEMENT

}
