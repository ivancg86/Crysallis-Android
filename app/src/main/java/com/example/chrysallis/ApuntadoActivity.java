package com.example.chrysallis;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.EventosService;
import com.example.chrysallis.adapters.EventoAdapter;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.ErrorMessage;
import com.example.chrysallis.components.languageManager;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ApuntadoActivity extends AppCompatActivity {

    private ArrayList<Evento> eventos;
    private RecyclerView recyclerView;
    private EventoAdapter adaptador;
    private TextView msgNotEvents;
    private Socio socio;

    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_apuntado);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        socio = (Socio)intent.getSerializableExtra("socio");
        int id = socio.getId();
        recyclerView = findViewById(R.id.recyclerEventosApuntado);
        msgNotEvents = findViewById(R.id.msgNotEventsApuntado);

        EventosService eventosService = Api.getApi().create(EventosService.class);
        Call<ArrayList<Evento>> eventosCall = eventosService.getEventosApuntado(id);
        eventosCall.enqueue(new Callback<ArrayList<Evento>>() {
            @Override
            public void onResponse(Call<ArrayList<Evento>> call, Response<ArrayList<Evento>> response) {
                switch (response.code()) {
                    case 200:
                        eventos = response.body();
                        rellenarRecyclerView();
                        break;
                    default:
                        Gson gson = new Gson();
                        ErrorMessage mensajeError = gson.fromJson(response.errorBody().charStream(), ErrorMessage.class);
                        Toast.makeText(getApplicationContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Evento>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void rellenarRecyclerView(){
        if (!eventos.isEmpty()) {
            recyclerView.setVisibility(VISIBLE);
            msgNotEvents.setVisibility(GONE);
            adaptador = new EventoAdapter(eventos);
            recyclerView.setAdapter(adaptador);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            //Listener para abrir el seleccionado
            adaptador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ApuntadoActivity.this, EventoActivity.class);
                    intent.putExtra("evento",eventos.get(recyclerView.getChildAdapterPosition(v)));
                    intent.putExtra("socio",socio);
                    startActivity(intent);

                }
            });
        }
        else{
            msgNotEvents.setVisibility(VISIBLE);
            recyclerView.setVisibility(GONE);
            recyclerView.removeAllViewsInLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        languageManager.loadLocale(this);
    }


}

