package com.secondproject.secondproject.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

public class UserFieldViolation {
    @Getter @Setter @Builder
    @AllArgsConstructor @NoArgsConstructor
    static class ErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;     // "Bad Request" 등 상태 텍스트
        private String message;   // 사람이 읽을 메시지
        private String path;      // 요청 URI
        private List<FieldViolation> fieldErrors; // 필드 단위 오류(선택)
    }

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class FieldViolation {
        private String field;     // 예: "0.email" (0번째 행의 email)
        private String message;   // 예: "이메일 형식이 올바르지 않습니다."
    }
}
