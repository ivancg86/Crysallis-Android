package com.example.chrysallis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrysallis.Api.Api;
import com.example.chrysallis.Api.ApiService.SociosService;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.languageManager;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        TextView recuperar = findViewById(R.id.recuperarClave);

        //comprobar que está logueado
        int userId = loadLogin();
        if(userId != -1){
            doLogin(userId);
            //finish();
        }

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                String passwordEncrypted = encryptThisString(password);
                SociosService sociosService = Api.getApi().create(SociosService.class);
                Call<Socio> callSocioLogin = sociosService.SocioLogin(editTextPhone.getText().toString(),passwordEncrypted);
                callSocioLogin.enqueue(new Callback<Socio>() {
                    @Override
                    public void onResponse(Call<Socio> call, Response<Socio> response) {
                        switch (response.code()){
                            case 200:
                                Socio socio = response.body();
                                if(socio != null){
                                    if(socio.isActivo()){
                                        saveLogin(socio.getId());
                                        idiomaSocio(socio);
                                        Intent intent = new Intent(MainActivity.this, DestacadosActivity.class);
                                        intent.putExtra("socio", socio);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.notActive), Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.loginIncorrect), Toast.LENGTH_LONG).show();
                                }

                                break;
                            case 404:
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.loginIncorrect), Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Socio> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecuperarActivity.class);
                startActivity(intent);
            }
        });
    }

    //Método que encipta el password con SHA-512
    public static String encryptThisString(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            hashtext = hashtext.toUpperCase();
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void idiomaSocio(Socio socio){
        if(socio.getIdiomaDefecto() != null){
            String idioma = socio.getIdiomaDefecto().toLowerCase();
            switch (idioma){
                case "spanish":
                    setLocale("es", false);
                    break;
                case "euskera":
                    setLocale("eu", false);
                    break;
                case "galician":
                    setLocale("gl", false);
                    break;
                case "catalan":
                    setLocale("ca", false);
                    break;
                case "english":
                default:
                    setLocale("en", false);
                    break;
            }
        }

    }

    /*Método que nos cambia el idioma del juego en función del seleccionado*/
    public void setLocale(String lang, boolean refrescar) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        if(!lang.equals(language)){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(langPref, lang);
            editor.commit();
        }


        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        setLocale(language, true);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        languageManager.loadLocale(this);
    }

    public int loadLogin() {
        String usuario = "user";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        int usuarioConectado = prefs.getInt(usuario, -1);
        return usuarioConectado;
    }

    public void doLogin(int id){
        SociosService sociosService = Api.getApi().create(SociosService.class);
        Call<Socio> callSocioLogin = sociosService.SocioLoginId(id);
        callSocioLogin.enqueue(new Callback<Socio>() {
            @Override
            public void onResponse(Call<Socio> call, Response<Socio> response) {
                switch (response.code()){
                    case 200:
                        Socio socio = response.body();
                        if(socio != null){
                            if(socio.isActivo()){
                                idiomaSocio(socio);
                                Intent intent = new Intent(MainActivity.this, DestacadosActivity.class);
                                intent.putExtra("socio", socio);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.notActive), Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.loginIncorrect), Toast.LENGTH_LONG).show();
                        }

                        break;
                    case 404:
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.loginIncorrect), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<Socio> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getCause() + "-" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveLogin(int id){
        String usuario = "user";
        SharedPreferences settings = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putInt(usuario, id);
        editor.commit();
    }
}
