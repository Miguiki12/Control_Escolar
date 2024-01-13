package com.example.control_escolar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.listaactividad.view.*
import kotlinx.android.synthetic.main.listaactividad.view.cd_Tareas
import kotlinx.android.synthetic.main.listaactividad.view.txt_fecha_actividad
import kotlinx.android.synthetic.main.lsita_historial.view.*

class adapter_record(private var mContext: Context, private  var list_record:List<datos_historial>):
    ArrayAdapter<datos_historial>(mContext,0,list_record) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout = LayoutInflater.from(mContext).inflate(R.layout.lsita_historial,parent,false)
        var activdad = list_record[position]
        layout.txt_name_record.setText(activdad.nombre)
        layout.txt_type_record.setText(activdad.tipo)
        layout.txt_matter_record.setText(activdad.materia)
        layout.txt_data_record.setText(activdad.fecha)
        layout.txt_matter_record.setTextColor(activdad.color)

        return layout
    }
}