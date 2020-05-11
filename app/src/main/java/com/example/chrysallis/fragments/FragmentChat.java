package com.example.chrysallis.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.EventosService;
import com.example.chrysallis.ChatActivity;
import com.example.chrysallis.R;
import com.example.chrysallis.adapters.EventoChatAdapter;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.ErrorMessage;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentChat extends Fragment {

    public final static int REQUEST_CHAT_ACTIVITY = 1;
    private ArrayList<Evento> eventos;
    private RecyclerView recyclerView;
    private Socio socio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        eventos = new ArrayList<>();
        recyclerView = view.findViewById(R.id.RecyclerChats);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayoutChats);
        cargarEventos(view);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarEventos(view);
                refreshLayout.setRefreshing(false);
            }
        });
        return view;

    }

    public void cargarEventos(View view){
        //Inicializar el recycler con el adapter para que no de error de no adapter attached: skipping layout
        EventoChatAdapter adaptador = new EventoChatAdapter();
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TextView msgNoEvents = view.findViewById(R.id.msgNotEventsChat);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(currentTime);
        EventosService eventosService = Api.getApi().create(EventosService.class);
        Call<ArrayList<Evento>> eventosCall = eventosService.getEventosApuntadoSocio(socio.getId(), formattedDate);
        eventosCall.enqueue(new Callback<ArrayList<Evento>>() {
            @Override
            public void onResponse(Call<ArrayList<Evento>> call, Response<ArrayList<Evento>> response) {
                switch (response.code()){
                    case 200:
                        eventos = response.body();
                        if(!eventos.isEmpty()){
                            EventoChatAdapter adaptador = new EventoChatAdapter(eventos);
                            recyclerView.setAdapter(adaptador);
                            recyclerView.setVisibility(View.VISIBLE);
                            msgNoEvents.setVisibility(View.GONE);
                            //Listener para abrir el seleccionado
                            adaptador.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("evento", eventos.get(recyclerView.getChildAdapterPosition(v)));
                                    intent.putExtra("socio", socio);
                                    startActivityForResult(intent, REQUEST_CHAT_ACTIVITY);
                                }
                            });

                        }
                        else{
                            msgNoEvents.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
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
    }
    public FragmentChat(Socio socio){
        this.socio= socio;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CHAT_ACTIVITY){
            getFragmentManager().beginTransaction().detach(this).attach(this).addToBackStack(null).commit();
        }
    }
}
