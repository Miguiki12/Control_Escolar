package com.example.control_escolar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.listaalumnos.view.*
import kotlinx.android.synthetic.main.listaasistencia.view.imageView


class adapterAlumnos(private var mContext: Context, private  var listaalumnos:List<DatosAlumnos>):
    ArrayAdapter<DatosAlumnos>(mContext,0,listaalumnos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout = LayoutInflater.from(mContext).inflate(R.layout.listaalumnos,parent,false)
        var alumnos = listaalumnos[position]
        layout.txt_Nombre_Alumno.setText(alumnos.nombre)
        layout.txt_Detalles_Alumno.setText(alumnos.detalles)
        layout.imageView.setImageResource(alumnos.foto)
        layout.txt_Folio_Alumno.setText(alumnos.folio)
        layout.txt_Correo_Alumno.setText(alumnos.correo)
        layout.txt_Numberphone_Alumno.setText(alumnos.numero)

        /*if (alumnos.sexo == 1){
            layout.txt_Nombre_Alumno.setTextColor(R.string.azul)
            //Toast.makeText(mContext, alumnos.nombre+" "+ alumnos.sexo.toString(), Toast.LENGTH_SHORT).show()
        }*/
        if (alumnos.sexo == 0){
            layout.txt_Nombre_Alumno.setTextColor(Color.MAGENTA)
            //Toast.makeText(mContext, alumnos.nombre+" "+alumnos.sexo.toString(), Toast.LENGTH_SHORT).show()
        }
        if (alumnos.sexo == 1){
            layout.txt_Nombre_Alumno.setTextColor(Color.parseColor("#1A8AF9"))
            //Toast.makeText(mContext, alumnos.nombre+" "+alumnos.sexo.toString(), Toast.LENGTH_SHORT).show()
        }
        return layout
    }
}