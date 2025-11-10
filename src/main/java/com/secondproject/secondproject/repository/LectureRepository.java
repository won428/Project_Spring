package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByUser(User user);

    @Query("""
            select l
              from Lecture l
             where not exists (
                   select 1
                     from CourseRegistration cr
                    where cr.lecture = l
                      and cr.user.id = :userId
             )
            """)
    List<Lecture> findAllNotRegisteredByUser(@Param("userId") Long userId);


}
