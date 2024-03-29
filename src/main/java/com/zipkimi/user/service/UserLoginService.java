package com.zipkimi.user.service;

import static com.zipkimi.global.utils.CommonUtils.generateNumber;

import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.entity.UserRole;
import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.jwt.JwtTokenProvider;
import com.zipkimi.global.jwt.dto.request.TokenRequest;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import com.zipkimi.global.jwt.entity.RefreshTokenEntity;
import com.zipkimi.global.jwt.repository.RefreshTokenRepository;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.global.utils.CodeConstant.SMS_AUTH_CODE;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.UserLoginRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class UserLoginService {

    //임시 비밀번호 발급
    private static final String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    private final UserRepository userRepository;
    private final SmsAuthRepository smsAuthRepository;

    /* JWT Refresh 토큰 Repository */
    private final RefreshTokenRepository refreshTokenRepository;
    private final SmsService smsService;

    /* JWT 관련 */
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // ************* 로그인 테스트를 위한 간단 일반 회원 가입 *************

    @Transactional
    public BaseResponse simpleJoinTest(UserLoginRequest userLoginRequest) {

        // TODO 회원가입 로직 개발 후 삭제 필요
        // TODO 로그인 테스트를 위한 간단 일반 회원 가입 로직
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(userLoginRequest.getEmail());
        if (userEntityOptional.isPresent()) {
            return BaseResponse.builder()
                    .message("이미 가입한 회원입니다.")
                    .build();
        }

        // 비밀번호 암호화 적용
        String encodePw = passwordEncoder.encode(userLoginRequest.getPassword());

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userLoginRequest.getEmail());
        userEntity.setPassword(encodePw);
        userEntity.setRole(UserRole.ROLE_USER);
        userEntity.setPhoneNumber("01094342762");

        userRepository.save(userEntity);

        return BaseResponse.builder()
                .message("일반 회원 가입 테스트에 성공했습니다.")
                .build();
    }

    // ************* 로그인 *************

    @Transactional
    public TokenResponse login(UserLoginRequest userLoginRequest) {

        // #1. 회원 정보가 존재하는지 확인 (이메일을 통해서)
        Optional<UserEntity> user = userRepository.findByEmail(userLoginRequest.getEmail());

        // 회원 정보 존재 여부 확인 - 회원 정보 없으면 예외 처리
        // 회원 패스워드 일치 여부 확인 - 일치하지 않으면 예외 처리
        if(user.isEmpty() || !user.get().getEmail().equals(userLoginRequest.getEmail()) ||
            !passwordEncoder.matches(userLoginRequest.getPassword(), user.get().getPassword())){
            return TokenResponse.builder()
                    .message("가입하지 않은 이메일이거나 잘못된 비밀번호입니다.")
                    .build();
        }

        // #2. 로그인 된 Email (ID) / PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = userLoginRequest.toAuthentication();
        log.info("login authenticationToken = " + authenticationToken);
        log.info("login authenticationToken.getName() = " + authenticationToken.getName());

        // #3. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        // authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("login authentication = " + authentication);
        log.info("login authentication.getCredentials() = " + authentication.getCredentials());

        // #4. 인증 정보를 기반으로 JWT 토큰 생성
        TokenResponse tokenResponse = jwtTokenProvider.createToken(authentication);
        log.info("login tokenResponse = " + tokenResponse);

        // #5. 이전의 RefreshToken을 DB에서 모두 삭제
        refreshTokenRepository.deleteAll();

        // #6. RefreshToken을 DB에 저장 (식별을 위해 userId도 함께 저장)
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userId(user.get().getUserId())
                .token(tokenResponse.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);

        // #7. 토큰 발급
        return TokenResponse.builder()
                .message("로그인에 성공하였습니다.")
                .grantType(tokenResponse.getGrantType())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .accessTokenExpireDate(tokenResponse.getAccessTokenExpireDate())
                .build();
    }


    // ************* 토큰 재발급 *************

    @Transactional
    public TokenResponse reissue(TokenRequest tokenRequest) {

        // #1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(tokenRequest.getRefreshToken())) {
            return TokenResponse.builder()
                    .message("Refresh Token이 유효하지 않습니다.")
                    .build();
        }

        // #2. Access Token 에서 User ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequest.getAccessToken());
        log.info("=========================== !!! reissue authentication = " + authentication);
        log.info("=========================== !!! reissue authentication.getCredentials() = " + authentication.getCredentials());
        log.info("=========================== !!! reissue authentication.getAuthorities() = " + authentication.getAuthorities());
        log.info("=========================== !!! reissue authentication.getDetails() = " + authentication.getDetails());
        log.info("=========================== !!! reissue authentication.getName() = " + authentication.getName());

        // #3. 저장소에서 User ID 를 기반으로 Refresh Token 값 가져옴
        Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByUserId(
                        Long.valueOf(authentication.getName()));

        if(refreshToken.isEmpty()){
            return TokenResponse.builder()
                    .message("로그아웃 된 사용자입니다.")
                    .build();
        }

        log.info("=========================== !!! reissue refreshToken = " + refreshToken);

        // #4. Refresh Token 일치하는지 검사
        if (!refreshToken.get().getRefreshToken().equals(tokenRequest.getRefreshToken())) {
            log.info("reissue refreshToken.getRefreshToken() = " + refreshToken.get().getRefreshToken());
            log.info("reissue tokenRequest.getRefreshToken() = " + tokenRequest.getRefreshToken());
            return TokenResponse.builder()
                    .message("토큰의 유저 정보가 일치하지 않습니다.")
                    .build();
        }

        // #5. 새로운 토큰 생성
        TokenResponse tokenDto = jwtTokenProvider.createToken(authentication);

        // #6. 저장소 정보 업데이트
        RefreshTokenEntity newRefreshToken = refreshToken.get().updateToken(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // #7. accessToken과 refreshToken 모두 재발행
        return TokenResponse.builder()
                .message("토큰 재발급에 성공하였습니다.")
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .accessTokenExpireDate(tokenDto.getAccessTokenExpireDate())
                .build();
    }

    // ************* 로그아웃 *************

    public TokenResponse logout(String refreshToken) {

        // #1. 로그아웃 시 기존의 RefreshToken을 삭제함.
        refreshTokenRepository.findByRefreshToken(refreshToken).ifPresent(refreshTokenRepository::delete);

        return TokenResponse.builder()
                .message("로그아웃 되었습니다.")
                .build();
    }


    // ************* 아이디 찾기 *************

    // 아이디 찾기 - SMS 인증번호 전송
    public FindSmsAuthNumberPostResponse sendFindIdSmsAuthNumber(
            SmsAuthNumberPostRequest requestDto) {

        // 휴대폰 번호 유효성 검사 - 타입, 글자수
        String phoneNumber = requestDto.getPhoneNumber().replaceAll("\\D", "");
        if (phoneNumber.length() != 11) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("휴대폰 번호를 정확히 입력해주세요.")
                    .build();
        }

        log.info("phoneNumber = " + phoneNumber);

        // 휴대폰 번호로 일치하는 회원 조회 (회원 사용여부 : 활성화)
        Optional<UserEntity> user = userRepository.findByPhoneNumberAndIsUseIsTrue(phoneNumber);

        if (user.isEmpty()) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        // 휴대폰번호/인증완료여부/만료시간만료여부/인증타입으로 만료되지 않은 인증번호 있는지 조회
        SmsAuthEntity existingSmsAuth =
                smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(phoneNumber, LocalDateTime.now(), SMS_AUTH_CODE.FIND_ID.getValue());

        String randomNumber;
        SmsAuthEntity smsAuthEntitySaved = null;

        if (existingSmsAuth != null) {
            // SMS 인증번호가 만료되지 않았을 경우
            existingSmsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuthRepository.save(existingSmsAuth);
            return FindSmsAuthNumberPostResponse.builder()
                    .message("유효시간이 만료되지 않은 인증번호가 존재합니다. \n인증번호를 확인하거나, 유효시간이 지난 후 다시 시도해주세요.")
                    .build();
        } else {
            // 인증번호 생성 : 4자리(중복 x)
            randomNumber = generateNumber(4, 2);

            SmsAuthEntity smsAuth = new SmsAuthEntity();
            smsAuth.setPhoneNumber(phoneNumber);
            smsAuth.setSmsAuthNumber(randomNumber);
            smsAuth.setIsAuthenticate(false);
            smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuth.setSmsAuthType(SMS_AUTH_CODE.FIND_ID.getValue());

            // SMS 내용 설정
            smsAuth.setContent("[집킴이] 아이디 찾기 인증번호는 [" + randomNumber + "] 입니다. 인증번호를 정확히 입력해주세요.");

            try {
                // DB 테이블에 insert
                smsAuthEntitySaved = smsAuthRepository.save(smsAuth);

                // SMS 전송 로직
                smsService.pushSMSMessage(smsAuthEntitySaved);
            } catch (Exception e) {
                throw new BadRequestException("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.");
            }
        }

        return FindSmsAuthNumberPostResponse.builder()
                .message("인증번호를 전송하였습니다.")
                .build();
    }

    // 아이디 찾기 - SMS 인증번호 검증 & 휴대폰 번호로 가입된 회원 이메일 조회
    public FindSmsAuthNumberGetResponse checkFindIdSmsAuth(FindIdCheckSmsGetRequest requestDto) {

        // #1. SMS 인증번호를 ID를 통해 DB 데이터 검증 후
        Optional<SmsAuthEntity> smsAuth = smsAuthRepository.findById(requestDto.getSmsAuthId());

        if (smsAuth.isEmpty() || !smsAuth.get().getSmsAuthNumber()
                .equals(requestDto.getSmsAuthNumber())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.")
                    .build();
        }

        // 2. SMS 인증번호 만료시간 검증
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = smsAuth.get().getExpirationTime();

        if (currentTime.isAfter(expirationTime)) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("인증번호가 만료되었습니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        if (Boolean.TRUE.equals(smsAuth.get().getIsUse())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("이미 사용된 인증번호입니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        // SMS 인증번호를 여러 번 사용하는 것 방지
        smsAuth.get().setIsUse(true);
        smsAuthRepository.save(smsAuth.get());

        // 휴대폰 번호 유효성 검사 - 타입, 글자수
        String phoneNumber = requestDto.getPhoneNumber().replaceAll("\\D", "");

        // 휴대폰 번호로 일치하는 회원 조회 (회원 사용여부 : 활성화)
        Optional<UserEntity> user = userRepository.findByPhoneNumberAndIsUseIsTrue(phoneNumber);
        String email = user.map(UserEntity::getEmail).orElse(null);

        if (email != null) {
            // 휴대폰 번호로 가입된 회원이 존재할 경우 : Email 반환
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("고객님의 집킴이 계정을 찾았습니다. 아이디 확인 후 로그인 해주세요.")
                    .email(email)
                    .build();
        } else {
            // 휴대폰 번호로 가입된 회원이 존재하지 않을 경우 : 고객센터 문의 요망 반환 안내
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        //TODO SNS 가입자인 경우 : SNS 로그인 개발 후 추가 필요
        // 카카오톡 가입자입니다. 카카오톡으로 로그인해주세요.

    }



    // ************* 비밀번호 찾기 *************

    // 비밀번호 찾기 - SMS 인증번호 전송
    public FindSmsAuthNumberPostResponse sendFindPwSmsAuthNumber(
            PassResetSmsAuthNumberPostRequest requestDto) {

        // 휴대폰 번호 유효성 검사 - 타입, 글자수
        String phoneNumber = requestDto.getPhoneNumber().replaceAll("\\D", "");
        if (phoneNumber.length() != 11) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("휴대폰 번호를 정확히 입력해주세요.")
                    .build();
        }

        // 휴대폰 번호로 일치하는 회원 조회 (회원 사용여부 : 활성화)
        Optional<UserEntity> user = userRepository.findByPhoneNumberAndIsUseIsTrue(phoneNumber);

        if (user.isEmpty()) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        // 휴대폰번호/인증완료여부/만료시간만료여부/인증타입으로 만료되지 않은 인증번호 있는지 조회
        SmsAuthEntity existingSmsAuth =
                smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(phoneNumber,LocalDateTime.now(), SMS_AUTH_CODE.FIND_PW.getValue());

        String randomNumber;
        SmsAuthEntity smsAuthEntitySaved = null;

        if (existingSmsAuth != null) {
            // SMS 인증번호가 만료되지 않았을 경우
            existingSmsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuthRepository.save(existingSmsAuth);
            return FindSmsAuthNumberPostResponse.builder()
                    .message("유효시간이 만료되지 않은 인증번호가 존재합니다. \n인증번호를 확인하거나, 유효시간이 지난 후 다시 시도해주세요.")
                    .build();
        } else {
            // 인증번호 생성 : 4자리(중복 x)
            randomNumber = generateNumber(4, 2);

            // DB 테이블에 insert
            SmsAuthEntity smsAuth = new SmsAuthEntity();
            smsAuth.setPhoneNumber(phoneNumber);
            smsAuth.setSmsAuthNumber(randomNumber);
            smsAuth.setIsAuthenticate(false);
            smsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuth.setSmsAuthType(SMS_AUTH_CODE.FIND_PW.getValue());

            // SMS 내용 설정
            smsAuth.setContent(
                    "[집킴이] 비밀번호 찾기 인증번호는 [" + randomNumber + "] 입니다. 인증번호를 정확히 입력해주세요.");

            try {
                // DB 테이블에 insert
                smsAuthEntitySaved = smsAuthRepository.save(smsAuth);

                // SMS 전송 로직
                smsService.pushSMSMessage(smsAuthEntitySaved);
            } catch (Exception e) {
                throw new BadRequestException("SMS 인증번호를 생성하던 중 오류가 발생하였습니다.");
            }
        }

        return FindSmsAuthNumberPostResponse.builder()
                .message("인증번호를 전송하였습니다.")
                .build();
    }

    // 비밀번호 찾기 - SMS 인증번호 검증 & 이메일과 휴대폰 번호로 가입 회원 조회
    public FindSmsAuthNumberGetResponse checkFindPwSmsAuth(FindPwCheckSmsGetRequest requestDto) {

        // #1. SMS 인증번호를 ID를 통해 DB 데이터 검증 후
        Optional<SmsAuthEntity> smsAuth = smsAuthRepository.findById(requestDto.getSmsAuthId());

        if (smsAuth.isEmpty() || !smsAuth.get().getSmsAuthNumber()
                .equals(requestDto.getSmsAuthNumber())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("SMS 인증에 실패하였습니다. \n인증번호를 정상적으로 입력해주세요.")
                    .build();
        }

        // 2. SMS 인증번호 만료시간 검증
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = smsAuth.get().getExpirationTime();

        if (currentTime.isAfter(expirationTime)) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("인증번호가 만료되었습니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        if (Boolean.TRUE.equals(smsAuth.get().getIsUse())) {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("이미 사용된 인증번호입니다. \n다시 인증번호를 발급받아주세요.")
                    .build();
        }

        // SMS 인증번호를 여러 번 사용하는 것 방지
        smsAuth.get().setIsUse(true);
        smsAuthRepository.save(smsAuth.get());

        // 3. 이메일과 휴대폰 번호로 가입 회원 조회
        Optional<UserEntity> user = userRepository.findByPhoneNumberAndEmailAndIsUseIsTrue(
                requestDto.getPhoneNumber(), requestDto.getEmail());

        // 2-1. 임시 비밀번호 생성
        String newPassword = tempPassword(10);
        // 비밀번호 암호화 적용
        String encodeNewPw = passwordEncoder.encode(newPassword);

        // 2.2 회원이 존재한다면, 생성한 임시비밀번호로 비밀번호 초기화(업데이트)
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            userEntity.setPassword(encodeNewPw);

            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("고객님의 비밀번호가 초기화 되었습니다. \n비밀번호 확인 후 로그인해주세요.")
                    .password(newPassword)
                    .build();
        } else {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        //TODO SNS 가입자인 경우 : SNS 로그인 개발 후 추가 필요
        // 네이버 가입자입니다. 네이버로 로그인해주세요.

    }

    //임시 비밀번호 생성
    public String tempPassword(int len) {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < len; i++) {
            int index = secureRandom.nextInt(CHAR_SET.length());
            char randomChar = CHAR_SET.charAt(index);
            password.append(randomChar);
        }

        return password.toString();
    }



}
