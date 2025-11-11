package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.MajorRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PublicSpecification {

    // 유저 학과 검색 조건
    public static Specification<User> hasMajor(Long major){
        return (root, query, criteriaBuilder) -> {

            if(major == null){
                return criteriaBuilder.conjunction();
            }else {
                return criteriaBuilder.equal(root.get("major").get("id"),major);
            }
        };
    }

    // 유저 타입 검색 조건
    public static Specification<User> hasUserType(UserType searchUserType) {
        return (root, query, criteriaBuilder) -> {
            if(searchUserType == null){
                return criteriaBuilder.conjunction();
            }else {
                return criteriaBuilder.equal(root.get("type"),searchUserType);
            }
        };
    }


    public static Specification<User> hasGender(String searchGender) {
        return (root, query, cb) -> {
            if (searchGender == null || searchGender.isBlank()) {
                return cb.conjunction(); // ✅ 조건 추가 안 함
            }
            // Gender 가 Enum 이라면:
            try {
                var g = com.secondproject.secondproject.Enum.Gender.valueOf(searchGender);
                return cb.equal(root.get("gender"), g);
            } catch (Exception e) {
                return cb.disjunction(); // 잘못 온 값은 매치 없음
            }
        };
    }



    public static Specification<User> keywordInAll(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction(); // ← 중요!

            String like = "%" + keyword.toLowerCase() + "%";
            String digitsOnly = keyword.replaceAll("\\D", "");

            var major = root.join("major", JoinType.LEFT);
            var college = major.join("college", JoinType.LEFT);

            var preds = new ArrayList<Predicate>();
            preds.add(cb.like(cb.lower(root.get("name")), like));
            preds.add(cb.like(cb.lower(root.get("email")), like));

            // userCode(Long) → String으로 캐스팅
            preds.add(cb.like(cb.lower(root.get("userCode").as(String.class)), like));

            // Enum → String으로 캐스팅 (원치 않으면 제거)
            preds.add(cb.like(cb.lower(root.get("gender").as(String.class)), like));
            preds.add(cb.like(cb.lower(root.get("type").as(String.class)), like));

            // phone: 숫자 키워드가 있으면 하이픈 제거본 사용
            if (!digitsOnly.isEmpty()) {
                var phoneDigits = cb.function("replace", String.class,
                        root.get("phone"), cb.literal("-"), cb.literal(""));
                preds.add(cb.like(phoneDigits, "%" + digitsOnly + "%"));
            } else {
                preds.add(cb.like(cb.lower(root.get("phone")), like));
            }

            preds.add(cb.like(cb.lower(major.get("name")), like));
            preds.add(cb.like(cb.lower(college.get("type")), like));

            return cb.or(preds.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> hasName(String searchKeyword) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("name"),"%"+searchKeyword+"%");
        };
    }



    public static Specification<User> hasEmail(String searchKeyword) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("email"),"%"+searchKeyword+"%");
        };
    }

    public static Specification<User> hasPhone(String searchKeyword) {
        return (root, query, cb) -> {
            if (searchKeyword == null || searchKeyword.isBlank()) return cb.conjunction();

            String digits = searchKeyword.replaceAll("\\D", "");
            if (!digits.isEmpty()) {
                var phoneDigits = cb.function(
                        "replace", String.class,
                        root.get("phone"), cb.literal("-"), cb.literal("")
                );
                return cb.like(phoneDigits, "%" + digits + "%");
            } else {
                return cb.like(cb.lower(root.get("phone")), "%" + searchKeyword.toLowerCase() + "%");
            }
        };
    }

    public static Specification<User> hasCollege(Long collegeId) {
        return (root, query, cb) -> {
            if (collegeId == null) return cb.conjunction();
            // user -> major -> college (LEFT JOIN)
            var major = root.join("major", JoinType.LEFT);
            var college = major.join("college", JoinType.LEFT);
            return cb.equal(college.get("id"), collegeId);
        };
    }

    public static Specification<User> hasLevel(Integer searchLevel) {
        return (root, query, cb) -> {
            if (searchLevel == null || searchLevel == 0) return cb.conjunction();

            Subquery<Long> sq = query.subquery(Long.class);
            Root<StatusRecords> sr = sq.from(StatusRecords.class);
            sq.select(sr.get("id"))
                    .where(
                            cb.equal(sr.get("user"), root),       // 상태레코드의 유저 == 바깥 User
                            cb.equal(sr.get("level"), searchLevel)
                    );

            return cb.exists(sq);
        };
    }

}
