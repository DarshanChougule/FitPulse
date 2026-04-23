package com.fitness.userservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitness.userservice.model.User;

@Repository
public interface UserRepo extends JpaRepository<User,String> {

    boolean existsByEmail(String email);

    User findByEmail(String email);

    boolean existsByKeykloakid(String id);

}
