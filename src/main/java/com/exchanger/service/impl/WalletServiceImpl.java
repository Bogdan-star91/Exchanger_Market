package com.exchanger.service.impl;

import com.exchanger.dto.*;
import com.exchanger.entity.*;
import com.exchanger.exceptions.CurrencyExistException;
import com.exchanger.exceptions.NotEnoughtMoneyException;
import com.exchanger.exceptions.UserNotFoundException;
import com.exchanger.repository.*;
import com.exchanger.service.WalletService;
import com.exchanger.telegram.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final RateRepository rateRepository;
    private final TelegramBot telegramBot;

    @Override
    public UserWalletsDto getUserWallets(String phone) {

        User user = Optional.ofNullable(userRepository.findByPhoneNumber(phone)).orElseThrow(UserNotFoundException::new);

        List<Wallet> userWallets = walletRepository.findAllByUser(user);

        return UserWalletsDto.builder()
                .userDto(UserDto.fromEntity(user))
                .userWallets(userWallets.stream().map(WalletDto::fromEntity).toList())
                .build();

    }

    @Override
    public UserWalletsDto createWallet(WalletRequest walletRequest) {
        User user = Optional.ofNullable(userRepository.findByPhoneNumber(walletRequest.phone())).orElseThrow(UserNotFoundException::new);

        List<Wallet> userWallets = walletRepository.findAllByUser(user);

        long count = userWallets.stream().filter(w -> w.getCurrency().equals(CurrencyEnum.valueOf(walletRequest.currencyISO()))).count();

        if (count > 0) throw new CurrencyExistException();

        walletRepository.save(new Wallet()
                .setUser(user)
                .setCurrency(CurrencyEnum.valueOf(walletRequest.currencyISO()))
                .setAmmount(BigDecimal.ZERO)
                .setLastUpdate(new Timestamp(System.currentTimeMillis())));

        return getUserWallets(walletRequest.phone());
    }


    @Override
    public Long putMoney(TransferDto transferDto) {
        User user = userRepository.findByPhoneNumber(transferDto.getPhoneNumber());

        String verificationCode = RandomStringUtils.randomAlphabetic(6);
        Transaction transaction = new Transaction()
                .setReceiver(transferDto.getPhoneNumber())
                .setAmount(transferDto.getAmmount())
                .setType(TransactionTypeEnum.PUT)
                .setUpdateAt(LocalDate.now())
                .setCurrency(transferDto.getCurrency())
                .setCode(verificationCode)
                .setStatus(TransactionStatusEnum.PENDING);


        telegramBot.sendMessage(user.getTelegramChatId(), verificationCode);

        transactionRepository.save(transaction);

        notificationRepository.save(new Notification()
                .setType(NotificationTypeEnum.PUT)
                .setUser(user)
                .setContent(String.format("User try put %s %s to wallet. Status pending",
                        transferDto.getAmmount(),
                        transferDto.getCurrency())));

        return transaction.getId();
    }

    @Override
    public Long putMoneyVerification(VerificationDto verificationDto) {
        User user = userRepository.findByPhoneNumber(verificationDto.phoneNumber());

        Transaction transaction = transactionRepository.findById(verificationDto.transactionId())
                .orElseThrow(NullPointerException::new);

        if (transaction.getCode().equals(verificationDto.code())) {
            Wallet wallet = walletRepository.findByUserAndCurrency(user, transaction.getCurrency());
            wallet.setAmmount(wallet.getAmmount().add(transaction.getAmount()));
            walletRepository.save(wallet);
            transaction.setCode(null);
            transaction.setStatus(TransactionStatusEnum.EXECUTED);
            transactionRepository.save(transaction);

            notificationRepository.save(new Notification()
                    .setType(NotificationTypeEnum.PUT)
                    .setUser(user)
                    .setContent(String.format("User put %s %s to wallet. Status execute",
                            transaction.getAmount(),
                            transaction.getCurrency())));
        }
        return transaction.getId();
    }

    @Override
    public Long getMoney(TransferDto transferDto) {
        User user = userRepository.findByPhoneNumber(transferDto.getPhoneNumber());
        Wallet wallet = walletRepository.findByUserAndCurrency(user, transferDto.getCurrency());
        if (wallet.getAmmount().compareTo(transferDto.getAmmount()) == -1)
            throw new NotEnoughtMoneyException();

        String verificationCode = RandomStringUtils.randomAlphabetic(6);
        Transaction transaction = new Transaction()
                .setReceiver(transferDto.getPhoneNumber())
                .setAmount(transferDto.getAmmount())
                .setType(TransactionTypeEnum.GET)
                .setUpdateAt(LocalDate.now())
                .setCurrency(transferDto.getCurrency())
                .setCode(verificationCode)
                .setStatus(TransactionStatusEnum.PENDING);


        telegramBot.sendMessage(user.getTelegramChatId(), verificationCode);

        transactionRepository.save(transaction);

        notificationRepository.save(new Notification()
                .setType(NotificationTypeEnum.GET)
                .setUser(user)
                .setContent(String.format("User try get %s %s from wallet. Status pending",
                        transferDto.getAmmount(),
                        transferDto.getCurrency())));

        return transaction.getId();
    }

    @Override
    public Long getMoneyVerification(VerificationDto verificationDto) {
        User user = userRepository.findByPhoneNumber(verificationDto.phoneNumber());

        Transaction transaction = transactionRepository.findById(verificationDto.transactionId())
                .orElseThrow(NullPointerException::new);

        if (transaction.getCode().equals(verificationDto.code())) {
            Wallet wallet = walletRepository.findByUserAndCurrency(user, transaction.getCurrency());
            wallet.setAmmount(wallet.getAmmount().subtract(transaction.getAmount()));
            walletRepository.save(wallet);
            transaction.setCode(null);
            transaction.setStatus(TransactionStatusEnum.EXECUTED);
            transactionRepository.save(transaction);

            notificationRepository.save(new Notification()
                    .setType(NotificationTypeEnum.PUT)
                    .setUser(user)
                    .setContent(String.format("User get %s %s from wallet. Status execute",
                            transaction.getAmount(),
                            transaction.getCurrency())));
        }
        return transaction.getId();
    }

    @Override
    public Long exchangeMoney(ExchangeDto exchangeDto) {
        User user = userRepository.findByPhoneNumber(exchangeDto.getPhoneNumber());

        List<Wallet> wallets = walletRepository.findAllByUser(user);

        Rate rate = rateRepository.findFirstByCurrencyOrderByReceiveDesc(
                exchangeDto.getCurrencyTo().equals(CurrencyEnum.UAH) ? exchangeDto.getCurrencyFrom() : exchangeDto.getCurrencyTo());


        BigDecimal summ = exchangeDto.getCurrencyTo().equals(CurrencyEnum.UAH) ?
                exchangeDto.getAmmount().divide(BigDecimal.valueOf(Double.parseDouble(rate.getBuy())), 2, RoundingMode.HALF_UP)
                :
                exchangeDto.getAmmount().multiply(BigDecimal.valueOf(Double.parseDouble(rate.getSale())))
                ;

        System.out.println(summ);

        Wallet wFrom = wallets.stream()
                .filter(w->w.getCurrency().equals(exchangeDto.getCurrencyFrom()))
                .findFirst().orElseThrow(UserNotFoundException::new);

        if (wFrom.getAmmount().compareTo(summ) == -1)
            throw new NotEnoughtMoneyException();

        String verificationCode = RandomStringUtils.randomAlphabetic(6);

        Transaction transaction = new Transaction()
                .setReceiver(exchangeDto.getPhoneNumber())
                .setAmount(summ)
                .setCurrency(exchangeDto.getCurrencyFrom())
                .setAmountTo(exchangeDto.getAmmount())
                .setCurrencyTo(exchangeDto.getCurrencyTo())
                .setType(TransactionTypeEnum.EXCH)
                .setUpdateAt(LocalDate.now())
                .setCode(verificationCode)
                .setStatus(TransactionStatusEnum.PENDING)
                .setComment("From -> to : " + exchangeDto.getCurrencyFrom() +" -> "+ exchangeDto.getCurrencyTo());


        telegramBot.sendMessage(user.getTelegramChatId(), verificationCode);
        transactionRepository.save(transaction);
        return transaction.getId();

    }

    @Override
    public Long exchangeMoneyVerification(VerificationDto verificationDto) {
        User user = userRepository.findByPhoneNumber(verificationDto.phoneNumber());

        Transaction transaction = transactionRepository.findById(verificationDto.transactionId())
                .orElseThrow(NullPointerException::new);

        if (transaction.getCode().equals(verificationDto.code())) {
            Wallet walletFrom = walletRepository.findByUserAndCurrency(user, transaction.getCurrency());
            Wallet walletTo = walletRepository.findByUserAndCurrency(user, transaction.getCurrencyTo());

            walletFrom.setAmmount(walletFrom.getAmmount().subtract(transaction.getAmount()));
            walletTo.setAmmount(walletTo.getAmmount().add(transaction.getAmountTo()));

            walletRepository.save(walletFrom);
            walletRepository.save(walletTo);
            transaction.setCode(null);
            transaction.setStatus(TransactionStatusEnum.EXECUTED);
            transactionRepository.save(transaction);

            notificationRepository.save(new Notification()
                    .setType(NotificationTypeEnum.EXCHANGE)
                    .setUser(user)
                    .setContent(String.format("User exchange %s to %s. Status execute",
                            transaction.getCurrency(),
                            transaction.getCurrencyTo())));
        }
        return transaction.getId();
    }
}
