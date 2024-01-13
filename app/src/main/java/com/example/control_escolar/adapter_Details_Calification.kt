package com.example.control_escolar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.list_details_calification.view.*
import java.text.SimpleDateFormat
import java.util.*

class adapter_Details_Calification(private var mContext: Context, private  var list_details:List<DataDetailsCalification>):
    ArrayAdapter<DataDetailsCalification>(mContext,0,list_details) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout = LayoutInflater.from(mContext).inflate(R.layout.list_details_calification,parent,false)
        var details = list_details[position]
        layout.txt_detail_name_calification.setText(details.nombre)
        layout.txt_detail_tipe_calification.setText(details.tipo)
        layout.txt_detail_matter_calification.setText(details.materia)
        layout.txt_detail_matter_calification.setTextColor(details.color)
        layout.txt_detail_date_calification.setText(details.fecha)
        layout.text_detail_calification_calification.setText(details.calificacion.toString())



        if (details.especial == 1){
            layout.txt_detail_especial_calification.setText("Seleccion")
            layout.txt_detail_especial_calification.setTextColor(Color.BLACK)
        }
        else{
            layout.txt_detail_especial_calification.setText("")
            //layout.txt_especial_Actividad.setTextColor(Color.BLUE)
        }
        if (details.tipo.substring(0, 3).uppercase() == "EXA")  layout.imagen_detail_calification.setImageResource(R.drawable.exa)
        if (details.tipo.substring(0, 3).uppercase() == "PRO")  layout.imagen_detail_calification.setImageResource(R.drawable.pro)
        if (details.tipo.substring(0, 3).uppercase() == "TRA")  layout.imagen_detail_calification.setImageResource(R.drawable.tra)
        if (details.tipo.substring(0, 3).uppercase() == "TAR")  layout.imagen_detail_calification.setImageResource(R.drawable.tar)
        return layout
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}