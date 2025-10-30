package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserListSearchDto {

    Long searchMajor;
    String searchGender;
    UserType searchUserType;
    String searchMode;
    String searchKeyword;

}
