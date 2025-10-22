package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.UserDto;
import com.secondproject.secondproject.dto.UserListDto;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.service.MajorService;
import com.secondproject.secondproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MajorService majorService;

    @PostMapping("/signup")
    public ResponseEntity<?> insertUser(@RequestBody UserDto userinfo){
        User user = new User();
        Major major = this.majorService.findMajor(userinfo.getMajor());

        user.setUName(userinfo.getU_name());
        user.setGender(userinfo.getGender());
        user.setEmail(userinfo.getEmail());
        user.setBirthDate(userinfo.getBirthdate());
        user.setPassword(userinfo.getPassword());
        user.setMajor(major);
        user.setPhone(userinfo.getPhone());
        user.setUType(userinfo.getU_type());

        this.userService.insertUser(user);

        return ResponseEntity.ok(200);
    }

    @GetMapping("/list")
    public List<UserListDto> userList(){
        List<UserListDto> userList = this.userService.findUserList();

        return userList;
    }


}
