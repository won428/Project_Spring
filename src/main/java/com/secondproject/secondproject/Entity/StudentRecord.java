package com.secondproject.secondproject.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.Student_status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter @Setter @ToString
@Entity
@Table(name = "student_record")
public class StudentRecord {
    private Long record_id;
    private User user;
    private StatusRecords statusRecords;
    private String title;
    private String  content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applied_date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate processed_date;

    @Enumerated(EnumType.STRING)
    private Status process_status;

    @Enumerated(EnumType.STRING)
    private Student_status academic_request;
}
