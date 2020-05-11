package com.example.chrysallis.Api.ApiService;

import com.example.chrysallis.classes.Asistir;
import com.example.chrysallis.classes.Evento;

import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EventosService {
    @GET("api/Eventos/SearchC/{id_comunidad}/{date}")
    Call<ArrayList<Evento>> busquedaEventosComunidad(@Path("id_comunidad")int id_comunidad,@Path("date")String date);

    @GET("api/Eventos/SearchNC/{nombre}/{id_comunidad}/{date}")
    Call<ArrayList<Evento>> busquedaEventosNameComunidad(@Path("nombre")String nombre,@Path("id_comunidad")int id_comunidad,@Path("date")String date);

    @GET("api/Eventos/SearchCD/{id_comunidad}/{date}")
    Call<ArrayList<Evento>> busquedaEventosComunidadDate(@Path("id_comunidad")int id_comunidad,@Path("date")String date);

    @GET("api/Eventos/Search/{nombre}/{id_comunidad}/{date}")
    Call<ArrayList<Evento>> busquedaEventosAll(@Path("nombre")String nombre,@Path("id_comunidad")int id_comunidad,@Path("date")String date);

    @GET ("api/Eventos/SearchSocio/{id_socio}")
    Call<ArrayList<Evento>> getEventosApuntado(@Path("id_socio") int id_socio);

    @GET ("api/Eventos/{id}")
    Call<Evento> getEvento(@Path("id") int id);

    @GET ("api/Eventos/SearchChat/{id_socio}/{date}")
    Call<ArrayList<Evento>> getEventosApuntadoSocio(@Path("id_socio") int id_socio,@Path("date")String date);

}
