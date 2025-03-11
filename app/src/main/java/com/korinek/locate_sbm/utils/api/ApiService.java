package com.korinek.locate_sbm.utils.api;

import com.korinek.locate_sbm.model.api.BuildingApiModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {

    @GET
    Call<BuildingApiModel> getBuildingData(@Url String url);
    @GET
    Call<ResponseBody> setRoomData(@Url String url);
}
