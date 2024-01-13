package com.example.control_escolar


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FilePickerActivity : AppCompatActivity() {
    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val uri = data.data
                    if (uri != null) {
                        val selectedFilePath = FilePickerActivity.FileUtils.getPath(this, uri)
                        if (selectedFilePath != null) {
                            copyFile(selectedFilePath, getDestinationPath())
                        }
                    }
                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_escuela)
        openFilePicker()
    }


    object FileUtils {
        fun getPath(context: Context, uri: Uri): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val path = column_index?.let { cursor.getString(it) }
            cursor?.close()
            return path
        }
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        filePickerLauncher.launch(intent)
    }

    private fun copyFile(sourcePath: String, destinationPath: String) {
        try {
            val sourceFileChannel = FileInputStream(sourcePath).channel
            val destinationFileChannel = FileOutputStream(destinationPath).channel

            destinationFileChannel.transferFrom(sourceFileChannel, 0, sourceFileChannel.size())

            sourceFileChannel.close()
            destinationFileChannel.close()

            Toast.makeText(this, "Archivo copiado exitosamente", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al copiar el archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDestinationPath(): String {
        // Puedes establecer la ubicación de destino aquí
        val destinationDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Logos/"
        val destinationFileName = "archivo_copiado.jpg"

        // Crea el directorio si no existe
        val dir = File(destinationDirectory)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        return destinationDirectory + destinationFileName
    }
}