package com.secondproject.secondproject.dto;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentSearchDto {
    @Column(nullable = true)
    private String searchMode;
    @Column(nullable = true)
    private String searchKeyword;
}
