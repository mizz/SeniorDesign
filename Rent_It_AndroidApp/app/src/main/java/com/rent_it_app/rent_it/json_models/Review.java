package com.rent_it_app.rent_it.json_models;

/**
 * Created by malhan on 3/8/17.
 */

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review implements Serializable
{

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("item_rating")
    @Expose
    private Integer itemRating;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("item_comment")
    @Expose
    private String itemComment;
    @SerializedName("owner_rating")
    @Expose
    private Integer ownerRating;
    @SerializedName("owner_comment")
    @Expose
    private String ownerComment;
    @SerializedName("reviewer")
    @Expose
    private String reviewer;
    @SerializedName("reviewer_info")
    @Expose
    private User reviewerInfo;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    private final static long serialVersionUID = 4583357801905039521L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Review() {
    }

    /**
     *
     * @param id
     * @param title
     * @param item
     * @param owner
     * @param dateCreated
     * @param ownerComment
     * @param reviewer
     * @param ownerRating
     * @param itemComment
     * @param itemRating
     * @param reviewerInfo
     */
    public Review(String id, String item, String owner, Integer itemRating, String title, String itemComment, Integer ownerRating, String ownerComment, String reviewer, User reviewerInfo, String dateCreated) {
        super();
        this.id = id;
        this.item = item;
        this.owner = owner;
        this.itemRating = itemRating;
        this.title = title;
        this.itemComment = itemComment;
        this.ownerRating = ownerRating;
        this.ownerComment = ownerComment;
        this.reviewer = reviewer;
        this.reviewerInfo = reviewerInfo;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getItemRating() {
        return itemRating;
    }

    public void setItemRating(Integer itemRating) {
        this.itemRating = itemRating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItemComment() {
        return itemComment;
    }

    public void setItemComment(String itemComment) {
        this.itemComment = itemComment;
    }

    public Integer getOwnerRating() {
        return ownerRating;
    }

    public void setOwnerRating(Integer ownerRating) {
        this.ownerRating = ownerRating;
    }

    public String getOwnerComment() {
        return ownerComment;
    }

    public void setOwnerComment(String ownerComment) {
        this.ownerComment = ownerComment;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public User getReviewerInfo() {
        return reviewerInfo;
    }

    public void setReviewerInfo(User reviewerInfo) {
        this.reviewerInfo = reviewerInfo;
    }


    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

}