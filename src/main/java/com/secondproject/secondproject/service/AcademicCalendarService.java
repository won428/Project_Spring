package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.AcademicCalendarDto;
import com.secondproject.secondproject.entity.AcademicCalendar;
import com.secondproject.secondproject.repository.AcademicCalendarRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicCalendarService {

    private final AcademicCalendarRepository academicCalendarRepository;


    public List<AcademicCalendar> getList() {
        List<AcademicCalendar> list = academicCalendarRepository.findAll();
        return list;

    }

    public Page<AcademicCalendar> getListByMonth(int year, int month, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "calStartDate"));
        LocalDate calStartDate = LocalDate.of(year, month, 1);
        LocalDate calEndDate = calStartDate.plusMonths(1).minusDays(1);

        return academicCalendarRepository.findByCalStartDateBetween(calStartDate, calEndDate, pageable);

    }

    public void deleteById(Long id) {
        academicCalendarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없는 일정"));
        academicCalendarRepository.deleteById(id);
    }
}
