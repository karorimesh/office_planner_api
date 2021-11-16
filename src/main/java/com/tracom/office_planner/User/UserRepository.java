package com.tracom.office_planner.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("FROM User u WHERE u.userName = ?1")
    User findUserByName(String user_name);
    @Query("FROM User u WHERE u.userEmail = ?1")
    User findByEmail(String user_email);
    @Query("FROM User u WHERE u.token = ?1")
    public User findByToken(String token);
    @Query("SELECT u FROM User u WHERE CONCAT(u.userName, ' ' ,u.userId,' ',u.userEmail,' ',u.userRole) LIKE %?1%")
    public Page<User> search(String keyword, Pageable pageable);
}
