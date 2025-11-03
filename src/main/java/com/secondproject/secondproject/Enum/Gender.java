package com.secondproject.secondproject.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String label;

    Gender(String label) {this.label = label;}

    @JsonValue
    public String getLabel(){return label;} // 응답에 "남자"/"여자"로 나감

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Gender from(Object raw) {
        if (raw == null) return null;
        String v = String.valueOf(raw).trim().toUpperCase(); // 받은 문자열을 공백제거해서 대문자로 변환
        switch (v) {
            case "여자", "FEMALE": return FEMALE;
            case "남자", "MALE": return MALE;
        }
        throw new IllegalArgumentException("성별은 '여자' 또는 '남자'여야 합니다.");
    }
}
