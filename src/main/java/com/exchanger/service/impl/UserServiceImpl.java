package com.exchanger.service.impl;

import com.exchanger.dto.UserView;
import com.exchanger.dto.UserDto;
import com.exchanger.dto.UserRecord;
import com.exchanger.entity.*;
import com.exchanger.exceptions.NotUniqueDataException;
import com.exchanger.exceptions.UserNotFoundException;
import com.exchanger.mapper.UserMapper;
import com.exchanger.repository.NotificationRepository;
import com.exchanger.repository.UserRepository;
import com.exchanger.repository.WalletRepository;
import com.exchanger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final NotificationRepository notificationRepository;
    private final UserMapper userMapper;

    @Override
    public Long createUser(UserRecord user) {

        long count = userRepository.findAllByPhoneNumberOrEmail(user.phone(), user.email()).size();
                if(count>0) {
                    throw new NotUniqueDataException();
                }


        User save = userRepository.save(
                new User()
                        .setLastName(user.last_name())
                        .setFirstName(user.first_name())
                        .setEmail(user.email())
                        .setPhoneNumber(user.phone())
                        .setStatus(false));


        Wallet uah = walletRepository.save(new Wallet()
                .setUser(save)
                .setCurrency(CurrencyEnum.UAH)
                .setAmmount(BigDecimal.ZERO)
                .setLastUpdate(new Timestamp(System.currentTimeMillis())));


        Wallet usd = walletRepository.save(new Wallet()
                .setUser(save)
                .setCurrency(CurrencyEnum.USD)
                .setAmmount(BigDecimal.ZERO)
                .setLastUpdate(new Timestamp(System.currentTimeMillis())));

        walletRepository.save(uah);
        walletRepository.save(usd);

        notificationRepository.save(new Notification()
                .setType(NotificationTypeEnum.REGISTRATION)
                .setUser(save)
                .setContent(String.format("create user: %s and create 2 wallet: UAH, USD ", save.getId())));

        return save.getId();
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhoneNumber(phone);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Page<User> getUsers(Pageable page) {
        return userRepository.findAll(page);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::fromEntity).toList();
    }

    @Override
    public Page<UserDto> getActiveUsers(Pageable page) {
        return userRepository.findAllByStatus(true, page).map(UserDto::fromEntity);
    }

    @Override
    public UserDto getUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    @Override
    public List<UserView> getUserWalletByPhone(String phone) {
        return userRepository.getUserWalletByPhone(phone);
    }
}
