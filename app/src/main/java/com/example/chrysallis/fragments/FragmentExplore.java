package com.example.chrysallis.fragments;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.ComunidadesService;
import com.example.chrysallis.Api.ApiService.EventosService;
import com.example.chrysallis.DestacadosActivity;
import com.example.chrysallis.EventoActivity;
import com.example.chrysallis.R;
import com.example.chrysallis.adapters.ComunidadesSpinnerAdapter;
import com.example.chrysallis.adapters.EventoAdapter;
import com.example.chrysallis.classes.Comunidad;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.DatePickerFragment;
import com.example.chrysallis.components.ErrorMessage;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentExplore extends Fragment {
    //Atributos
    private ArrayList<Evento> eventos;
    private Spinner spnComunidades;
    private RecyclerView recyclerView;
    private EventoAdapter adaptador;
    private Socio socio;
    private TextView msgNotEvents;
    public final static int REQUEST_EVENTO_ACTIVITY = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View item = inflater.inflate(R.layout.fragment_explore, container, false);
        //Views
        EditText txtDate = item.findViewById(R.id.editTextFecha);
        EditText txtName = item.findViewById(R.id.editTextNameEvent);
        spnComunidades = item.findViewById(R.id.spnComunity);
        recyclerView = item.findViewById(R.id.recyclerEventosSearch);
        Button btnSearch = item.findViewById(R.id.buttonSearch);
        msgNotEvents = item.findViewById(R.id.msgNotEventsExplore);

        //Oculta el mensaje de que no hay eventos
        msgNotEvents.setVisibility(getView().GONE);

        adaptador = new EventoAdapter();
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Rellena el spinner de comunidades llamando a la Api
        ComunidadesService comunidadesService = Api.getApi().create(ComunidadesService.class);
        Call<ArrayList<Comunidad>> comunidadesCall = comunidadesService.getComunidades();
        comunidadesCall.enqueue(new Callback<ArrayList<Comunidad>>() {
            @Override
            public void onResponse(Call<ArrayList<Comunidad>> call, Response<ArrayList<Comunidad>> response) {
                switch (response.code()) {
                    case 200:
                        ArrayList<Comunidad> comunidades = response.body();
                        ComunidadesSpinnerAdapter spinnerAdapter = new ComunidadesSpinnerAdapter(getActivity(), comunidades);
                        spnComunidades.setAdapter(spinnerAdapter);
                        break;
                    default:
                        Gson gson = new Gson();
                        ErrorMessage mensajeError = gson.fromJson(response.errorBody().charStream(), ErrorMessage.class);
                        Toast.makeText(getContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comunidad>> call, Throwable t) {

            }
        });

        //Muestra un calendario estilo fragment para seleccionar la fecha
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //Evento del btnSearch que dependiendo de los campos rellenados busca una cosa u otra llamando a la Api
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventosService eventosService = Api.getApi().create(EventosService.class);
                Call<ArrayList<Evento>> eventosCall;
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(currentTime);
                switch (comprobarCampos()) {
                    case 0:
                        eventosCall = eventosService.busquedaEventosComunidad(((Comunidad) spnComunidades.getSelectedItem()).getId(),formattedDate);
                        eventosCall.enqueue(new Callback<ArrayList<Evento>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Evento>> call, Response<ArrayList<Evento>> response) {
                                switch (response.code()) {
                                    case 200:
                                        eventos = response.body();
                                        rellenarRecyclerView();
                                        break;
                                    default:
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Evento>> call, Throwable t) {
                                Toast.makeText(getContext(), t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case 1:
                        String[] aux = txtDate.getText().toString().split("/");
                        String date = aux[2] + "-" + aux[1] + "-" + aux[0];
                        eventosCall = eventosService.busquedaEventosComunidadDate(((Comunidad) spnComunidades.getSelectedItem()).getId(), date);
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
                                        Toast.makeText(getContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Evento>> call, Throwable t) {
                                Toast.makeText(getContext(), t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    case 2:
                        eventosCall = eventosService.busquedaEventosNameComunidad(txtName.getText().toString().trim(),((Comunidad) spnComunidades.getSelectedItem()).getId(),formattedDate);
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
                                        Toast.makeText(getContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Evento>> call, Throwable t) {
                                Toast.makeText(getContext(), t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case 3:
                        aux = txtDate.getText().toString().split("/");
                        date = aux[2] + "-" + aux[1] + "-" + aux[0];
                        eventosCall = eventosService.busquedaEventosAll(txtName.getText().toString().trim(),((Comunidad) spnComunidades.getSelectedItem()).getId(),date);
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
                                        Toast.makeText(getContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Evento>> call, Throwable t) {
                                Toast.makeText(getContext(), t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });
        return item;
    }

    public int comprobarCampos() {
        EditText txtDate = getView().findViewById(R.id.editTextFecha);
        EditText txtName = getView().findViewById(R.id.editTextNameEvent);
        int control;
        if (!txtName.getText().toString().trim().equals("") && !txtDate.getText().toString().trim().equals("")) {
            control = 3;
        } else if (!txtName.getText().toString().trim().equals("")) {
            control = 2;
        } else if (!txtDate.getText().toString().trim().equals("")) {
            control = 1;
        } else {
            control = 0;
        }
        return control;
    }

    public void rellenarRecyclerView() {
        if (!eventos.isEmpty()) {
            recyclerView.setVisibility(getView().VISIBLE);
            msgNotEvents.setVisibility(getView().GONE);
            adaptador = new EventoAdapter(eventos);
            recyclerView.setAdapter(adaptador);
            //Listener para abrir el seleccionado
            adaptador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EventoActivity.class);
                    intent.putExtra("evento", eventos.get(recyclerView.getChildAdapterPosition(v)));
                    intent.putExtra("socio",socio);
                    startActivityForResult(intent,REQUEST_EVENTO_ACTIVITY);
                }
            });
        }
        else{
            msgNotEvents.setVisibility(getView().VISIBLE);
            recyclerView.setVisibility(getView().GONE);
            recyclerView.removeAllViewsInLayout();
        }
    }

    private void showDatePickerDialog() {
        TextInputEditText txtDate = getView().findViewById(R.id.editTextFecha);
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                String d, m;
                if(day < 10){
                    d = "0" + day;
                }else{
                    d = Integer.toString(day);
                }
                if((month + 1) < 10){
                    m = "0" + (month + 1);
                }else{
                    m = Integer.toString(month + 1);
                }
                String selected = year + "/" + m + "/" + d;
                final String selectedDate = day + "/" + (month + 1) + "/" + year;

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String formattedDate = sdf.format(currentTime);
                int com = selected.compareTo(formattedDate);
                if(com < 0){
                    txtDate.setText("");
                    Toast.makeText(getContext(),getString(R.string.wrongDate), Toast.LENGTH_LONG).show();
                }else{
                    txtDate.setText(selectedDate);
                }
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_EVENTO_ACTIVITY){
            socio =(Socio)data.getSerializableExtra("socio");
        }
    }

    public FragmentExplore(Socio socio){
        this.socio= socio;
    }

}
