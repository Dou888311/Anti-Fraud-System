package com.Dou888311.antifraud.Entity;

import com.Dou888311.antifraud.transaction.Region;
import com.Dou888311.antifraud.transaction.Result;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @JsonProperty(value="transactionId")
    private long id;

    @Min(1)
    private long amount;

    @Pattern(regexp = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])." +
            "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])")
    @NotEmpty
    private String ip;

    @NotEmpty
    private String number;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Region region;

    @NotNull
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Result result;

    private String feedback = "";
}
