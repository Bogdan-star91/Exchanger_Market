package com.exchanger.dto;

import com.exchanger.entity.CurrencyEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TransferDto {
    private String phoneNumber;
    private String distPhoneNumber;
    private CurrencyEnum currency;
    private BigDecimal amount;

}
