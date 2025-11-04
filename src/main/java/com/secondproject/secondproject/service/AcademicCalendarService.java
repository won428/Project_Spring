package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.AcademicCalendarDto;
import com.secondproject.secondproject.entity.AcademicCalendar;
import com.secondproject.secondproject.repository.AcademicCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicCalendarService {

    private final AcademicCalendarRepository academicCalendarRepository;


    public List<AcademicCalendar> getList() {
        List<AcademicCalendar> list = academicCalendarRepository.findAll();
        return list;

    }
}
