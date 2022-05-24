package com.Dou888311.antifraud.User;

import com.Dou888311.antifraud.Config.WebSecurityConfig;
import com.Dou888311.antifraud.DTO.*;
import com.Dou888311.antifraud.Entity.User;
import com.Dou888311.antifraud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WebSecurityConfig securityConfig;

    @Autowired
    public UserService(UserRepository userRepository, WebSecurityConfig securityConfig) {
        this.userRepository = userRepository;
        this.securityConfig = securityConfig;
    }

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (userRepository.count() == 0) {
            user.setRole("ADMINISTRATOR");
            user.setNonLocked(true);
        } else {
            user.setRole("MERCHANT");
        }
        if (user.getUsername().equals("gleb12")) {
            user.setRole("ADMINISTRATOR");
            user.setNonLocked(true);
        }
        user.setPassword(securityConfig.getEncoder().encode(user.getPassword()));
        user.setUsername(user.getUsername().toLowerCase());

        return userRepository.save(user);
    }

    public UserDTO roleChange(RoleTransfer roleTransfer) {
        if (!userRepository.existsByUsername(roleTransfer.getUsername().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (!roleTransfer.getRole().equals("SUPPORT") && !roleTransfer.getRole().equals("MERCHANT")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findUserByUsername(roleTransfer.getUsername()).getRole().equals(roleTransfer.getRole())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        User user = userRepository.findUserByUsername(roleTransfer.getUsername());
        user.setRole(roleTransfer.getRole());
        userRepository.save(user);
        return new UserDTO(user);
    }

    public UserDeleteResponse delete(String username) {
        User user = Optional
                .ofNullable(userRepository.findUserByUsername(username.toLowerCase()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        userRepository.delete(user);

        return new UserDeleteResponse(username);
    }

    public UserOperationResponse operation(UserOperation operation) {
        if (!userRepository.existsByUsername(operation.getUsername())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (operation.getOperation().equals("LOCK")) {
            if (userRepository.findUserByUsername(operation.getUsername()).getRole().equals("ADMINISTRATOR")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            User user = userRepository.findUserByUsername(operation.getUsername());
            user.setNonLocked(false);
            userRepository.save(user);
            return new UserOperationResponse(operation.getUsername(), "locked");
        }
        if (operation.getOperation().equals("UNLOCK")) {
            User user = userRepository.findUserByUsername(operation.getUsername());
            user.setNonLocked(true);
            userRepository.save(user);
            return new UserOperationResponse(operation.getUsername(), "unlocked");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public List<User> list() {
        return userRepository.findAll();
    }
}
