package com.example.control_escolar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.listaactividad.view.*
import java.text.SimpleDateFormat
import java.util.*


class adapterActividad(private var mContext: Context, private  var listaactividad:List<DatosActivdad>):
    ArrayAdapter<DatosActivdad>(mContext,0,listaactividad) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout = LayoutInflater.from(mContext).inflate(R.layout.listaactividad,parent,false)
        var activdad = listaactividad[position]
        layout.txt_Nombre_Actividad.setText(activdad.nombre)
        layout.txt_Tipo_Actividad.setText(activdad.tipo)
        layout.txt_Materia_Actividad.setText(activdad.materia)
        layout.txt_Materia_Actividad.setTextColor(activdad.color)
        layout.txt_fecha_actividad.setText(activdad.fecha)


        if (activdad.especial == 1) layout.text_porciento_actividad.setText(activdad.porciento.toString()+ "% de 100")
        if (activdad.especial == 0) layout.text_porciento_actividad.setText(activdad.porciento.toString()+ "% de "+activdad.valor)

        if (activdad.fecha == getCurrentDate()){
            layout.cd_Tareas.setBackgroundColor( Color.parseColor("#F7EF8C"))
        }

        if (activdad.especial == 1){
            layout.txt_especial_Actividad.setText("Seleccion")
            layout.txt_especial_Actividad.setTextColor(Color.BLACK)
        }
        else{
            layout.txt_especial_Actividad.setText("")
            //layout.txt_especial_Actividad.setTextColor(Color.BLUE)
        }
        if (activdad.tipo.substring(0, 3).uppercase() == "EXA")  layout.imagen_Actividad.setImageResource(R.drawable.exa)
        if (activdad.tipo.substring(0, 3).uppercase() == "PRO")  layout.imagen_Actividad.setImageResource(R.drawable.pro)
        if (activdad.tipo.substring(0, 3).uppercase() == "TRA")  layout.imagen_Actividad.setImageResource(R.drawable.tra)
        if (activdad.tipo.substring(0, 3).uppercase() == "TAR")  layout.imagen_Actividad.setImageResource(R.drawable.tar)
        if (activdad.terminada == 1) layout.cd_Tareas.setBackgroundColor(Color.parseColor("#DCDCDC"))
        return layout
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}