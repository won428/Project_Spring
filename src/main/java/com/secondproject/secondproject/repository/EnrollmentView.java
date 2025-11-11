package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.Enum.CompletionDiv;
import com.secondproject.secondproject.Enum.Status;

public interface EnrollmentView {
    Long getEnrollmentId();
    Long getLectureId();
    String getLectureName();
    String getUserName();
    Integer getCredit();
    com.secondproject.secondproject.Enum.CompletionDiv getCompletionDiv();
    String getMajorName();
}
