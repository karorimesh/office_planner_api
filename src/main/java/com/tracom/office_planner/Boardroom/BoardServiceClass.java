package com.tracom.office_planner.Boardroom;


import com.tracom.office_planner.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceClass {

    private BoardRepository boardRepository;

    @Autowired
    public BoardServiceClass(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Page<BoardRoom> listAll(String keyword, int pageNo, String sortDir, String field){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,
                sortDir.equals("asc")? Sort.by(field).ascending():Sort.by(field).descending());
        if (keyword != null){
            return boardRepository.search(keyword, pageable);
        }
        return boardRepository.findAll(pageable);
    }
}
