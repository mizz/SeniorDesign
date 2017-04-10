package com.rent_it_app.rent_it.json_models;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Miz on 2/1/17.
 */

public interface ReviewEndpoint {

    //get latest review by item id
    @GET("api/review/item/{item}")
    Call<Review> getLatestReviewByItemId(@Path("item") String item);

    //Iem Reviews
    @GET("api/reviews/item/{item}")
    Call<ArrayList<Review>> getReviewsByItemId(@Path("item") String item);

    //get review by rental id
    @GET("api/review/rental/{rental_id}")
    Call<Review> getReviewByRentalId(@Path("rental_id") String rental_id);


    //Owner Reviews
    @GET("api/reviews/owner/{owner}")
    Call<ArrayList<Review>> getReviewsByOwnerId(@Path("owner") String owner);

    //Update a Review
    @PUT("api/review/{rental_id}")
    Call<Review> updateReview(@Path("rental_id") String rental_id, @Body Review review);

    //Create a Review
    @POST("api/reviews")
    Call<Review> addReview(@Body Review review);

}
