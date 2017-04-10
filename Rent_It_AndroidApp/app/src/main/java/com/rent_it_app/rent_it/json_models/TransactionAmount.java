package com.rent_it_app.rent_it.json_models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionAmount implements Serializable
{

    @SerializedName("chargeAmount")
    @Expose
    private Double chargeAmount;
    private final static long serialVersionUID = 7948722064501608107L;

    /**
     * No args constructor for use in serialization
     *
     */
    public TransactionAmount() {
    }

    /**
     *
     * @param chargeAmount
     */
    public TransactionAmount(Double chargeAmount) {
        super();
        this.chargeAmount = chargeAmount;
    }

    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

}