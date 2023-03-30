package com.exchanger.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RateService {
    void getRates() throws JsonProcessingException;
}
