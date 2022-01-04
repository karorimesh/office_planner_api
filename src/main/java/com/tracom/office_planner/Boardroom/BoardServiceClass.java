package com.tracom.office_planner.Boardroom;

/*
Logic for boardroom management
 */
// TODO: 12/23/2021 Move code from controller to this place

import com.tracom.office_planner.Organization.Organization;
import com.tracom.office_planner.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceClass {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardServiceClass(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
//    Paginated list of boadrooms
    public Page<BoardRoom> pageAll(String keyword, int pageNo, String sortDir, String field, Organization organization){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,
                sortDir.equals("asc")? Sort.by(field).ascending():Sort.by(field).descending());
        if (keyword != null){
            return boardRepository.search(keyword, organization, pageable );
        }
        return boardRepository.searchAll(organization,pageable);
    }
}
