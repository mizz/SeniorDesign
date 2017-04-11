package com.rent_it_app.rent_it.json_models;

import com.google.firebase.database.Transaction;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Miz on 3/20/17.
 */

public interface BraintreeEndpoint {

    @GET("/api/bt/client_token/{uid}")
    Call<ResponseBody> getToken(@Path("uid") String uid);

    //Create a Transaction
    @POST("api/bt/complete_payment/{rental_id}")
    Call<GenericResult> processTransaction(@Path("rental_id") String rental_id, @Body TransactionAmount transactionAmount);

    //Add paymentMethodToken to the rental so we have the right details when charging
    @PUT("api/bt/add_payment_method/{rental_id}")
    Call<RentalPaymentMethodNonce> addPaymentMethodToken(@Path("rental_id") String rental_id, @Body RentalPaymentMethodNonce rpmn);
}
