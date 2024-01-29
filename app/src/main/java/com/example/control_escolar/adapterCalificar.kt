package com.example.control_escolar

import LogicLayer.ManagerImage
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.listaasistencia.view.imageView
import kotlinx.android.synthetic.main.listacalificar.view.*
import java.text.SimpleDateFormat


class adapterCalificar(private var mContext: Context, private  var listacalificar:List<DatosCalificar>, private var calificacion : MutableList<MutableMap<String, Any>>):
    ArrayAdapter<DatosCalificar>(mContext,0,listacalificar) {



    @RequiresApi(Build.VERSION_CODES.N)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layout = LayoutInflater.from(mContext).inflate(R.layout.listacalificar,parent,false)
        var calificar = listacalificar[position]
        layout.txt_Nombre_Calificar.setText(calificar.nombre)
        layout.txt_Detalles_Calificar.setText(calificar.detalles)
        layout.imageView.setImageResource(R.drawable.alumno)

        val valor = findValue(calificacion,"Folio", calificar.folio)
        ManagerImage(mContext).loadImageList(layout.imageView, listacalificar[position].folio)



        /*if (calificar.f_baja.length > 0){
        val result = compareDates(calificar.f_baja, calificar.fecha)
                when {
                    result > 0 -> {
                        // La fecha objetivo es anterior a la fecha actual
                        val emptyView = View(mContext)
                        emptyView.layoutParams = ViewGroup.LayoutParams(0, 0)
                        //listToUsed.add(calificar.folio)
                        //listToUsed.removeIf{it == calificar.folio}
                        return emptyView
                        //layout.txt_Nombre_Calificar.text = calificar.nombre  + " baja"
                        //Toast.makeText(context, "La fecha objetivo es anterior a la fecha actual ${calificar.nombre}", Toast.LENGTH_SHORT).show()
                    }
                    result < 0 -> {
                        //Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()
                        // La fecha objetivo es posterior a la fecha actual
                        //layout.txt_Nombre_Calificar.text = calificar.nombre  + " adelante"
                        /*val emptyView = View(mContext)
                        emptyView.layoutParams = ViewGroup.LayoutParams(0, 0)
                        return emptyView*/
                        println("La fecha objetivo es posterior a la fecha actual")
                    }
                    else -> {
                        // Las fechas son iguales
                        println("Las fechas son iguales")
                    }
                }
        }*/

                layout.edit_calificar.setText(valor.toString())




                //layout.imageView.setImageBitmap(calificar.foto)
                if (valor.toString().isNotEmpty()) {
                    if (valor  <= 50) layout.edit_calificar.setTextColor(Color.RED)
                    if (valor.toFloat() in 60f..69f) layout.edit_calificar.setTextColor(Color.parseColor("#FF8700"))
                    if (valor.toFloat() in 70f..79f) layout.edit_calificar.setTextColor(Color.parseColor("#79D253"))
                    if (valor.toFloat() in 80f..89f) layout.edit_calificar.setTextColor(Color.parseColor("#C569E0"))
                    if (valor.toFloat() in 90f .. 100f) layout.edit_calificar.setTextColor(Color.parseColor("#009AFF"))
                }

                if (calificar.situacion == "BAJA"){
                    layout.txt_situacin_Calificar.setTextColor(Color.RED)
                    layout.txt_situacin_Calificar.text = calificar.situacion
                }



                if (calificar.sexo == 0){
                    layout.txt_Nombre_Calificar.setTextColor(Color.MAGENTA)
                }


                layout.edit_calificar.isEnabled = false
                return layout!!
    }


fun compareDates(targetDate: String, dateActivity: String): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    try {
        val targetDateAsDate = sdf.parse(targetDate)
        val dateActivityAsDate = sdf.parse(dateActivity)

        if (dateActivityAsDate != null && targetDateAsDate != null) {
            // Comparar targetDate con dateActivity
            return dateActivityAsDate.compareTo(targetDateAsDate)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        // Manejo de errores, por ejemplo, si el formato de la fecha es incorrecto
    }
    return 0
}
    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, value1:String):Int{
        val foundData = dataTable.find {it[cell1] == value1}
        //Toast.makeText(context, "$cell1 = $value1 and $cell2 = $value2", Toast.LENGTH_LONG).show()
        if (foundData != null) {
            return foundData["Calificacion"].toString().toInt()
        } else {
            return 0
        }
    }
}



