package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.AppealDto;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.repository.AppealRepository;
import com.secondproject.secondproject.repository.EnrollmentRepository;
import com.secondproject.secondproject.service.AppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/appeals")
public class AppealController {

    private final AppealService appealService;

    public AppealController(AppealService appealService) {
        this.appealService = appealService;
    }

    @PostMapping
    public AppealDto createAppeal(@RequestBody AppealDto dto) {
        return appealService.createAppeal(dto);
    }
}

