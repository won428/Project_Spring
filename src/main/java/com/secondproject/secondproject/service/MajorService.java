package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.repository.CollegeRepository;
import com.secondproject.secondproject.repository.MajorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataLocationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MajorService {

    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;

    // 단과대학에 속한 학과 리스트 조회
    public List<MajorInCollegeDto> getMajorListByCollege(Long collegeId) {
        List<Major> majorList = this.majorRepository.findByCollege_Id(collegeId);
        List<MajorInCollegeDto> result = new ArrayList<>();

        for (Major m : majorList){
            Long id = m.getId();
            String name = m.getName();
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

    // 학과 등록하기
    public MajorResponseDto insertMajor(@Valid MajorInsertDto majorInsertDto) {

        // 해당 단과대학 존재여부 확인
        College college = collegeRepository.findById(majorInsertDto.getCollegeId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (majorRepository.existsByNameAndCollegeId(majorInsertDto.getName(),majorInsertDto.getCollegeId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // 학과 등록
        Major major = new Major();
        major.setName(majorInsertDto.getName());
        major.setOffice(majorInsertDto.getOffice());
        major.setCollege(college);

        try{
            Major saved = majorRepository.save(major);

            // 응답Dto로 변환
            MajorResponseDto responseDto = new MajorResponseDto();
            responseDto.setId(saved.getId());
            responseDto.setName(saved.getName());
            responseDto.setOffice(saved.getOffice());
            responseDto.setCollege(saved.getCollege());

            return responseDto;
        }catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"유효하지 않은 데이터 : "+e);
        }
    }


    public Page<MajorListDto> findAllMajors(MajorSearchDto majorSearchDto, Pageable pageable) {
        return majorRepository.findAllWithCollege(majorSearchDto,pageable);
    }

    public void deleteMajor(Long id) {
        majorRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return majorRepository.existsById(id);
    }
}