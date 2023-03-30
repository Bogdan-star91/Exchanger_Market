
package com.exchanger.controller;


import com.exchanger.dto.*;
import com.exchanger.entity.CurrencyEnum;
import com.exchanger.exceptions.UnsupportedCurrencyException;
import com.exchanger.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @version 0.0.1
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/user/{phone}/wallet")
    public UserWalletsDto getUserWallet(@RequestParam String phone) {
        return walletService.getUserWallets(phone);
    }

    @PostMapping("/create-wallet")
    public UserWalletsDto createWallet(@RequestBody WalletRequest wRequest){
        try {
            CurrencyEnum.valueOf(wRequest.currencyISO());
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedCurrencyException();
        }

        return walletService.createWallet(wRequest);
    }

    @PostMapping("/put/verification")
    public Long putTransactionVerification(@RequestBody VerificationDto verificationDto){
        return walletService.putMoneyVerification(verificationDto);
    }

    @PostMapping("/put")
    public Long putMoneyToAccount(@RequestBody TransferDto transferDto) {
        return walletService.putMoney(transferDto);
    }

    @PostMapping("/get")
    public Long getMoneyfromAccount(@RequestBody TransferDto transferDto){
        return walletService.getMoney(transferDto);
    }

    @PostMapping("/get/verification")
    public Long getTransactionVerification(@RequestBody VerificationDto verificationDto){
        return walletService.getMoneyVerification(verificationDto);
    }

    @PostMapping("/exchange")
    public Long exchangeMoney(@RequestBody ExchangeDto exchangeDto){
        return walletService.exchangeMoney(exchangeDto);
    }

    @PostMapping("/exchange/verification")
    public Long exchangeTransactionVerification(@RequestBody VerificationDto verificationDto){
        return walletService.exchangeMoneyVerification(verificationDto);
    }


}
