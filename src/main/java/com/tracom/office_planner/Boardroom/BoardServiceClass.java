package com.tracom.office_planner.Boardroom;

/*
Logic for boardroom management
 */
// TODO: 12/23/2021 Move code from controller to this place

import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class BoardServiceClass {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Autowired
    public BoardServiceClass( UserRepository userRepository ,BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }
/*
   Paginated list of boadrooms
 */
    public Page<BoardRoom> pageAll(String keyword, int pageNo, String sortDir, String field, Organization organization){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,
                sortDir.equals("asc")? Sort.by(field).ascending():Sort.by(field).descending());
        if (keyword != null){
            return boardRepository.search(keyword, organization, pageable );
        }
        return boardRepository.searchAll(organization,pageable);
    }

    /*
    View boardrooms logic
     */
    public void viewBoardsList(HttpServletRequest request, String keyword, int page, String dir, String field, Model model){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        Page<BoardRoom> content = pageAll(keyword,page,dir,field, user.getOrganization());
        List<BoardRoom> listBoards = content.getContent();
        // TODO: 11/16/2021 Filter Boards to the user, also add ant matchers to spring security
        model.addAttribute("board", listBoards);
        model.addAttribute("keyword",keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", content.getTotalPages());
        model.addAttribute("totalBoards",content.getTotalElements());
        model.addAttribute("sortDir", dir);
        model.addAttribute("sortField",field);
        model.addAttribute("reverseDir",dir.equals("asc")?"desc":"asc");
    }

    /*
    Delete Boardroom logic
     */

    public void deleteBoard(HttpServletRequest request, int id){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        BoardRoom boardRoom = boardRepository.getById(id);
        PlannerLogger.deleteBoardroom(boardRoom,user);
        boardRepository.deleteById(id);
    }

    /*
    Save a boardroom logic
     */
    public void saveNewBoard(HttpServletRequest request, BoardRoom boardRoom){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        boardRoom.setOrganization(user.getOrganization());
        boardRepository.save(boardRoom);
        PlannerLogger.createBoardroom(boardRoom,user);
    }

    /*
    Update a boardroom logic
     */
    public void editedBoard(HttpServletRequest request, BoardRoom boardRoom){
        Principal principal = request.getUserPrincipal();
        String name = principal.getName();
        User user = userRepository.findUserByName(name);
        boardRoom.setOrganization(user.getOrganization());
        boardRepository.save(boardRoom);
        PlannerLogger.editBoardroom(boardRoom,user);
    }


}
