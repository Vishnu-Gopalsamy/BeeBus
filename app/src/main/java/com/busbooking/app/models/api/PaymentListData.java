package com.busbooking.app.models.api;

import java.util.List;

public class PaymentListData {
    private List<PaymentData> payments;
    private int count;

    public List<PaymentData> getPayments() { return payments; }
    public void setPayments(List<PaymentData> payments) { this.payments = payments; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
