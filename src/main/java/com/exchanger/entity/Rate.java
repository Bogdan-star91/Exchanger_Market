package com.exchanger.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import jakarta.persistence.*;
import java.sql.Timestamp;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "rate")
@Accessors(chain = true)
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    CurrencyEnum currency;
    String sale;
    String buy;
    Timestamp receive;
}