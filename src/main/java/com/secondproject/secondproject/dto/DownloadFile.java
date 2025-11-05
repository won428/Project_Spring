package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.io.InputStream;
import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadFile {
    @JsonIgnore
    private InputStream inputStream;
    @NotNull
    private String name;
    private String contentType;
    private Long sizeBytes;
    private String sha256;
    private LocalDate uploadAt;
}
