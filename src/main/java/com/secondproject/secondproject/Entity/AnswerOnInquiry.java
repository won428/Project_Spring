package com.secondproject.secondproject.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "AnswerOnInquiry")
public class AnswerOnInquiry {
    private Long answer_id;
    private Long inquiry_id;
    private String user;
    private String answer_content;




}
