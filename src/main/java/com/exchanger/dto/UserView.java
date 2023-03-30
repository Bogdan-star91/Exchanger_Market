package com.exchanger.dto;

import java.math.BigDecimal;

public interface UserView {
    String getFirstName();

    String getLastName();

    String getPhoneNumber();

    BigDecimal getAmmount();

    String getCurrency();
}
