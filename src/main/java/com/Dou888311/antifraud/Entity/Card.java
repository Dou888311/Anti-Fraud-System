package com.Dou888311.antifraud.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    public Card(String number) {
        this.number = number;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String number;
    private Integer MAX_ALLOW = 200;
    private Integer MIN_PROHIBITED = 1500;
}
