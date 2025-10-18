package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Entity.Member;
import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.MemberRepository;
import com.secondproject.secondproject.repository.RecordStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final RecordStatusRepository recordStatusRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository, RecordStatusRepository recordStatusRepository) {
        this.memberRepository = memberRepository;
        this.recordStatusRepository = recordStatusRepository;
    }

    public Member getUserById(Long id) {
        Member member = memberRepository.findByUid(id).orElse(null);
        if (member == null) {
            // throw new RuntimeException("해당 유저 없음");
            return null;
        }
        return member;
    }

    public StatusRecords getStatusRecordById(Long statusId) {
        StatusRecords status = recordStatusRepository.findBySid(statusId).orElse(null);
        if (status == null) {
            // throw new RuntimeException("해당 학적 정보 없음");
            return null;
        }
        return status;
    }

    public Object issueCertificate(Long id, String type) {
        // 1. 유저 조회
        Member member = getUserById(id);
        if (member == null || member.getU_type() != UserType.STUDENT) {
            return Map.of("error", "학생 전용 서비스입니다.");
        }

        // 2. 학적 정보(RecordStatus) 조회
        StatusRecords sr = getStatusRecordById(member.getStatus_id());
        if (sr == null) {
            return Map.of("error", "학적 정보가 없습니다.");
        }
        boolean isGraduated = "졸업".equals(sr.getStudent_status());

        // 3. 졸업생 여부 체크
        if (!isGraduated) {
            return Map.of("error", "재학생/휴학생은 증명서 발급 메뉴에서 신청하세요.");
        }

        // 4. 증명서 종류 확인
        if (!List.of("등록금납부내역서", "성적증명서", "장학수혜내역서").contains(type)) {
            return Map.of("error", "졸업생만 발급 가능한 증명서 종류입니다.");
        }

        // 5. 실제 증명서 발급 로직 (여기선 단순 성공 메시지 반환)
        return Map.of("message", type + " 발급 완료 (유저ID: " + id + ")");
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
