package com.zipkimi.user.service;

import com.zipkimi.global.jwt.entity.RefreshTokenEntity;
import com.zipkimi.entity.UserRole;
import com.zipkimi.global.dto.response.BaseResponse;
import com.zipkimi.global.jwt.dto.request.TokenRequest;
import com.zipkimi.global.jwt.dto.response.TokenResponse;
import com.zipkimi.global.exception.BadRequestException;
import com.zipkimi.global.jwt.JwtTokenProvider;
import com.zipkimi.global.service.SmsService;
import com.zipkimi.global.utils.CodeConstant.SMS_AUTH_CODE;
import com.zipkimi.global.jwt.repository.RefreshTokenRepository;
import com.zipkimi.user.dto.request.FindIdCheckSmsGetRequest;
import com.zipkimi.user.dto.request.FindPwCheckSmsGetRequest;
import com.zipkimi.user.dto.request.PassResetSmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.SmsAuthNumberPostRequest;
import com.zipkimi.user.dto.request.UserLoginRequest;
import com.zipkimi.user.dto.response.FindSmsAuthNumberGetResponse;
import com.zipkimi.user.dto.response.FindSmsAuthNumberPostResponse;
import com.zipkimi.entity.SmsAuthEntity;
import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.SmsAuthRepository;
import com.zipkimi.repository.UserRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
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
    private final Random random = new Random();

    /* JWT 관련 */
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // ************* 로그인 *************

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

    @Transactional
    public TokenResponse login(UserLoginRequest userLoginRequest) {

        // 회원 정보 존재하는지 확인
        Optional<UserEntity> user = userRepository.findByEmail(userLoginRequest.getEmail());

        // Login Email (ID) / PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = userLoginRequest.toAuthentication();
        log.info("login authenticationToken = " + authenticationToken);
        log.info("login authenticationToken.getName() = " + authenticationToken.getName());

        // 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("login authentication = " + authentication);

        // 4. 인증 정보를 기반으로 JWT 토큰 생성
        TokenResponse tokenResponse = jwtTokenProvider.createToken(authentication);
        log.info("login tokenResponse = " + tokenResponse);

        // 5. RefreshToken을 DB에 저장
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userId(user.get().getUserId())
                .token(tokenResponse.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        // 5. 토큰 발급
        return TokenResponse.builder()
                .message("로그인에 성공하였습니다.")
                .grantType(tokenResponse.getGrantType())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .accessTokenExpireDate(tokenResponse.getAccessTokenExpireDate())
                .build();
    }

    @Transactional
    public TokenResponse reissue(TokenRequest tokenRequest) {

        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(tokenRequest.getRefreshToken())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // Access Token 에서 Member ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequest.getAccessToken());
        log.info("=========================== !!! reissue authentication = " + authentication);
        log.info("=========================== !!! reissue authentication.getCredentials() = " + authentication.getCredentials());
        log.info("=========================== !!! reissue authentication.getAuthorities() = " + authentication.getAuthorities());
        log.info("=========================== !!! reissue authentication.getDetails() = " + authentication.getDetails());
        log.info("=========================== !!! reissue authentication.getName() = " + authentication.getName());

        // 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByUserId(
                        Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));
        log.info("=========================== !!! reissue refreshToken = " + refreshToken);

        // Refresh Token 일치하는지 검사
        if (!refreshToken.getRefreshToken().equals(tokenRequest.getRefreshToken())) {
            log.info("reissue refreshToken.getRefreshToken() = " + refreshToken.getRefreshToken());
            log.info("reissue tokenRequest.getRefreshToken() = " + tokenRequest.getRefreshToken());
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 새로운 토큰 생성
        TokenResponse tokenDto = jwtTokenProvider.createToken(authentication);

        // 저장소 정보 업데이트
        RefreshTokenEntity newRefreshToken = refreshToken.updateToken(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // accessToken과 refreshToken 모두 재발행
        return TokenResponse.builder()
                .message("토큰 재발급에 성공하였습니다.")
                .grantType(tokenDto.getGrantType())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .accessTokenExpireDate(tokenDto.getAccessTokenExpireDate())
                .build();
    }


    // ************* 아이디 찾기 *************

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

        //3. 휴대폰 번호로 일치하는 회원 조회
        Optional<UserEntity> user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());
        String email = user.map(UserEntity::getEmail).orElse(null);

        if (email != null) {
            // 휴대폰 번호로 가입된 회원이 존재할 경우 : Email 반환
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("회원님의 아이디는 '" + email + "' 입니다.")
                    .build();
        } else {
            // 휴대폰 번호로 가입된 회원이 존재하지 않을 경우 : 고객센터 문의 요망 반환 안내
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("입력하신 휴대폰 번호와 일치하는 아이디 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }
    }

    // 아이디 찾기 - SMS 인증번호 전송
    public FindSmsAuthNumberPostResponse sendFindIdSmsAuthNumber(
            SmsAuthNumberPostRequest requestDto) {

        // 휴대폰 번호로 일치하는 회원 조회
        Optional<UserEntity> user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());

        if (user.isEmpty()) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        // 만료되지 않은 인증번호 있는지 조회
        SmsAuthEntity existingSmsAuth =
                smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(requestDto.getPhoneNumber(),
                    LocalDateTime.now(), SMS_AUTH_CODE.FIND_ID.getValue());

        String randomNumber;
        SmsAuthEntity smsAuthEntitySaved = null;

        if (existingSmsAuth != null) {
            // SMS 인증번호가 만료되지 않았을 경우
            existingSmsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuthRepository.save(existingSmsAuth);
        } else {
            // 인증번호 생성 : 4자리(중복 x)
            randomNumber = generateNumber(4, 2);

            SmsAuthEntity smsAuth = new SmsAuthEntity();
            smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
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

    // ************* 비밀번호 찾기 *************

    // 비밀번호 찾기 - SMS 인증번호 전송
    public FindSmsAuthNumberPostResponse sendFindPwSmsAuthNumber(
            PassResetSmsAuthNumberPostRequest requestDto) {

        // 휴대폰 번호로 가입된 회원 확인
        Optional<UserEntity> user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());

        if (user.isEmpty()) {
            return FindSmsAuthNumberPostResponse.builder()
                    .message("입력하신 휴대폰 번호와 일치하는 정보가 없습니다. \n(고객센터 문의 요망)")
                    .build();
        }

        // 만료되지 않은 SMS 인증번호 있는지 조회
        SmsAuthEntity existingSmsAuth =
                smsAuthRepository.findValidSmsAuthByPhoneNumberAndType(requestDto.getPhoneNumber(),
                LocalDateTime.now(), SMS_AUTH_CODE.FIND_PW.getValue());

        String randomNumber;
        SmsAuthEntity smsAuthEntitySaved = null;

        if (existingSmsAuth != null) {
            // SMS 인증번호가 만료되지 않았을 경우
            existingSmsAuth.setExpirationTime(LocalDateTime.now().plusMinutes(5L));
            smsAuthRepository.save(existingSmsAuth);
        } else {
            // 인증번호 생성 : 4자리(중복 x)
            randomNumber = generateNumber(4, 2);

            // DB 테이블에 insert
            SmsAuthEntity smsAuth = new SmsAuthEntity();
            smsAuth.setPhoneNumber(requestDto.getPhoneNumber());
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
        Optional<UserEntity> user = userRepository.findByPhoneNumberAndEmail(
                requestDto.getPhoneNumber(), requestDto.getEmail());

        // 2-1. 임시 비밀번호 생성
        String newPassword = tempPassword(10);

        // 2.2 회원이 존재한다면, 생성한 임시비밀번호로 비밀번호 초기화(업데이트)
        if (user.isPresent()) {
            UserEntity userEntity = user.get();
            userEntity.setPassword(newPassword);

            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("비밀번호가 '" + newPassword + "'로 초기화 되었습니다.")
                    .build();
        } else {
            return FindSmsAuthNumberGetResponse
                    .builder()
                    .message("입력하신 휴대폰 번호와 이메일 정보가 일치하는 사용자가 없습니다.")
                    .build();
        }

    }

    // ************* 공통 로직 *************

    //난수로 인증번호 생성
    public String generateNumber(int len, int dupCd) {

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
