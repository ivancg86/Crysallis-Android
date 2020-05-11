package com.example.chrysallis.Api.ApiService;

import com.example.chrysallis.classes.Documento;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DocumentosService {
    @GET("api/Documentos/SearchEvent/{id_evento}")
    Call<ArrayList<Documento>> busquedaDocumentosEvento(@Path("id_evento") int id_evento);
}
