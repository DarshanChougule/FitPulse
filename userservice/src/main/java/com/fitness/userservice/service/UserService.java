package com.fitness.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitness.userservice.dto.RegesterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repo.UserRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public UserResponse regester(RegesterRequest request) {

        if(userRepo.existsByEmail(request.getEmail())){
            User existingUser = userRepo.findByEmail(request.getEmail());

            UserResponse response=new UserResponse();

            response.setId(existingUser.getId());
            response.setKeykloakid(existingUser.getKeykloakid());
            response.setEmail(existingUser.getEmail());
            response.setPassword(existingUser.getPassword());
            response.setFirstName(existingUser.getFirstName());
            response.setLastName(existingUser.getLastName());
            response.setCreatedAt(existingUser.getCreatedAt());
            response.setUpdatedAt(existingUser.getUpdatedAt());

            return response;
        }


        User user=new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setKeykloakid(request.getKeykloakid());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User save = userRepo.save(user);

        UserResponse response=new UserResponse();

        response.setId(save.getId());
        response.setKeykloakid(save.getKeykloakid());
        response.setEmail(save.getEmail());
        response.setPassword(save.getPassword());
        response.setFirstName(save.getFirstName());
        response.setLastName(save.getLastName());
        response.setCreatedAt(save.getCreatedAt());
        response.setUpdatedAt(save.getUpdatedAt());
        
        return response;
    }

    public UserResponse getUserProfile(String userId) {
        User user= userRepo.findById(userId)
        .orElseThrow(() -> new RuntimeException("User Not Found"));

         UserResponse response=new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;

    }

    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for UserId: "+userId);
        return userRepo.existsById(userId);
    }

    public Boolean existByKeykloakId(String userId) {
        return userRepo.existsByKeykloakid(userId);
    }

}
