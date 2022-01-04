package com.tracom.office_planner.User;

/*
Repository to retrieve users passwords
 */

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordRepository extends JpaRepository<UserPassword, Integer> {

}
