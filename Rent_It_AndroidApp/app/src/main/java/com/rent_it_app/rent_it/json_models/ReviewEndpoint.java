package com.rent_it_app.rent_it.json_models;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Miz on 2/1/17.
 */

public interface ReviewEndpoint {

    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("api/reviews")
    Call<ArrayList<Review>> getReviews(/*@Path("uid") String uid*/);

    @GET("api/review/item/{item}")
    Call<Review> getLatestReviewByItemId(@Path("item") String item);


    @GET("api/reviews/item/{item}")
    Call<ArrayList<Review>> getReviewsByItemId(@Path("item") String item);

    @GET("api/reviews/owner/{owner}")
    Call<ArrayList<Review>> getReviewsByOwnerId(@Path("owner") String owner);


    @POST("api/reviews")
    Call<Review> addReview(@Body Review review);

}
