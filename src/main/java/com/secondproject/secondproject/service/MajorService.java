package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.MajorPaging;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.CollegeRepository;
import com.secondproject.secondproject.repository.MajorRepository;
import com.secondproject.secondproject.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MajorService {

    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;

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

            return new MajorResponseDto(saved.getId(), saved.getName(),saved.getOffice(),saved.getCollege().getId());
        }catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"유효하지 않은 데이터 : "+e);
        }
    }

    // 숫자만 입력가능하게 하는 필터함수
    private Long parseLongOrNull(String s) {
        try { return s.isBlank()? null : Long.parseLong(s); }
        catch (NumberFormatException e) { return null; }
    }

    // 학과 검색조건 설정 및 검색
    @Transactional(readOnly = true)
    public Page<MajorListDto> findAllMajors(Pageable pageable, MajorPaging searchType, String searchKeyword) {
        String keyWord = searchKeyword == null ? "":searchKeyword.trim();

        switch (searchType) {
            case MAJORID -> {
                Long id = parseLongOrNull(keyWord);
                return majorRepository.searchById(pageable,id);
            } // 학과 ID 검색
            case MAJORNAME -> { return majorRepository.searchByName(pageable,searchKeyword);} // 학과명 검색
            case COLLEGENAME -> {return majorRepository.seachByCollegeName(pageable,searchKeyword);} //단과대학명 검색
            case ALL -> {return majorRepository.searchAll(pageable,searchKeyword);} // 전체검색
            default -> {return majorRepository.searchAll(pageable,searchKeyword);} // 기본값 전체검색

        }


    }

    public void deleteMajor(Long id) {
        majorRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return majorRepository.existsById(id);
    }


    public Optional<MajorListDto> findById(Long id) {
        return majorRepository.findByMajorId(id);
    }

    // 전체 학과 조회
    public List<MajorListDto> majorList() {
        List<Major> majorList = this.majorRepository.findAll();
        List<MajorListDto> majorListDtos = new ArrayList<>();
        for(Major major : majorList){
            MajorListDto majorListDto = new MajorListDto();

            majorListDto.setId(major.getId());
            majorListDto.setName(major.getName());
            majorListDto.setOffice(major.getOffice());
            majorListDto.setCollegeId(major.getCollege().getId());

            majorListDtos.add(majorListDto);
        }

        return  majorListDtos;
    }


//    public MajorListDto toDto(Major major) {
//        MajorListDto majorListDto = new MajorListDto();
//
//        majorListDto.setId(major.getId());
//        majorListDto.setName(major.getName());
//        majorListDto.setOffice(major.getOffice());
//        majorListDto.setCollegeId(major.getCollege().getId());
//        majorListDto.setCollegeName(major.getCollege().getType());
//
//        return majorListDto;
//    }

    public MajorResponseDto updateMajor(Long id, @Valid MajorInsertDto majorInsertDto) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지않는 ID : "+id));
        College college = collegeRepository.findById(majorInsertDto.getCollegeId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        major.setName(majorInsertDto.getName());
        major.setOffice(majorInsertDto.getOffice());
        major.setCollege(college);

        Major saved = majorRepository.save(major);

        return new MajorResponseDto(saved.getId(), saved.getName(), saved.getOffice(), saved.getCollege().getId());
    }

    public List<MajorListDto> selectAll() {
        List<Major> majorLists = this.majorRepository.findAll();
        List<MajorListDto> majorDtos = new ArrayList<>();

        for (Major m : majorLists){
            Long id = m.getId();
            String name = m.getName();
            String office = m.getOffice();
            Long collegeId = m.getCollege().getId();
            String collegeName = m.getCollege().getType();

            MajorListDto rs = new MajorListDto(id,name,office,collegeId,collegeName);

            majorDtos.add(rs);
        }
        return majorDtos;
    }


    public List<ListForLectureDto> ListForLecture() {
        List<Major> majorLists = this.majorRepository.findAll();
        List<ListForLectureDto> majorDtos = new ArrayList<>();


        for (Major m : majorLists){
            List<User> userList = this.userRepository.findAllByMajor_IdAndType(m.getId(), UserType.PROFESSOR);
            List<UserDto> userList1 = new ArrayList<>();
            for(User user : userList){
                UserDto userDto = new UserDto();
                userDto.setId(user.getId());
                userDto.setName(user.getName());

                userList1.add(userDto);
            }
            Long id = m.getId();
            String name = m.getName();
            String office = m.getOffice();
            Long collegeId = m.getCollege().getId();
            String collegeName = m.getCollege().getType();



            ListForLectureDto rs = new ListForLectureDto(id,name,office,collegeId,collegeName,userList1);

            majorDtos.add(rs);
        }
        return majorDtos;
    }
}