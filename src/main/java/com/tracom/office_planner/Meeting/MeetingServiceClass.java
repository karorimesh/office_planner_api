package com.tracom.office_planner.Meeting;


import com.azure.cosmos.implementation.guava25.collect.FluentIterable;
import com.tracom.office_planner.Boardroom.BoardRepository;
import com.tracom.office_planner.Boardroom.BoardRoom;
import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class MeetingServiceClass {

    private final RepeatMeetingsRepo repeatMeetingsRepo;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final MeetingRepository meetingRepository;

    @Autowired
    public MeetingServiceClass(MeetingRepository meetingRepository, BoardRepository boardRepository,RepeatMeetingsRepo repeatMeetingsRepo, UserRepository userRepository) {
        this.repeatMeetingsRepo = repeatMeetingsRepo;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.meetingRepository = meetingRepository;
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

    public void viewMeetList(HttpServletRequest request, String keyword, int page, String dir, String field, Model model){
        Principal principal = request.getUserPrincipal();
        User user = userRepository.findUserByName(principal.getName());
        Page<RepeatMeetings> content = listAll(keyword,page,dir,field, user.getOrganization());
        List<RepeatMeetings> listMeet = content.getContent();
        model.addAttribute("meetings", listMeet);
        model.addAttribute("keyword",keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", content.getTotalPages());
        model.addAttribute("totalUsers",content.getTotalElements());
        model.addAttribute("sortDir", dir);
        model.addAttribute("sortField",field);
        model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
    }

    public void viewMyMeetList(HttpServletRequest request,String keyword, int page, String dir, String field, Model model){
        Principal principal = request.getUserPrincipal();
        User user = userRepository.findUserByName(principal.getName());
        Page<RepeatMeetings> content = listAll(keyword,page,dir,field, user.getOrganization());
        List<RepeatMeetings> listMeet = content.getContent();
        List<RepeatMeetings> listMeets = FluentIterable
                .from(listMeet)
                .filter(l -> l.getMeeting().getUsers().contains(user))
                .toList();
        model.addAttribute("meetings", listMeets);
        model.addAttribute("keyword",keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", content.getTotalPages());
        model.addAttribute("totalUsers",content.getTotalElements());
        model.addAttribute("sortDir", dir);
        model.addAttribute("sortField",field);
        model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
    }

    public void deleteMeet(HttpServletRequest request, int id){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        Meeting meeting = repeatMeetingsRepo.getById(id).getMeeting();
        PlannerLogger.deleteMeeting(meeting, user);
        repeatMeetingsRepo.deleteById(id);
    }

    /*
    Logic before rendering form
     */

    public void meetingForm(HttpServletRequest request, Model model, Meeting meeting){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        List<BoardRoom> boards = boardRepository.findBoards(user.getOrganization());
        List<User> usersList = userRepository.findUsers(user.getOrganization());
        List<User> users = FluentIterable.from(usersList)
                .filter(u -> u != user)
                .toList();
        model.addAttribute("meet", meeting);
        model.addAttribute("board",boards);
        model.addAttribute("users",users);
    }

    /*
    Save a meeting logic
     */
    public Model saveNewMeet(Meeting meet, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        meet.setOrganization(user.getOrganization());
        meet.getUsers().add(user);
        meet.getRepeatMeetings().forEach(r->r.setMeeting(meet));
        List<RepeatMeetings> meetings = meet.getRepeatMeetings();
//            Check for any conflicting meeting
        for (RepeatMeetings r : meetings){
            if(repeatMeetingsRepo.findConflictingMeet(meet.getBoardroom(), r.getMeetDate(),meet.getMeetStart()) != null){
                model.addAttribute("error","This boardroom will be busy at this time! contact an admin for a reschedule or check the calendar for available time");
                return model;
            }
        }
        PlannerLogger.createMeeting(meet,user);
        meetingRepository.save(meet);
        model.addAttribute("message", "Meeting scheduled successfully");
        return model;
        // TODO: 1/24/2022 Add message div in the view

    }

    /*
    Update an edited meeting
     */
    public void saveEditedMeet(HttpServletRequest request, Meeting meet){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        meet.setOrganization(user.getOrganization());
        meet.getRepeatMeetings().forEach(r->r.setMeeting(meet));
        meetingRepository.save(meet);
        PlannerLogger.updateMeeting(meet,user);
    }

    /*
    Logic for Edit form
     */
    public void showEdit(HttpServletRequest request, ModelAndView mnv, Model model, int id){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        List<BoardRoom> boards = boardRepository.findBoards(user.getOrganization());
        List<User> users = userRepository.findUsers(user.getOrganization());
        Meeting m = meetingRepository.getById(id);
        model.addAttribute("board",boards);
        model.addAttribute("users",users);
        mnv.addObject("meet", m);
    }
}
