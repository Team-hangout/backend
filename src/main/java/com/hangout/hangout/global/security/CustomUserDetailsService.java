package com.hangout.hangout.global.security;

import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email)  {
        User user = this.userRepository.findByEmail(email)
            .orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. email: " + email)
            );
        return new UserPrincipal(user);
    }
}