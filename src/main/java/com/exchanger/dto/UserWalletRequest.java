package com.exchanger.dto;

import com.exchanger.entity.CurrencyEnum;

public record UserWalletRequest(String phone, CurrencyEnum currencyIso) {
}
