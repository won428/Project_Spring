package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.entity.CourseRegistration;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.repository.CourseRegRepository;
import com.secondproject.secondproject.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CourseRegService {
    private final CourseRegRepository courseRegRepository;
    private final LectureRepository lectureRepository;

    public void updateStatus(Long id, Status status) {
        CourseRegistration courseRegistration = this.courseRegRepository.findByLecture_Id(id);
        Lecture lecture = this.lectureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));
        Status nowStatus = lecture.getStatus();

        if (nowStatus.equals(Status.IN_PROGRESS)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 개강된 강의는 확정할 수 없습니다. 목록에서 삭제해주세요.");
        } else if (nowStatus.equals(Status.CANCELED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 폐강된 강의는 확정할 수 없습니다. 목록에서 삭제해주세요.");
        } else if (nowStatus.equals(Status.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 종강한 강의는 확정할 수 없습니다. 목록에서 삭제해주세요.");
        }


        courseRegistration.setStatus(status);

        this.courseRegRepository.save(courseRegistration);

    }
}
