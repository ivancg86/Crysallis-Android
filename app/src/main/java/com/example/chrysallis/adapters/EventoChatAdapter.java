package com.example.chrysallis.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chrysallis.R;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Mensaje;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collections;


public class EventoChatAdapter extends RecyclerView.Adapter<EventoChatAdapter.EventosViewHolder> implements View.OnClickListener{
    private ArrayList<Evento> eventos;
    private View.OnClickListener listener;

    public EventoChatAdapter(ArrayList<Evento> eventos) {
        this.eventos = eventos;
    }
    public EventoChatAdapter() {
        this.eventos = new ArrayList<>();
    }

    //crea el viewHolder basado en el layout evento_item
    @NonNull
    @Override
    public EventosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.evento_chat_item, viewGroup, false);
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
        private TextView ultimoMensaje;
        private TextView hora;

        //constructor del viewholder para guardar las views
        public EventosViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.TxtNombre);
            ultimoMensaje = itemView.findViewById(R.id.TxtUltimo);
            hora = itemView.findViewById(R.id.TxtFechaUltimo);
        }

        // metodo bindEvento para rellenar los campos del eventoItem
        public void bindEvento(Evento evento) {
            ArrayList<Mensaje> mensajesArray = new ArrayList<>(evento.getMensajes());
            Collections.sort(mensajesArray);
            nom.setText(evento.getNombre());
            if(!mensajesArray.isEmpty()){
                Mensaje ultimoMsg = mensajesArray.get(mensajesArray.size()-1);
                String mensajeEmojis = StringEscapeUtils.unescapeJava(ultimoMsg.getMensaje());
                ultimoMensaje.setText(mensajeEmojis);
                String soloHora = ultimoMsg.getFecha().substring(11,16);
                hora.setText(soloHora);
            }
        }
    }
}
