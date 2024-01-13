package LogicLayer

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.signature.ObjectKey
import com.example.control_escolar.Nombre_Escuela
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LogoSchool(private val activity: Activity, private val contentResolver: ContentResolver?, private val imageSchool: ImageView?) {

    private val ruta =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/Imprimibles/"
    private val PICK_IMAGE_REQUEST = 123  // Puedes elegir cualquier número

    //private lateinit var imageSchool: ImageView
    public lateinit var image: Uri
    public var takedImage = 0


    fun createDirectoryLogo() {
        val dir = File("$ruta/Logos/")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    fun createDirectoryLogoStudents() {
        val dir = File("$ruta/Students/${Nombre_Escuela.getName()}/")
        if (!dir.exists()) {
            dir.mkdirs()
        }

    }





    fun openFolder() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val uri: Uri = Uri.parse(Environment.getExternalStorageDirectory().path + "/myFolder/")
        intent.setDataAndType(uri, "*/*")
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        activity.startActivityForResult(Intent.createChooser(intent, "Abrir carpeta"),PICK_IMAGE_REQUEST)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?):Int {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                val bitmap = getBitmapFromUri(contentResolver!!, uri)
                imageSchool!!.setImageBitmap(null)//limpiamos el contenido de la imagen
                imageSchool!!.scaleType = ImageView.ScaleType.FIT_XY//ponemos la imagen del tamaño total
                imageSchool!!.setImageBitmap(bitmap)//cargamos la imagen
                takedImage = 1
                image = uri

            }
        }
        return takedImage
    }


    private fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap? {
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

    public fun copyFileToDestinationSchool(uri: Uri) {
        try {
            val DESTINATION_DIRECTORY =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/Imprimibles/Logos"
            val DESTINATION_FILE_NAME = "${Nombre_Escuela.getName()}.jpg"
            val inputStream = contentResolver!!.openInputStream(uri)
            val outputStream = FileOutputStream(File(DESTINATION_DIRECTORY, DESTINATION_FILE_NAME))
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            Toast.makeText(activity, "Archivo copiado exitosamente", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "Error al copiar el archivo", Toast.LENGTH_SHORT).show()
        }
    }

    public fun copyFileToDestinationStudent(nameStudent: String, uri: Uri) {
        try {
            createDirectoryLogoStudents()
            val DESTINATION_DIRECTORY =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/Imprimibles/Students/${Nombre_Escuela.getName()}/"
            val DESTINATION_FILE_NAME = "$nameStudent.jpg"

            val destinationFile = File(DESTINATION_DIRECTORY, DESTINATION_FILE_NAME)

            if (destinationFile.exists()) {
                // El archivo ya existe, entonces lo eliminamos antes de copiar el nuevo
                destinationFile.delete()
            }
            val inputStream = contentResolver!!.openInputStream(uri)
            val outputStream = FileOutputStream(File(DESTINATION_DIRECTORY, DESTINATION_FILE_NAME))
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            //ManagerImage(activity).refreshImage(,nameStudent)

            Toast.makeText(activity, "Archivo copiado exitosamente", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "Error al copiar el archivo", Toast.LENGTH_SHORT).show()
        }
    }

    public fun copyBitmapToDestinationStudent(imageBitmap: Bitmap, nameStudent: String) {
        try {
            // Crea un directorio si no existe
            createDirectoryLogoStudents()

            val DESTINATION_DIRECTORY =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/Imprimibles/Students/${Nombre_Escuela.getName()}/"

            val DESTINATION_FILE_NAME = "$nameStudent.jpg"

            val file = File(DESTINATION_DIRECTORY, DESTINATION_FILE_NAME)

            val outputStream = FileOutputStream(file)

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            outputStream.flush()
            outputStream.close()

            Toast.makeText(activity, "Archivo copiado exitosamente", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(activity, "Error al copiar el archivo", Toast.LENGTH_SHORT).show()
        }
    }

}

class ManagerImage(context: Context) {

    private val glide: RequestManager = Glide.with(context)

    fun doesFileExist(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    public fun loadImageStudents(imageView: ImageView, nameStudent: String) {
        val filePatch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/Students/${Nombre_Escuela.getName()}/$nameStudent.jpg"
        //val filePatch  =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Students/${Nombre_Escuela.getName()}/1.jpg"
        if (doesFileExist(filePatch)) {


            // Obtiene la última modificación del archivo como firma
            val file = File(filePatch)
            val objectKey = ObjectKey(file.lastModified().toString())

// Carga la imagen con la clave única (firma)
            glide.load(File(filePatch))
                .signature(objectKey)
                .into(imageView)
        }
    }


    public fun loadImageList(imageView: ImageView, nameStudent: String) {
        val filePatch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/Students/${Nombre_Escuela.getName()}/$nameStudent.jpg"
        //val filePatch  =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Students/${Nombre_Escuela.getName()}/1.jpg"
        if (doesFileExist(filePatch)) {
            glide.load(File(filePatch))
                .into(imageView)
        }
    }

    fun refreshImage(imageView: ImageView, nameStudent: String) {
        val filePatch =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString() + "/Imprimibles/Students/${Nombre_Escuela.getName()}/$nameStudent.jpg"

// Genera una clave única basada en la última modificación del archivo
        val file = File(filePatch)
        val objectKey = ObjectKey(file.lastModified().toString())

// Carga la imagen con la clave única (firma)
        glide.load(File(filePatch))
            .signature(objectKey)
            .into(imageView)
    }

    public fun loadImageSchool(imageView: ImageView, nameSchool: String){
        val filePatch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/logos/$nameSchool.jpg"
        if (doesFileExist(filePatch)) {
            // Obtiene la última modificación del archivo como firma
            val file = File(filePatch)
            val objectKey = ObjectKey(file.lastModified().toString())
            glide.load(File(filePatch))
                .signature(objectKey)
                .into(imageView)
        }
    }

}

class ImagesForDocuments(){

    private val ruta =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/Imprimibles/"

    fun createDirectoryLogo() {
        val dir = File("$ruta/logos/")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    fun encontrarArchivo(nombreArchivo: String, directorio: String): File? =
        File(directorio).takeIf { it.exists() && it.isDirectory }
            ?.listFiles { file -> file.name == nombreArchivo }
            ?.firstOrNull()

    fun createDirectoryLogoStudents() {
        val dir = File("$ruta/Students/${Nombre_Escuela.getName()}/")
        if (!dir.exists()) {
            dir.mkdirs()
        }

    }

    public fun putImageSchool(up: Int,right: Int,width: Float,height: Float,writer: PdfWriter,nameSchool: String) {
           createDirectoryLogo()
           encontrarArchivo(nameSchool, "$ruta/logos/")?.run {
               val posicionImagenX = 495f + right
               val posicionImagenY = 690f + up
               // Agregar imagen de estudiante a la celda en la posición deseada
               val cb: PdfContentByte = writer.directContent
               val imagenEscuela = com.itextpdf.text.Image.getInstance("$ruta/logos/$nameSchool")
               imagenEscuela.scaleToFit(width, height)
               imagenEscuela.setAbsolutePosition(posicionImagenX, posicionImagenY)
               cb.addImage(imagenEscuela)
           } ?: run {
            println("Archivo no encontrado.")
        }

    }

    public fun putImageStudent(
        up: Int,
        right: Int,
        width: Float,
        height: Float,
        writer: PdfWriter,
        nameStudent: String
    ) {
        createDirectoryLogoStudents()
        encontrarArchivo(nameStudent, "$ruta/Students/${Nombre_Escuela.getName()}")?.run {
            val posicionImagenX = 65f + right
            val posicionImagenY = 675f + up
            // Agregar imagen de estudiante a la celda en la posición deseada
            val cb: PdfContentByte = writer.directContent
            val imagenEscuela = com.itextpdf.text.Image.getInstance("$ruta/Students/${Nombre_Escuela.getName()}/$nameStudent")
            imagenEscuela.scaleToFit(width, height)
            imagenEscuela.setAbsolutePosition(posicionImagenX, posicionImagenY)
            cb.addImage(imagenEscuela)
        } ?: run {
            println("Archivo no encontrado.")
        }
    }

    public fun deleteLogoSchool(nameSchool: String){
        val DESTINATION_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/logos/"
        val DESTINATION_FILE_NAME = "$nameSchool.jpg"

        val destinationFile = File(DESTINATION_DIRECTORY, DESTINATION_FILE_NAME)

        if (destinationFile.exists()) {
            destinationFile.delete()
        }
    }

    fun deleteFolderStudents(nameSchool: String) {
        val folder = File("$ruta/Students/$nameSchool")

        if (folder.exists() && folder.isDirectory) {
            folder.deleteRecursively()
            println("Carpeta eliminada correctamente.")
        } else {
            println("La carpeta no existe o no es un directorio válido.")
        }
    }

}




