package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.GradingWeights;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingWeightsRepository extends JpaRepository<GradingWeights, Long> {
    GradingWeights findByLecture_Id(Long id);

    void deleteByLecture_Id(Long id);
}
