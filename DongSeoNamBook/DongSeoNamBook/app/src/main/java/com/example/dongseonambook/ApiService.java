package com.example.dongseonambook;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/submit_feel")
    Call<Void> sendFeel(@Body FeelData data);
}