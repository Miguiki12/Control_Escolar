package DocumentsPDF

import BDLayer.schoolBD
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.control_escolar.Nombre_Escuela
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File

open class Pdf(var context: Context) {

    var error = ""
    var Name_teacher  =""
    var Cct = ""
    var Name_school = ""
    var Addres_school = ""
    var Tel_school = ""
    var Grado = ""
    var Grupo = ""
    var Turno = ""
    var Ciclo = ""
    var Address_Student = ""
    var Estado = ""
    var Colonia = ""
    var Name_Student = ""
    var Apellido1 = ""
    var Apellido2 = ""
    var Curp = ""
    var Name_tutor = ""
    var PhoneNumber_Tutor = ""

    lateinit var schoolBD: schoolBD
    public val ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/"//Environment.DIRECTORY_DOCUMENTS+"/Imprimibles/" //"/storage/emulated/0/Documents/Imprimibles/Reporte_Asistencia.pdf"

    init {
        getDefault()
    }


    public fun getDefault(){
        try {
            val schoolBD = schoolBD(context, Nombre_Escuela.getName())
            //Toast.makeText(context, Nombre_Escuela.getName(),Toast.LENGTH_SHORT).show()
            Name_teacher = schoolBD.getNameTeacher()
            val data = schoolBD.getDataSchool()
            if (data.moveToFirst()) {
                Name_school = data.getString(0)
                Grado = data.getString(1)
                Grupo = data.getString(2)
                Turno = data.getString(3)
                Ciclo = data.getString(4)
                Addres_school = data.getString(5)
                Colonia = data.getString(6)
                Estado = data.getString(7)
                Tel_school = data.getString(8)
                Cct = data.getString(9)

                schoolBD.close()
                data.close()
            }

        }catch (Ex:Exception){
            //Toast.makeText(context, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }


    fun putPosition(up:Int, right: Int, writer: PdfWriter, table: PdfPTable){
        val x = 30f + right  // coordenada X en puntos
        val y = 700f - up // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
    }

    fun abrirdocumento(nombre:String,activity: Activity){
        val ruta =  this.ruta+ "$nombre.pdf"
        Toast.makeText(activity.applicationContext, ruta, Toast.LENGTH_SHORT).show()
        val file = File(ruta)
        val uri = FileProvider.getUriForFile(activity, "com.example.control_escolar.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(intent)
    }

    fun deletePdf(nombre:String) {
        val ruta =  this.ruta+ "$nombre.pdf"
        val file = File(ruta)
        if (file.exists()) {
            file.delete()
        }

    }

    fun addTable(up: Int, right: Int, large: Float, bottom: Float,title:String, content: String, writer: PdfWriter) {
        val table = PdfPTable(1)
        table.totalWidth = large * 72 / 1.54f

        // Crea una nueva celda para la primera fila (fondo gris y texto blanco)
        val cell1 = PdfPCell(Phrase(title, Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL, BaseColor.WHITE)))
        cell1.backgroundColor = BaseColor(158, 158, 158)
        cell1.borderColor = BaseColor(191, 191, 191)
        cell1.borderWidth = 2f

        // Ajusta la altura de la primera celda para que quepa el texto
        cell1.fixedHeight = 20f // Ajusta este valor según sea necesario para el contenido

        table.addCell(cell1)

        // Crea una nueva celda para la segunda fila (fondo blanco)
        val cell2 = PdfPCell(Paragraph(content, Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)))
        cell2.backgroundColor = BaseColor.WHITE
        cell2.borderColor = BaseColor(191, 191, 191)
        cell2.borderWidth = 2f
        cell2.fixedHeight = bottom * 72 / 2.54f // Altura doble para la segunda celda
        table.addCell(cell2)

        putPosition(up,right,writer,table)
    }


    fun addTableNote(up: Int, right: Int, large: Float, bottom: Float, content: String, writer: PdfWriter) {
        val table = PdfPTable(1)
        table.totalWidth = large * 72 / 1.54f

        // Crea una nueva celda para la primera fila (fondo gris y texto blanco)
        val cell1 = PdfPCell(Phrase("        NOTA IMPORTANTE", Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL, BaseColor.WHITE)))
        cell1.backgroundColor = BaseColor(158, 158, 158)
        cell1.borderColor = BaseColor(191, 191, 191)
        cell1.borderWidth = 2f

        // Ajusta la altura de la primera celda para que quepa el texto
        cell1.fixedHeight = 20f // Ajusta este valor según sea necesario para el contenido

        table.addCell(cell1)

        // Crea una nueva celda para la segunda fila (fondo blanco)
        val cell2 = PdfPCell(Paragraph("Esta boleta presenta los datos solo del periodo de recuperación, tiene los fines informativos internos y no tiene validez oficial fuera de la escuela ",Font(Font.FontFamily.HELVETICA, 11f, Font.NORMAL, BaseColor(158,158,158))))
        cell2.backgroundColor = BaseColor.WHITE
        cell2.borderColor = BaseColor(191, 191, 191)
        cell2.borderWidth = 2f
        cell2.fixedHeight = bottom * 72 / 2.54f // Altura doble para la segunda celda
        table.addCell(cell2)

        putPosition(up,right,writer,table)
    }
}