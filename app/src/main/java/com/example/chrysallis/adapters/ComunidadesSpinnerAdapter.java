package com.example.chrysallis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chrysallis.R;
import com.example.chrysallis.classes.Comunidad;

import java.util.ArrayList;

public class ComunidadesSpinnerAdapter extends ArrayAdapter<Comunidad> {
    private Context context;
    private ArrayList<Comunidad> comunidades;

    public ComunidadesSpinnerAdapter(Context context, ArrayList<Comunidad> comunidades) {
        super(context, R.layout.spinner_selected_item, comunidades);
        this.context = context;
        this.comunidades = comunidades;
    }
    public ComunidadesSpinnerAdapter(Context context) {
        super(context, R.layout.spinner_selected_item);
        this.context = context;
        this.comunidades = new ArrayList<>();
    }

    //este método establece el elemento seleccionado sobre el botón del spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.spinner_selected_item,null);
        }
        ((TextView) convertView.findViewById(R.id.textoSpinner)).setText(comunidades.get(position).getNombre());


        return convertView;
    }

    //gestiona la lista usando el View Holder Pattern. Equivale a la típica implementación del getView
    //de un Adapter de un ListView ordinario
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.spinner_list_item, parent, false);
        }

        if (row.getTag() == null)
        {
            ComunidadesHolder comunidadesHolder = new ComunidadesHolder();
            comunidadesHolder.setTextView((TextView) row.findViewById(R.id.textoSpinner));
            row.setTag(comunidadesHolder);
        }

        //rellenamos el layout con los datos de la fila que se está procesando
        Comunidad redSocial = comunidades.get(position);
        ((ComunidadesHolder) row.getTag()).getTextView().setText(redSocial.getNombre());

        return row;
    }
    private static class ComunidadesHolder
    {
        private TextView textView;


        public TextView getTextView()
        {
            return textView;
        }

        public void setTextView(TextView textView)
        {
            this.textView = textView;
        }

    }
}
