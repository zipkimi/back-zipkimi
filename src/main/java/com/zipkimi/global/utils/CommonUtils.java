package com.zipkimi.global.utils;

import java.util.Random;

/**
 * 공통적으로 사용하는 유틸 클래스
 */
public class CommonUtils {

    private static final Random random = new Random();

    // 난수로 인증번호 생성
    public static String generateNumber(int len, int dupCd) {

        //난수가 저장될 변수
        StringBuilder numStr = new StringBuilder();

        for (int i = 0; i < len; i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(random.nextInt(10));

            if (dupCd == 1) {
                //중복 허용시 numStr 변수에 append
                numStr.append(ran);
            } else if (dupCd == 2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if (!numStr.toString().contains(ran)) {
                    //중복된 값이 없으면 numStr 변수에  append
                    numStr.append(ran);
                } else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i -= 1;
                }
            }
        }
        return numStr.toString();
    }

}
