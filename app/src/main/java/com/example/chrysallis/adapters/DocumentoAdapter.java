package com.example.chrysallis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrysallis.R;
import com.example.chrysallis.classes.Documento;

import java.util.ArrayList;

public class DocumentoAdapter extends ArrayAdapter<Documento> {
    //Atributos
    private ArrayList<Documento> documentos;
    private Context context;

    //Constructor
    public DocumentoAdapter(Context context, ArrayList<Documento> documentos) {
        super(context, R.layout.document_item, documentos);
        // Guardamos los parámetros en variables de clase.
        this.context = context;
        this.documentos = documentos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.document_item, null);

        TextView nombre = item.findViewById(R.id.txtDocument);
        ImageView icon = item.findViewById(R.id.iconoDocument);
        nombre.setText(documentos.get(position).getNombre());
        if(documentos.get(position).getNombre().contains("pdf")){
            icon.setImageResource(R.drawable.pdf);
        }
        else{
            icon.setImageResource(R.drawable.png);
        }

        return item;
    }
}
