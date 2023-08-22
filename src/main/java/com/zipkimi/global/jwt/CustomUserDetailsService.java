package com.zipkimi.global.jwt;

import com.zipkimi.global.exception.CustomUserNotFoundException;
import com.zipkimi.repository.UserRepository;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {
        System.out.println(" UserDetails loadUserByUsername userPk = " + userPk);
        return (UserDetails) userRepository.findById(Long.parseLong(userPk))
                .orElseThrow(CustomUserNotFoundException::new);
    }

}
