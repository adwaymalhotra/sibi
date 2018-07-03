package com.sibi.primary.dashboard._interface;

import java.util.Map;

/**
 * Created by adway on 02/12/17.
 */

public interface IDashboardView {
    void showFilePicker();
    void setBudgetStatus(double monthlyBudget, double spending, double remaining);
    void updateChart(Map<String, Double> catTransMap);
    void showToast(String msg);
}
