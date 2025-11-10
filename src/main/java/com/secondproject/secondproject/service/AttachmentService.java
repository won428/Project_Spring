package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.DownloadFile;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.publicMethod.DownloadForProject;
import com.secondproject.secondproject.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${lectureVid.upload-dir}")
    private String vidDir;

    @Value("${image.upload-dir}")
    private String imageDir;

    private final AttachmentRepository attachmentRepository;

    public Attachment save(MultipartFile file, User user) throws IOException {
        String storedKey = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 스토리지키 생성 + 뒤에 오리지널 이름 붙임


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


    public Attachment saveVid(MultipartFile file, User user) throws IOException {
        String storedKey = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 스토리지키 생성 + 뒤에 오리지널 이름 붙임


        Path savePath = Paths.get(vidDir + storedKey);
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

    public Attachment savedImage(MultipartFile file, User user) throws IOException {
        String storedKey = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 스토리지키 생성 + 뒤에 오리지널 이름 붙임


        Path savePath = Paths.get(imageDir + storedKey);
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

    public Optional<Attachment> findByStoredKey(String storedKey) {
        return attachmentRepository.findByStoredKey(storedKey);
    }

    // 파일 다운로드(매개변수 파일 id)
    public DownloadFile downloadFile(Long id) {
        Attachment attachment = this.attachmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        DownloadFile downloadFile = DownloadForProject.download(attachment, uploadDir);

        return downloadFile;
    }

    // 단일 파일 삭제
    public void deleteFile(String storedKey) {
        if (storedKey == null || storedKey.isEmpty()) {
            return; // 삭제할 키가 없음
        }
        Path filePath = Paths.get(vidDir).resolve(storedKey).normalize();
        try {
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 실패 ");
        }
    }

}
