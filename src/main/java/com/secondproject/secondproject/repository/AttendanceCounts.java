package com.secondproject.secondproject.repository;

public interface AttendanceCounts {
    Long getTotal();
    Long getPresent();
    Long getLate();
    Long getEarlyLeave();
    Long getAbsent();
    Long getExcused();
}
