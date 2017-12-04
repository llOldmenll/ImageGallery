package com.oldmen.imagegallery.Api;

import com.oldmen.imagegallery.Model.PixabayModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by MVP on 04.12.2017.
 */

public interface ApiService {

    @GET("api/")
    Call<PixabayModel> findImg(@Query("key") String key, @Query("q") String q);
}
