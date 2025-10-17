
package com.example.attachment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //실행 시 database lms 로 table 생성
public class AttachmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttachmentApplication.class, args);
    }
}
