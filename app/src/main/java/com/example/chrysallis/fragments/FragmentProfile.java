package com.example.chrysallis.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import androidx.appcompat.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.ComunidadesService;
import com.example.chrysallis.Api.ApiService.SociosService;
import com.example.chrysallis.ApuntadoActivity;
import com.example.chrysallis.DestacadosActivity;
import com.example.chrysallis.MainActivity;
import com.example.chrysallis.R;
import com.example.chrysallis.adapters.ComunidadesSpinnerAdapter;
import com.example.chrysallis.classes.Comunidad;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.languageManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class FragmentProfile extends Fragment {
    private static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,}$";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1 ;
    private Socio socio;
    private ImageView imagenPerfil;
    private String idioma;
    private String lang;
    TextView ubicacionPerfil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    //Cuando se crea la activity
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imagenPerfil = getView().findViewById(R.id.ImgPerfil);
        mostrarPerfil();
    }

    public FragmentProfile(Socio socio) {
        this.socio = socio;
    }

    public void mostrarPerfil(){
        TextView nombrePerfil = getView().findViewById(R.id.nombrePerfil);
        ubicacionPerfil = getView().findViewById(R.id.ubicacionPerfil);
        TextView idiomaPerfil = getView().findViewById(R.id.languagePerfil);
        TextView seeEvents = getView().findViewById(R.id.eventosPerfil);
        TextView txtComunidad = getView().findViewById(R.id.ubicacionPerfil);
        TextView txtPassword = getView().findViewById(R.id.passwordPerfil);
        TextView txtIdioma = getView().findViewById(R.id.languagePerfil);
        TextView txtContact = getView().findViewById(R.id.contactarPerfil);
        TextView txtLogout = getView().findViewById(R.id.txtLogout);

        ImageButton btnEditPassword = getView().findViewById(R.id.buttonEditPassword);
        ImageButton btnEditLanguage =  getView().findViewById(R.id.buttonEditLanguage);
        ImageButton btnEditCommunity = getView().findViewById(R.id.buttonEditComunidad);
        ImageButton btnPowerOff = getView().findViewById(R.id.powerOff);
        String nombreEmoji = StringEscapeUtils.unescapeJava(socio.getNombre());
        nombrePerfil.setText(nombreEmoji);

        getComunidad();

        refrescarImagen();
        idioma = "english";
        if(socio.getIdiomaDefecto() != null){
            idioma = socio.getIdiomaDefecto().toLowerCase();
        }
        switch (idioma){
            case "spanish":
                idioma = getResources().getString(R.string.Spanish);
                break;
            case "euskera":
                idioma = getResources().getString(R.string.Euskera);
                break;
            case "galician":
                idioma = getResources().getString(R.string.Galician);
                break;
            case "catalan":
                idioma = getResources().getString(R.string.Catalan);
                break;
            case "english":
            default:
                idioma = getResources().getString(R.string.English);
                break;
        }
        idiomaPerfil.setText(idioma);

        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionGranted()){
                    cambiarImagen();
                }
            }
        });

        //Listeners cambio de Comunidad
        txtComunidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComunidad();
            }
        });

        btnEditCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComunidad();
            }
        });

        //Listener de Eventos
        seeEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEvents();
            }
        });

        //Listeners de cambio de password
        txtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPassword();
            }
        });

        btnEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPassword();
            }
        });

        //Listeners de cambio de idioma
        txtIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLanguage();
            }
        });

        btnEditLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLanguage();
            }
        });

        btnPowerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerOffButton();
            }
        });

        txtContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://chrysallis.org.es/contacto/general/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerOffButton();
            }
        });



    }

    private void getComunidad() {
        ComunidadesService comunidadesService = Api.getApi().create(ComunidadesService.class);
        Call<Comunidad> comunidadCall = comunidadesService.getComunidad(socio.getId_comunidad());
        comunidadCall.enqueue(new Callback<Comunidad>() {
            @Override
            public void onResponse(Call<Comunidad> call, Response<Comunidad> response) {
                switch (response.code()) {
                    case 200:
                        Comunidad comunidad = response.body();
                        ubicacionPerfil.setText(comunidad.getNombre());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<Comunidad> call, Throwable t) {
                Toast.makeText(getActivity(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }


    public void refrescarImagen(){

        if(socio.getImagenUsuario() != null){

            byte[] byteArray = Base64.decode(socio.getImagenUsuario(), Base64.DEFAULT);

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            imagenPerfil.setImageBitmap(bmp);
        }else{
            imagenPerfil.setImageResource(R.drawable.imagen_profile);
        }

    }



    public void cambiarImagen(){

        final CharSequence[] options = { getResources().getString(R.string.TakePhoto), getResources().getString(R.string.ChooseGallery),getResources().getString(R.string.cancel) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.ChooseProfilePicture));

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(getResources().getString(R.string.TakePhoto))) {
                    if(hasCamera()){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);

                    }else{
                        Toast.makeText(getContext(), getResources().getString(R.string.NoCamera), Toast.LENGTH_LONG);
                    }
                } else if (options[item].equals(getResources().getString(R.string.ChooseGallery))) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private boolean hasCamera() {
        if (getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)){
            return true;
        } else {
            return false;
        }
    }


    public  boolean isPermissionGranted() {
        boolean permiso;
        //si la version es mayor de 6.0
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.CAMERA) //Si tiene permiso
                    == PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) //Si tiene permiso
                    == PackageManager.PERMISSION_GRANTED ) {
                Log.v("TAG",getResources().getString(R.string.PermisoConcedido));
                permiso = true;
            } else { //Si no tiene permiso lo pide
                Log.v("TAG",getResources().getString(R.string.PermisoDenegado));
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                permiso = false;
            }
        }
        else { //Si la version es menor de android 6.0
            Log.v("TAG",getResources().getString(R.string.PermisoConcedido));
            permiso = true;
        }
        return permiso;
    }


    //Método para comprobar que el usuario a dado permisos y ejecutar el programa
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(requestCode==1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //si el usuario acepta los permisos
                cambiarImagen();  //Ejecutamos el programa
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.PermisoDenegado), Toast.LENGTH_SHORT).show();
                //si el usuario no nos da permisos no hacemos nada
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //ImageView foto = getView().findViewById(R.id.ImgPerfil);
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap bmp = (Bitmap) data.getExtras().get("data");
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap resized = Bitmap.createScaledBitmap(bmp, 150, 150, true);
                        resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] imagen = stream.toByteArray();

                        String foto = Base64.encodeToString(imagen, Base64.DEFAULT);
                        if(imagen.length < 2097152){
                            socio.setImagenUsuario(foto);
                            saveUser(getResources().getString(R.string.ImageChanged), getResources().getString(R.string.ImageNotChanged));
                            refrescarImagen();
                        }
                        else{
                            Toast.makeText(getActivity(), getResources().getString(R.string.ImageCannot2mb),Toast.LENGTH_LONG).show();
                        }
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {

                        Uri uri = data.getData();
                        byte[] imagen = convertImageToByte(uri);

                        String foto = Base64.encodeToString(imagen, Base64.DEFAULT);

                        if(imagen.length < 2097152){
                            socio.setImagenUsuario(foto);
                            saveUser(getResources().getString(R.string.ImageChanged), getResources().getString(R.string.ImageNotChanged));
                            refrescarImagen();
                        }
                        else{
                            Toast.makeText(getActivity(), getResources().getString(R.string.ImageCannot2mb),Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    }

    public byte[] convertImageToByte(Uri uri){
        byte[] data = null;
        try {
            ContentResolver cr = getContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            Bitmap resized = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, baos);
            data = baos.toByteArray();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void showDialogPassword() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_password, null);


        builder.setTitle(Html.fromHtml("<b>"+getString(R.string.changePassword)+"</b>"));
        builder.setView(v);

        final EditText editTextPassword = v.findViewById(R.id.editTextPassword);
        final EditText editTextConfirmation = v.findViewById(R.id.editTextConfirmation);

        builder.setPositiveButton(R.string.accept, null);
        builder.setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnAccept = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editTextPassword.getText().toString().equals(editTextConfirmation.getText().toString())){
                            if(Pattern.matches(REGEX, editTextPassword.getText().toString()) && Pattern.matches(REGEX,editTextConfirmation.getText().toString())){
                                String password = MainActivity.encryptThisString(editTextPassword.getText().toString());
                                socio.setPassword(password);
                                saveUser(getString(R.string.passwordChanged),getString(R.string.passwordNotChanged));
                                dialog.dismiss();
                            }else{
                                Toast.makeText(getActivity(),getString(R.string.security), Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),getString(R.string.passwordsNotMatch), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void showDialogLanguage() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        builder.setTitle(R.string.changeLanguage);

        //list of items
        final String[] items = getResources().getStringArray(R.array.languages);
        int pos = 0;

        do{
            if( !items[pos].equals(idioma)){
                pos++;
            }
        }while(pos < items.length && !items[pos].equals(idioma));
        // set single choice items
        builder.setSingleChoiceItems(items, pos,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                socio.setIdiomaDefecto("English");
                                idioma = getResources().getString(R.string.English);
                                lang = "en";
                                break;
                            case 1:
                                socio.setIdiomaDefecto("Spanish");
                                idioma = getResources().getString(R.string.Spanish);
                                lang = "es";
                                break;
                            case 2:
                                socio.setIdiomaDefecto("Catalan");
                                idioma = getResources().getString(R.string.Catalan);
                                lang = "ca";
                                break;
                            case 3:
                                socio.setIdiomaDefecto("Euskera");
                                idioma = getResources().getString(R.string.Euskera);
                                lang = "eu";
                                break;
                            case 4:
                                socio.setIdiomaDefecto("Galician");
                                idioma = getResources().getString(R.string.Galician);
                                lang = "gl";
                                break;
                        }
                    }
                });

        builder.setPositiveButton(R.string.accept,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveUser(getString(R.string.languageChanced), getString(R.string.languageNotChanced));
                        refrescarIdioma();
                        setLocale(lang);
                    }
                });

        builder.setNegativeButton(R.string.cancel,null);
        builder.show();
    }

    public void saveUser(String success, String fail){
        SociosService sociosService = Api.getApi().create(SociosService.class);
        Call<Socio> socioCall = sociosService.putSocio(socio.getId(),socio);
        socioCall.enqueue(new Callback<Socio>() {
            @Override
            public void onResponse(Call<Socio> call, Response<Socio> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(),success,Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), fail,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Socio> call, Throwable t) {
                Toast.makeText(getActivity(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void refrescarIdioma(){
        TextView idiomaPerfil = getView().findViewById(R.id.languagePerfil);
        idiomaPerfil.setText(idioma);
    }

    /*Método que nos cambia el idioma en función del seleccionado*/
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        DestacadosActivity.refrescar(getFragmentManager());
        saveLocale(lang);

    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getActivity().getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }

    public void powerOffButton(){
        saveLogin(-1);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
        getActivity().finish();


        android.os.Process.killProcess(android.os.Process.myPid());
        //Process.killProcess(Process.myPid());
        //System.exit(0);

        //System.exit(2);

    }


    public void saveLogin(int id){
        String usuario = "user";
        SharedPreferences settings = getActivity().getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putInt(usuario, id);
        editor.commit();
    }

    private void showDialogComunidad() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_comunidad, null);

        builder.setTitle(Html.fromHtml("<b>"+getString(R.string.community)+"</b>"));
        builder.setView(v);


        Spinner spnComunidades = v.findViewById(R.id.spinnerComunidades);
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
                        break;
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comunidad>> call, Throwable t) {

            }
        });



        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                socio.setId_comunidad(((Comunidad) spnComunidades.getSelectedItem()).getId());
                saveUser(getResources().getString(R.string.communityChanged), getResources().getString(R.string.communityNotChanged));

                DestacadosActivity.refrescar(getFragmentManager());
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    public void showEvents(){

        Intent intent = new Intent(getActivity(), ApuntadoActivity.class);
        intent.putExtra("socio", socio);
        startActivity(intent);
    }
}
