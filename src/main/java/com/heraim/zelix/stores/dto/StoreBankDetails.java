package com.heraim.zelix.stores.dto;

import com.heraim.zelix.stores.entity.Store;

public record StoreBankDetails(
        String paymentAccountName,
        String paymentAccountNumber,
        String paymentBankName
) {
    public static StoreBankDetails from(Store store) {
        return new StoreBankDetails(
                store.getPaymentAccountName(),
                store.getPaymentAccountNumber(),
                store.getPaymentBankName()
        );
    }
}
