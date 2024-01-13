package com.example.control_escolar

import BDLayer.MateriasBD
import BDLayer.crearPDF
import BDLayer.sendEMail
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_act_horario.*
import java.io.File

class act_Horario : AppCompatActivity() {
    lateinit var horario : MateriasBD
    lateinit var eMail: sendEMail
    lateinit var pdf : crearPDF
    var Semana = arrayOf("domingo","lunes", "martes", "miércoles", "jueves", "viernes", "sabado")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_horario)
        this.supportActionBar?.title = "Materias por dia"
        this.supportActionBar?.subtitle = Nombre_Escuela.getAlias()
        horario = MateriasBD(this)
        eMail = sendEMail(this)
        pdf = crearPDF(this)
        setDays()

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_horario, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_email_schedule -> sendShedulePdf()//send_schedule_mail()
            R.id.nav_ver_pdf->  showInPdf()

        }
        return  super.onOptionsItemSelected(item)
    }

    fun cargarDias(day:String):String{
        var todos = ""
        val dias = horario.obtenerHorario(day)
        if (dias.moveToFirst()){
            do{
                todos += " "+dias.getString(0)
            }while (dias.moveToNext())
        }
        dias.close()
        return todos
    }

    fun setDays(){
        txt_Materias_lunes.text = cargarDias("lunes")
        txt_Materias_martes.text = cargarDias("martes")
        txt_Materias_miercoles.text = cargarDias("miércoles")
        txt_Materias_jueves.text = cargarDias("jueves")
        txt_Materias_viernes.text = cargarDias("viernes")
    }
    fun prepare_schedule():String{
        var schedule = ""
        schedule = "lunes\n"
        schedule += txt_Materias_lunes.text
        schedule += "\nmartes\n"
        schedule += txt_Materias_martes.text
        schedule += "\nmiércoles\n"
        schedule += txt_Materias_miercoles.text
        schedule += "\njueves\n"
        schedule += txt_Materias_jueves.text
        schedule += "\nviernes\n"
        schedule += txt_Materias_viernes.text
        return schedule
    }

    fun send_schedule_mail(){
        pdf.Horaio(txt_Materias_lunes.text.toString(), txt_Materias_martes.text.toString(), txt_Materias_miercoles.text.toString(),txt_Materias_jueves.text.toString(), txt_Materias_viernes.text.toString())
        eMail.adjuntar("Horario","")
        eMail.sendwhitattach("HORARIO DE CLASES","",eMail.allMails())
        //eMail.send("Materias por dia", prepare_schedule(), eMail.allMails())
        Toast.makeText(this, eMail.error, Toast.LENGTH_SHORT).show()
    }

    fun showInPdf(){
        pdf.Horaio(txt_Materias_lunes.text.toString(), txt_Materias_martes.text.toString(), txt_Materias_miercoles.text.toString(),txt_Materias_jueves.text.toString(), txt_Materias_viernes.text.toString())
        pdf.abrirdocumento("Horario",this)
    }

    fun sendShedulePdf() {
        pdf.Horaio(txt_Materias_lunes.text.toString(),txt_Materias_martes.text.toString(),txt_Materias_miercoles.text.toString(),txt_Materias_jueves.text.toString(),txt_Materias_viernes.text.toString()        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        val file = File(pdf.ruta + "Horario.pdf")
        val uri = FileProvider.getUriForFile(this, "com.example.control_escolar.fileprovider", file)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Compartir archivo PDF"))

    }
}