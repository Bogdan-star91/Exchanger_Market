package com.exchanger.service;

import com.exchanger.dto.UserView;
import com.exchanger.dto.UserDto;
import com.exchanger.dto.UserRecord;
import com.exchanger.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    Long createUser(UserRecord user);

    List<UserDto> getAllUsers();

    Page<User> getUsers(Pageable page);

    Page<UserDto> getActiveUsers(Pageable page);

    UserDto getUserById(Long id);

    User getUserByPhone(String phone);

    void saveUser(User user);

    List<UserView> getUserWalletByPhone(String phone);
}
