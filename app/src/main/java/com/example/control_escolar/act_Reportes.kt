package com.example.control_escolar

import BDLayer.ReporteBD
import BDLayer.TareasBD
import BDLayer.crearPDF
import BDLayer.sendEMail
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import kotlinx.android.synthetic.main.activity_act_activdad.*
import kotlinx.android.synthetic.main.activity_act_reportes.*
import kotlinx.android.synthetic.main.activity_act_reportes.view.*
import kotlinx.android.synthetic.main.listaactividad.*
import kotlinx.android.synthetic.main.listaasistencia.*
import kotlinx.android.synthetic.main.view_stake.*
import kotlinx.android.synthetic.main.view_suspender_justificar.*
import kotlinx.android.synthetic.main.view_suspender_justificar.view.*
import kotlinx.android.synthetic.main.viewdatosactividad.view.*
import java.text.SimpleDateFormat
import java.util.*

class act_Reportes : AppCompatActivity() {
    lateinit var TareasBD: TareasBD
    lateinit var ReporteBD: ReporteBD
    lateinit var email: sendEMail
    lateinit var pdf: crearPDF
    var C_materias = ArrayList<String>()
    var Folio = ""
    var correo = ""
    var Nombre = ""
    var Telefono = ""
    var Tipo_reporte = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_reportes)
        pdf = crearPDF(this)
        TareasBD = TareasBD(this)
        ReporteBD = ReporteBD(this)
        email = sendEMail(this)

        settitles()

        loadMaterias()

        txt_Calendario_Reportes.text = hoy()
        txt_Calendario_Reportes.setOnClickListener{ updatedate() }



        btn_Reporte.setOnClickListener {
            if (validatedata())  insertPospone()
            else Toast.makeText(this, "Especifique todos los datos para poder registrar la incisdencia", Toast.LENGTH_LONG).show()
        }


            txt_Historial_Reportes.setOnClickListener {
                gotoReports()
            }
    }


    private fun updatedate(){
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            myCalendar.set(android.icu.util.Calendar.YEAR, i)
            myCalendar.set(android.icu.util.Calendar.MONTH, i2)
            myCalendar.set(android.icu.util.Calendar.DAY_OF_MONTH, i3)
            updatedate(myCalendar)
        }
        DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH),).show()
    }

    fun updatedate(myCalendar:Calendar){
            val myformat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myformat, Locale.UK)
            txt_Calendario_Reportes.setText(sdf.format(myCalendar.time))
        }


    fun settitles(){
        this.supportActionBar?.title = "Registrar Incidencia"
        var  alumnoreportado = intent.extras
        Folio = alumnoreportado?.getString("folio").toString()
        correo = alumnoreportado?.getString("email").toString()
        Nombre = alumnoreportado?.getString("nombre").toString()
        Telefono = alumnoreportado?.getString("telefono").toString()
        txt_Titulo_Reporte.text = Nombre
        if (alumnoreportado?.getString("sexo").toString().toInt()  == 0) txt_Titulo_Reporte.setBackgroundColor(Color.MAGENTA)
    }


    fun loadMaterias(){
        val adaptadorM = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,cargarMaterias())
        cb_Materias_Reportes.adapter =  adaptadorM
    }


    fun cargarMaterias():ArrayList<String>{
            var  arrayMaterias = ArrayList<String>()
            var count = 0
            val materias = TareasBD.obtenerMAterias()
            if(materias.moveToFirst()){
                while(count < materias.count){
                    arrayMaterias.add(materias.getString(1))
                    C_materias.add(materias.getString(0))
                    materias.moveToNext()
                    count ++
                }
            }
       return arrayMaterias
    }

    fun insertPospone(){
        try {
            ReporteBD.iFolio = Folio.toString().toInt()
            ReporteBD.iTipo  = get_type_report()
            ReporteBD.sDescripcion = ed_Descripcion_Reportes.text.toString()
            ReporteBD.dPuntaje = ed_Porcentaje_Reporte.text.toString().toInt()
            ReporteBD.sFecha = txt_Calendario_Reportes.text.toString()
            ReporteBD.bMostrar = false
            ReporteBD.iMateria = C_materias[ cb_Materias_Reportes.selectedItemPosition].toInt()
            if (ReporteBD.WriteReport()){
                ///email.send("Incidencia " + txt_Calendario_Reportes.text.toString(), ed_Descripcion_Reportes.text.toString(), correo)
                //Toast.makeText(this,correo,Toast.LENGTH_SHORT).show()
                pdf.insidencia(Nombre, txt_Calendario_Reportes.text.toString(),ed_Descripcion_Reportes.text.toString())
                email.adjuntar("Insidencia","")
                email.sendwhitattach(Tipo_reporte,"",correo)
                send_whatsapp()
                this.onBackPressed()}
            Toast.makeText(this, ReporteBD.error,Toast.LENGTH_SHORT).show()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
            //Toast.makeText(this, "Hubo un problema al registrar la incidencia",Toast.LENGTH_SHORT).show()
        }
    }


    fun enviarAdjuntoemail(rutaArchivo: String, numeroTelefono: String) {
        // Obtenemos la URI del archivo y la URI del número de teléfono de WhatsApp
        val uriArchivo = Uri.parse("file://$rutaArchivo")
        val uriNumero = Uri.parse("whatsapp://send?phone = $numeroTelefono")

        // Creamos el Intent para enviar el archivo como adjunto
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            putExtra(Intent.EXTRA_STREAM, uriArchivo)
            data = uriNumero
            type = "application/pdf"
        }
        // Iniciamos la actividad para enviar el archivo
        startActivity(intent)
    }

    fun send_whatsapp(){
        val sendIntent = Intent()
        var count = 0
        sendIntent.setAction(Intent.ACTION_VIEW)
        //Nombre_Escuela.Alumnos.getString()
        val url = "whatsapp://send?phone=$Telefono&text= Estimado Padre de Familia,\n"+
        "Le informamos que la conducta de su hijo en clase ha sido reportada y será revisada por la administración. Por favor, tenga en cuenta que se espera un comportamiento apropiado en todo momento para mantener un ambiente de aprendizaje positivo.\n"+
        "Atentamente,\n"+
        "Profesor del Alumno.\n"+
        "Mas detalles enviados a su corroe electronico"
        sendIntent.data = Uri.parse(url)
        startActivity(sendIntent)
    }

    fun get_type_report():Int{
        var seleccion = 0
        if (rb_llamada_atencion.isChecked){
            Tipo_reporte = "Llamada de atencion"
            seleccion = 1
        }
        if (rb_Reporte.isChecked){
            Tipo_reporte = "Reporte"
            seleccion =  2
        }
        if (rb_Citatorio.isChecked){
            Tipo_reporte = "Citatorio"
            seleccion =  3
        }

        if (rb_Suspender.isChecked){
            Tipo_reporte = "Suspensión"
            seleccion =  4
        }
        return seleccion
    }

    fun gotoReports(){
        var inten = Intent(this, act_Historial::class.java)
        inten.putExtra("folio", Folio)
        inten.putExtra("nombre", Nombre)
        inten.putExtra("tipo", "2")
        startActivity((inten))
    }

    fun validatedata():Boolean{
        var correct = false
        if (get_type_report() > 0 && ed_Descripcion_Reportes.text.toString().length > 0
            && ed_Porcentaje_Reporte.text.toString().length > 0 && cb_Materias_Reportes.isNotEmpty()) correct = true
        return correct
    }

    private fun hoy():String{
        var dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var dia = dateFormat.format(Date())

        return dia
    }
}