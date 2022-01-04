package com.tracom.office_planner.Boardroom;

//Boardroom controller to enable boardroom management

import com.tracom.office_planner.MeetingsLog.PlannerLogger;
import com.tracom.office_planner.User.User;
import com.tracom.office_planner.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
public class BoardroomController {


        private final BoardRepository boardRepository;
        private final BoardServiceClass serviceClass;
        private final UserRepository userRepository;

    @Autowired
    public BoardroomController(BoardRepository boardRepository, BoardServiceClass serviceClass, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.serviceClass = serviceClass;
        this.userRepository = userRepository;
    }




    @GetMapping("/boardroom")
        public String viewBoards(Model model, HttpServletRequest request){
            return viewBoardsList(model, request, null,1,"boardName","asc");
        }

        @GetMapping("/boardroom/page/{page}")
        public String viewBoardsList(Model model, HttpServletRequest request, @Param("keyword") String keyword,
                                    @PathVariable(name = "page") int page,
                                    @Param("field") String field, @Param("dir") String dir) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            Page<BoardRoom> content = serviceClass.pageAll(keyword,page,dir,field, user.getOrganization());
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
            return "boardrooms";
        }
        @RequestMapping("/delete_board/{board_id}")
        public String deleteBoard(@PathVariable(name = "board_id") int id, HttpServletRequest request) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            BoardRoom boardRoom = boardRepository.getById(id);
            PlannerLogger.deleteBoardroom(boardRoom,user);
            boardRepository.deleteById(id);
            return "/boardroom";
        }

        @GetMapping("/new_board")
        public String boardForm(Model model) {

            BoardRoom boardRoom = new BoardRoom();
            model.addAttribute("board", boardRoom);
            return "boardroom";
        }

        @PostMapping("/save_board")
        public String saveNewBoard(BoardRoom boardRoom, HttpServletRequest request) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            boardRoom.setOrganization(user.getOrganization());
            boardRepository.save(boardRoom);
            PlannerLogger.createBoardroom(boardRoom,user);
            return "redirect:/boardroom";

        }

        @PutMapping("/edited_board")
        public String saveEditedBoard(BoardRoom boardRoom, HttpServletRequest request) {
            Principal principal = request.getUserPrincipal();
            String name = principal.getName();
            User user = userRepository.findUserByName(name);
            boardRoom.setOrganization(user.getOrganization());
            boardRepository.save(boardRoom);
            PlannerLogger.editBoardroom(boardRoom,user);
            return "redirect:/boardroom";

        }

        @RequestMapping("/edit/{board_id}")
        public ModelAndView showEditBoardForm(@PathVariable(name = "board_id") Integer id) {
            ModelAndView mnv = new ModelAndView("editBoardroom");
            BoardRoom boardRoom = boardRepository.getById(id);
            mnv.addObject("board", boardRoom);
            return mnv;
        }


}
