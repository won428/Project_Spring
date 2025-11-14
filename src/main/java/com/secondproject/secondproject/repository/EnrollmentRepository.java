package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.EnrollmentSearchDto;
import com.secondproject.secondproject.dto.EnrollmentStudentDto;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.Enum.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Enrollment> findByStatus(Status status);

    List<Enrollment> findAllByLecture_Id(Long id);

    @Query("""
            select 
            e.id as enrollmentId,
            l.id as lectureId, 
            l.name as lectureName, 
            l.user.name as userName, 
            l.credit as credit,
            l.completionDiv as completionDiv,
            m.name as majorName
            from Enrollment e
            join e.lecture l
            left join l.major m
            where e.user.id = :studentId
            """)
    List<EnrollmentView> findDtoByUserId(@Param("studentId")Long studentId);

    List<Enrollment> findAllByUser_Id(Long id);

    @Query("""
            select
            u.id as id,
            u.userCode as userCode,
            u.name as name,
            m.name as majorName,
            u.email as email,
            u.phone as phone
            from Enrollment e
            join e.user u
            join u.major m
            join e.lecture l
            where l.id = :lectureId
            and (
               :keyword is null
               or :keyword = ''
               or :mode is null
               or :mode = ''
               or :mode = 'ALL'
               or (:mode = 'STUDENT_CODE' and cast(u.userCode as string) like concat('%', :keyword, '%'))
               or (:mode = 'NAME'         and u.name like concat('%', :keyword, '%'))
             )
            """)
    Page<EnrollmentStudentDto> searchEnrolledStudents(@Param("lectureId") Long lectureId,
                                                      @Param("mode") String searchMode,
                                                      @Param("keyword") String searchKeyword,
                                                      Pageable pageable);
}

