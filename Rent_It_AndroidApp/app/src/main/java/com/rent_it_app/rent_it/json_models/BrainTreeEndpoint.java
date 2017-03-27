package com.rent_it_app.rent_it.json_models;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Miz on 3/20/17.
 */

public interface BrainTreeEndpoint {

    @GET("/api/bt/client_token/{uid}")
    Call<ResponseBody> getToken(@Path("uid") String uid);


}
