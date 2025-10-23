package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.CollegeCreateReq;
import com.secondproject.secondproject.dto.ColResponseDto;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.mapper.CollegeMapper;
import com.secondproject.secondproject.repository.CollegeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeMapper collegeMapper;
    private final CollegeRepository collegeRepository;

    @Transactional // 단과대학 데이터 insert
    public ColResponseDto insert(CollegeCreateReq collegeCreateReq){
        College insertCollege = collegeRepository.save(collegeMapper.InsertToEntity(collegeCreateReq));
        return collegeMapper.toResponse(insertCollege);
    }

    public boolean existsById(Long college_id) {return collegeRepository.existsById(college_id);
    }

    public void deleteById(Long collegeId) {
        collegeRepository.deleteById(collegeId);
    }

    public List<ColResponseDto> getList(){
        List<College> collegeEntityList  = collegeRepository.findAllByOrderByTypeAsc();
        List<ColResponseDto> collegesLists = new ArrayList<>();
        for (College c : collegeEntityList){
            ColResponseDto collegeResponse = new ColResponseDto();
            collegeResponse.setId(c.getId());
            collegeResponse.setC_type(c.getType());
            collegeResponse.setC_office(c.getOffice());
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
    public ColResponseDto update(Long id, CollegeCreateReq req) {
        College entity = collegeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID: " + id));
        entity.setType(req.getC_type());
        entity.setOffice(req.getC_office());
        return collegeMapper.toResponse(entity);
    }

    public Optional<ColResponseDto> findById(Long id) {
        return collegeRepository.findById(id).map(collegeMapper::toResponse);
    }
}