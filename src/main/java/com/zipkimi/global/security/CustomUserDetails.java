package com.zipkimi.global.security;

import com.zipkimi.entity.UserEntity;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    public final UserEntity getUser() {
        return user;
    }

    //계정이 갖고있는 권한 목록을 리턴한다. (권한이 여러개 있을수있어서 루프를 돌아야 하는데  우리는 한개만)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collectors = new ArrayList<>();
        //collectors.add(()-> "ROLE_"+user.getRole()); //add에 들어올 파라미터는 GrantedAuthority밖에 없으니
        collectors.add(
                () -> String.valueOf(user.getRole())); //add에 들어올 파라미터는 GrantedAuthority밖에 없으니

        return collectors;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}