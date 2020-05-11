package com.example.chrysallis.Api.ApiService;

import com.example.chrysallis.classes.Socio;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SociosService {
    @GET("api/Socios")
    Call<ArrayList<Socio>> getSocios();

    @GET("api/Socios/{telefono}/{password}")
    Call<Socio> SocioLogin(@Path("telefono")String telefono,@Path("password") String password);

    @POST("api/Socios/modificar/{id}")
    Call<Socio>  putSocio(@Path("id") int id, @Body Socio socio);

    @GET("api/Socios/busquedaRecuperar/{mail}/{telefono}")
    Call<Socio> SocioRecuperar(@Path("mail") String mail, @Path("telefono")String telefono);

    @GET("api/Socios/{id}")
    Call<Socio> SocioLoginId(@Path("id") int id);
}
