package com.exchanger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phoneNumber;

    private String email;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "status")
    boolean status;

    @Column(name="telegram_chat_id")
    private Long telegramChatId;
}
