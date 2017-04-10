package com.rent_it_app.rent_it.json_models;

/**
 * Created by malhan on 4/9/17.
 */


import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RentalPaymentMethodNonce implements Serializable
{

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("paymentMethodNonce")
    @Expose
    private String paymentMethodNonce;
    private final static long serialVersionUID = 6941288643142427697L;

    /**
     * No args constructor for use in serialization
     *
     */
    public RentalPaymentMethodNonce() {
    }

    /**
     *
     * @param paymentMethodNonce
     * @param user
     */
    public RentalPaymentMethodNonce(String user, String paymentMethodNonce) {
        super();
        this.user = user;
        this.paymentMethodNonce = paymentMethodNonce;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(String paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }

}