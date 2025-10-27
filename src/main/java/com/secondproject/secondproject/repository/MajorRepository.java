package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.dto.MajorListDto;
import com.secondproject.secondproject.dto.MajorSearchDto;
import com.secondproject.secondproject.entity.Major;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major,Long> {
    List<Major> findByCollege_Id(Long collegeId);

    Major findMajorById(Long id);

    // 동일한 학과가 이미 존재하는지 확인
    boolean existsByNameAndCollegeId(@NotBlank(message = "학과명은 필수입니다.") @Size(min = 2, max = 50, message = "학과명은 2~50자여야 합니다.") @Pattern(
            // 한글/영문/숫자 + 공백 + 흔히 쓰는 기호(· - & ( ) /)만 허용
            regexp = "^[\\p{L}\\p{N}·&()\\-\\/ ]+$",
            message = "학과명에는 한글·영문·숫자와 공백, · - & ( ) /만 사용할 수 있습니다."
    ) String name, @NotNull Long collegeId);

    @Query("""
            select new com.secondproject.secondproject.dto.MajorListDto(
            m.id, m.name,m.office,c.id,c.type
            )
            from Major m
            join m.college c
            where (:#{majorSearchDto.majorId} is null or m.id = :#{majorSearchDto.majorId})
            and (:#{majorSearchDto.majorName} is null or m.name like concat('%',:#{majorSearchDto.majorName,'%')})
            and (:#{majorSearchDto.collegeId} is null or c.id = :#{majorSearchDto.collegeId})
            and (:#{majorSearchDto.collegeName} is null or c.type like concat('%',:#{majorSearchDto.collegeName},'%'))
            """)
    Page <MajorListDto> findAllWithCollege(@Param("majorSearchDto") MajorSearchDto majorSearchDto, Pageable pageable);
}