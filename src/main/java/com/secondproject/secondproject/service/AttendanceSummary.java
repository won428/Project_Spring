package com.secondproject.secondproject.service;

public record AttendanceSummary(long total, long present, long late, long earlyLeave, long absent, long excused, double score) {
}
