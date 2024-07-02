package com.demo.dto;

public class PaymentDetails {
    private String cardNumber;
    private String expiry;
    private String cvv;
    private int busId;  // Assuming busId is needed for payment confirmation

    // Constructors, getters, and setters

    public PaymentDetails() {
    }

    public PaymentDetails(String cardNumber, String expiry, String cvv, int busId) {
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cvv = cvv;
        this.busId = busId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }
}
