package com.secondproject.secondproject.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CollegeUpdateReq {
    @Size(min=2,max=50)
    String c_type;   // nullable

    @Pattern(regexp="^(|\\d{2}-\\d{4}-\\d{4})$")
    String c_office; // nullable
}
