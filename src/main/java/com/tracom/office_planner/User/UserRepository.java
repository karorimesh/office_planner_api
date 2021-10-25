package com.tracom.office_planner.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("FROM User u WHERE u.user_name = ?1")
    User findUserByName(String user_name);
}
