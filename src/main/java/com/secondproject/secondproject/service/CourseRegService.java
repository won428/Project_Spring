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

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRegService {
    private final CourseRegRepository courseRegRepository;
    private final LectureRepository lectureRepository;

    public void updateStatus(Long lecId ,Long id, Status status) {
        CourseRegistration courseRegistration = this.courseRegRepository.findByLecture_IdAndUser_Id(lecId,id);
        Lecture lecture = this.lectureRepository.findById(lecId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));
        Status nowStatus = lecture.getStatus();

        if (nowStatus.equals(Status.INPROGRESS)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 개강된 강의는 확정할 수 없습니다. 목록에서 삭제해주세요.");
        } else if (nowStatus.equals(Status.CANCELED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 폐강된 강의는 확정할 수 없습니다. 목록에서 삭제해주세요.");
        } else if (nowStatus.equals(Status.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 종강한 강의는 확정할 수 없습니다. 목록에서 삭제해주세요.");
        }


        courseRegistration.setStatus(status);

        this.courseRegRepository.save(courseRegistration);

    }

    public void delete(Long id, Long lecId) {
        CourseRegistration courseRegistration = this.courseRegRepository.findByLecture_IdAndUser_Id(lecId,id);

        this.courseRegRepository.deleteById(courseRegistration.getId());
    }

    public void deleteAll(Long id, List<Long> cancelSelected) {

        if (cancelSelected == null || cancelSelected.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강의는 최소 1개 이상 선택해야합니다..");
        } else if (id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음");
        }

        for(Long lecId : cancelSelected){
            CourseRegistration courseRegistration = this.courseRegRepository.findByLecture_IdAndUser_Id(lecId,id);

            this.courseRegRepository.deleteById(courseRegistration.getId());
        }
    }


    public void statusAll(Long id, List<Long> selected, Status status) {
        if (selected == null || selected.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강의는 최소 1개 이상 선택해야합니다..");
        } else if (id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음");
        }

        for(Long lecId : selected){
            CourseRegistration courseRegistration = this.courseRegRepository.findByLecture_IdAndUser_Id(lecId, id);
            courseRegistration.setStatus(status);
            this.courseRegRepository.save(courseRegistration);
        }
    }
}
