package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.CollegePaging;
import com.secondproject.secondproject.dto.CollegeInsertDto;
import com.secondproject.secondproject.dto.CollegeResponseDto;
import com.secondproject.secondproject.dto.CollegeSearchDto;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.repository.CollegeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    // College 엔터티 id로 조회해서 응답Dto로 받기
    public Optional<CollegeResponseDto> findById(Long id) {
        return collegeRepository.findById(id).map(this::toDto);
    }

    // 엔터티를 응답Dto로 반환
    public CollegeResponseDto toDto(College college){
        CollegeResponseDto collegeResponseDto = new CollegeResponseDto();
        collegeResponseDto.setId(college.getId());
        collegeResponseDto.setType(college.getType());
        collegeResponseDto.setOffice(college.getOffice());

        return collegeResponseDto;
    }

    public Page<College> listCollage(Pageable pageable){
        return this.collegeRepository.findAll(pageable);
    }

    public Page<College> search(CollegeSearchDto collegeSearchDto) {
        Pageable pageable = PageRequest.of(collegeSearchDto.getPage(),collegeSearchDto.getSize());
        String keyword = Optional.ofNullable(collegeSearchDto.getSearchKeyword()).orElse("");
        CollegePaging mode = Optional.ofNullable(collegeSearchDto.getCollegePaging()).orElse(CollegePaging.ALL);

        if (mode == null || mode == CollegePaging.ALL) {
            return collegeRepository.findByTypeContainingIgnoreCaseOrOfficeContaining(keyword, keyword, pageable);
        } else if (mode == CollegePaging.TYPE) {
            return collegeRepository.findByTypeContainingIgnoreCase(keyword, pageable);
        } else if (mode == CollegePaging.OFFICE) {
            return collegeRepository.findByTypeContainingIgnoreCase(keyword, pageable);
        } else {
            // 예상치 못한 값 처리
            return collegeRepository.findByTypeContainingIgnoreCaseOrOfficeContaining(keyword, keyword, pageable);
            // 또는 throw new IllegalStateException("Unexpected mode: " + mode);
        }
    }
}