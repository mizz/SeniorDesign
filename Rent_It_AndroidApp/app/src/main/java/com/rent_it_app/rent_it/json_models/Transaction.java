package com.rent_it_app.rent_it.json_models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction implements Serializable
{

    @SerializedName("paymentMethodNonce")
    @Expose
    private String paymentMethodNonce;
    @SerializedName("chargeAmount")
    @Expose
    private Double chargeAmount;
    private final static long serialVersionUID = -3081645607543686490L;

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
     */
    public Transaction(String paymentMethodNonce, Double chargeAmount) {
        super();
        this.paymentMethodNonce = paymentMethodNonce;
        this.chargeAmount = chargeAmount;
    }

    public String getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(String paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }

    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

}