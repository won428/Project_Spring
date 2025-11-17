package com.secondproject.secondproject.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class PasswordChangeReqDto {

    private String username;
    
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$",
            message = "비밀번호는 8~20자, 영문, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String newPassword;

    @NotBlank(message = "새 비밀번호를 다시 입력해주세요.")
    private String newPasswordConfirm;

}
