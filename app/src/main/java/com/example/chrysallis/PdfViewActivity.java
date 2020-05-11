package com.example.chrysallis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.chrysallis.components.languageManager;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import static android.view.View.GONE;

public class PdfViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        Intent intent = getIntent();
        File file = (File)intent.getSerializableExtra("file");
        PDFView pdfView = findViewById(R.id.pdfView);
        ImageView imageView = findViewById(R.id.imgDoc);
        if(file.toString().contains(".pdf")){
            pdfView.fromFile(file).load();
            imageView.setVisibility(GONE);
        }else{
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            pdfView.setVisibility(GONE);
        }

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        languageManager.loadLocale(this);
    }
}
