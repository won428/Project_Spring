package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.EnrollmentRequestDto;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.Enum.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // 만약 Enrollment가 User를 참조하고 User에 type 필드가 있다면, JPQL 직접 작성

    @Query("SELECT e FROM Enrollment e WHERE e.lecture.id = :lectureId AND e.user.type = :userType")
    List<Enrollment> findByLectureIdAndType(@Param("lecture_id") Long id, @Param("u_Type") UserType type);


    List<Enrollment> findByUser(User user);

    List<Enrollment> findByLecture_IdAndUser_IdIn(Long id, List<Long> userIds);

    List<Enrollment> findByLecture_Id(Long lectureId);

    // 총 수강 인원 카운트 (학생 출결등록 및 중복 저장 방지용)
    long countByLecture_Id(Long lectureId);

    List<Enrollment> findByUser_Id(Long userId);

    boolean existsByUserAndLecture(User user, Lecture lecture);

    Enrollment findByUserIdAndLectureId(Long userId, Long lectureId);

    @Query("""
            select new com.secondproject.secondproject.dto.EnrollmentRequestDto(
            e.user.id, e.lecture.id, e.grade.id, e.completionDiv, e.status ) from Enrollment e
            join e.user u
            join e.lecture l
            where e.user.id = :userId
            """)
    List<EnrollmentRequestDto> findDtoByUserId(@Param("userId") Long userId);
}

