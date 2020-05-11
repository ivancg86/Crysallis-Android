package com.example.chrysallis.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrysallis.components.ImageButtonRounded;
import com.example.chrysallis.R;
import com.example.chrysallis.classes.Evento;

import java.util.ArrayList;


public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventosViewHolder> implements View.OnClickListener{
    private ArrayList<Evento> eventos;
    private View.OnClickListener listener;

    public EventoAdapter(ArrayList<Evento> eventos) {
        this.eventos = eventos;
    }

    public EventoAdapter() {
        this.eventos = new ArrayList<>();
    }

    //crea el viewHolder basado en el layout evento_item
    @NonNull
    @Override
    public EventosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.evento_item, viewGroup, false);
        itemView.setOnClickListener(this);
        EventosViewHolder eventosViewHolder = new EventosViewHolder(itemView);
        return eventosViewHolder;
    }

    //metodo que recibe una posicion del recyclerview para llenarla con un evento de esa posicion
    @Override
    public void onBindViewHolder(@NonNull EventosViewHolder viewHolder, int pos) {
        Evento evento = eventos.get(pos);
        viewHolder.bindEvento(evento);
    }

    //cuenta la cantidad de Eventos
    @Override
    public int getItemCount() {
        return eventos.size();
    }

    //Metodo onItemCliclListener
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    public static class EventosViewHolder extends RecyclerView.ViewHolder {

        private TextView nom;
        private ImageButtonRounded imagen;
        private TextView ubicacion;
        private TextView fecha;

        //constructor del viewholder para guardar las views
        public EventosViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.TxtEvento);
            imagen = itemView.findViewById(R.id.ImgEvento);
            ubicacion = itemView.findViewById(R.id.TxtUbicacion);
            fecha = itemView.findViewById(R.id.TxtFecha);
        }

        // metodo bindEvento para rellenar los campos del eventoItem
        public void bindEvento(Evento evento) {
            nom.setText(evento.getNombre());
            if(evento.getImagen() != null){
                byte[] byteArray = Base64.decode(evento.getImagen(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imagen.setImageBitmap(bmp);
            }
            ubicacion.setText(evento.getUbicacion());
            String formatDate[] = evento.getFecha().substring(0,10).split("-");
            fecha.setText(formatDate[2] + "-" + formatDate[1] + "-" + formatDate[0]);
        }
    }
}
