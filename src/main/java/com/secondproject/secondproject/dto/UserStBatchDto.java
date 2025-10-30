package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStBatchDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
    private String name;

    @NotNull(message = "생년월일은 필수 입력 사항입니다.")
    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotNull(message = "성별을 선택해주세요.")
    private String gender;

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255, message = "이메일은 255자 이하로 입력하세요.")
    private String email;

    @NotBlank(message = "휴대전화는 필수 입력 사항입니다.")
    @Pattern(regexp = "^(01[0-9])[-]?[0-9]{3,4}[-]?[0-9]{4}$",
            message = "휴대전화 번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotNull(message = "소속 학과는 필수 선택입니다.")
    @Positive(message = "소속 학과 ID가 올바르지 않습니다.")
    private Long majorId;

    private List<String> errors = new ArrayList<>();

    private boolean valid;

    // userType, pw(전화번호로 강제) 서비스에서 강제할당, id는 자동증가
}


