package com.fitness.aiservice.repo;
import com.fitness.aiservice.model.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RecommendationRepo extends MongoRepository<Recommendation,String> {

    List<Recommendation> findByUserId(String userId);

    Optional<Recommendation> findByActivityId(String activityId);


}
