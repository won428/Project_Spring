package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.CompletionDiv;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.repository.MajorRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
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

    public static Specification<Lecture> hasLecMajor(Long searchMajor) {
        return (root, query, criteriaBuilder) -> {

            if(searchMajor == null){
                return criteriaBuilder.conjunction();
            }else {
                return criteriaBuilder.equal(root.get("major").get("id"),searchMajor);
            }
        };
    }

    public static Specification<Lecture> hasLecLevel(Integer searchLevel) {
        return (root, query, cb) -> {
            if (searchLevel == null || searchLevel == 0) return cb.conjunction(); // 0을 '전체'로 쓴다면 유지
            return cb.equal(root.get("level"), searchLevel);
        };
    }

    public static Specification<Lecture> hasLecCredit(Integer searchCredit) {
        return (root, query, criteriaBuilder) -> {
            if (searchCredit == null || searchCredit == 0) {
                return criteriaBuilder.conjunction();
            }else {
                return criteriaBuilder.equal(root.get("credit"),searchCredit);
            }
        };
    }


    public static Specification<Lecture> hasLecCompletionDiv(CompletionDiv searchCompletionDiv) {
        return (root, query, criteriaBuilder) -> {
          if(searchCompletionDiv == null){
              return criteriaBuilder.conjunction();
          }else {
              return criteriaBuilder.equal(root.get("completionDiv"),searchCompletionDiv);
          }
        };
    }

    public static Specification<Lecture> hasLecYear(String searchYear) {
        return (root, query, cb) -> {
            if (searchYear == null || searchYear.isBlank()) return cb.conjunction();
            try {
                Integer y = Integer.valueOf(searchYear);
                return cb.equal(cb.function("year", Integer.class, root.get("startDate")), y);
            } catch (NumberFormatException e) {
                return cb.disjunction(); // 잘못된 값이면 매칭 없음
            }
        };
    }

    public static Specification<Lecture> hasLecStartDate(String searchStartDate) {
        return (root, query, cb) -> {
            if (searchStartDate == null || searchStartDate.isBlank()) return cb.conjunction();
            try {
                Integer month = Integer.valueOf(searchStartDate);
                return cb.equal(cb.function("month", Integer.class, root.get("startDate")), month);
            } catch (NumberFormatException e) {
                return cb.disjunction();
            }
        };
    }

    public static Specification<Lecture> hasScheduleDay(DayOfWeek day) {
        return (root, query, cb) -> {
            if (day == null) return cb.conjunction(); // 필터 미사용

            Subquery<Long> sq = query.subquery(Long.class);
            Root<LectureSchedule> s = sq.from(LectureSchedule.class);
            sq.select(cb.literal(1L))
                    .where(
                            cb.equal(s.get("lecture"), root),  // 자식의 부모참조 == 바깥 Lecture
                            cb.equal(s.get("day"), day)        // 요일 조건
                    );

            return cb.exists(sq);
        };
    }

    public static Specification<Lecture> hasLecUser(Long searchUser) {
        return (root, query, criteriaBuilder) -> {
            if(searchUser == null){
                return criteriaBuilder.conjunction();
            }  else {
                return criteriaBuilder.equal(root.get("user").get("id"),searchUser);
            }
        };
    }

    public static Specification<Lecture> hasLecName(String keyword) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + keyword.trim().toLowerCase() + "%");
    }

    public static Specification<Lecture> hasLecProfessorName(String keyword) {
        return (root, query, cb) -> {
            Join<Lecture, User> user = root.join("user", JoinType.LEFT);
            query.distinct(true);
            return cb.like(cb.lower(user.get("name")), "%" + keyword.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Lecture> hasLecMajorName(String keyword) {
        return (root, query, cb) -> {
            Join<Lecture, Major> major = root.join("major", JoinType.LEFT);
            query.distinct(true);
            return cb.like(cb.lower(major.get("name")), "%" + keyword.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Lecture> keywordInAllLecture(String keyword) {
        return (root, query, cb) -> {
            String q = "%" + keyword.trim().toLowerCase() + "%";
            Join<Lecture, User> user = root.join("user", JoinType.LEFT);
            Join<Lecture, Major> major = root.join("major", JoinType.LEFT);
            query.distinct(true);
            return cb.or(
                    cb.like(cb.lower(root.get("name")), q),          // 강의명
                    cb.like(cb.lower(user.get("name")), q),          // 교수명
                    cb.like(cb.lower(major.get("name")), q)          // 전공명
            );
        };
    }

    public static Specification<Lecture> hasLecStatus(Status searchStatus) {
        return (root, query, cb) -> searchStatus == null ? cb.conjunction() : cb.equal(root.get("status"), searchStatus);
    }
}
