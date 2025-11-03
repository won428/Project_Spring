package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secondproject.secondproject.Enum.Gender;
import com.secondproject.secondproject.Enum.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserImportDto {

    private Long id;

    // @JsonProperty(access = JsonProperty.Access.READ_ONLY) : 클라이언트에서 보낸 값을 무시하고 null로 채웠다가 서비스에서 강제할당
    // 대신 검증 어노테이션을 무시함, 검증을 안붙이거나 엔터티에서 검증.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userCode;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String passWord;

    @NotNull(message = "생년월일은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank
    private String email;

    @Pattern(regexp = "^(01[0-9])[-]?[0-9]{3,4}[-]?[0-9]{4}$",
            message = "휴대전화번호 형식 오류")
    @NotBlank
    private String phoneNumber;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotNull(message = "학과코드는 필수입니다.")
    private Long majorId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserType type;


}
