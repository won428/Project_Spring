package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.Mapping.AssignmentAttach;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentAttachRepository extends JpaRepository<AssignmentAttach, Long> {
    List<AssignmentAttach> findByAssignment_Id(Long id);
}
