package com.tracom.office_planner.User;

import com.tracom.office_planner.Organization.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("FROM User u WHERE u.userName = ?1")
    User findUserByName(String user_name);
    @Query("FROM User u WHERE u.userEmail = ?1")
    User findByEmail(String user_email);
    @Query("FROM User u WHERE u.token = ?1")
    public User findByToken(String token);
    @Query("SELECT u FROM User u WHERE CONCAT(u.userName, ' ' ,u.userId,' ',u.userEmail,' ',u.userRole) LIKE %?1% AND u.organization=?2")
    public Page<User> search(String keyword, Organization organization, Pageable pageable);
    @Query("SELECT u FROM User u WHERE u.organization =?1")
    public Page<User> searchAll( Organization organization,Pageable pageable);
    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.userEmail = ?2")
    @Modifying
    public void updateFailedAttempt(int failed, String email);
    @Query("SELECT u FROM User u WHERE u.organization=?1")
    List<User> findUsers(Organization organization);
}
