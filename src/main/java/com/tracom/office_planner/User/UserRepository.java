package com.tracom.office_planner.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
//    @Query("FROM User u WHERE u.email = ?1")
//    User findByEmail(String email);
}
