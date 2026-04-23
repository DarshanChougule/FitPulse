package com.fitness.activityservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repo.ActivityRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepo activityRepo;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValidUser= userValidationService.validateUser(request.getUserId());
        if(!isValidUser){
            throw new RuntimeException("Invalid User: "+request.getUserId());
        }
        Activity activity = Activity.builder()
                            .userId(request.getUserId())
                            .type(request.getType())
                            .duration(request.getDuration())
                            .caloriesBurned(request.getCaloriesBurned())
                            .startTime(request.getStartTime())
                            .additionalMetrices(request.getAdditionalMetrices())
                            .build();
        
        Activity save = activityRepo.save(activity);

        // Send RabbitMq to AI processing

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, save);
            
        } catch (Exception e) {
            log.error("Failed to publish Activity to RabbitMq: ",e);
        }

        return maptoResponse(save);
        
    }

    private ActivityResponse maptoResponse(Activity activity){
        ActivityResponse response =new ActivityResponse();

        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setAdditionalMetrices(activity.getAdditionalMetrices());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setCreatedAt(activity.getCreatedAt());
        response.setDuration(activity.getDuration());
        response.setStartTime(activity.getStartTime());
        response.setType(activity.getType());
        response.setUpdatedAt(activity.getUpdatedAt());
        
        return response;
    }

    public List<ActivityResponse> getUserActivity(String userId) {
        List<Activity> activities= activityRepo.findByUserId(userId);

        return activities.stream()
                .map(this:: maptoResponse)
                .collect(Collectors.toList());
    }

    public ActivityResponse getActivity(String id) {

        return activityRepo.findById(id)
                .map(this:: maptoResponse)
                .orElseThrow(() -> new RuntimeException("Activity Not Found with id: "+id));
    }

}
