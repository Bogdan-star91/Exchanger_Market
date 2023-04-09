package com.exchanger.repository;

import com.exchanger.dto.UserView;
import com.exchanger.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    String QUERY = """
            SELECT
                u.last_name as lastName,
                u.first_name as firstName,
                u.phone as phoneNumber,
                w.amount,
                w.currency
            from user u
            left join wallet w on u.id = w.user_id
            where u.phone = :phone
            """;

    User findByPhoneNumber(String phoneNumber);

    List<User> findAllByPhoneNumberOrEmail(String phone, String email);

    @Query(value = QUERY, nativeQuery = true)
    List<UserView> getUserWalletByPhone(@Param("phone") String phone);

    Page<User> findAllByStatus(Boolean status, Pageable pageable);
}
