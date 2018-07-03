package com.sibi.primary.editTransaction._interface;

/**
 * Created by adway on 05/12/17.
 */

public interface IEditTransactionView {
    void updateData();
    String getName();
    double getAmount();
    String getCategory();
    long getTimestamp();
    void onUpdateSuccess();
    void onUpdateFailure();
    void onDelete();
}