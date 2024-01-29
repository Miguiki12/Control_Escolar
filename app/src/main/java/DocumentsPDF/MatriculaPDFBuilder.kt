package DocumentsPDF

import android.content.Context
import android.graphics.Color
import android.widget.Toast
import com.example.control_escolar.Nombre_Escuela
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream

class MatriculaPDFBuilder(context: Context):Pdf(context) {

    fun Create() {
        val document = Document(PageSize.LETTER.rotate())
        val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Matricula.pdf"))
        document.open()

        val fontBold = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD)
        val fontRegular = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL) // Nuevo tamaño de fuente
        val fontSmall = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL)
        val fontSmallM = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL, BaseColor(Color.MAGENTA))
        val fontSmallH = Font(
            Font.FontFamily.HELVETICA, 6f, Font.NORMAL,
            BaseColor(Color.parseColor("#1A8AF9"))
        )

        val table = PdfPTable(17)
        val columnWidths = floatArrayOf(120f, 10f, 15f, 14f,14f,20f,80f,60f,45f,37f,40f,50f,60f,45f,37f,40f,50f)//,35f,35f)
        table.setTotalWidth(columnWidths)

        table.isLockedWidth = true

        // Añadir celda combinada para el nombre
        val nombreCell = PdfPCell(Phrase("ALUMNO", fontBold))


        var phrase = Phrase("DIA",fontSmall)
        var cellDia = PdfPCell(phrase)
        cellDia.rotation = 90

        phrase = Phrase("MES",fontSmall)
        val cellMes = PdfPCell(phrase)

        cellMes.rotation = 90

        phrase = Phrase("AÑO",fontSmall)
        val cellAño = PdfPCell(phrase)
        cellAño.rotation = 90

        phrase = Phrase("SEXO",fontSmall)
        val cellSexo = PdfPCell(phrase)
        cellSexo.rotation = 90

        phrase = Phrase("EDAD",fontSmall)
        val cellEdad = PdfPCell(phrase)
        cellEdad.rotation = 90

        table.addCell(nombreCell)
        table.addCell(cellSexo)
        table.addCell(cellEdad)

        table.addCell(cellDia)
        table.addCell(cellMes)
        table.addCell(cellAño)
        table.addCell(Phrase("DOMICILIO", fontRegular))
        table.addCell(Phrase("TUTOR 1", fontRegular))
        table.addCell(Phrase("OCUPACION", fontRegular))
        table.addCell(Phrase("ESTUDIOS", fontRegular))
        table.addCell(Phrase("CELULAR", fontRegular))
        table.addCell(Phrase("CORREO", fontRegular))
        table.addCell(Phrase("TUTOR 2", fontRegular))
        table.addCell(Phrase("OCUPACION", fontRegular))
        table.addCell(Phrase("ESTUDIOS", fontRegular))
        table.addCell(Phrase("CELULAR", fontRegular))
        table.addCell(Phrase("CORREO", fontRegular))

        try {
            var sexo = "F"
            var font = fontSmallM
            if (Nombre_Escuela.Alumnos.moveToFirst()) {
                do {

                    if (Nombre_Escuela.Alumnos.getInt(4) == 0) {
                        sexo = "F"
                        font = fontSmallM
                    }
                    if (Nombre_Escuela.Alumnos.getInt(4) == 1) {
                        sexo = "M"
                        font = fontSmallH
                    }
                    val birthdate = Nombre_Escuela.Alumnos.getString(17).split('-')
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(1) + " " + Nombre_Escuela.Alumnos.getString(2) + " " + Nombre_Escuela.Alumnos.getString(3), font))
                    table.addCell(Phrase(sexo, font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(13), font))
                    table.addCell(Phrase(birthdate[2], font))
                    table.addCell(Phrase(birthdate[1], font))
                    table.addCell(Phrase(birthdate[0], font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(5), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(14), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(21), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(20), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(15), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(16), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(22), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(26), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(25), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(23), font))
                    table.addCell(Phrase(Nombre_Escuela.Alumnos.getString(24), font))
                } while (Nombre_Escuela.Alumnos.moveToNext())
            }
        }catch (Ex:Exception){
            Toast.makeText(context,Ex.message.toString(), Toast.LENGTH_LONG).show()}

        document.add(table)
        document.close()
        //abrirdocumento("Matricula")
    }

}