package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.entity.SubmitAsgmt;
import com.secondproject.secondproject.service.SubmitAsgmtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subs")
public class SubmitAsgmtController {

    private final SubmitAsgmtService submitAsgmtService;


}
