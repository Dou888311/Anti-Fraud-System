package com.Dou888311.antifraud.Entity;

import com.Dou888311.antifraud.DTO.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(View.UserView.class)
    private long id;

    @JsonIgnore
    private boolean isNonLocked = false;

    @NotEmpty
    @JsonView(View.UserView.class)
    private String name;

    @NotEmpty
    @Column(unique = true)
    @JsonView(View.UserView.class)
    private String username;

    @Column
    @JsonView(View.UserView.class)
    private String role;

    @NotEmpty
    private String password;
}
