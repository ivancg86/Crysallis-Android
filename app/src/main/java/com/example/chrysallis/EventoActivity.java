package com.example.chrysallis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.AsistirService;
import com.example.chrysallis.Api.ApiService.DocumentosService;
import com.example.chrysallis.adapters.DocumentoAdapter;
import com.example.chrysallis.classes.Asistir;
import com.example.chrysallis.classes.Documento;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.ErrorMessage;
import com.example.chrysallis.components.GeocodingLocation;
import com.example.chrysallis.components.Mail;
import com.example.chrysallis.components.Traductor;
import com.example.chrysallis.components.languageManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.chrysallis.components.PasswordUtils.generateCode;

public class EventoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Evento evento;
    private Socio socio;
    private Boolean asistencia = false;
    private ArrayList<Documento> documentos;
    private Boolean traducido;
    private int asistentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Views
        TextView txtTraducido = findViewById(R.id.txtTraducido);
        TextView txtEvent = findViewById(R.id.txtEvent);
        TextView txtCom = findViewById(R.id.txtComunEvent);
        TextView txtDate = findViewById(R.id.txtDateEvent);
        TextView txtLimitDate = findViewById(R.id.txtLimitDateEvent);
        TextView txtTime = findViewById(R.id.txtTimeEvent);
        TextView txtDescription = findViewById(R.id.txtDesEvent);
        TextView txtLocation = findViewById(R.id.txtLocationEvent);
        Button btnJoin = findViewById(R.id.buttonJoin);
        ImageView imgEvento = findViewById(R.id.imgEvent);
        GridView gridDocs = findViewById(R.id.gridDocs);
        //Se recupera el intent y los dos extras (socio y evento)
        Intent intent = getIntent();
        evento = (Evento) intent.getSerializableExtra("evento");
        socio = (Socio)intent.getSerializableExtra("socio");
        //Asignación de valores a las views
        txtEvent.setText(evento.getNombre());
        txtCom.setText(evento.getComunidades().getNombre());
        String date = convertDate(evento.getFecha());
        String limitDate = convertDate(evento.getFechaLimite());
        txtDate.setText(date);
        txtLimitDate.setText(limitDate);
        String time = evento.getHora().substring(0,5);
        txtTime.setText(time);
        txtLocation.setText(evento.getUbicacion());

        String descripcionTraducida = Traductor.traducir(evento.getDescripcion(), this);
        txtDescription.setText(descripcionTraducida);
        if(evento.getDescripcion().equals(descripcionTraducida)){
            txtTraducido.setVisibility(View.GONE);
            traducido = false;
        }
        else{
            txtTraducido.setVisibility(View.VISIBLE);
            traducido = true;
        }


        //Se obtiene la fecha actual
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(currentTime);
        String formattedDate = df.format(currentTime);

        //Recupera los documentos
        cargarDocumentos();
        //On Click del textView de la dirección que abre Google Maps
        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search for restaurants nearby
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + txtLocation.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        txtTraducido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(traducido){
                    txtDescription.setText(evento.getDescripcion());
                    traducido = false;
                    txtTraducido.setText(getString(R.string.noTraducido));
                }
                else{
                    txtDescription.setText(descripcionTraducida);
                    traducido = true;
                    txtTraducido.setText(getString(R.string.traducido));
                }
            }
        });


        gridDocs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Documento documento = documentos.get(position);
                byte[] pdfAsBytes = Base64.decode(documento.getDocumento(), 0);

                File filePath = new File(getCacheDir() + documento.getNombre());
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(filePath, true);
                    os.write(pdfAsBytes);
                    os.flush();
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent1 = new Intent(EventoActivity.this,PdfViewActivity.class);
                intent1.putExtra("file",filePath);
                try {
                    startActivity(intent1);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }

            }
        });

        //Si el evento tiene una imagen se convierte a Bitmap y se le asigna a la View
        if(evento.getImagen() != null){
            byte[] byteArray = Base64.decode(evento.getImagen(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgEvento.setImageBitmap(bmp);
        }
        //Se comprueba si el socio ya está participando en evento para que no se vuelva a apuntar y el texto del botón cambie
        AsistirService asistirService = Api.getApi().create(AsistirService.class);
        Call<Asistir> asistirCall = asistirService.getAsistir(socio.getId(),evento.getId());
        asistirCall.enqueue(new Callback<Asistir>() {
            @Override
            public void onResponse(Call<Asistir> call, Response<Asistir> response) {
                switch(response.code()){
                    case 200:
                        Asistir asistir = response.body();
                        asistencia = true;
                        if(evento.getFecha().compareTo(currentDate) >= 0){
                            btnJoin.setText(getString(R.string.joined));
                        }else{
                            btnJoin.setText(getString(R.string.rate));
                        }
                        break;
                    case 404:
                        asistencia = false;
                    default:
                        break;
                }
            }
            @Override
            public void onFailure(Call<Asistir> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(evento.getFecha().compareTo(currentDate) >= 0){
                    if(!asistencia){
                        if(evento.getFechaLimite().compareTo(currentDate) >= 0){
                            showDialogAttendance();
                        }else{
                            Toast.makeText(getApplicationContext(),getString(R.string.fechaLimite), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        //Si el socio está apuntado se muestra un diálogo para que se pueda desapuntar
                        showDialogNotAttendance();
                    }
                }else{
                    if(asistencia){
                        showDialogAssessment();
                    }
                }
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String address = evento.getUbicacion();
        GeocodingLocation locationAddress = new GeocodingLocation();
        locationAddress.getAddressFromLocation(address,
                getApplicationContext(), new GeocoderHandler());
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

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if(locationAddress != null){
                String[] result = locationAddress.split(";");
                double latitude= Double.parseDouble(result[0]);
                double longitude= Double.parseDouble(result[1]);
                // Add a marker in the event and move the camera
                LatLng place = new LatLng(latitude,longitude);
                mMap.addMarker(new MarkerOptions().position(place).title(getString(R.string.event)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            }else{
                LatLng place = new LatLng(41.384724, 2.171768);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
            }
        }
    }


    private void showDialogNotAttendance(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle(Html.fromHtml("<b>"+getString(R.string.notAttendanceConfirmation)+"</b>"));
         builder.setMessage(getString(R.string.notAttendance));
         builder.setNegativeButton(getString(R.string.no),null);
         builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 AsistirService asistirService = Api.getApi().create(AsistirService.class);
                 Call<String> asistirCall = asistirService.deleteAsistir(socio.getId(),evento.getId());
                 asistirCall.enqueue(new Callback<String>() {
                     @Override
                     public void onResponse(Call<String> call, Response<String> response) {
                         if(response.isSuccessful()){
                             Toast.makeText(getApplicationContext(),getString(R.string.notAttendanceConfirmed), Toast.LENGTH_LONG).show();
                             Button btnJoin = findViewById(R.id.buttonJoin);
                             btnJoin.setText(getString(R.string.join));
                             asistencia = false;
                             Asistir asistir = new Asistir(socio.getId(),evento.getId());
                             socio.getAsistir().remove(asistir);
                         }else{
                             Gson gson = new Gson();
                             ErrorMessage mensajeError = gson.fromJson(response.errorBody().charStream(), ErrorMessage.class);
                             Toast.makeText(getApplicationContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void onFailure(Call<String> call, Throwable t) {
                         Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 });
             }
         });
        builder.show();
    }
    private void showDialogAttendance() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_asistir, null);


        builder.setTitle(Html.fromHtml("<b>"+getString(R.string.attendanceConfirmation)+"</b>"));
        builder.setView(v);

        final EditText editTextNumAttendants = v.findViewById(R.id.editTextNumAttendants);
        builder.setPositiveButton(getString(R.string.accept), null);
        builder.setNegativeButton(getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnAccept = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editTextNumAttendants.getText().toString().equals("") && Integer.parseInt(editTextNumAttendants.getText().toString()) != 0){
                            int numAsist = Integer.parseInt(editTextNumAttendants.getText().toString());
                            AsistirService asistirService = Api.getApi().create(AsistirService.class);
                            Call<Integer> asistentesCall = asistirService.getAsistirTotal(evento.getId());
                            asistentesCall.enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    switch (response.code()){
                                        case 200:
                                            asistentes = response.body();
                                            if(evento.getNumAsistentes() > 0){
                                                if((evento.getNumAsistentes() - asistentes) >= numAsist){
                                                    apuntarseEvento(numAsist,asistirService);
                                                    dialog.dismiss();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), getString(R.string.notSpots) + " " + String.valueOf(evento.getNumAsistentes()-asistentes), Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                apuntarseEvento(numAsist,asistirService);
                                                dialog.dismiss();
                                            }
                                            break;
                                        case 404:
                                            break;
                                    }
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(),getString(R.string.notNumber), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }


    private void showDialogAssessment() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_valorar, null);


        builder.setTitle(Html.fromHtml("<b>"+getString(R.string.attendanceConfirmation)+"</b>"));
        builder.setView(v);
        final RatingBar ratingBarPoints = v.findViewById(R.id.ratingBarPuntos);
        final EditText editTextComment = v.findViewById(R.id.editTextComment);


        builder.setPositiveButton(getString(R.string.accept), null);
        builder.setNegativeButton(getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button btnAccept = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ratingBarPoints.getRating() != 0){
                            Asistir asistir = getAsistir();
                            if(asistir != null){
                                asistir.setValoracion((int)ratingBarPoints.getRating());
                                asistir.setComentario(editTextComment.getText().toString());
                                AsistirService asistirService = Api.getApi().create(AsistirService.class);
                                Call<Asistir> asistirCall = asistirService.putAsistir(socio.getId(),evento.getId(),asistir);
                                asistirCall.enqueue(new Callback<Asistir>() {
                                    @Override
                                    public void onResponse(Call<Asistir> call, Response<Asistir> response) {
                                        if(response.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),getString(R.string.ratingSaved), Toast.LENGTH_LONG).show();

                                        }else{
                                            Gson gson = new Gson();
                                            ErrorMessage mensajeError = gson.fromJson(response.errorBody().charStream(), ErrorMessage.class);
                                            Toast.makeText(getApplicationContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<Asistir> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                dialog.dismiss();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), getString(R.string.noStars), Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
        dialog.show();
    }

    public void enviarMail(String codigoAsistir){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Mail m=new Mail("eventschrysallis@gmail.com","chrysallis2005");
        String linkBaja = "http://abp-politecnics.com/2020/dm0201/api/Asistir/eliminar/" + codigoAsistir;

        String[] toArr = {socio.getMail()};
        m.setTo(toArr);
        m.setFrom("Chrysallis");
        m.setSubject(evento.getNombre());

        m.setBody(getString(R.string.apuntado) + ": " + evento.getNombre() + "\n" +
                getString(R.string.description) + ": " + evento.getDescripcion() + "\n" +
                getString(R.string.date) + ": " + evento.getFecha().substring(0,10) + "\n" +
                getString(R.string.time) + ": " + evento.getHora().substring(0,5) + "\n\n" +

                //Enlace de baja
                getString(R.string.notAttendanceConfirmationEmail) + linkBaja + "\n" +

                getString(R.string.noResponder));

        try {
            boolean i= m.send();
            if(i != true){
                Toast.makeText(getApplicationContext(),R.string.errorEmail,Toast.LENGTH_LONG).show();
            }

        } catch (Exception e2) {
            Toast.makeText(getApplicationContext(), e2.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public String convertDate(String date){
        String formatDate[] = date.substring(0,10).split("-");
        date = formatDate[2] + "-" + formatDate[1] + "-" + formatDate[0];
        return date;
    }

    public Asistir getAsistir(){
        Asistir a = null;
        boolean found = false;
        Iterator<Asistir> iterator = socio.getAsistir().iterator();
        while(iterator.hasNext() && !found){
            Asistir aux = (Asistir) iterator.next();
            if(aux.getId_evento() == evento.getId() && aux.getId_socio() == socio.getId()){
                found = true;
                a = aux;
            }
        }
        return a;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("socio", socio);
        setResult(RESULT_OK, resultIntent);
        finish();
        super.onBackPressed();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        languageManager.loadLocale(this);
    }

    public void cargarDocumentos(){
        GridView gridDocs = findViewById(R.id.gridDocs);
        TextView txtNoDocs = findViewById(R.id.msgNoDocuments);
        DocumentosService documentosService = Api.getApi().create(DocumentosService.class);
        Call<ArrayList<Documento>> listCall = documentosService.busquedaDocumentosEvento(evento.getId());
        listCall.enqueue(new Callback<ArrayList<Documento>>() {
            @Override
            public void onResponse(Call<ArrayList<Documento>> call, Response<ArrayList<Documento>> response) {
                switch (response.code()){
                    case 200:
                        documentos = response.body();
                        if(!documentos.isEmpty()){
                            txtNoDocs.setVisibility(View.GONE);
                            //Se crea el adapter y se asigna a la grid
                            DocumentoAdapter documentoAdapter = new DocumentoAdapter(getApplicationContext(),documentos);
                            gridDocs.setAdapter(documentoAdapter);
                        }
                        else{
                            txtNoDocs.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Documento>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void apuntarseEvento(int numAsist, AsistirService asistirService){
        String codigoAsistir = generateCode();
        Asistir asistir = new Asistir(socio.getId(),evento.getId(),numAsist, codigoAsistir);
        Call<Asistir> asistirCall = asistirService.insertAsistir(asistir);
        asistirCall.enqueue(new Callback<Asistir>() {
            @Override
            public void onResponse(Call<Asistir> call, Response<Asistir> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),getString(R.string.attendanceConfirmed), Toast.LENGTH_LONG).show();
                    enviarMail(codigoAsistir);
                    Button btnJoin = findViewById(R.id.buttonJoin);
                    btnJoin.setText(getString(R.string.joined));
                    asistencia = true;
                    socio.getAsistir().add(asistir);
                }else{
                    Gson gson = new Gson();
                    ErrorMessage mensajeError = gson.fromJson(response.errorBody().charStream(), ErrorMessage.class);
                    Toast.makeText(getApplicationContext(), mensajeError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Asistir> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
