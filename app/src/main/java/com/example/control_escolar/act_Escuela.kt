package com.example.control_escolar

import BDLayer.schoolBD
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_act_escuela.*
import kotlinx.android.synthetic.main.view_form_school.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class act_Escuela : AppCompatActivity() {
    lateinit var school:schoolBD
    private val PICK_IMAGE_REQUEST = 1
    lateinit var imageSchool: ImageView
    lateinit var image: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_escuela)
        this.supportActionBar?.title = "Escuela"
        school = schoolBD(this, Nombre_Escuela.getName())
        imageSchool = findViewById<ImageView>(R.id.image_datos_Escuelas)
        loadDatas()




        imageSchool.setOnClickListener{
            openFolder()//openFilePicker()
        }
    }

    fun openFolder() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val uri: Uri = Uri.parse(
            Environment.getExternalStorageDirectory().path
                    + "/myFolder/"
        )
        intent.setDataAndType(uri, "*/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Abrir carpeta"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val bitmap = getBitmapFromUri(uri)
                // Aquí puedes usar el bitmap en tu ImageView

                //imageView.setImageDrawable(null)
                imageSchool.setImageBitmap(null)
                imageSchool.scaleType = ImageView.ScaleType.FIT_XY
                imageSchool.setImageBitmap(bitmap)
                image = uri
                // Copiar el archivo a la ubicación deseada
                //copyFileToDestination(uri)
            }
        }
    }


    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
            image
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    /*private fun getBitmapFromUri(uri: Uri, targetWidth: Int, targetHeight: Int): Bitmap? {
        return try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor

            // Decodificar la imagen con las dimensiones deseadas
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)

            // Calcular el factor de escala
            val scaleWidth = targetWidth.toFloat() / options.outWidth
            val scaleHeight = targetHeight.toFloat() / options.outHeight
            val scale = min(scaleWidth, scaleHeight)

            // Decodificar el bitmap con el factor de escala calculado
            options.inJustDecodeBounds = false
            options.inSampleSize = scale.toInt()
            val scaledBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)

            parcelFileDescriptor?.close()

            scaledBitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }*/

    // Llamada a la función con las dimensiones del ImageView



    private fun copyFileToDestination(uri: Uri) {
        try {
            val DESTINATION_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Logos"
            val DESTINATION_FILE_NAME = "${Nombre_Escuela.getName()}.jpg"
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(File(DESTINATION_DIRECTORY, DESTINATION_FILE_NAME))
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            Toast.makeText(this, "Archivo copiado exitosamente", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al copiar el archivo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_save_settings -> updateDataSchool()
        }
        return  super.onOptionsItemSelected(item)
    }
    fun loadDatas(){
        val escuela = school.getAllData()
        if (escuela.moveToFirst()){
            var posicion = 0
            posicion = (cbx_Grado_Escuelas.adapter as ArrayAdapter<String>).getPosition(escuela.getString(2))
            cbx_Grado_Escuelas.setSelection(posicion)
            posicion = (cbx_Grupo_Escuelas.adapter as ArrayAdapter<String>).getPosition(escuela.getString(3))
            cbx_Grupo_Escuelas.setSelection(posicion)
            posicion = (cbx_Turno_Escuelas.adapter as ArrayAdapter<String>).getPosition(escuela.getString(4))
            cbx_Turno_Escuelas.setSelection(posicion)
            posicion = (cbx_Ciclo_Escuelas.adapter as ArrayAdapter<String>).getPosition(escuela.getString(5))
            cbx_Ciclo_Escuelas.setSelection(posicion)
            posicion = (cbx_Estado_Escuelas.adapter as ArrayAdapter<String>).getPosition(escuela.getString(9))
            cbx_Estado_Escuelas.setSelection(posicion)
            cbx_Tipo_Escuelas.isEnabled = false
            edit_Nombre_Escuelas.setText(escuela.getString(1))
            edit_Direccion_Escuelas.setText(escuela.getString(7))
            edit_Telefono_Escuelas.setText(escuela.getString(10))
            edit_Colonia_Escuelas.setText(escuela.getString(8))
            edit_Cct_Escuelas.setText(escuela.getString(12))
            imageSchool.setImageBitmap(null)
            imageSchool.scaleType = ImageView.ScaleType.FIT_XY
            val imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/logos/${Nombre_Escuela.getName()}.jpg"



            val bitmap = BitmapFactory.decodeFile(imagePath)
            imageSchool.setImageBitmap(bitmap)

        }
        escuela.close()
    }

    fun updateDataSchool(){
        school.deleteSchool()
        school.sName = edit_Nombre_Escuelas.text.toString()
        school.sGrado = cbx_Grado_Escuelas.selectedItem.toString()
        school.sGrupo = cbx_Grupo_Escuelas.selectedItem.toString()
        school.sTurno = cbx_Turno_Escuelas.selectedItem.toString()
        school.sCiclo = cbx_Ciclo_Escuelas.selectedItem.toString()
        school.sDomicilio = edit_Direccion_Escuelas.text.toString()
        school.sColonia = edit_Colonia_Escuelas.text.toString()
        school.sMunicipio = ""
        school.sTelefono = edit_Telefono_Escuelas.text.toString()
        school.sTipo = cbx_Tipo_Escuelas.selectedItem.toString()
        school.sCct = edit_Cct_Escuelas.text.toString()
        school.iStatus = 0
        school.sMunicipio = cbx_Estado_Escuelas.selectedItem.toString()
        if (school.insertDataSchool()) copyFileToDestination(image)
        Toast.makeText(this, school.error, Toast.LENGTH_SHORT).show()
    }

    fun new_school(){
       // school.iAnticipacion
        //school.newSettings()
    }
}