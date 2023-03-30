package com.exchanger.dto;

import com.exchanger.entity.CurrencyEnum;
import com.exchanger.entity.Wallet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Accessors(chain = true)
public class WalletDto {
    private CurrencyEnum currency;

    private BigDecimal ammount;

    public static WalletDto fromEntity(Wallet wallet) {
        return WalletDto.builder()
                .ammount(wallet.getAmmount())
                .currency(wallet.getCurrency())
                .build();
    }
}
