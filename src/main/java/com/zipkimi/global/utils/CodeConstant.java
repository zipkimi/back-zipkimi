package com.zipkimi.global.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 코드와 관련된 상수 및 Enum 클래스를 관리하는 Constant Class
 */
public class CodeConstant {

    @Getter
    @AllArgsConstructor
    public enum SMS_AUTH_CODE {

        // 가입
        JOIN("join"),

        //아이디 찾기
        FIND_ID("findId"),

        //비밀번호 찾기
        FIND_PW("findPw"),

        //시공사 회원가입
        BUILDER_JOIN("builderJoin");

        final String value;
    }


}
