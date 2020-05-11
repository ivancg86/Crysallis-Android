package com.example.chrysallis.Api.ApiService;

import com.example.chrysallis.classes.Mensaje;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MensajesService {
    @GET("api/Mensajes/SearchByEvent/{id_evento}")
    Call<ArrayList<Mensaje>> getMensajesByEvent(@Path ("id_evento") int id_evento);

    @POST("api/Mensajes")
    Call<Mensaje> insertMensaje(@Body Mensaje mensaje);

    @POST("api/Mensajes/eliminar/{id}")
    Call<Mensaje> borrarMensaje(@Path("id") int id);
}
