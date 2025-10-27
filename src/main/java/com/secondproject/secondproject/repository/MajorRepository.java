package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.Major;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

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
}