package com.exchanger.service.impl;

import com.exchanger.entity.CurrencyEnum;
import com.exchanger.entity.Notification;
import com.exchanger.entity.NotificationTypeEnum;
import com.exchanger.entity.Rate;
import com.exchanger.repository.NotificationRepository;
import com.exchanger.repository.RateRepository;
import com.exchanger.service.RateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.sql.Timestamp;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateServiceImpl implements RateService {
    @Value("${bank.rate.url}")
    private String bankRateUrl;

    private final NotificationRepository notificationRepository;

    private final RateRepository rateRepository;
    @Scheduled(cron = "0 * * * * MON-FRI") // means once per minute on weekdays
    /*
    +-------------------- second (0 - 59)
    |  +----------------- minute (0 - 59)
    |  |  +-------------- hour (0 - 23)
    |  |  |  +----------- day of month (1 - 31)
    |  |  |  |  +-------- month (1 - 12)
    |  |  |  |  |  +----- day of week (0 - 6) (Sunday=0 or 7)
    |  |  |  |  |  |  +-- year [optional]
    |  |  |  |  |  |  |
    *  *  *  *  *  *  * command to be executed
    */
    @Override
    public void getRates() throws JsonProcessingException {
        log.info("start cron job");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(bankRateUrl, String.class);
        log.error(response.getBody());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(response.getBody());

        actualObj.forEach(c -> {
            if ( c.get("ccy").asText().equals("USD")
                    || c.get("ccy").asText().equals("EUR")) {
                Rate rate = new Rate()
                        .setBuy(c.get("buy").asText())
                        .setSale(c.get("sale").asText())
                        .setReceive(new Timestamp(System.currentTimeMillis()))
                        .setCurrency(CurrencyEnum.valueOf(c.get("ccy").asText()));
                rateRepository.save(rate);
            }
        });

        notificationRepository.save(
                new Notification()
                        .setType(NotificationTypeEnum.RATE)
                        .setContent(String.format(actualObj.toString())));
        log.info("finish cron job");
    }
}
