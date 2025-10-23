package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.CollegeInsertDto;
import com.secondproject.secondproject.dto.CollegeResponseDto;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.repository.CollegeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeRepository collegeRepository;

    @Transactional // 단과대학 데이터 insert
    public CollegeResponseDto insert(CollegeInsertDto collegeInsertDto){
        College college = new College();
        college.setType(collegeInsertDto.getType());
        college.setOffice(collegeInsertDto.getOffice());

        College saved = collegeRepository.save(college);

        CollegeResponseDto collegeResponseDto = new CollegeResponseDto();
        collegeResponseDto.setId(saved.getId());
        collegeResponseDto.setType(saved.getType());
        collegeResponseDto.setOffice(saved.getOffice());

        return collegeResponseDto;
    }

    public boolean existsById(Long college_id) {return collegeRepository.existsById(college_id);
    }

    public void deleteById(Long collegeId) {
        collegeRepository.deleteById(collegeId);
    }

    public List<CollegeResponseDto> getList(){
        List<College> collegeEntityList  = collegeRepository.findAllByOrderByTypeAsc();
        List<CollegeResponseDto> collegesLists = new ArrayList<>();
        for (College c : collegeEntityList){
            CollegeResponseDto collegeResponse = new CollegeResponseDto();
            collegeResponse.setId(c.getId());
            collegeResponse.setType(c.getType());
            collegeResponse.setOffice(c.getOffice());
            collegesLists.add(collegeResponse);
        }
        return collegesLists;
    }

    // 업데이트에 사용할, 선택한 객체 1개 갖고오기
    public College getCollegeId(long id) {
        Optional<College> college = this.collegeRepository.findById(id);
        return college.orElse(null);
    }

    // 입력한 정보로 업데이트하기
    @Transactional
    public CollegeResponseDto update(Long id, CollegeInsertDto req) {
        College entity = collegeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID: " + id));
        entity.setType(req.getType());
        entity.setOffice(req.getOffice());

        CollegeResponseDto collegeResponseDto = new CollegeResponseDto();
        collegeResponseDto.setId(entity.getId());
        collegeResponseDto.setType(entity.getType());
        collegeResponseDto.setOffice(entity.getOffice());

        return collegeResponseDto;
    }

    //
    public Optional<CollegeResponseDto> findById(Long id) {
        return collegeRepository.findById(id).map(this::toDto);
    }

    public CollegeResponseDto toDto(College college){
        CollegeResponseDto collegeResponseDto = new CollegeResponseDto();
        collegeResponseDto.setId(college.getId());
        collegeResponseDto.setType(college.getType());
        collegeResponseDto.setOffice(college.getOffice());

        return collegeResponseDto;
    }
}