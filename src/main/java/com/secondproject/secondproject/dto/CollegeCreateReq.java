package com.secondproject.secondproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CollegeCreateReq {
    @NotBlank(message = "계열은 필수입니다.")
    @Size(min = 2, max = 50, message = "계열은 2~50자여야 합니다.")
    @Pattern(
            // 한글/영문/숫자 + 공백 + 흔히 쓰는 기호(· - & ( ) /)만 허용
            regexp = "^[\\p{L}\\p{N}·&()\\-\\/ ]+$",
            message = "계열에는 한글·영문·숫자와 공백, · - & ( ) /만 사용할 수 있습니다."
    )
    private String c_type; // 계열

    @Pattern(regexp = "^(|\\d{2,3}-\\d{3,4}-\\d{4})$",
            message = "전화번호는 빈 값이거나 00(0)-000(0)-0000 형식이어야 합니다.")
    @Size(max = 20)
    private String c_office; // 행정실 번호

}
