package com.secondproject.secondproject.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service

public class FileAttachmentService {

    @Transactional
    public void createFile() {
            
    }


    @Transactional //Transaction 으로 덩어리로 관리
    public void deleteFile() {


    }


}
