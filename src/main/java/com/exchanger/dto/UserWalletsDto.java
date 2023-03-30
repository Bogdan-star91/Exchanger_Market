package com.exchanger.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Builder
@Accessors(chain = true)
public class UserWalletsDto {
    private UserDto userDto;
    List<WalletDto> userWallets;
}
