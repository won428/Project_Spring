package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

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
        return (root, query, criteriaBuilder) -> {
            if(searchGender == null || searchGender.isBlank()){
                return criteriaBuilder.conjunction();
            }else {
                return criteriaBuilder.equal(root.get("gender"),searchGender);
            }
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
        return (root, query, criteriaBuilder) -> {
          return criteriaBuilder.like(root.get("phone"),"%"+searchKeyword+"%");
        };
    }
}
