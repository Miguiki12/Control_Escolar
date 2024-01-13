package com.example.control_escolar

import BDLayer.sendEMail
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.size
import kotlinx.android.synthetic.main.activity_act_asistencia.*
import kotlinx.android.synthetic.main.activity_act_avisos.*
import kotlinx.android.synthetic.main.listaasistencia.view.*
import kotlinx.android.synthetic.main.view_select_addressee.*
import kotlinx.android.synthetic.main.view_select_addressee.view.*
import kotlinx.android.synthetic.main.view_suspender_justificar.view.*
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.BodyPart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart


class act_Avisos : AppCompatActivity() {
    var listaddressee = ArrayList<Asistencia>()
    var checado = false
    lateinit var email: sendEMail
    lateinit var view : View
    var _multipart = MimeMultipart()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_avisos)
        this.supportActionBar?.title = "Avisos"
        email =  sendEMail(this)
        CargarAlumnos()
        list_addressee2.adapter = adapterAsistencia(this, listaddressee, 1, 0)
        btn_para_Avisos.setOnClickListener {selectaddressee()}//allasist()}
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_avisos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_send_avisos -> send()
            R.id.nav_attach_avisos-> attachfile()

        }
        return  super.onOptionsItemSelected(item)
    }

    fun CargarAlumnos(){
        var count = 0
        listaddressee.clear()
        try {
            if(Nombre_Escuela.Alumnos.moveToFirst()){
                while(count < Nombre_Escuela.Alumnos.count){
                    //Toast.makeText(this,Nombre_Escuela.Alumnos.getString(1), Toast.LENGTH_SHORT).show()
                    listaddressee.add(
                        Asistencia(Nombre_Escuela.Alumnos.getString(1) + " " +Nombre_Escuela.Alumnos.getString(2),
                            Nombre_Escuela.Alumnos.getString(16),R.drawable.alumno//PictureLoader.loadPicture(this,Nombre_Escuela.Alumnos.getBlob(19))
                            , false, Nombre_Escuela.Alumnos.getString(4).toInt(), Nombre_Escuela.Alumnos.getString(0).toInt(),Nombre_Escuela.Alumnos.getString(15), 0,Nombre_Escuela.Alumnos.getString(16))
                    )
                    Nombre_Escuela.Alumnos.moveToNext()
                    count ++
                }
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun selectaddressee() {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.view_select_addressee, null)
        builder.setView(view)
        val dialog = builder.create()
        CargarAlumnos()
        view.cbx_all_addressee.isChecked = false
        view.list_addressee.adapter = adapterAsistencia(view.context, listaddressee, 1, 0)
        dialog.show()
        view.cbx_all_addressee.setOnClickListener {
            TodosAsistieron()
            //allasist()
        }
        view.txt_listo_addressee.setOnClickListener {
            val select =  view.list_addressee.checkedItemPositions.size
            Toast.makeText(this, select.toString(), Toast.LENGTH_SHORT).show()
            //allasist()
            //dialog.hide()
        }
    }

    fun TodosAsistieron(){
        var cont = 0
        if(checado) checado = false
        else checado = true
        while(cont < listaddressee.count()) {
            listaddressee[cont].cbxasistencia = checado
            cont ++
        }
        view.list_addressee.adapter = adapterAsistencia(this, listaddressee, 1,0)
    }


    fun attachfile(){
        try {
            //email.main()
            //Toast.makeText(this, email.error, Toast.LENGTH_LONG).show()
           //email.addAttachment("/storage/emulated/0/Documents/Imprimibles/Enviar.txt")
            email.adjuntar("","")
            email.sendwhitattach(ed_asinto_Avisos.text.toString(), ed_redactar_Avisos.text.toString(), email.allMails())
            //email.attach()
            //Toast.makeText(this,ObtenerDatos("Enviar.txt"), Toast.LENGTH_SHORT).show()
            Toast.makeText(this, email.error, Toast.LENGTH_SHORT).show()
        }
        catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }

    }

    fun allasist(){
        val lista = findViewById<ListView>(R.id.list_addressee2)
        //Toast.makeText(this,, Toast.LENGTH_SHORT).show()
        lista.checkedItemPositions.put(1, true)
    }
    fun send(){
        email.send(ed_asinto_Avisos.text.toString(), ed_redactar_Avisos.text.toString(), email.allMails())
        Toast.makeText(this, email.error, Toast.LENGTH_SHORT)
        this.onBackPressed()
    }


    @Throws(java.lang.Exception::class)
    fun addAttachment(filename: String) {

        // Antes de adjuntar el fichero se comprime en zip
        val outputFilename = "$filename.pdf"
        val outputFile = File(outputFilename)
        val fos = FileOutputStream(outputFile)
        val inputFile = File(filename)
        val fis = BufferedInputStream(FileInputStream(inputFile))
        val zos = ZipOutputStream(BufferedOutputStream(fos))
        try {
            val buffer = ByteArray(1024)
            val stream = ByteArrayOutputStream()
            var len1 = 0
            while (fis.read(buffer).also { len1 = it } != -1) {
                stream.write(buffer, 0, len1)
            }
            val bytes: ByteArray = stream.toByteArray()
            val entry = ZipEntry(outputFilename)
            zos.putNextEntry(entry)
            zos.write(bytes)
            zos.closeEntry()
        } finally {
            zos.close()
            fos.close()
            fis.close()
        }

        // Se adjunta el zip
        val messageBodyPart: BodyPart = MimeBodyPart()
        val source: DataSource = FileDataSource(outputFile)
        messageBodyPart.dataHandler = DataHandler(source)
        messageBodyPart.fileName = filename
        _multipart.addBodyPart(messageBodyPart)
    }
    public fun ObtenerDatos(archivo:String):String {
        var tex:String
        tex = ""
        val pathName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)//obtenemos la ruta del programa emulado
        val miCarpeta = File(pathName, "Imprimibles")
        if (miCarpeta.exists() == true) {//creamos la carpeta en la ruta obtenida
             Toast.makeText(applicationContext,"Si existe el directorio", Toast.LENGTH_LONG).show()
            val miArchivo = File(miCarpeta, archivo)
            if (miArchivo.exists() == true) {
                Toast.makeText(this,"Si existe el archivo" + miArchivo.toString(), Toast.LENGTH_LONG).show()
                val archivo = BufferedReader(InputStreamReader(FileInputStream(miArchivo)))
                attachfile()
                val texto = archivo.use(BufferedReader::readText)
                tex = texto.toString()
            }
            else {
                Toast.makeText(this, "Usuario no registrado", Toast.LENGTH_LONG).show()
                tex = ""
            }
        }else Toast.makeText(applicationContext,"No existe el directorio", Toast.LENGTH_LONG).show()
        return tex
    }
}




