//package com.zipkimi.user.controller;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//import com.zipkimi.user.dto.request.FindIdVerifySmsRequest;
//import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
//import com.zipkimi.user.dto.response.SendFindSmsAuthNumberPostResponse;
//import com.zipkimi.user.service.UserLoginService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ExtendWith(MockitoExtension.class)
//class UserLoginControllerTest {
//
//    @Mock
//    private UserLoginService loginService;
//
//    @InjectMocks
//    private UserLoginController loginController;
//
//    // ************* 아이디 찾기 *************
//
//    @Test
//    void sendFindIdSmsAuthNumberSuccessTest(){
//
//        //given
//        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest("01094342762");
//        SendFindSmsAuthNumberPostResponse expectedResponse = new SendFindSmsAuthNumberPostResponse("01094342762");
//
//        when(loginService.sendFindIdSmsAuthNumber(requestDto)).thenReturn(expectedResponse);
//
//        //when
//        ResponseEntity<SendFindSmsAuthNumberPostResponse> responseEntity = loginController.sendFindIdSmsAuthNumber(requestDto);
//
//        //then
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(expectedResponse, responseEntity.getBody());
//    }
//
//    @Test
//    void sendFindIdSmsAuthNumberFailureTest() {
//
//        //given
//        String unregisteredPhoneNumber = "01094342762";
//        SmsAuthNumberPostRequest requestDto = new SmsAuthNumberPostRequest(unregisteredPhoneNumber);
//
//        when(loginService.sendFindIdSmsAuthNumber(requestDto)).thenReturn(
//                SendFindSmsAuthNumberPostResponse.builder()
//                        .result("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
//                        .build()
//        );
//
//        //when
//        ResponseEntity<SendFindSmsAuthNumberPostResponse> response = loginController.sendFindIdSmsAuthNumber(requestDto);
//        System.out.println("response = " + response.getBody().getResult());
//
//        //then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody().getResult());
//
//    }
//
//    @Test
//    void verifySmsAuthAndFindIdSuccessTest() {
//
//        // given
//        FindIdVerifySmsRequest requestDto = new FindIdVerifySmsRequest("01012345678", "1234");
//        when(loginService.verifySmsAuth("01012345678", "1234")).thenReturn(true);
//        when(loginService.getEmailByPhoneNumber("01012345678")).thenReturn("example@example.com");
//
//        // when
//        ResponseEntity<String> response = loginController.verifySmsAuthAndFindId(requestDto);
//        System.out.println("testVerifySmsAuthAndFindId_Success : " + response);
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("회원님의 아이디는 'example@example.com' 입니다.", response.getBody());
//    }
//    @Test
//    void verifySmsAuthAndFindIdFailureTest() {
//
//        // given
//        FindIdVerifySmsRequest requestDto = new FindIdVerifySmsRequest("01012345678", "1111");
//        when(loginService.verifySmsAuth("01012345678", "1111")).thenReturn(false);
//
//        // when
//        ResponseEntity<String> response = loginController.verifySmsAuthAndFindId(requestDto);
//        System.out.println("testVerifySmsAuthAndFindId_Failure: " + response);
//
//        // then
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertEquals("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.", response.getBody());
//    }
//
//    @Test
//    void verifySmsAuthAndFindIdNotFoundTest() {
//
//        // given
//        FindIdVerifySmsRequest requestDto = new FindIdVerifySmsRequest("01012345678", "0000");
//        when(loginService.verifySmsAuth("01012345678", "0000")).thenReturn(true);
//        when(loginService.getEmailByPhoneNumber("01012345678")).thenReturn(null);
//
//        //when
//        ResponseEntity<String> response = loginController.verifySmsAuthAndFindId(requestDto);
//        System.out.println("testVerifySmsAuthAndFindId_NotFound: " + response);
//
//        // then
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)", response.getBody());
//    }
//
//}