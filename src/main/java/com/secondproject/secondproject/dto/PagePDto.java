package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.AcademicCalendar;
import com.secondproject.secondproject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagePDto {

    private Long userCode;
    private String userName;
    private String major;
    private String college;
    private UserType u_type;

    private List<LectureDto> lectureDtoList;
    private Page<AcademicCalendar> academicCalendar;


    public static PagePDto fromEntity(User user, List<LectureDto> lec, Page<AcademicCalendar> res) {
        PagePDto pagePDto = new PagePDto();


        pagePDto.setUserCode(user.getUserCode());
        pagePDto.setUserName(user.getName());
        pagePDto.setMajor(user.getMajor().getName());
        pagePDto.setCollege(user.getMajor().getCollege().getType());
        pagePDto.setU_type(user.getType());


        pagePDto.setLectureDtoList(lec);
        pagePDto.setAcademicCalendar(res);

        return pagePDto;
    }
}
