package com.secondproject.secondproject.publicMethod;

import com.secondproject.secondproject.dto.DownloadFile;
import com.secondproject.secondproject.entity.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Component
public final class DownloadForProject {

    public static DownloadFile download (Attachment attachment, String uploadDir){

        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path path = base.resolve(attachment.getStoredKey()).toAbsolutePath().normalize();

            // path가 잘 만들어졌는지 확인
            if (!path.startsWith(base)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid path");
            }
            //존재 여부 확인 (없으면 404)
            if (!Files.exists(path)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }

            // DB 값만 신뢰해서 사용
            String contentType = attachment.getContentType();

            // 혹시 null/빈문자면 기본값으로만 처리
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            Long size = (attachment.getSizeBytes() != null) ? attachment.getSizeBytes() : Files.size(path);
            InputStream in;
            try {
                in = Files.newInputStream(path, StandardOpenOption.READ);
            } catch (NoSuchFileException e) {
                // 존재 체크 이후 열기 사이의 레이스를 대비해 재확인
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            } catch (AccessDeniedException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
            }

            return DownloadFile.builder()
                    .inputStream(in)
                    .name(attachment.getName())
                    .contentType(contentType)
                    .sizeBytes(size)
                    .sha256(attachment.getSha256())
                    .uploadAt(attachment.getUploadAt())
                    .build();

        }catch (IOException ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
