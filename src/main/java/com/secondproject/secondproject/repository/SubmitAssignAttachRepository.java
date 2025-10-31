package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Mapping.SubmitAssignAttach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmitAssignAttachRepository extends JpaRepository<SubmitAssignAttach, Long> {
    List<SubmitAssignAttach> findByAssignment_Id(Long id);

}
