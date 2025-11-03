package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.AppealDto;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.repository.AppealRepository;
import com.secondproject.secondproject.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AppealService {

    private final AppealRepository appealRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AppealService(AppealRepository appealRepository, EnrollmentRepository enrollmentRepository) {
        this.appealRepository = appealRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public AppealDto createAppeal(AppealDto dto) {
        Appeal appeal = new Appeal();

        appeal.setSendingId(dto.getSendingId());
        appeal.setReceiverId(dto.getReceiverId());

        // Enrollment 프록시 가져오기
        Enrollment enrollment = enrollmentRepository.getReferenceById(dto.getEnrollmentId());
        appeal.setEnrollment(enrollment);

        appeal.setTitle(dto.getTitle());
        appeal.setContent(dto.getContent());
        appeal.setAppealDate(dto.getAppealDate() != null ? dto.getAppealDate() : LocalDate.now());
        appeal.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.PENDING);
        appeal.setAppealType(dto.getAppealType());

        Appeal saved = appealRepository.save(appeal);

        return new AppealDto(
                saved.getId(),
                saved.getSendingId(),
                saved.getReceiverId(),
                saved.getEnrollment().getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getAppealDate(),
                saved.getStatus(),
                saved.getAppealType()
        );
    }
}
