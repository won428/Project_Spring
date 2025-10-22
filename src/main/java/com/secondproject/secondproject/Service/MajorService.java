package com.secondproject.secondproject.Service;

import com.secondproject.secondproject.dto.MajorInCollegeDto;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.repository.MajorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorService {

    private final MajorRepository majorRepository;

    // 단과대학에 속한 학과 리스트 조회
    public List<MajorInCollegeDto> getMajorListByCollege(Long collegeId) {
         List<Major> majorList = this.majorRepository.findByCollege_Id(collegeId);
         List<MajorInCollegeDto> result = new ArrayList<>();

         for (Major m : majorList){
             Long id = m.getId();
             String name = m.getM_name();
             Long college = m.getCollege().getId();

             MajorInCollegeDto major = new MajorInCollegeDto(id, name, college);
             result.add(major);
         }

         return result;
    }

    // 학과 코드로 학과 찾기
    public Major findMajor(Long major) {
        return majorRepository.findById(major)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 학과: " + major));
    }
}
