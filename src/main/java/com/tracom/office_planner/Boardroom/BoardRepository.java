package com.tracom.office_planner.Boardroom;

import com.tracom.office_planner.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<BoardRoom, Integer> {

    @Query("FROM BoardRoom b WHERE b.boardName=?1")
    BoardRoom findByName(String board_name);
    @Query("SELECT b FROM BoardRoom b WHERE CONCAT(b.boardName, ' ' ,b.boardId,' ',b.boardLocation,' ',b.Capacity) LIKE %?1%")
    Page<BoardRoom> search(String keyword, Pageable pageable);
}
