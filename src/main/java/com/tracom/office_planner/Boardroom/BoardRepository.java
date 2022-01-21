package com.tracom.office_planner.Boardroom;

/* Repository to CRUD boardrooms in an organizations */

import com.tracom.office_planner.Organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional

public interface BoardRepository extends JpaRepository<BoardRoom, Integer> {

    /* Find room by name */
    @Query("FROM BoardRoom b WHERE b.boardName=?1")
    BoardRoom findByName(String board_name);
    /* Search boardroom by all its properties*/
    @Query("SELECT b FROM BoardRoom b WHERE CONCAT(b.boardName, ' ' ,b.boardId,' ',b.boardLocation,' ',b.Capacity) LIKE %?1% AND b.organization =?2")
    Page<BoardRoom> search(String keyword,Organization organization, Pageable pageable);
    /* Search with an empty field*/
    @Query("SELECT b FROM BoardRoom b WHERE b.organization =?1 ")
    Page<BoardRoom> searchAll(Organization organization,Pageable pageable);
    /* All boardrooms in an organization without pagination */
    @Query("SELECT b FROM  BoardRoom b WHERE b.organization =?1")
    List<BoardRoom> findBoards(Organization organization);

    /*
    API queries
     */
    @Query(value = "SELECT boardId,boardName,boardLocation FROM BoardRoom WHERE organization=?1")
    List<String> findRooms(Organization organization);

    @Modifying
    @Query("UPDATE BoardRoom b SET b.boardName =?1, b.Capacity=?2, b.TV =?3, b.Phone =?4, b.Whiteboard = ?5, b.boardLocation = ?6, b.Others = ?7 WHERE b.boardId = ?8")
    void updateRoom(String boardName,int capacity, boolean TV,boolean phone,boolean whiteboard,String location,String others, int id);

    @Query("FROM BoardRoom b WHERE b.boardId=?1 AND b.organization=?2")
    BoardRoom findBoard(Integer id, Organization organization);


    /*
    End of Api Queries
     */

}
