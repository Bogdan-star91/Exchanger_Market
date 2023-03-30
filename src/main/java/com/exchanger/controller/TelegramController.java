package com.exchanger.controller;

import com.exchanger.telegram.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 0.0.1
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class TelegramController {

    private final TelegramBot telegramBot;

    @GetMapping("/telegram")
    public String ping() {

       telegramBot.sendMessage(1l, "hellooooooo");
       return null;
    }
}
