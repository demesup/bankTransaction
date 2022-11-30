package org.transactionmanager.transaction.enums;

import lombok.Getter;

@Getter
public enum Currency {
    UAH("Ukrainian hryvnia"),
    USD("US dollar"),
    EUR("Euro"),
    JPY("Japanese yen"),
    GBR("Pound Sterling"),
    AUD("Australian dollar"),
    CAD("Canadian dollar"),
    CHF("Swiss franc"),
    CNH("Chinese renminbi");
    private final String description;

    Currency(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
