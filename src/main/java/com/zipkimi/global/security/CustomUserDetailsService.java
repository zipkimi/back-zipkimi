package com.zipkimi.global.security;

import com.zipkimi.global.security.exception.CustomUserNotFoundException;
import com.zipkimi.repository.UserRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {
        log.info("===================== UserDetails loadUserByUsername userPk = " + userPk);


        return (UserDetails) userRepository.findById(Long.parseLong(userPk))
                .orElseThrow(CustomUserNotFoundException::new);
    }

}
