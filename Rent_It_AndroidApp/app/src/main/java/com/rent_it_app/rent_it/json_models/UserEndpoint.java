package com.rent_it_app.rent_it.json_models;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Miz on 2/1/17.
 */

public interface UserEndpoint {

    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter


    @GET("api/items/user/{uid}")
    Call<ArrayList<Item>> getItemsByUid(@Path("uid") String uid);

    @PUT("api/item/{id}")
    Call<Item> updateItem(@Path("id") String id, @Body Item item);

    @POST("api/users")
    Call<Item> addItem(@Body Item item);

}
