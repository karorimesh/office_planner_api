package com.tracom.office_planner.Boardroom;

import com.tracom.office_planner.Meeting.Meeting;
import com.tracom.office_planner.Meeting.MeetingRepository;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetings;
import com.tracom.office_planner.RepeatMeetings.RepeatMeetingsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
@Controller
public class BoardroomController {

        @Autowired
        private BoardRepository boardRepository;

        @GetMapping("/boardroom")
        public String viewBoards(Model model){
            List<BoardRoom> boardRooms = boardRepository.findAll();
            model.addAttribute("board", boardRooms);
            return "boardroom";
        }

        @RequestMapping("/delete_board/{board_id}")
        public String deleteBoard(@PathVariable(name = "board_id") int id) {
            boardRepository.deleteById(id);
            return "boardroom";
        }

//        @InitBinder
//        protected void initBinder(WebDataBinder binder) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
//            binder.registerCustomEditor(LocalTime.class, new CustomDateEditor(
//                  dateFormat, true));
//        }

        @GetMapping("/new_board")
        public String boardForm(Model model) {
            BoardRoom boardRoom = new BoardRoom();
            model.addAttribute("board", boardRoom);
            return "new_board";
        }

        @PostMapping("/save_board")
        public String saveNewBoard(BoardRoom boardRoom) {
            boardRepository.save(boardRoom);
            return "boardroom";
        }

        @RequestMapping("/edit/{board_id}")
        public ModelAndView showEditBoardForm(@PathVariable(name = "board_id") Integer id) {
            ModelAndView mnv = new ModelAndView("boardroom");
            BoardRoom boardRoom = (BoardRoom) boardRepository.getById(id);
            mnv.addObject("meet", boardRoom);
            return mnv;
        }


}
