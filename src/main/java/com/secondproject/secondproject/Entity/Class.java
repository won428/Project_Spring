package com.secondproject.secondproject.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

@Getter
@Setter
@ToString
@Entity
@Table(name = "class")
public class Class {
    // 강의 테이블

    private long class_id; // 강의코드

    private String cl_name; // 강의명

    private String cl_sche; // 강의 계획서 (파일 업로드 기능이라 매핑테이블이랑 조인 필요합니다.)



}
