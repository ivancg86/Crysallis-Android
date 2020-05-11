package com.example.chrysallis.Api.ApiService;

import com.example.chrysallis.classes.Comunidad;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ComunidadesService {
    @GET("api/Comunidades")
    Call<ArrayList<Comunidad>> getComunidades();
    @GET("api/Comunidades/{id}")
    Call<Comunidad> getComunidad(@Path("id") int id);
}
