package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.UserDto;
import com.secondproject.secondproject.dto.UserListDto;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.service.CollegeService;
import com.secondproject.secondproject.service.MajorService;
import com.secondproject.secondproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MajorService majorService;
    private final CollegeService collegeService;

    @PostMapping("/signup")
    public ResponseEntity<?> insertUser(@RequestBody UserDto userinfo){
        User user = new User();
        Major major = this.majorService.findMajor(userinfo.getMajor());

        user.setName(userinfo.getU_name());
        user.setGender(userinfo.getGender());
        user.setEmail(userinfo.getEmail());
        user.setBirthDate(userinfo.getBirthdate());
        user.setPassword(userinfo.getPassword());
        user.setMajor(major);
        user.setPhone(userinfo.getPhone());
        user.setType(userinfo.getU_type());

        this.userService.insertUser(user);

        return ResponseEntity.ok(200);
    }

    @GetMapping("/list")
    public List<UserListDto> userList(){
        List<UserListDto> userList = this.userService.findUserList();

        return userList;
    }

    @GetMapping("/selectUserCode/{id}")
    public UserListDto findByUsercode(@PathVariable Long id){

        UserListDto userDto = new UserListDto();
        Optional<User> optUser = this.userService.findByUsercode(id);
        User user = optUser
                .orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, id + "사용자 없음"));
        College college = this.collegeService.getCollegeId(user.getMajor().getCollege().getId());

        userDto.setU_name(user.getName());
        userDto.setGender(user.getGender());
        userDto.setMajor(user.getMajor().getName());
        userDto.setCollege(college.getType());
        userDto.setPhone(user.getPhone());
        userDto.setEmail(user.getEmail());
        userDto.setBirthdate(user.getBirthDate());
        userDto.setPassword(user.getPassword());
        userDto.setUser_code(user.getUserCode());
        userDto.setU_type(user.getType());


        return userDto;
    }
}
