package DocumentsPDF

import android.content.Context
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream

class InsidenciaPDFBuilder(context: Context):Pdf(context) {

    fun insidencia(nombre:String, fecha:String,detalles:String){
        val documento = Document()
        try {
            //eliminamos si existe el documento
            deletePdf("Insidencia.pdf")
            //Ruta del documento
            //val ruta = File("/storage/emulated/0/Documents/Imprimibles/Insidencia.pdf")
            val ruta =  this.ruta+"Insidencia.pdf"
            error = ruta.toString()
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)

            // Abrimos el documento
            documento.open()

            documento.add(Paragraph( "Estimados padres de familia,"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Es un placer para nosotros dirigirnos a ustedes en este momento. Nos gustaría informarles sobre una incidencia reciente que ha tenido lugar en nuestra escuela."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Se ha reportado un comportamiento inapropiado por parte de su hijo/a ($nombre) en el aula el día ($fecha) durante la hora (hora). Específicamente, ($detalles)."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph(       "Entendemos que este comportamiento es inaceptable y va en contra de las normas y valores de nuestra escuela. Nos tomamos muy en serio la disciplina y el comportamiento apropiado en el aula y esperamos que esta incidencia sea una situación aislada."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Esperamos que ustedes puedan hablar con su hijo/a sobre el comportamiento inapropiado y el impacto que tiene en el aula y en sus compañeros de clase. Si tiene alguna pregunta o necesita discutir esta incidencia en detalle, por favor no dude en ponerse en contacto con nosotros."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph(       "Agradecemos su cooperación y esperamos que trabajemos juntos para asegurar un entorno de aprendizaje positivo y seguro para todos nuestros estudiantes."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph(       "Atentamente,"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.close()

        } catch (e: Exception) {
            error =  e.message.toString()
        } finally {
            // Cerramos el documento
            documento.close()
        }
    }
}