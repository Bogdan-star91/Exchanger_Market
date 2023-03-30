package com.exchanger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
public class Notification {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    private String content;

    private User user;

    private LocalDate createAt = LocalDate.now();
}
