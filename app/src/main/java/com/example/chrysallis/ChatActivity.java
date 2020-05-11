package com.example.chrysallis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.AsistirService;
import com.example.chrysallis.Api.ApiService.EventosService;
import com.example.chrysallis.Api.ApiService.MensajesService;
import com.example.chrysallis.adapters.EventoChatAdapter;
import com.example.chrysallis.adapters.MensajeAdapter;
import com.example.chrysallis.classes.Asistir;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Mensaje;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.ErrorMessage;
import com.example.chrysallis.components.languageManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private Evento evento;
    private Socio socio;
    private ArrayList<Mensaje> mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageButton enviar = findViewById(R.id.btnEnviar);
        EditText txtMensaje = findViewById(R.id.txtMensaje);
        SwipyRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);


        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra("evento");
        socio = (Socio) intent.getSerializableExtra("socio");
        setTitle(evento.getNombre());


        cargarMensajes();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensajeString = txtMensaje.getText().toString();
                if(!mensajeString.equals("")){
                    enviarMensaje();
                }

            }
        });


        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                cargarMensajes();
                refreshLayout.setRefreshing(false);
            }
        });

    }

    public void enviarMensaje(){
        EditText txtMensaje = findViewById(R.id.txtMensaje);
        String mensajeString = txtMensaje.getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = df.format(currentTime);

        String mensajeEmoji = StringEscapeUtils.escapeJava(mensajeString);

        Mensaje mensaje = new Mensaje(socio.getId(),evento.getId(), socio.getNombre() + ":" + mensajeEmoji, formattedDate);
        txtMensaje.setText("");
        MensajesService mensajesService = Api.getApi().create(MensajesService.class);
        Call<Mensaje> mensajeCall = mensajesService.insertMensaje(mensaje);
        mensajeCall.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                if(response.isSuccessful()){

                    cargarMensajes();
                }else{
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

    public void cargarMensajes(){
        MensajesService mensajesService = Api.getApi().create(MensajesService.class);
        Call<ArrayList<Mensaje>> mensajesCall = mensajesService.getMensajesByEvent(evento.getId());
        mensajesCall.enqueue(new Callback<ArrayList<Mensaje>>() {
            @Override
            public void onResponse(Call<ArrayList<Mensaje>> call, Response<ArrayList<Mensaje>> response) {
                switch (response.code()){
                    case 200:
                        mensajes = response.body();
                        if(!mensajes.isEmpty()){
                            RecyclerView recyclerViewMensajes = findViewById(R.id.RecyclerMensajes);

                            Collections.sort(mensajes);

                            MensajeAdapter adapter = new MensajeAdapter(mensajes, socio);
                            adapter.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    Mensaje msg = mensajes.get(recyclerViewMensajes.getChildAdapterPosition(v));
                                    if(msg.getId_socio() == socio.getId()){
                                        showDialogDeleteMessage(msg);
                                    }
                                    return false;
                                }
                            });
                            recyclerViewMensajes.setAdapter(adapter);
                            recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerViewMensajes.scrollToPosition(mensajes.size() - 1);

                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Mensaje>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void borrarMensaje(Mensaje msg){
        MensajesService mensajesService1 = Api.getApi().create(MensajesService.class);
        Call<Mensaje> mensajesCall2 = mensajesService1.borrarMensaje(msg.getId());
        mensajesCall2.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                if(response.isSuccessful()){
                    mensajes.remove(msg);
                    Toast.makeText(getApplicationContext(),getString(R.string.messageDeleted), Toast.LENGTH_LONG).show();
                    cargarMensajes();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.messageNotDeleted), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {
            }
        });
    }

    private void showDialogDeleteMessage(Mensaje msg){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle(Html.fromHtml("<b>"+getString(R.string.deleteConfirmation)+"</b>"));
        builder.setMessage(getString(R.string.messageDelete));
        builder.setNegativeButton(getString(R.string.no),null);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                borrarMensaje(msg);
            }
        });
        builder.show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        languageManager.loadLocale(this);
    }
}
