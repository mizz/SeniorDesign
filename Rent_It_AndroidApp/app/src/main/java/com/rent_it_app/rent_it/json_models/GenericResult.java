package com.rent_it_app.rent_it.json_models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenericResult implements Serializable
{

    @SerializedName("result")
    @Expose
    private String result;
    private final static long serialVersionUID = -6709916928375384900L;

    /**
     * No args constructor for use in serialization
     *
     */
    public GenericResult() {
    }

    /**
     *
     * @param result
     */
    public GenericResult(String result) {
        super();
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}