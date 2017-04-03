package com.rent_it_app.rent_it.json_models;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Miz on 2/1/17.
 */

public interface RentalEndpoint {

    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("api/rentals/contacted/renter/{renter}")
    Call<ArrayList<Rental>> getContactedRentalsItems(@Path("renter") String renter);

    @GET("api/rentals/active/renter/{renter}")
    Call<ArrayList<Rental>> getActiveRentalsItems(@Path("renter") String renter);

    @PUT("api/rental/request/{rental_id}")
    Call<Rental> sendRequest(@Path("rental_id") String rental_id, @Body Rental rental);

    @PUT("api/rental/{rental_id}")
    Call<Rental> updateRental(@Path("rental_id") String rental_id, @Body Rental rental);

    @POST("api/rentals")
    Call<Rental> addRental(@Body Rental rental);

    //send lender notofication
    @POST("/api/rental/cancel/{rental_id}")
    Call<ResponseBody> cancelRequest(@Path("rental_id") String rental_id);


}
