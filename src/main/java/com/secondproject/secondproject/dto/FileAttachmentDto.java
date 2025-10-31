package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.FileType;
import com.secondproject.secondproject.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FileAttachmentDto {
    private List<MultipartFile> files;
    private FileType fileType;
    private Long id;
    private User user;

}
