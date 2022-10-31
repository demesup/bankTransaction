package org.transactionmanager.transaction.enums;

public enum Status {
    REQUEST,
    IN_PROCESS,
    APPROVED,
    FAILED;

    @Override
    public String toString() {
        return this.name();
    }
}
