package com.baro.domain.order.repository;

import com.baro.domain.order.domain.CocktailQueue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface JPAMongoCocktailQueueRepository extends MongoRepository<CocktailQueue, String> {
    Optional<CocktailQueue> findByMachineId(String machine_id);
    @Query(value = "{ 'machineId': ?0 }", sort = "{ 'waitingLine' : -1 }")
    Optional<CocktailQueue> findTopByMachineIdOrderByWaitingLineDesc(String machineId);

    Optional<CocktailQueue> findByMachineIdAndWaitingLine(String machine_id , int waitingLine);

    boolean existsByMachineIdAndWaitingLine(String machine_id , int waitingLine);

}
