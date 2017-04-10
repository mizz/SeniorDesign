package com.rent_it_app.rent_it.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable
{

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("chargeAmount")
    @Expose
    private Double chargeAmount;
    @SerializedName("paymentMethodNonce")
    @Expose
    private String paymentMethodNonce;
    private final static long serialVersionUID = -1427674605071040881L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Transaction() {
    }

    /**
     *
     * @param chargeAmount
     * @param paymentMethodNonce
     * @param user
     */
    public Transaction(String user, Double chargeAmount, String paymentMethodNonce) {
        super();
        this.user = user;
        this.chargeAmount = chargeAmount;
        this.paymentMethodNonce = paymentMethodNonce;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(String paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }

}