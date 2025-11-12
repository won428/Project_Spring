package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.LectureNotice;
import com.secondproject.secondproject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToDoListDto {
    private Long user_code; // 유저 번호

    private String username;

    private String major; // 소속학과ID (number, FK)

    private String college; // 소속 단과대학

    private UserType u_type; // 구분: 학생, 교수, 관리자 (enum)

    private Page<AssignmentDto> assignmentDto;

    private Page<LectureNoticeListDto> listDto;

    public static ToDoListDto fromEntity(
            User user,
            Page<LectureNoticeListDto> map,
            Page<AssignmentDto> mapAS
    ) {
        ToDoListDto dto = new ToDoListDto();

        dto.setUser_code(user.getUserCode());
        dto.setUsername(user.getName());
        dto.setMajor(user.getMajor().getName());
        dto.setCollege(user.getMajor().getCollege().getType());
        dto.setU_type(user.getType());
        dto.setListDto(map);
        dto.setAssignmentDto(mapAS);

        return dto;
    }


}
