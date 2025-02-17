package com.korinek.indoorlocalizatorapp.utils.api;

import com.korinek.indoorlocalizatorapp.model.BuildingApiModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    Call<BuildingApiModel> getBuildingData(@Url String url);
}
