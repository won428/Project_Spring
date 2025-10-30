package com.secondproject.secondproject.service;

import com.secondproject.secondproject.repository.SubmitAsgmtRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmitAsgmtService {

    private final SubmitAsgmtRepository submitAsgmtRepository;


}
