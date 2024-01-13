package com.example.control_escolar

import LogicLayer.ManagerImage
import android.content.Context
import android.graphics.Color
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.listaasistencia.view.*
import java.io.File


class adapterAsistencia(private var mContext: Context, private  var listaasistencia:List<Asistencia>, var select:Int, var suspendido:Int):
    ArrayAdapter<Asistencia>(mContext,0,listaasistencia) {
    lateinit var layout:View

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //var layout = LayoutInflater.from(mContext).inflate(R.layout.listaasistencia,parent,false)
        if (select == 0) layout = LayoutInflater.from(mContext).inflate(R.layout.listaasistencia,parent,false)
        if (select == 1)  layout = LayoutInflater.from(mContext).inflate(R.layout.listaddressee,parent,false)
        var asistencia = listaasistencia[position]
        layout.txt_NombreAsistencia.setText(asistencia.nombre)
        layout.txt_DetallesAsistencia.setText(asistencia.detalles)
        layout.imageView.setImageResource(R.drawable.alumno)

        ManagerImage(mContext).loadImageList(layout.imageView, asistencia.Folio.toString())
        //layout.imageView.setImageBitmap(asistencia.imagen)
        layout.cbx_asistioAsistencia.isChecked = (asistencia.cbxasistencia)
        //Toast.makeText(mContext, layout.txt_NombreAsistencia.text.toString()+ " " +layout.cbx_asistioAsistencia.isChecked.toString(), Toast.LENGTH_SHORT).show()
        if (asistencia.sexo == 0){
            layout.txt_NombreAsistencia.setTextColor(Color.MAGENTA)
        }
        if (asistencia.status == 0 ){
            layout.txt_status_Asistencia.setBackgroundColor(Color.WHITE)
            //layout.txt_status_Asistencia.setTextColor(Color.RED)
            layout.txt_status_Asistencia.text = ""
            layout.txt_status_Asistencia.isVisible = false
        }
        if (asistencia.status == 1 ){
            layout.txt_status_Asistencia.setBackgroundColor(Color.RED)
            //layout.txt_status_Asistencia.setTextColor(Color.RED)
            layout.cbx_asistioAsistencia.isChecked = true
            layout.txt_status_Asistencia.text = "Retardo"
            layout.txt_status_Asistencia.isVisible = true
        }
        if (asistencia.status == 2 ){
            layout.txt_status_Asistencia.setBackgroundColor(Color.parseColor("#FFA000"))
            //layout.txt_status_Asistencia.setTextColor(Color.parseColor("#FFA000"))
            layout.cbx_asistioAsistencia.isChecked = true
            layout.txt_status_Asistencia.text = "Justificado"
            layout.txt_status_Asistencia.isVisible = true
        }
        if (asistencia.status == 3 ){
            layout.txt_status_Asistencia.setBackgroundColor(Color.MAGENTA)
            if (suspendido == 1) layout.cbx_asistioAsistencia.isChecked = true
            if (suspendido == 0) layout.cbx_asistioAsistencia.isChecked = false
            layout.txt_status_Asistencia.text = "Suspendido"
            layout.txt_status_Asistencia.isVisible = true
        }
        return layout
    }

    fun doesFileExist(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    fun loadImageStudent(imageView: ImageView, nameStudent: String){
        val filePatch  =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Students/${Nombre_Escuela.getName()}/$nameStudent.jpg"
        //val filePatch  =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Students/${Nombre_Escuela.getName()}/1.jpg"
        if(doesFileExist(filePatch)){
            //imageView.setImageBitmap(null)

            //val bitmap = BitmapFactory.decodeFile(filePatch)
            //imageView.setImageBitmap(bitmap)
            Glide.with(mContext)
                .load(File(filePatch))
                .into(imageView)
        }
    }


}