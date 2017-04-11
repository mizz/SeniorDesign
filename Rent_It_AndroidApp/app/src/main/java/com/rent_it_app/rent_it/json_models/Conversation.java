package com.rent_it_app.rent_it.json_models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conversation implements Serializable
{

    @SerializedName("rental_id")
    @Expose
    private String rental_id;
    @SerializedName("item_id")
    @Expose
    private String item_id;
    @SerializedName("item_name")
    @Expose
    private String item_name;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("ownerName")
    @Expose
    private String ownerName;
    @SerializedName("renter")
    @Expose
    private String renter;
    @SerializedName("renterName")
    @Expose
    private String renterName;
    @SerializedName("lastMsgDate")
    @Expose
    private Date lastMsgDate;
    @SerializedName("last_active")
    @Expose
    private Long last_active;
    @SerializedName("chat")
    @Expose
    private List<Chat> chat = null;
    private final static long serialVersionUID = -2720223726977249907L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Conversation() {
    }

    /**
     *
     * @param item_name
     * @param lastMsgDate
     * @param lastMsgDate
     * @param last_active
     * @param renter
     * @param renterName
     * @param owner
     * @param ownerName
     * @param rental_id
     * @param chat
     * @param item_id
     */
    public Conversation(String rental_id, String item_id, String item_name, String owner, String ownerName, String renter, String renterName, Long last_active,Date lastMsgDate, List<Chat> chat) {
        super();
        this.rental_id = rental_id;
        this.item_id = item_id;
        this.item_name = item_name;
        this.owner = owner;
        this.ownerName = ownerName;
        this.renter = renter;
        this.renterName = renterName;
        this.last_active = last_active;
        this.lastMsgDate = lastMsgDate;
        this.chat = chat;
    }

    public String getRental_id() {
        return rental_id;
    }

    public void setRental_id(String rental_id) {
        this.rental_id = rental_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    public String getRenter() {
        return renter;
    }

    public void setRenter(String renter) {
        this.renter = renter;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }


    public Long getLast_active() {
        return last_active;
    }

    public void setLast_active(Long last_active) {
        this.last_active = last_active;
    }

    public Date getLastMsgDate() {
        return lastMsgDate;
    }

    public void setLastMsgDate(Date lastMsgDate) {
        this.lastMsgDate = lastMsgDate;
    }


    public List<Chat> getChat() {
        return chat;
    }

    public void setChat(List<Chat> chat) {
        this.chat = chat;
    }

}
