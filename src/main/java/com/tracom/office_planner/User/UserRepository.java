package com.tracom.office_planner.User;

/*
Handle crud functions for the user
 */

import com.tracom.office_planner.Organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
//    Find user by their username
    @Query("FROM User u WHERE u.userName = ?1")
    User findUserByName(String user_name);
//    Find user by their user emails
    @Query("FROM User u WHERE u.userEmail = ?1")
    User findByEmail(String user_email);
//    Find user by their access tokens
    @Query("FROM User u WHERE u.token = ?1")
    User findByToken(String token);
//    Find users by a search keyword
    @Query("SELECT u FROM User u WHERE CONCAT(u.userName, ' ' ,u.userId,' ',u.userEmail,' ',u.userRole) LIKE %?1% AND u.organization=?2")
    Page<User> search(String keyword, Organization organization, Pageable pageable);
//    find all users based on their organization
    @Query("SELECT u FROM User u WHERE u.organization =?1")
    Page<User> searchAll(Organization organization, Pageable pageable);
//    update failed login attempts by the user
    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.userEmail = ?2")
    @Modifying
    void updateFailedAttempt(int failed, String email);
//    find users by their organization
    @Query("SELECT u FROM User u WHERE u.organization=?1")
    List<User> findUsers(Organization organization);

    @Query("FROM User u WHERE u.userId=?1 AND u.organization=?2")
    User findUserById(Integer id, Organization organization);
}
