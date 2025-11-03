package com.secondproject.secondproject.service;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.AttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final AttachmentRepository attachmentRepository;

    public Attachment save(MultipartFile file, User user) throws IOException {
        String storedKey = UUID.randomUUID() + "_" + file.getOriginalFilename();


        Path savePath = Paths.get(uploadDir + storedKey);
        Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
        //파일을 copy 해서 로컬에 저장
        Attachment attachment = new Attachment();
        attachment.setName(file.getOriginalFilename());
        attachment.setUser(user);
        attachment.setContentType(file.getContentType());
        attachment.setSizeBytes(file.getSize());
        attachment.setStoredKey(storedKey);
        attachment.setSha256(sha256(file.getBytes()));


        return attachmentRepository.save(attachment);
    }

    //
//    public void fileSave(FileAttachmentDto dto) {
//        dto.getFiles();
//        dto.getId();
//        dto.getFileType();
//        dto.getUser();
//
//        attachmentRepository.save("");
//    }
    public static String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not found", e);
        }
    }


    public Optional<Attachment> findById(Long attachmentId) {
        return attachmentRepository.findById(attachmentId);
    }


    public void deleteById(Long id) {
        attachmentRepository.deleteById(id);
    }
}
