package com.exchanger.dto;

public record VerificationDto(String phoneNumber, String code, Long transactionId) {
}
