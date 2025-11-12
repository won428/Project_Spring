package com.secondproject.secondproject.repository;

import com.secondproject.secondproject.entity.AcademicCalendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {
    Page<AcademicCalendar> findByCalStartDateBetween(LocalDate start, LocalDate end, Pageable pageable);
}
