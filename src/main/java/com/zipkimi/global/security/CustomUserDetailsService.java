package com.zipkimi.global.security;

import com.zipkimi.entity.UserEntity;
import com.zipkimi.repository.UserRepository;
import java.util.Collections;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info(" ===== CustomUserDetailsService loadUserByUsername ===== ");
        log.info("CustomUserDetailsService email = " + email);
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(
                        () -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(UserEntity user) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().toString());
        log.info(" ===== CustomUserDetailsService createUserDetails ===== ");
        log.info("CustomUserDetailsService grantedAuthority = " + grantedAuthority);
        log.info("CustomUserDetailsService user = " + user);

        return new User(
                String.valueOf(user.getUserId()),
                user.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }


}
