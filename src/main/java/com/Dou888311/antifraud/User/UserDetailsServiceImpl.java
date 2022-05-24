package com.Dou888311.antifraud.User;

import com.Dou888311.antifraud.Entity.User;
import com.Dou888311.antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Optional
                .ofNullable(userRepository.findUserByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));

        return new UserDetailsImpl(user);
    }
}
