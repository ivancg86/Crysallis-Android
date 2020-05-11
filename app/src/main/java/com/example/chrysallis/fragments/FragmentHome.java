package com.example.chrysallis.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.EventosService;
import com.example.chrysallis.DestacadosActivity;
import com.example.chrysallis.EventoActivity;
import com.example.chrysallis.R;
import com.example.chrysallis.adapters.EventoAdapter;
import com.example.chrysallis.classes.Documento;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.ErrorMessage;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentHome extends Fragment {
    private ArrayList<Evento> eventos;
    private RecyclerView recyclerView;
    private Socio socio;
    private TextView msgNotEvents;
    public final static int REQUEST_EVENTO_ACTIVITY = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //-----------------------------------PRUEBAS----------------------------------------------//
        eventos = new ArrayList<>();

        recyclerView = view.findViewById(R.id.RecyclerDestacados);
        msgNotEvents = view.findViewById(R.id.msgNotEvents);


        EventoAdapter adaptador = new EventoAdapter();
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(currentTime);
        EventosService eventosService = Api.getApi().create(EventosService.class);
        Call<ArrayList<Evento>> eventosCall = eventosService.busquedaEventosComunidad(socio.getId_comunidad(),formattedDate);
        eventosCall.enqueue(new Callback<ArrayList<Evento>>() {
            @Override
            public void onResponse(Call<ArrayList<Evento>> call, Response<ArrayList<Evento>> response) {
                switch (response.code()){
                    case 200:
                        eventos = response.body();
                        if(!eventos.isEmpty()){
                            msgNotEvents.setVisibility(View.GONE);
                            EventoAdapter adaptador = new EventoAdapter(eventos);
                            recyclerView.setAdapter(adaptador);
                            //Listener para abrir el seleccionado
                            adaptador.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), EventoActivity.class);
                                    intent.putExtra("evento",eventos.get(recyclerView.getChildAdapterPosition(v)));
                                    intent.putExtra("socio", socio);
                                    startActivityForResult(intent,REQUEST_EVENTO_ACTIVITY);
                                }
                            });

                        }
                        break;
                    default:
                        Gson gson = new Gson();
                        ErrorMessage mensajeError = gson.fromJson(response.errorBody().charStream(), ErrorMessage.class);
                        Toast.makeText(getContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Evento>> call, Throwable t) {
                Toast.makeText(getContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_EVENTO_ACTIVITY){
            socio =(Socio)data.getSerializableExtra("socio");
        }
    }

    public FragmentHome(Socio socio){
        this.socio= socio;
    }
}
