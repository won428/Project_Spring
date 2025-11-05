package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Gender;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name; // 이름
    @Size(max = 255, message = "비밀번호는 255자 이하로 입력하세요.")
    private String password; // 비밀번호

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "생년월일은 필수 입력 사항입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    private LocalDate birthdate; // 생년월일 (date 타입)

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255, message = "이메일은 255자 이하로 입력하세요.")
    private String email; // 이메일

    @NotBlank(message = "휴대전화는 필수 입력 사항입니다.")
    @Pattern(regexp = "^(01[0-9])[-]?[0-9]{3,4}[-]?[0-9]{4}$",
            message = "휴대전화 번호 형식이 올바르지 않습니다.")
    private String phone; // 휴대전화


    @NotNull(message = "성별을 선택해주세요.")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "소속 학과는 필수 선택입니다.")
    private Long major; // 소속학과ID (number, FK)

    @NotNull(message = "사용자 구분을 선택해주세요.")
    private UserType type; // 구분: 학생, 교수, 관리자 (enum)

    private String majorName;
    private String college;
    private Long userCode;



}