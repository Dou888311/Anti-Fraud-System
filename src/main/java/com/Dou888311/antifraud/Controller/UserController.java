package com.Dou888311.antifraud.Controller;

import com.Dou888311.antifraud.DTO.*;
import com.Dou888311.antifraud.Entity.User;
import com.Dou888311.antifraud.User.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth/user")
    @JsonView(View.UserView.class)
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody User user) {
        return userService.register(user);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public UserDeleteResponse delete(@PathVariable String username) {
        return userService.delete(username);
    }

    @GetMapping("/api/auth/list")
    @JsonView(View.UserView.class)
    public List<User> list() {
        return userService.list();
    }

    @PutMapping("/api/auth/role")
    public UserDTO roleChange(@RequestBody RoleTransfer roleTransfer) {
        return userService.roleChange(roleTransfer);
    }

    @PutMapping("/api/auth/access")
    public UserOperationResponse operation(@RequestBody UserOperation operation) {
        return userService.operation(operation);
    }
}
