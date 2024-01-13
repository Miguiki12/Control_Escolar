package com.example.control_escolar

import BDLayer.TareasBD
import LogicLayer.ManagerImage
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.listaasistencia.view.imageView
import kotlinx.android.synthetic.main.listacalificar.view.*


class adapterCalificar(private var mContext: Context, private  var listacalificar:List<DatosCalificar>):
    ArrayAdapter<DatosCalificar>(mContext,0,listacalificar) {
    var focusedPosition = -1
    var Tareas : TareasBD

    init {
        Tareas = TareasBD(mContext)

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout = LayoutInflater.from(mContext).inflate(R.layout.listacalificar,parent,false)
        var calificar = listacalificar[position]
        layout.txt_Nombre_Calificar.setText(calificar.nombre)
        layout.txt_Detalles_Calificar.setText(calificar.detalles)
        layout.imageView.setImageResource(R.drawable.alumno)

        ManagerImage(mContext).loadImageList(layout.imageView, listacalificar[position].folio)


        //layout.imageView.setImageBitmap(calificar.foto)
        if (calificar.calificacion.isNotEmpty()) {
            if (calificar.calificacion.toFloat()  <= 50) layout.edit_calificar.setTextColor(Color.RED)
            if (calificar.calificacion.toFloat() in 60f..69f) layout.edit_calificar.setTextColor(Color.parseColor("#FF8700"))
            if (calificar.calificacion.toFloat() in 70f..79f) layout.edit_calificar.setTextColor(Color.parseColor("#79D253"))
            if (calificar.calificacion.toFloat() in 80f..89f) layout.edit_calificar.setTextColor(Color.parseColor("#C569E0"))
            if (calificar.calificacion.toFloat() in 90f .. 100f) layout.edit_calificar.setTextColor(Color.parseColor("#009AFF"))
        }

        layout.edit_calificar.setText(calificar.calificacion)


        if (calificar.sexo == 0){
            layout.txt_Nombre_Calificar.setTextColor(Color.MAGENTA)
        }

        layout.edit_calificar.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                focusedPosition = position
            }
            else{
                if (layout.edit_calificar.text.toString().length > 0){
                    Tareas.borrarCalificaicionAlumno(calificar.c_tarea, calificar.folio)
                    //Toast.makeText(mContext,Tareas.error,Toast.LENGTH_SHORT).show()
                    Tareas.Calificar_Todos(calificar.c_tarea, calificar.folio,calificar.fecha,layout.edit_calificar.text.toString(),"0")
                    //Toast.makeText(mContext,calificar.calificacion.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        layout.edit_calificar.isEnabled = focusedPosition == position
        return layout
    }
}



