package com.zipkimi.global.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 정규식을 사용하는 유틸리티 클래스
 */
public class RegexUtils {

    public static boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        boolean err = false;
        String regex = "^\\d{3}\\d{3,4}\\d{4}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phoneNumber);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidName(String name) {
        boolean err = false;
        // 한글, 영문
        String regex = "^[a-zA-Z가-힣\\s]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        if (m.matches()) {
            err = true;
        }
        return err;
    }


    public static boolean isValidPassword(String password) {
        boolean err = false;
        // 영문, 숫자, 특수문자 조합 8~16자
        String regex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (m.matches()) {
            err = true;
        }
        return err;
    }


    // 한글 이름이면 띄어쓰기 삭제, 영문 이름은 띄어쓰기 유지
    public static String getFormatName(String name) {
        String koreanRegex = "^[가-힣]*$"; // 한글
        Pattern p = Pattern.compile(koreanRegex);
        Matcher m = p.matcher(name);
        if (m.matches()) {
            name = name.replace(" ", "");
        }
        return name;
    }

}
