package BDLayer

//import org.apache.poi.ss.usermodel.*
import LogicLayer.Formats
import LogicLayer.ImagesForDocuments
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.control_escolar.Nombre_Escuela
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import java.io.File
import java.io.FileOutputStream
import java.text.Normalizer
import java.text.SimpleDateFormat
import kotlin.math.pow
import kotlin.math.roundToInt


class crearPDF(var context: Context) {

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
    var tRepetidoresM = 0
    var tRepetidoresH = 0
    var tNuevoM = 0
    var tNuevoH = 0
    var tBajasM = 0
    var tBajasH = 0
    var tAltasM = 0
    var tAltasH = 0
    var tInscripcionM = 0
    var tInscripcionH = 0
    var tExistenciaM = 0
    var tExistenciaH = 0
    var tAprovadosM = 0
    var tAprovadosH = 0
    var tReprovadosM = 0
    var tReprovadosH = 0



    lateinit var schoolBD: schoolBD
    public val ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/"//Environment.DIRECTORY_DOCUMENTS+"/Imprimibles/" //"/storage/emulated/0/Documents/Imprimibles/Reporte_Asistencia.pdf"

    init {
        getDefault(context)
    }


    public fun getDefault(context: Context){
        try {
        val schoolBD = schoolBD(context,Nombre_Escuela.getName())
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

    fun sendHomework(fecha:String, Descripcion:String):Boolean {
        // Creamos el documento
        val documento = Document()
        try {
            deletePdf("Tarea")
            val ruta =  this.ruta+ "Tarea.pdf"
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)
            // Abrimos el documento
            documento.open()
            // Añadimos contenido al documento
            documento.add(Paragraph( "TAREA"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph( "DEL DIA ${Formats.getCurrentDate()} PARA ENTREGAR $fecha"))

            documento.add(Paragraph("\nACTIVIDAD\n\n"+Descripcion+"."))
            documento.add(Paragraph("Le recordamos que la entrega de actividades es esencial para el buen rendimiento académico de su hij@."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))

            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))
            error = fecha+" "+Descripcion
            return  true
        } catch (e: Exception) {
            error =  e.message.toString()
            return false
        } finally {
            // Cerramos el documento
            documento.close()
        }
    }

    fun pdf_Actividad_pendiente(fecha:String, Descripcion:String):Boolean {
        // Creamos el documento
        val documento = Document()
        try {
            deletePdf("Actividad")
            val ruta =  this.ruta+ "Actividad.pdf"
            //val ruta = File("/storage/emulated/0/Documents/Imprimibles/Actividad.pdf")
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)
            // Abrimos el documento
            documento.open()
            // Añadimos contenido al documento
            documento.add(Paragraph( "Estimados padres o tutores:"))
            documento.add(Paragraph("\n"))
            //checamos si es del mismo dia la actividad
            if (Formats.convertdate(fecha) == Formats.getCurrentDate()) documento.add(Paragraph("Se le informa que el dia  ($fecha) se trabajó con la actividad descrita abajo"))
            else documento.add(Paragraph("Le informamos que su hij@  tiene pendiente la entrega de la actividad escolar asignada el ["+fecha+"]"))
            documento.add(Paragraph("\nACTIVIDAD\n\n"+Descripcion+"."))
            documento.add(Paragraph("Le recordamos que la entrega de actividades es esencial para el buen rendimiento académico de su hij@."))
            documento.add(Paragraph("Le pedimos que sea realizada a la brevedad y tome las medidas necesarias para asegurar su entrega."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))
            error = fecha+" "+Descripcion
            return  true
        } catch (e: Exception) {
            error =  e.message.toString()
            return false
        } finally {
            // Cerramos el documento
            documento.close()
        }
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    fun pdf_falta(fecha:String) {
        // Creamos el documento
        val documento = Document()
        try {
            deletePdf("Falta")
            //val ruta = File("/storage/emulated/0/Documents/Imprimibles/Falta.pdf")
            val ruta = File(ruta+"Falta.pdf")
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)

            // Abrimos el documento
            documento.open()

            // Añadimos contenido al documento
            documento.add(Paragraph( "Estimados padres o tutores:"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Le informamos que su hijo/a no asistió a la escuela el "+fecha+" sin aviso previo. Esta falta se considera como injustificada y debe ser informada a la autoridad educativa competente."))
            documento.add(Paragraph("Es importante recordar que la asistencia regular a clase es esencial para el buen rendimiento académico de su hijo/a y para mantener un ambiente de aprendizaje adecuado en la escuela. Le pedimos que informe a la escuela lo antes posible sobre las razones de la falta de su hijo/a y que tome las medidas necesarias para evitar futuras faltas injustificadas."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Atentamente"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))

        } catch (e: Exception) {
            error =  e.message.toString()
        } finally {
            // Cerramos el documento
            documento.close()
        }
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    fun reporteAsistencia(fechacompleta:String, fecha:String, alumnos:Cursor, context: Context):Boolean{
        val documento = Document()
        try {
            //eliminamos si existe el documento
            deletePdf("Reporte_Asistencia.pdf")
            //Ruta del documento
            val ruta =  this.ruta+ "Reporte_Asistencia.pdf"
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)

            val fuenteFalta = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.YELLOW)
            val fuenteAsistencia = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
            val fuenteRetardo = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.RED)
            val fuenteJustifciacion = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.PINK)
            var fuente = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)



            // Abrimos el documento
            documento.open()
            //Añade encabezados a las columnas
            documento.add(Paragraph("Reporte de Asistencia: $fechacompleta"))
            documento.add(Paragraph("\n"))

            val tableDetails =PdfPTable(3)
            tableDetails.addCell("N_lista")
            tableDetails.addCell("Nombre")
            tableDetails.addCell("Asistencia")
            val asistencias  = AsistenciaBD(context)
            val lista = asistencias.getAsisten(fecha)
            var status = "1"
            if (lista.moveToFirst()) {
                    do {
                        if ( lista.getInt(0) == 0) {
                            status = "0"
                            fuente = fuenteFalta
                        }
                        if ( lista.getInt(1) == 0 && lista.getInt(0) == 1) {
                            status = "I"
                            fuente = fuenteAsistencia
                        }
                        if ( lista.getInt(1) == 1) {
                            status = "R"
                            fuente = fuenteRetardo
                        }
                        if ( lista.getInt(1) == 2) {
                            status = "J"
                            fuente = fuenteJustifciacion
                        }
                        if ( lista.getInt(1) == 3) {
                            status = "S"

                        }

                        tableDetails.addCell(Paragraph(lista.getString(3),fuenteAsistencia))
                        tableDetails.addCell(Paragraph(lista.getString(4), fuenteAsistencia))
                        tableDetails.addCell(Paragraph(status, fuente))
                        status = "1"
                    } while (lista.moveToNext())
                }
            lista.close()
            asistencias.close()
            documento.add(tableDetails)
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))


            val table = PdfPTable(3)
            // Crea una tabla con tres columnas
            table.addCell("Hombre")
            table.addCell("Mujeres")
            table.addCell("Total")
            //Posicionamos el cursor en la primera posicion
            //Toast.makeText(context, alumnos.count, Toast.LENGTH_SHORT).show()
            if(alumnos.moveToFirst()){
                var contador = 0
                var tota_asistencias = 0
                if (alumnos.count ==1 ){
                    while (contador < alumnos.count){//si
                        if (alumnos.getInt(1) == 1) table.addCell(alumnos.getString(0))
                        else table.addCell("0")
                        if (alumnos.getInt(1) == 0) table.addCell(alumnos.getString(0))
                        else table.addCell("0")
                        tota_asistencias += alumnos.getString(0).toInt()
                        alumnos.moveToNext()
                        contador ++
                    }
                }
                if (alumnos.count == 2){
                    while (contador < alumnos.count){//si
                        table.addCell(alumnos.getString(0))
                        tota_asistencias += alumnos.getString(0).toInt()
                        alumnos.moveToNext()
                        contador ++
                    }
                }

                table.addCell(tota_asistencias.toString())

            }

            // Añade la tabla al documento
            documento.add(table)
            //hacemos espacio
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            //firma del maestro
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))

            return true

        } catch (e: Exception) {
            error =  e.message.toString()
            return false
        } finally {
            // Cerramos el documento
            documento.close()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.N)
    fun asistencia_mes(fechaInterval:String, asistencia:MutableList<MutableMap<String, Any>>, Dias:Cursor, totalsexo: MutableList<MutableMap<String, Any>>, alumnos:Int, context: Context):Boolean{

        val documento = Document(PageSize.LETTER.rotate())
        val table = PdfPTable(Dias.count+1)
        try {
            //Ruta del documento
            val ruta =  this.ruta+ "Asistencia_Mes.pdf"
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            var contadordias = 0
            val escritor = PdfWriter.getInstance(documento, fichero)
            val fuenteFalta = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.RED)
            val fuenteAsistencia = Font(Font.FontFamily.HELVETICA, 8f, Font.BOLD, BaseColor.BLACK)
            val fuenteRetardo = Font(Font.FontFamily.HELVETICA, 8f, Font.BOLD, BaseColor.ORANGE)
            val fuenteJustifciacion = Font(Font.FontFamily.HELVETICA, 8f, Font.BOLD, BaseColor.PINK)
            val fuenteSuspendido = Font(Font.FontFamily.HELVETICA, 8f, Font.BOLD, BaseColor.RED)
            var fuente = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.BLACK)
            val fuenteMujer = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.MAGENTA)
            val fuenteHombre = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLUE)
            val fuenteTitulo = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD, BaseColor.BLACK)
            val ReporteMes = Paragraph(fechaInterval, fuenteTitulo)
            ReporteMes.alignment = Paragraph.ALIGN_CENTER


            documento.open()
            documento.add(ReporteMes)
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Reporte de Asistencia"))
            documento.add(Paragraph("\n"))

            //Añade encabezados a las columnas
            table.addCell("NL")
            Dias.moveToFirst()
            while (contadordias < Dias.count){
                table.addCell(get_Day(Dias.getString(2)).toString())
                Dias.moveToNext()
                contadordias ++
            }

            var contador = 1
            var contadorposicion = 0

            Dias.moveToFirst()
            //asistencia por alumnos
            while (contador <= alumnos)
            {
                //insertamos los numeros de lista
                //table.addCell(((contador).toString()))
                val n_lista = asistencia[contadorposicion]!!["n_lista"].toString()
                table.addCell((n_lista))
                do{
                    //obtenemos los datos buscados en la tabla de asistencia
                    val (lista2, lista) = findStatusAsistenciaMes(asistencia, "fecha", "n_lista", Dias.getString(2), n_lista)
                    var status = ""
                    var fuente = fuenteAsistencia
                    var totalAsistencia = 0
                    var totalFaltas = 0
                    var totalJustificados = 0
                    var totalSuspendido = 0
                    var totalRetardos = 0

                        if ( lista == 0) {
                            status = "I"
                            fuente = fuenteFalta
                            totalFaltas ++
                        }
                        if ( lista == 0 && lista2 == 1) {
                            status = "O"
                            fuente = fuenteAsistencia
                            totalAsistencia ++
                        }
                        if ( lista == 1) {
                            status = "R"
                            fuente = fuenteRetardo
                            totalRetardos++
                        }
                        if ( lista == 2) {
                            status = "J"
                            fuente = fuenteJustifciacion
                            totalJustificados++
                        }
                        if ( lista == 3) {
                            status = "S"
                            fuente = fuenteSuspendido
                            totalSuspendido++
                        }

                    table.addCell(Paragraph(status,fuente))
                    contadorposicion ++
                }while (Dias.moveToNext())
                contador ++
                Dias.moveToFirst()

            }

            contadordias = 0
            contadorposicion = 1
            Dias.moveToFirst()

            //insertamos el total de sexo por dia
            while(contadordias < 2){
                if (contadordias == 0) table.addCell(Paragraph("M",fuenteMujer))
                else table.addCell(Paragraph("H",fuenteHombre))
                while (contadorposicion <= Dias.count){
                    if (contadordias == 0) table.addCell(Paragraph(findValue(totalsexo,"fecha","sexo",Dias.getString(2), contadordias.toString() ).toString(), fuenteMujer))
                    else table.addCell(Paragraph(findValue(totalsexo,"fecha","sexo",Dias.getString(2), contadordias.toString() ).toString(), fuenteHombre))
                    Dias.moveToNext()
                    contadorposicion++
                }
                Dias.moveToFirst()
                contadorposicion = 1
                contadordias++
            }


            // Añade la tabla al documento
            documento.add(table)
            //hacemos espacio
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            //firma del maestro
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))
            return true
        } catch (e: Exception) {
            documento.add(table)
            error =  e.message.toString()
            return false
        } finally {
            // Cerramos el documento
            documento.close()

        }
    }


    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.N)
    fun asistencia_mes1(fechaInterval:String, asistencia:MutableList<MutableMap<String, Any>>, Dias:Cursor, totalsexo: MutableList<MutableMap<String, Any>>, alumnos:Int, context: Context):Boolean{
        val documento = Document()
        val table = PdfPTable(Dias.count+1)
        try {

            //Ruta del documento
            val ruta =  this.ruta+ "Asistencia_Mes.pdf"
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            var contadordias = 0
            val escritor = PdfWriter.getInstance(documento, fichero)
            val fuenteFalta = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.RED)
            val fuenteAsistencia = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.BLACK)
            val fuenteRetardo = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.ORANGE)
            val fuenteJustifciacion = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.PINK)
            val fuenteSuspendido = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.RED)
            var fuenteColumnas = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.BLACK)
            val fuenteMujer = Font(Font.FontFamily.HELVETICA, 13f, Font.NORMAL, BaseColor.MAGENTA)
            val fuenteHombre = Font(Font.FontFamily.HELVETICA, 13f, Font.NORMAL, BaseColor.BLUE)
            val fuenteTitulo = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD, BaseColor.BLACK)
            val ReporteMes = Paragraph(fechaInterval, fuenteTitulo)
            ReporteMes.alignment = Paragraph.ALIGN_CENTER


            documento.open()
            documento.add(ReporteMes)
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Reporte de Asistencia"))
            documento.add(Paragraph("\n"))
            val table1 = PdfPTable(Dias.count + 6)

            //Añade encabezados a las columnas
            table1.addCell(Paragraph("NL",fuenteColumnas))
            Dias.moveToFirst()
            while (contadordias < Dias.count){
                table1.addCell(Paragraph(get_Day(Dias.getString(2)).toString(), fuenteColumnas))
                Dias.moveToNext()
                contadordias ++
            }

            table1.addCell(Paragraph("TotalAsistencia", fuenteColumnas))
            table1.addCell(Paragraph("TotalFaltas", fuenteColumnas))
            table1.addCell(Paragraph("TotalRetardos", fuenteColumnas))
            table1.addCell(Paragraph("TotalJustificados",fuenteColumnas))
            table1.addCell(Paragraph("TotalSuspendidos", fuenteColumnas))

            documento.add(table1)


            var contador = 1
            var contadorposicion = 0

            Dias.moveToFirst()
            //asistencia por alumnos
            while (contador <= alumnos)
            {
                //insertamos los numeros de lista
                //table.addCell(((contador).toString()))
                val n_lista = asistencia[contadorposicion]!!["n_lista"].toString()
                table.addCell((n_lista))
                do{
                    //obtenemos los datos buscados en la tabla de asistencia
                    val (lista2, lista) = findStatusAsistenciaMes(asistencia, "fecha", "n_lista", Dias.getString(2), n_lista)
                    var status = ""
                    var fuente = fuenteAsistencia
                    var totalAsistencia = 0
                    var totalFaltas = 0
                    var totalJustificados = 0
                    var totalSuspendido = 0
                    var totalRetardos = 0

                    if ( lista == 0) {
                        status = "O"
                        fuente = fuenteFalta
                        totalFaltas ++
                    }
                    if ( lista == 0 && lista2 == 1) {
                        status = "I"
                        fuente = fuenteAsistencia
                        totalAsistencia ++
                    }
                    if ( lista == 1) {
                        status = "R"
                        fuente = fuenteRetardo
                        totalRetardos++
                    }
                    if ( lista == 2) {
                        status = "J"
                        fuente = fuenteJustifciacion
                        totalJustificados++
                    }
                    if ( lista == 3) {
                        status = "S"
                        fuente = fuenteSuspendido
                        totalSuspendido++
                    }

                    table.addCell(Paragraph(status,fuente))
                    table.addCell(Paragraph(totalAsistencia.toString(),fuente))
                    table.addCell(Paragraph(totalFaltas.toString(),fuente))
                    table.addCell(Paragraph(totalRetardos.toString(),fuente))
                    table.addCell(Paragraph(totalJustificados.toString(),fuente))
                    table.addCell(Paragraph(totalSuspendido.toString(),fuente))
                    contadorposicion ++
                }while (Dias.moveToNext())
                contador ++
                Dias.moveToFirst()

            }

            contadordias = 0
            contadorposicion = 1
            Dias.moveToFirst()

            //insertamos el total de sexo por dia
            while(contadordias < 2){
                if (contadordias == 0) table.addCell(Paragraph("M",fuenteMujer))
                else table.addCell(Paragraph("H",fuenteHombre))
                while (contadorposicion <= Dias.count){
                    table.addCell(findValue(totalsexo,"fecha","sexo",Dias.getString(2), contadordias.toString() ).toString())
                    Dias.moveToNext()
                    contadorposicion++
                }
                Dias.moveToFirst()
                contadorposicion = 1
                contadordias++
            }


            // Añade la tabla al documento
            documento.add(table)
            //hacemos espacio
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            //firma del maestro
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))
            return true
        } catch (e: Exception) {
            documento.add(table)
            error =  e.message.toString()
            return false
        } finally {
            // Cerramos el documento
            documento.close()
            asistencia.clear()
            Dias.close()
            totalsexo.clear()

        }
    }






    //@RequiresApi(Build.VERSION_CODES.O)
    fun bajoDesempeño(alumnos:Cursor):Boolean{
        val documento = Document()
        try {
            //eliminamos si existe el documento
            deletePdf("Bajo_desempeño.pdf")
            //Ruta del documento
            val ruta =  this.ruta+"Bajo_desempeño.pdf"
            //val ruta = File("/storage/emulated/0/Documents/Imprimibles/Bajo_desempeño.pdf")
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)
            //Posicionamos el cursor en la primera posicion
            alumnos.moveToFirst()
            val fecha = alumnos.getString(5).toString()
            // Abrimos el documento
            documento.open()
            documento.add(Paragraph("Alumnos con bajo desempeño "+fecha))
            documento.add(Paragraph("\n"))
            // Crea una tabla con tres columnas
            val table = PdfPTable(6)
            //Añade encabezados a las columnas
            table.addCell("Alumno")
            table.addCell("Calificacion")
            table.addCell("Actividad")
            table.addCell("Materia")
            table.addCell("Nombre")
            table.addCell("Calificada")
            // Añade filas a la tabla
            var contador = 0

            while (contador < alumnos.count){
                table.addCell(alumnos.getString(0))
                table.addCell(alumnos.getString(1))
                table.addCell(alumnos.getString(3))
                table.addCell(alumnos.getString(2))
                table.addCell(alumnos.getString(4))
                table.addCell(alumnos.getString(6))
                alumnos.moveToNext()
                contador ++
            }
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))
            // Añade la tabla al documento
            documento.add(table)
            return true
        } catch (e: Exception) {
            error =  e.message.toString()
            return false
        } finally {
            // Cerramos el documento
            documento.close()
        }
    }

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

    fun Suspencion(alumno:String, periodo:String){
        val documento = Document()
        try {
            //getDefault(context)
            //eliminamos si existe el documento
            deletePdf("Suspension.pdf")
            //Ruta del documento
            //val ruta = File("/storage/emulated/0/Documents/Imprimibles/Insidencia.pdf")
            val ruta =  this.ruta+"Suspension.pdf"
            error = ruta.toString()
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)

            // Abrimos el documento
            documento.open()
            documento.add(Paragraph( "Estimados padres de familia,"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Esperamos que se encuentren bien. Nos dirigimos a ustedes para informarles que, debido a ciertas circunstancias, hemos tomado la difícil decisión de suspender temporalmente ($periodo) a su hijo/a $alumno de nuestra institución educativa."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Esta suspensión se ha llevado a cabo con el fin de garantizar el bienestar y la seguridad de todos los estudiantes y del personal escolar. Durante este período de suspensión, les pedimos su comprensión y cooperación para brindar el apoyo necesario a su hijo/a y trabajar conjuntamente en su desarrollo académico y comportamental."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph(       "Nos tomamos muy en serio el bienestar de todos nuestros alumnos, y estamos comprometidos en proporcionar un ambiente educativo seguro y enriquecedor para cada estudiante. Durante la suspensión, estaremos trabajando de cerca con su hijo/a para ayudarle a enfrentar las situaciones que han llevado a esta medida, y estaremos disponibles para cualquier consulta o duda que ustedes puedan tener."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("La suspensión es una oportunidad para reflexionar y aprender de las acciones pasadas, y confiamos en que, con el apoyo adecuado, su hijo/a podrá regresar a la escuela con una actitud positiva y un compromiso renovado para su crecimiento académico y personal."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph(       "Les informamos que una vez concluida la suspensión, estaremos encantados de recibir nuevamente a su hijo/a en nuestra institución, siempre y cuando demuestre una actitud de mejora y compromiso con las normas y valores de nuestra comunidad educativa."))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Agradecemos su comprensión y colaboración en este proceso. Si tienen alguna duda o inquietud, no duden en comunicarse con nosotros."))

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

    fun Horaio(lunes:String,martes:String,miercoles:String, jueves:String, viernes:String):Boolean{
        val documento = Document()
        try {
            //eliminamos si existe el documento
            deletePdf("Horario.pdf")
            //Ruta del documento
            val ruta =  this.ruta+ "Horario.pdf"
            //val ruta = File("/storage/emulated/0/Documents/Imprimibles/Reporte_Asistencia.pdf")
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)
            // Abrimos el documento
            documento.open()
            documento.add(Paragraph("HORARIO ESCUELA"))
            documento.add(Paragraph("\n"))
            // Crea una tabla con tres columnas
            val table = PdfPTable(5)
            //Añade encabezados a las columnas
            table.addCell("LUNES")
            table.addCell("MARTES")
            table.addCell("MIERCOLES")
            table.addCell("JUEVES")
            table.addCell("VIERNES")

            // Añade filas a la tabla
            table.addCell(lunes)
            table.addCell(martes)
            table.addCell(miercoles)
            table.addCell(jueves)
            table.addCell(viernes)

            // Añade la tabla al documento
            documento.add(table)
            //hacemos espacio
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            //firma del maestro
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))
            return true

        } catch (e: Exception) {
            error =  e.message.toString()
            return false
        } finally {
            // Cerramos el documento
            documento.close()
        }
    }

    fun calificacion_especifica(nombre:String,tipo:String,materia:String,calificacion:Int,recomendacion:String, alumno:String):Boolean{
        //deletePdf("Calificacion.pdf")
        val documento = Document()
        try {
            val ruta =  this.ruta+ "Calificacion.pdf"
            // Creamos el PdfWriter y asignamos el OutputStream
            val fichero = FileOutputStream(ruta)
            val escritor = PdfWriter.getInstance(documento, fichero)
            // Abrimos el documento
            documento.open()
            documento.add(Paragraph("CALIFICACION DE LA ACTIVIDAD"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Alumno: $alumno"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Materia: $materia"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Actividad: $tipo"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Nombre: $nombre"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Calificacion: $calificacion / 100"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Recomendacion: $recomendacion"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("\n"))
            documento.add(Paragraph("Att. Maestro $Name_teacher"))
            documento.add(Paragraph("Escuela. $Name_school    Cct. $Cct"))
            documento.add(Paragraph("Tel. $Tel_school"))
            documento.add(Paragraph("Domicilio. $Addres_school   Col. $Colonia"))

            return  true
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
         finally {
        // Cerramos el documento
        documento.close()
        }
    }

    fun Matricula() {
        val document = Document(PageSize.LETTER.rotate())
        val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Matricula.pdf"))
        document.open()

        val fontBold = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD)
        val fontRegular = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL) // Nuevo tamaño de fuente
        val fontSmall = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL)
        val fontSmallM = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL, BaseColor(Color.MAGENTA))
        val fontSmallH = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL,BaseColor(Color.parseColor("#1A8AF9")))

        val table = PdfPTable(17)
        val columnWidths = floatArrayOf(120f, 10f, 15f, 14f,14f,20f,80f,60f,45f,37f,40f,50f,60f,45f,37f,40f,50f)//,35f,35f)
        table.setTotalWidth(columnWidths)
        //table.totalWidth = 16 * 72 / 1.54f //PageSize.LETTER.width
        table.isLockedWidth = true

        // Añadir celda combinada para el nombre
        val nombreCell = PdfPCell(Phrase("ALUMNO", fontBold))
        //nombreCell.rowspan = 2 // Combinar dos filas en una celda
        //nombreCell.colspan = 3

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
        }catch (Ex:Exception){Toast.makeText(context,Ex.message.toString(),Toast.LENGTH_LONG).show()}

        document.add(table)
        document.close()
    }


    public fun createCredentialWhitPicture() {
        // Crear documento PDF con tamaño carta (8.5 x 11 pulgadas)
        val document = Document(PageSize.LETTER)
        val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Credenciales.pdf"))
        document.open()

        // Configuración de la tabla
        val table = PdfPTable(2)
        table.totalWidth = 10.8f * 72 / 1.54f //PageSize.LETTER.width
        table.isLockedWidth = true

        // Configuración de las celdas
        val cellPadding = 10f
        val cellMargin = 10f

        // Establecer el número de credenciales que deseas crear
        val numCredenciales = 8

        // Tamaño de la celda personalizada (8.6 x 5.4 cm)
        val celdaAncho = 8.6f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        val celdaAlto = 5.4f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)

        // Agregar contenido a las credenciales
        for (i in 1..numCredenciales) {
            // Agregar celda para cada credencial
            val cell = PdfPCell()
            //Asignamos tamaño a las celdas
            cell.setPadding(cellPadding)
            cell.borderWidth = 2f
            cell.fixedHeight = celdaAlto // Establecer alto de la celda

            // Agregar contenido a la credencial
            val fontBold = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
            val fontNormal = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)

            // Agregar información de la credencial

            val cb2 = writer.directContent

// Escribir el texto en la posición deseada

            val contenidoCredencial = Paragraph()

            val margins = Rectangle(150f, 90f, 200f, 200f) // izquierda, derecha, superior, inferior
            contenidoCredencial.spacingBefore = margins.top
            contenidoCredencial.spacingAfter = margins.bottom
            contenidoCredencial.indentationLeft = margins.left
            contenidoCredencial.indentationRight = margins.right

            contenidoCredencial.add(Paragraph("Alumno: $Name_teacher", fontNormal))
            contenidoCredencial.add(Paragraph("Grado y Grupo: $Grado $Grupo", fontNormal))
            contenidoCredencial.add(Paragraph("Escuela: $Name_school", fontNormal))
            document.add(contenidoCredencial)
            //cell.addElement(contenidoCredencial)

            // Obtener la posición deseada para la imagen en la celda
            val posicionImagenX = 60f
            val posicionImagenY = 610f

            // Agregar imagen de estudiante a la celda en la posición deseada
            val cb: PdfContentByte = writer.directContent
            val imagenEstudiante = Image.getInstance(ruta + "Miguel.png")
            imagenEstudiante.scaleToFit(140f, 140f)
            imagenEstudiante.setAbsolutePosition(posicionImagenX, posicionImagenY)

            val imagenEscuela = Image.getInstance(ruta + "Escuela.png")
            imagenEscuela.scaleToFit(140f, 140f)
            imagenEscuela.setAbsolutePosition(posicionImagenX + 250, posicionImagenY)

            cb.addImage(imagenEscuela)
            cb.addImage(imagenEstudiante)
            table.addCell(cell)
        }

        // Agregar la tabla al documento
        document.add(table)

        // Cerrar el documento
        document.close()
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
        val cell2 = PdfPCell(Paragraph(content,Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)))
        cell2.backgroundColor = BaseColor.WHITE
        cell2.borderColor = BaseColor(191, 191, 191)
        cell2.borderWidth = 2f
        cell2.fixedHeight = bottom * 72 / 2.54f // Altura doble para la segunda celda
        table.addCell(cell2)

        val x = 0f + right // coordenada X en puntos
        val y = 800f - up // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
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

        val x = 0f + right // coordenada X en puntos
        val y = 800f - up // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
    }

    fun addTableTotal(up:Int, right:Int, writer: PdfWriter, type:String, value:String){
        val table = PdfPTable(1)
        table.totalWidth = 1.2f * 72 / 1.54f//PageSize.LETTER.width
        table.isLockedWidth = false
        val fuente = Font(Font.FontFamily.HELVETICA, 7f, Font.BOLD)
        // Configuración de las celdas
        val cellPadding = 10f
        val cellColor = BaseColor(255,0,0)
        // Tamaño de la celda personalizada (8.6 x 5.4 cm)
        val celdaAlto = 1.2f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        val celdaancho = 1.2f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        // Agregar celda para cada credencial
        val cell1 = PdfPCell(Phrase("$type\n"+"$value", fuente))

        //Asignamos tamaño a las celdas
        cell1.setPadding(cellPadding)
        cell1.borderColor = cellColor
        cell1.borderWidth = 1f
        cell1.fixedHeight = celdaAlto // Altura doble para la primera celda
        cell1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell1.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        table.addCell(cell1)

        // Establecer las coordenadas de la tabla
        val x = 178f + right// coordenada X en puntos
        val y = 500f - up// coordenada Y en puntos
        // Obtener el contenido directo de la página
        val contentByte = writer.directContent
        // Guardar el estado del contenido
        contentByte.saveState()
        // Agregar la tabla en las coordenadas especificadas
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        // Restaurar el estado del contenido
        contentByte.restoreState()
    }

    fun addTableAspects(up:Int, right:Int, writer: PdfWriter, name:String, percent:String, value1: String, value2: String, value3:String){
        val table = PdfPTable(3)
        table.totalWidth = 1.3f * 72 / 1.54f//PageSize.LETTER.width
        table.isLockedWidth = false
        val fuente = Font(Font.FontFamily.HELVETICA, 7f, Font.BOLD)
        // Configuración de las celdas
        val cellPadding = 0f
        // Tamaño de la celda personalizada (8.6 x 5.4 cm)
        val celdaAlto = 1.2f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)

        // Agregar celda para cada aspecto
        val cell1 = PdfPCell(Phrase(name +"\n"+ percent + "\n"+ value1, fuente))
        val cell2 = PdfPCell(Phrase("Total\n$value2\n$value3", fuente))
        val cell3 = PdfPCell(Phrase("Total"))
        //Asignamos tamaño a las celdas

        cell1.setPadding(cellPadding)
        cell1.borderWidth = 1f
        cell1.fixedHeight = celdaAlto // Altura doble para la primera celda
        cell1.colspan = 2 // Ocupar tres columnas

        cell1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell1.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        cell2.setPadding(cellPadding)
        cell2.borderWidth = 1f
        cell2.fixedHeight = celdaAlto

        cell2.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell2.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        cell3.setPadding(cellPadding)
        cell3.borderWidth = 1f
        cell3.fixedHeight = celdaAlto


        table.addCell(cell1)
        table.addCell(cell2)
        table.addCell(cell3)
        //table.addCell(cell3)
        // Establecer las coordenadas de la tabla
        val x = 179f + right// coordenada X en puntos
        val y = 500f - up// coordenada Y en puntos
        // Obtener el contenido directo de la página
        val contentByte = writer.directContent
        // Guardar el estado del contenido
        contentByte.saveState()
        // Agregar la tabla en las coordenadas especificadas
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        // Restaurar el estado del contenido
        contentByte.restoreState()
    }

    fun addTableMaterrs(up:Int, writer: PdfWriter, value:String){
        val table = PdfPTable(1)
        table.totalWidth = 3.2f * 72 / 1.54f//PageSize.LETTER.width
        table.isLockedWidth = false
        // Configuración de las celdas
        val cellPadding = 5f
        // Tamaño de la celda personalizada (8.6 x 5.4 cm)
        val celdaAlto = 1.2f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        val celdaancho = 1.2f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        // Agregar celda para cada credencial
        val cell1 = PdfPCell(Phrase(value))
        //Asignamos tamaño a las celdas
        cell1.setPadding(cellPadding)
        cell1.borderWidth = 1.5f
        cell1.fixedHeight = celdaAlto // Altura doble para la primera celda
        cell1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell1.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        //cell1.setColspan(2) // Ocupar dos columnas
        table.addCell(cell1)
        // Establecer las coordenadas de la tabla
        val x = 30f // coordenada X en puntos
        val y = 500f - up// coordenada Y en puntos
        // Obtener el contenido directo de la página
        val contentByte = writer.directContent
        // Guardar el estado del contenido
        contentByte.saveState()
        // Agregar la tabla en las coordenadas especificadas
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        // Restaurar el estado del contenido
        contentByte.restoreState()
    }

    fun addTableDetailsParcial(folio:Int, writer: PdfWriter, materias: Cursor, aspectos:MutableList<MutableMap<String, Any>>, allActivity: MutableList<MutableMap<String, Any>>, delivered: MutableList<MutableMap<String, Any>>, assess:ASSESS){
        if(materias.moveToFirst()){
            var x = 0f
            var y = 525f //+ arriba
            var arriba = 0
            var derecha = 0
            val cb = writer.directContent
            var type_aspects = ArrayList<String>()
            var result = 0.0f
            var cal_parcial = 0.0f
            var total_parcial = 0.0f

            cb.beginText()

            arriba +=34

            cb.setTextMatrix(x, y) // Establecer la posición de inicio del texto



            //addTable(300, 29,10f,0f,"  ASIGNATURAS                  DETALLES","",writer)
            ///Toast.makeText(context, aspectos.size.toString(), Toast.LENGTH_SHORT).show()

            do { //colocamos la calificacion en cada parcial por materia
                //if (Nombre_Escuela.get_tipo() == 1)  type_aspects = findValueAspects(aspectos)//si es primaria cargamos  actividades en general
                //else  type_aspects = findValueAspects(aspectos,"n_matter",materias.getString(1))//si es secundaria cargamos los aspectos por materia
                //Colocamos color a cada registro
                type_aspects = findValueAspects(aspectos,"n_matter",materias.getString(1))//si es secundaria cargamos los aspectos por materia

                val color = BaseColor(materias.getInt(2))
                cb.setColorFill(color)
                cb.setFontAndSize(BaseFont.createFont(),8f) // Establecer la fuente y el tamaño del texto
                //colocamos la cantidad de actividades por materia
                for(i in 0 until type_aspects.size) {
                    //obtenemos los valores que necesitamos dependiendo de la matria y la actividad
                    val deliveredActivitys = findValuePartials(delivered, "activity", "matter", "folio", type_aspects[i],materias.getString(1), folio.toString())
                    val totalActivitys = findValuePartials(allActivity, "activity", "matter", type_aspects[i],materias.getString(1))
                    val value_activity = findValueActivity(aspectos, "name", "n_matter", type_aspects[i],materias.getString(1))
                    var calification = 0.0f

                    //SI TENEMOS POR LO MENOS UNA ACTIVIDAD PARA NO DIVIDIR EN CERO
                    if (totalActivitys > 0 ){
                        result = (deliveredActivitys.first.toFloat()/totalActivitys.toFloat())
                        calification = (deliveredActivitys.second.toFloat()/totalActivitys.toFloat())
                        cal_parcial += result * value_activity
                    }
                    else{
                        result = (deliveredActivitys.first.toFloat()/1)
                        calification = (deliveredActivitys.second.toFloat()/1)
                        cal_parcial += result * value_activity
                    }
                    val total_aspecto = result * value_activity
                    calification *= value_activity
                    calification /= 100
                    total_parcial += calification
                    //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"${roundDecimalFloat(result*10,1)}",x+288+derecha, y-(arriba+15),0f )
                    ///addTableAspects(arriba, derecha, writer, type_aspects[i], "$value_activity%","$deliveredActivitys/$totalActivitys","${roundDecimalFloat(result*10,1)}")
                    addTableAspects(arriba, derecha, writer, type_aspects[i], "$value_activity%","${deliveredActivitys.first}/$totalActivitys","${roundDecimalFloat(total_aspecto,1)}", calification.toString())
                    derecha += 60
                }
                //encabezado del parcial y su calificacion en total
                //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"Parcial",x+130+derecha, y-arriba,0f )
                //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"${roundDecimalFloat(cal_parcial, 1)}",x+135+(derecha), y-(arriba+15),0f )
                //total_parcial += roundDecimalFloat(cal_parcial, 1)
                //tabla calificacion del parcial
                addTableTotal(arriba, derecha+1, writer, "Parcial", total_parcial.toString())
                //cb.setFontAndSize(BaseFont.createFont(),7f)
                val faltas = assess.getPercentAttendance1("c_matter","folio",materias.getString(1), folio.toString())
                cb.setFontAndSize(BaseFont.createFont(),8f)
                //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"Asistencia",x+172+derecha, y-arriba,0f )
                //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,faltas,x+173+(derecha), y-(arriba+15),0f )
                addTableTotal(arriba, derecha+57, writer, "Fal/Asis", faltas)
                //nombre de la materia
                cb.setFontAndSize(BaseFont.createFont(),11f)
                //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,materias.getString(1),x+39, y-(arriba + 10),0f )
                //rellenamos la tabla de las materias
                addTableMaterrs(arriba, writer, materias.getString(1))
                arriba +=34
                derecha = 0
                total_parcial = 0f
                cal_parcial = 0.0f

            }while (materias.moveToNext())

            cb.endText()
            //tablas de firmas y datos de la escuela
            addTableNote(790,120, 4f, 2.5f, " NOTA IMPORTANTE ",writer)
            addTable(690,40, 3.5f, 1.2f, " DIRECTOR DEL PLANTEL  ", " $Name_teacher ",writer)
            addTable(690,240, 3.5f, 1.2f, " MAESTRO(A) DEL GRUPO ","  "+Name_teacher.uppercase(),writer)
            addTable(790,370, 4.5f, 2.5f, " PADRE O TUTOR DEL ALUMNO(A)  ", "   ",writer)
            addTable(300,28, 10.8f, 0f, "        ASIGNATURAS          DETALLES ENCUADRE DE LA CALIFICACIÓN ", "",writer)
        }

    }

    fun getAllParcial(parcial:Int){
        try {
            getDefault(context)
            val document = Document(PageSize.LETTER)
            val assess = ASSESS(context)
            assess.getDatesByParcial(parcial)
            val aspects = assess.getAspectsByMatters()
            val delivered = assess.getAllActivitysDelivered()
            val materias = assess.getAllMatters()
            val totalActivitys = assess.getAllActivitys()
            assess.getTotalAttendance()
            val faltas = assess.getAbsenceByFolio()
            val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Parcial.pdf"))
            val putImage = ImagesForDocuments()
            document.open()
            if (Nombre_Escuela.Alumnos.moveToFirst()) {
                do {
                    Name_Student = Nombre_Escuela.Alumnos.getString(1)
                    Apellido1 = Nombre_Escuela.Alumnos.getString(2)
                    Apellido2 = Nombre_Escuela.Alumnos.getString(3)
                    Curp = Nombre_Escuela.Alumnos.getString(7)
                    putImage.putImageSchool(-20,15, 100.0f, 100.0f, writer,"${Nombre_Escuela.getName()}.jpg")
                    putImage.putImageStudent(-10,-30, 100.0f, 100.0f, writer,"${Nombre_Escuela.Alumnos.getString(0)}.jpg")
                    //addTableBoleta(writer)
                    addInfoBoleta(0, writer)
                    addTableDetailsParcial(Nombre_Escuela.Alumnos.getInt(0), writer, materias, aspects,totalActivitys,delivered,assess)
                    document.newPage()
                } while (Nombre_Escuela.Alumnos.moveToNext())
            }
            document.close()
            assess.close()
            aspects.clear()
            materias.close()
            delivered.clear()
        }
        catch (Ex:Exception){
            Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun getParcialByStudent(parcial:Int, folio: Int, nombre:String, apellidop:String, apellidom:String, curp: String){
        try {
            getDefault(context)
            val document = Document(PageSize.LETTER)
            val assess = ASSESS(context)
            assess.getDatesByParcial(parcial)
            val aspects = assess.getAspectsByMatters()
            val delivered = assess.getAllActivitysDelivered()
            val materias = assess.getAllMatters()
            val totalActivitys = assess.getAllActivitys()
            assess.getTotalAttendance()
            val faltas = assess.getAbsenceByFolio()
            val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Parcial.pdf"))
            document.open()

                    Name_Student = nombre
                    Apellido1 = apellidop
                    Apellido2 = apellidom
                    Curp = curp
                    addInfoBoleta(0, writer)
                    addTableDetailsParcial(folio, writer, materias, aspects,totalActivitys,delivered,assess)


            document.close()
            assess.close()
            aspects.clear()
            materias.close()
            delivered.clear()
        }
        catch (Ex:Exception){
            Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    fun addMattersBoleta(folio:Int, writer: PdfWriter, cursor: Cursor, calificacion:MutableList<MutableMap<String, Any>>){
        if(cursor.moveToFirst()){
            var x = 0f
            var y = 505f //+ arriba
            var arriba = 0
            var derecha = 0
            var final = 0.0f
            val parciales: Array<Float> = arrayOf(1.5f, 2.3f, 3.7f,4.0f)
            val cb = writer.directContent
            cb.beginText()
            cb.setFontAndSize(BaseFont.createFont(),10f) // Establecer la fuente y el tamaño del texto
            //encabezado del las materias

            addTableBoleta(arriba, writer,"MATERIAS","1er","2do", "3ro", "Final")

            arriba +=34
            //escribimos las materias
            cb.setTextMatrix(x, y) // Establecer la posición de inicio del texto
            //obtenemos los parciales por materia
            do {
                for(i in 1..3) {
                   val points = findValuePartials(calificacion,"n_materia","folio","periodo",cursor.getString(1),folio.toString(),i.toString())
                    // Color en formato entero (por ejemplo: -10527822)
                    final += points.first
                    val colorEntero = cursor.getInt(2)

                    // Crear el objeto BaseColor con el valor entero
                    val textColor = BaseColor(colorEntero)
                    cb.setColorFill(textColor)
                    //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,points.toString(),x+140+derecha, y-arriba,0f )
                    parciales[i - 1] = points.first.toFloat()
                    derecha += 45
                }

                //cb.showTextAligned(PdfContentByte.ALIGN_LEFT,cursor.getString(1),x+41, y-arriba,0f)
                addTableBoleta(arriba, writer, cursor.getString(1), parciales[0].toString(), parciales[1].toString(), parciales[2].toString(), (final/3).toString())
                arriba +=34
                derecha = 0
                final = 0f

            }while (cursor.moveToNext())
            cb.endText()
        }
    }

    fun addInfoBoleta(arriba:Int, writer: PdfWriter){
        val x = 0f
        val y = 800f //+ arriba
        val cb = writer.directContent
        cb.beginText()
        cb.setFontAndSize(BaseFont.createFont(),14f) // Establecer la fuente y el tamaño del texto
        cb.setTextMatrix(x, y) // Establecer la posición de inicio del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"SISTEMA DE EDUCATIVO NACIONAL",x+190, y-70,0f )
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "BOLETA DE EVALUACIÓN", x+230, y -90, 0f)
        cb.setFontAndSize(BaseFont.createFont(),10f) // Establecer la fuente y el tamaño del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "${Grado.uppercase()} GRADO DE EDUCACION PRIMARIA    CICLO ESCOLAR $Ciclo ", x+150, y - 110, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "DATOS DEL ALUMNO", x+50, y - 150, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Apellido1.uppercase(), x+70, y - 165, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Apellido2.uppercase(), x+180, y - 165, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Name_Student.uppercase(), x+280, y - 165, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Curp.uppercase(), x+430, y - 165, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "___________________________________________________________________   __________________________", x+30, y - 170, 0f)
        cb.setFontAndSize(BaseFont.createFont(),7f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "   PRIMER APELLIDO                 SEGUNDO APELLIDO                              NOMBRE(S)                                                                     CURP   ", x+60, y - 180, 0f)
        cb.setFontAndSize(BaseFont.createFont(),10f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "DATOS DE LA ESCUELA", x+50, y - 210, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Name_school.uppercase(), x+70, y - 225, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Grado.uppercase() + " "+Grupo.uppercase(), x+295, y - 225, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Turno.uppercase(), x+348, y - 225, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, Cct.uppercase(), x+440, y - 225, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "_____________________________________________  _________  __________  ___________________________", x+30, y - 230, 0f)
        cb.setFontAndSize(BaseFont.createFont(),7f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "                         NOMBRE DE LA ESCUELA                                                     GRUPO               TURNO                                            CCT", x+60, y - 240, 0f)
        cb.endText()
    }

    fun addTableBoleta(arriba:Int,writer: PdfWriter, materia:String, parcial1:String, parcial2: String, parcial3:String, final:String){
        val table = PdfPTable(10)
        table.totalWidth = 10f * 72 / 1.54f//PageSize.LETTER.width
        table.isLockedWidth = true

        val fuente = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD)
        // Configuración de las celdas
        val cellPadding = 5f
        // Establecer el número de credenciales que deseas crear

        // Tamaño de la celda personalizada (8.6 x 5.4 cm)
        val celdaAlto = 1.2f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        val celdaancho = 1.2f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        //for (i in 1..numCredenciales) {
        // Agregar celda para cada credencial
        val cell1 = PdfPCell()
        val cell2 = PdfPCell()
        val cell3 = PdfPCell()
        val cell4 = PdfPCell()
        val cell5 = PdfPCell()

        //Asignamos tamaño a las celdas
        cell1.addElement(Phrase(materia,fuente))

        cell1.setPadding(cellPadding)
        cell1.borderWidth = 1f
        cell1.fixedHeight = celdaAlto // Altura doble para la primera celda
        cell1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell1.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cell1.setColspan(6) // Ocupar dos columnas

        table.addCell(cell1)

        cell2.addElement(Phrase(parcial1))

        cell2.borderWidth = 1f
        cell2.fixedHeight = celdaancho // Establecer alto de la celda
        cell2.horizontalAlignment = PdfPCell.ALIGN_LEFT
        cell2.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cell2.setPadding(cellPadding)

        cell3.addElement(Phrase(parcial2))

        cell3.borderWidth = 1f
        cell3.fixedHeight = celdaAlto // Establecer alto de la celda
        cell3.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell3.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cell3.setPadding(cellPadding)

        cell4.addElement(Phrase(parcial3))

        cell4.borderWidth = 1f
        cell4.fixedHeight = celdaAlto // Establecer alto de la celda
        cell4.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell4.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cell4.setPadding(cellPadding)


        cell5.addElement(Phrase(final))

        cell5.borderWidth = 1f
        cell5.fixedHeight = celdaAlto // Establecer alto de la celda
        cell5.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cell5.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cell5.setPadding(cellPadding)


        //table.addCell(cell1)
        table.addCell(cell2)
        table.addCell(cell3)
        table.addCell(cell4)
        table.addCell(cell5)

        // Establecer las coordenadas de la tabla
        val x = 30f // coordenada X en puntos
        val y = 500f - arriba// coordenada Y en puntos


        // Obtener el contenido directo de la página
        val contentByte = writer.directContent

        // Guardar el estado del contenido
        contentByte.saveState()

        // Agregar la tabla en las coordenadas especificadas
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)

        // Restaurar el estado del contenido
        contentByte.restoreState()

        //}

    }


    fun addTable():PdfPTable{
        val table = PdfPTable(2)
        table.totalWidth = 10.8f * 72 / 1.54f //PageSize.LETTER.width
        table.isLockedWidth = true
        // Configuración de las celdas
        val cellPadding = 10f
        // Establecer el número de credenciales que deseas crear
        val numCredenciales = 4
        // Tamaño de la celda personalizada (8.6 x 5.4 cm)
        val celdaAlto = 5.4f * 72 / 2.54f // Convertir cm a puntos (1 cm = 72 puntos)
        //for (i in 1..numCredenciales) {
            // Agregar celda para cada credencial
            val cellAlumno = PdfPCell()
            val cellEscuela = PdfPCell()
            //Asignamos tamaño a las celdas
            cellAlumno.setPadding(cellPadding)
            cellAlumno.borderWidth = 2f
            cellAlumno.fixedHeight = celdaAlto // Establecer alto de la celda
            cellEscuela.setPadding(cellPadding)
            cellEscuela.borderWidth = 2f
            cellEscuela.fixedHeight = celdaAlto // Establecer alto de la celda
            table.addCell(cellAlumno)
            table.addCell(cellEscuela)
        //}
        return table
    }
    fun addInformation(arriba:Int, writer: PdfWriter){

        val x = 170f
        val y = 710f + arriba
        val cb = writer.directContent
        cb.beginText()
        cb.setFontAndSize(BaseFont.createFont(),12f) // Establecer la fuente y el tamaño del texto
        cb.setTextMatrix(x, y) // Establecer la posición de inicio del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"ESCUELA: ${Name_school.uppercase()} ",x - 105, y+30,0f )
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "ALUMNO:", x-15, y -20, 0f)
        cb.setFontAndSize(BaseFont.createFont(),10f) // Establecer la fuente y el tamaño del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "${Name_Student.uppercase()}", x - 100, y - 45, 0f)
        cb.setFontAndSize(BaseFont.createFont(),9f) // Establecer la fuente y el tamaño del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"Maestro $Name_teacher", x-100,y - 60,0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"${Grado}-${Grupo}/$Ciclo", x+65,y - 60,0f)
        //cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "CICLO $Ciclo", x, y - 45, 0f)
        cb.endText()

        val x2 = 305f
        val y2 = 710f + arriba
        val cb2 = writer.directContent
        cb2.beginText()
        cb2.setFontAndSize(BaseFont.createFont(),11f) // Establecer la fuente y el tamaño del texto
        cb2.setTextMatrix(x, y) // Establecer la posición de inicio del texto
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT,"    DATOS DEL ALUMNO : ", x2 + 35,y2 + 25,0f)
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT, "   DOMICILIO: ", x2, y2 + 10, 0f)
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT, Address_Student,x2 + 10,y2 - 10,0f)
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT, "   TUTOR: ", x2, y2 - 30f, 0f)
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT,Name_tutor,x2 + 10,y2 - 50,0f)
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT,"   TELEFONO: ",x2 + 175,y2 - 30f,0f)
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT, PhoneNumber_Tutor, x2 + 185, y2 - 50f, 0f)
        val cb3 = writer.directContent
        cb3.setFontAndSize(BaseFont.createFont(), 9f)
        cb3.showTextAligned(PdfContentByte.ALIGN_LEFT,"Escuela '${Name_school.uppercase()}'",x2 + 10,y2 - 70f,0f)
        cb3.showTextAligned(PdfContentByte.ALIGN_LEFT,"CCT: '${Cct.uppercase()}' Turno: $Turno",x2 + 10,y2 - 85f,0f)
        cb3.showTextAligned(PdfContentByte.ALIGN_LEFT,"${Addres_school} Col.: $Colonia",x2 + 10,y2 - 100f,0f)
        cb3.endText()

        val cb4 = writer.directContent
        val barcode = Barcode128()
        barcode.codeType = Barcode128.CODE128
        barcode.code = quitarAcentos(Name_Student + " " + Grado + " " + Grupo + " " + Turno) // Código de barras que deseas mostrar
        barcode.x = 0.4f // Escala horizontal del código de barras
        barcode.barHeight = 30f // Altura del código de barras
        val image = barcode.createImageWithBarcode(cb4, null, null)
        image.setAbsolutePosition(70f, 605f + arriba) // Posición del código de barras
        cb.addImage(image)
    }


    public fun Credential() {
        // Crear documento PDF con tamaño carta (8.5 x 11 pulgadas)
        deletePdf("Credenciales")
        getDefault(context)

        val document = Document(PageSize.LETTER)
        val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Credenciales.pdf"))
        document.open()
        var arriba = 0
        var tablas = 0
        val putLogo = ImagesForDocuments()

        if (Nombre_Escuela.Alumnos.moveToFirst()) {
            for (i in 0..Nombre_Escuela.Alumnos.count-1) {
                //dibujamos los rows  de la tabla
                document.add(addTable())
                putLogo.putImageSchool(arriba, 0, 60.0f, 60.0f,writer, "${Nombre_Escuela.getName()}.jpg")
                putLogo.putImageStudent(arriba, 0, 60.0f, 60.0f,writer, "${Nombre_Escuela.Alumnos.getString(0)}.jpg")
                //asignamos los valores a las variables de cada alumno
                Name_Student  = Nombre_Escuela.Alumnos.getString(1) +" "+Nombre_Escuela.Alumnos.getString(2)+" "+Nombre_Escuela.Alumnos.getString(3)
                Name_tutor  = Nombre_Escuela.Alumnos.getString(14)
                PhoneNumber_Tutor = Nombre_Escuela.Alumnos.getString(15)
                Address_Student = Nombre_Escuela.Alumnos.getString(5)
                Nombre_Escuela.Alumnos.moveToNext()
                //insertamos la informacion al pdf
                addInformation(arriba, writer)
                //avanzamos hacia abajo del documento
                arriba += -152
                //para saber cuantas credenciales van en cada hoja
                tablas += 1
                //si se llega al maximo de credenciales que son 4 reiniciamos la pocision y el conteo de credenciales para continuar en la siguente hoja
                if (tablas == 4) {
                    arriba = 0
                    tablas = 0
                }
            }
        }

        document.close()
    }

    fun Boleta(){
        deletePdf("Boleta")
        getDefault(context)

        val document = Document(PageSize.LETTER)
        val materiasdb = MateriasBD(context)
        val assess = ASSESS(context)
        val partials = assess.getCalificactionsParciales()
        val materias = materiasdb.obtenerAll()
        val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Boletas.pdf"))
        document.open()
        val putImage = ImagesForDocuments()
        if (Nombre_Escuela.Alumnos.moveToFirst()){
            do {
                Name_Student = Nombre_Escuela.Alumnos.getString(1)
                Apellido1 = Nombre_Escuela.Alumnos.getString(2)
                Apellido2 = Nombre_Escuela.Alumnos.getString(3)
                Curp = Nombre_Escuela.Alumnos.getString(7)
                try {
                    putImage.putImageSchool(-20,15, 100.0f, 100.0f, writer,"${Nombre_Escuela.getName()}.jpg")
                    putImage.putImageStudent(-10,-30, 100.0f, 100.0f, writer,"${Nombre_Escuela.Alumnos.getString(0)}.jpg")
                }catch (Ex:Exception){Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_SHORT).show()}

                //addTableBoleta(writer)
                addInfoBoleta(0, writer)
                //addInfoBoleta(0, writer)
                addMattersBoleta(Nombre_Escuela.Alumnos.getInt(0), writer,materias, partials)
                addTableNote(790,150, 4f, 2.5f, " NOTA IMPORTANTE ",writer)
                addTable(690,260, 3.5f, 1.2f, " MAESTRO(A) DEL GRUPO ","  "+Name_teacher.uppercase(),writer)
                addTable(690,70, 3.5f, 1.2f, " DIRECTOR DEL PLANTEL  ", Name_teacher.uppercase(),writer)
                document.newPage()
            }while (Nombre_Escuela.Alumnos.moveToNext())
        }
        document.close()
        materiasdb.close()
        assess.close()
        partials.clear()
    }

    fun Estadistica(normal:MutableList<MutableMap<String, Any>>, repetidores:MutableList<MutableMap<String, Any>>,normal2:MutableList<MutableMap<String, Any>>,
                    altas:MutableList<MutableMap<String, Any>>, bajas:MutableList<MutableMap<String, Any>>, final:MutableList<MutableMap<String, Any>>, existencia:MutableList<MutableMap<String, Any>>,
                    aprovados:MutableList<MutableMap<String, Any>>, reprovados:MutableList<MutableMap<String, Any>>){
        getDefault(context)

        val document = Document(PageSize.LETTER)
        val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Estadistica.pdf"))

        val inicio = normal[0]["edad"].toString().toInt()
        val fin = normal[normal.size-1]["edad"].toString().toInt()
        val numColumnas = ((fin + 1) - inicio)
        val Table = PdfPTable(numColumnas)
        Table.totalWidth = (70 * numColumnas).toFloat()//tamaño de las columnas
        Table.isLockedWidth = true
        document.open()

        val cb = writer.directContent
        cb.beginText()
        cb.setFontAndSize(BaseFont.createFont(),14f) // Establecer la fuente y el tamaño del texto
        cb.setTextMatrix(10f, 700f) // Establecer la posición de inicio del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "GRADO  ${Grado.uppercase()} GRUPO ${Grupo.uppercase()} DOCENTE ${Name_teacher.uppercase()}", 60f, 700f, 0f)
        cb.setFontAndSize(BaseFont.createFont(),10f) // Establecer la fuente y el tamaño del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT,"REPETIDORES",30f, 626f,0f )
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "NUEVO-INGRESO", 30f, 610f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "TOTAL", 30f, 595f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "ALTAS", 30f, 432f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "SUMAN", 30f, 417f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "BAJAS", 30f, 402f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "TOTAL", 30f, 385f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "INSCRIPCIÓN TOTAL", 26f, 300f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "EXISTENCIA", 30f, 282f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "APROVADOS", 30f, 264f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "REPROVADOS", 30f, 248f, 0f)
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "TOTAL", 30f, 232f, 0f)

        cb.endText()
        var right = 0

        //añadimos las estasticas iniciales con repetidores

        for(i in inicio .. fin){
            addTableEstadisct1(normal, repetidores,107,100 + right,writer,i)

            right += 75
        }
        //creamos tabla totales
        val totalsRepit = addTableTotales()//tabla para los totales
        fillTableStadisticRepit(totalsRepit)//llenamos los totales de la estadistica de repetidores
        putPosition(107,100+right,writer,totalsRepit)
        right = 0

        //añadimos las estadisticas de altas y basjas
        for(i in inicio .. fin){
            addTableEstadisct2(normal2, altas, bajas,317,100 + right,writer,i)
            right += 75
        }
        //creamos tabla totales
        val totalsRegisters = addTableTotales()//tabla para los totales
        fillTableStadisticRegister(totalsRegisters) //llenamos los totales de basjas-altas
        putPosition(317,100+right,writer,totalsRegisters)
        right = 0

        //creamos la tabla de aprovados y reprovados
        for(i in inicio .. fin){
            addTableEstadisct3(final, existencia,aprovados,reprovados, 467,100 + right,writer,i)
            right += 75
        }

        addTable(128,129,9.3f,0f,"ESTADÍSTICA INICIAL - (Agosto, Septiembre,Octubre, Noviembre, Diciembre)","", writer)
        addTable(320,129,9.3f,0f,"ESTADÍSTICA SEMESTRAL - (Enero, Febrero, Marzo, Abril, Mayo)","", writer)
        addTable(455,129,9.3f,0f,"ESTADÍSTICA FINAL - (Junio y Julio)","", writer)
        //addTable(314,9,9.3f,0f,"ESTADÍSTICA INICIAL - (Agosto, Septiembre,Octubre, Noviembre, Diciembre)","", writer)
        //document.add(Table)
        document.close()
        normal.clear()
        repetidores.clear()
        normal2.clear()
        altas.clear()
        bajas.clear()
        final.clear()
        aprovados.clear()
        reprovados.clear()
    }

    fun addCell(data:String, sexo:Int):PdfPCell{
        val fuenteM = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.MAGENTA)
        val fuenteH = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))

        var phraseHorizontal = Phrase(data,fuenteM)
        if (sexo == 1) phraseHorizontal = Phrase(data,fuenteH)
        if (sexo == 2) phraseHorizontal = Phrase(data)
        val cellHorizontal = PdfPCell(phraseHorizontal)
        cellHorizontal.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellHorizontal.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        return cellHorizontal
    }

    fun addTableTotales():PdfPTable{
        val fuenteM = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.MAGENTA)
        val fuenteH = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))

        // Crear una tabla principal con una fila y tres columnas
        val tablePrincipal = PdfPTable(3)
        tablePrincipal.totalWidth = 75f
        tablePrincipal.isLockedWidth = true

        // Elemento Phrase para la celda horizontal

        val phraseHorizontal = Phrase("TOTALES")
        // Añadir el elemento Phrase a una celda de la tabla principal
        val cellHorizontal = PdfPCell(phraseHorizontal)
        cellHorizontal.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellHorizontal.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellHorizontal.colspan = 3 // Establecer el ancho de la celda para que ocupe las tres columnas
        tablePrincipal.addCell(cellHorizontal)

        // Elementos Phrase para las celdas verticales
        val phraseVertical1 = Phrase("H",fuenteH)
        val phraseVertical2 = Phrase("M",fuenteM)
        val phraseVertical3 = Phrase("T")

        val cellVertical1 = PdfPCell(phraseVertical1)
        val cellVertical2 = PdfPCell(phraseVertical2)
        val cellVertical3 = PdfPCell(phraseVertical3)

        cellVertical1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical2.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical3.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical1.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical2.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical3.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellVertical1)
        tablePrincipal.addCell(cellVertical2)
        tablePrincipal.addCell(cellVertical3)

        return tablePrincipal
    }
    fun clearTotals(){
        tRepetidoresM = 0
        tRepetidoresH = 0
        tNuevoH = 0
        tNuevoM = 0
        tBajasH = 0
        tBajasM = 0
        tAltasH = 0
        tAltasM = 0
        tInscripcionM = 0
        tInscripcionH = 0
        tExistenciaM = 0
        tExistenciaH = 0
        tAprovadosM = 0
        tAprovadosH = 0
        tReprovadosM = 0
        tReprovadosH = 0

    }

    fun fillTableStadisticRepit(table:PdfPTable){
        table.addCell(addCell(tRepetidoresH.toString(),1))
        table.addCell(addCell(tRepetidoresM.toString(),0))
        table.addCell(addCell((tRepetidoresH + tRepetidoresM).toString(),2))
        table.addCell(addCell(tNuevoH.toString(),1))
        table.addCell(addCell(tNuevoM.toString(),0))
        table.addCell(addCell((tNuevoH + tNuevoM).toString(),2))
        val totalH = tRepetidoresH + tNuevoH
        val totalM = tRepetidoresM + tNuevoM
        val totalT = totalH + totalM
        table.addCell(addCell((totalH).toString(),1))
        table.addCell(addCell((totalM).toString(),0))
        table.addCell(addCell((totalT).toString(),2))
        clearTotals()
    }
    fun fillTableStadisticRegister(table:PdfPTable){
        table.addCell(addCell(tAltasH.toString(),1))
        table.addCell(addCell(tAltasM.toString(),0))
        table.addCell(addCell((tAltasH + tAltasM).toString(),2))
        table.addCell(addCell(tNuevoH.toString(),1))
        table.addCell(addCell(tNuevoM.toString(),0))
        table.addCell(addCell((tNuevoH + tNuevoM).toString(),2))
        table.addCell(addCell(tBajasH.toString(),1))
        table.addCell(addCell(tBajasM.toString(),0))
        table.addCell(addCell((tBajasH + tBajasM).toString(),2))
        val totalH = tAltasH + tNuevoH + tBajasH
        val totalM = tAltasM + tNuevoM + tBajasM
        val totalT = totalH + totalM
        table.addCell(addCell((totalH).toString(),1))
        table.addCell(addCell((totalM).toString(),0))
        table.addCell(addCell((totalT).toString(),2))
        clearTotals()
    }

    fun fillTableStadisticFinal(table:PdfPTable){
        table.addCell(addCell(tAltasH.toString(),1))
        table.addCell(addCell(tAltasM.toString(),0))
        table.addCell(addCell((tAltasH + tAltasM).toString(),2))
        table.addCell(addCell(tNuevoH.toString(),1))
        table.addCell(addCell(tNuevoM.toString(),0))
        table.addCell(addCell((tNuevoH + tNuevoM).toString(),2))
        table.addCell(addCell(tBajasH.toString(),1))
        table.addCell(addCell(tBajasM.toString(),0))
        table.addCell(addCell((tBajasH + tBajasM).toString(),2))
        val totalH = tAltasH + tNuevoH + tBajasH
        val totalM = tAltasM + tNuevoM + tBajasM
        val totalT = totalH + totalM
        table.addCell(addCell((totalH).toString(),1))
        table.addCell(addCell((totalM).toString(),0))
        table.addCell(addCell((totalT).toString(),2))
        clearTotals()
    }


    fun addTableEstadisct1(ingreso:MutableList<MutableMap<String, Any>>, repetidores:MutableList<MutableMap<String, Any>>, up:Int, right: Int,writer: PdfWriter, año:Int) {

        val fuenteM = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.MAGENTA)
        val fuenteH = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))


        // Crear una tabla principal con una fila y tres columnas
        val tablePrincipal = PdfPTable(3)
        tablePrincipal.totalWidth = 75f
        tablePrincipal.isLockedWidth = true

        // Elemento Phrase para la celda horizontal

        val phraseHorizontal = Phrase("$año años")
        // Añadir el elemento Phrase a una celda de la tabla principal
        val cellHorizontal = PdfPCell(phraseHorizontal)
        cellHorizontal.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellHorizontal.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellHorizontal.colspan = 3 // Establecer el ancho de la celda para que ocupe las tres columnas
        tablePrincipal.addCell(cellHorizontal)

        // Elementos Phrase para las celdas verticales
        val phraseVertical1 = Phrase("H",fuenteH)
        val phraseVertical2 = Phrase("M",fuenteM)
        val phraseVertical3 = Phrase("T")

        val cellVertical1 = PdfPCell(phraseVertical1)
        val cellVertical2 = PdfPCell(phraseVertical2)
        val cellVertical3 = PdfPCell(phraseVertical3)

        cellVertical1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical2.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical3.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical1.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical2.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical3.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellVertical1)
        tablePrincipal.addCell(cellVertical2)
        tablePrincipal.addCell(cellVertical3)


        //contenido de la tabla
        var  h = 0
        var  m = 0
        var total = 0
        var totalM = 0
        var totalH = 0
        var totalT =0





        m = findValue(repetidores, "sexo", "edad", "0", año.toString())
        h = findValue(repetidores, "sexo", "edad", "1", año.toString())
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tRepetidoresM += m
        tRepetidoresH += h

        var phraseContentM = Phrase(m.toString(),fuenteM)
        var phraseContentH = Phrase(h.toString(),fuenteH)
        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)
        var phraseContentT = Phrase(total.toString())
        var cellverticalM = PdfPCell(phraseContentM)
        var cellverticalH = PdfPCell(phraseContentH)
        var cellverticalT = PdfPCell(phraseContentT)
        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE


        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        m = findValue(ingreso, "sexo", "edad", "0", año.toString())
        h = findValue(ingreso, "sexo", "edad", "1", año.toString())
        total =  m + h
        totalM += m
        totalH += h
        totalT += total


        tNuevoM += m
        tNuevoH += h


        phraseContentM = Phrase(m.toString(),fuenteM)
        phraseContentH = Phrase(h.toString(),fuenteH)

        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)
        phraseContentT = Phrase(total.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE


        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        //Totales

        phraseContentM = Phrase(totalM.toString(),fuenteM)
        phraseContentH = Phrase(totalH.toString(),fuenteH)

        /*if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)*/
        phraseContentT = Phrase(totalT.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)


        val x = 30f + right  // coordenada X en puntos
        val y = 700f - up // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(tablePrincipal.totalWidth, tablePrincipal.totalHeight)
        tablePrincipal.writeSelectedRows(0, -1, 0f, tablePrincipal.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
    }

    fun addTableEstadisct2(ingreso:MutableList<MutableMap<String, Any>>, altas:MutableList<MutableMap<String, Any>>, bajas:MutableList<MutableMap<String, Any>>, up:Int, right: Int,writer: PdfWriter, año:Int) {

        val fuenteM = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.MAGENTA)
        val fuenteH = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))

        // Crear una tabla principal con una fila y tres columnas
        val tablePrincipal = PdfPTable(3)
        tablePrincipal.totalWidth = 75f
        tablePrincipal.isLockedWidth = true

        // Elemento Phrase para la celda horizontal

        val phraseHorizontal = Phrase("$año años")
        // Añadir el elemento Phrase a una celda de la tabla principal
        val cellHorizontal = PdfPCell(phraseHorizontal)
        cellHorizontal.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellHorizontal.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellHorizontal.colspan = 3 // Establecer el ancho de la celda para que ocupe las tres columnas
        tablePrincipal.addCell(cellHorizontal)

        // Elementos Phrase para las celdas verticales
        val phraseVertical1 = Phrase("H",fuenteH)
        val phraseVertical2 = Phrase("M",fuenteM)
        val phraseVertical3 = Phrase("T")

        val cellVertical1 = PdfPCell(phraseVertical1)
        val cellVertical2 = PdfPCell(phraseVertical2)
        val cellVertical3 = PdfPCell(phraseVertical3)

        cellVertical1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical2.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical3.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical1.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical2.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical3.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellVertical1)
        tablePrincipal.addCell(cellVertical2)
        tablePrincipal.addCell(cellVertical3)


        //contenido de la tabla
        var  h = 0
        var  m = 0
        var total = 0
        var totalM = 0
        var totalH = 0
        var totalT =0

        //altas
        m = findValue(altas, "sexo", "edad", "situacion","0", año.toString(), "ALTA")
        h = findValue(altas, "sexo", "edad", "situacion","1", año.toString(), "ALTA")
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tAltasM += m
        tAltasH += h

        var phraseContentM = Phrase(m.toString(),fuenteM)
        var phraseContentH = Phrase(h.toString(),fuenteH)
        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)

        var phraseContentT = Phrase(total.toString())
        var cellverticalM = PdfPCell(phraseContentM)
        var cellverticalH = PdfPCell(phraseContentH)
        var cellverticalT = PdfPCell(phraseContentT)
        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE



        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        //normales

        m = findValue(ingreso, "sexo", "edad","0", año.toString())
        h = findValue(ingreso, "sexo", "edad","1", año.toString())
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tNuevoM += m
        tNuevoH += h

        phraseContentM = Phrase(m.toString(),fuenteM)
        phraseContentH = Phrase(h.toString(),fuenteH)
        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)

        phraseContentT = Phrase(total.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE


        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        //bajas

        m = findValue(bajas, "sexo", "edad", "situacion","0", año.toString(), "BAJA")
        h = findValue(bajas, "sexo", "edad", "situacion","1", año.toString(), "BAJA")
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tBajasM += m
        tBajasH += h

        phraseContentM = Phrase(m.toString(),fuenteM)
        phraseContentH = Phrase(h.toString(),fuenteH)

        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)
        phraseContentT = Phrase(total.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        //Totales

        phraseContentM = Phrase(totalM.toString(),fuenteM)
        phraseContentH = Phrase(totalH.toString(),fuenteH)

        /*if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)*/
        phraseContentT = Phrase(totalT.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)




        val x = 30f + right  // coordenada X en puntos
        val y = 700f - up // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(tablePrincipal.totalWidth, tablePrincipal.totalHeight)
        tablePrincipal.writeSelectedRows(0, -1, 0f, tablePrincipal.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
    }

    fun addTableEstadisct3(ingreso:MutableList<MutableMap<String, Any>>, repetidores:MutableList<MutableMap<String, Any>>, aprovados:MutableList<MutableMap<String, Any>>,
                           reprovados:MutableList<MutableMap<String, Any>>, up:Int, right: Int,writer: PdfWriter, año:Int) {

        val fuenteM = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.MAGENTA)
        val fuenteH = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))


        // Crear una tabla principal con una fila y tres columnas
        val tablePrincipal = PdfPTable(3)
        tablePrincipal.totalWidth = 75f
        tablePrincipal.isLockedWidth = true

        // Elemento Phrase para la celda horizontal

        val phraseHorizontal = Phrase("$año años")
        // Añadir el elemento Phrase a una celda de la tabla principal
        val cellHorizontal = PdfPCell(phraseHorizontal)
        cellHorizontal.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellHorizontal.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellHorizontal.colspan = 3 // Establecer el ancho de la celda para que ocupe las tres columnas
        tablePrincipal.addCell(cellHorizontal)

        // Elementos Phrase para las celdas verticales
        val phraseVertical1 = Phrase("H",fuenteH)
        val phraseVertical2 = Phrase("M",fuenteM)
        val phraseVertical3 = Phrase("T")

        val cellVertical1 = PdfPCell(phraseVertical1)
        val cellVertical2 = PdfPCell(phraseVertical2)
        val cellVertical3 = PdfPCell(phraseVertical3)

        cellVertical1.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical2.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical3.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellVertical1.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical2.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellVertical3.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellVertical1)
        tablePrincipal.addCell(cellVertical2)
        tablePrincipal.addCell(cellVertical3)


        //contenido de la tabla
        var  h = 0
        var  m = 0
        var total = 0
        var totalM = 0
        var totalH = 0
        var totalT =0


        m = findValue(ingreso, "sexo", "edad", "0", año.toString())
        h = findValue(ingreso, "sexo", "edad", "1", año.toString())

        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tNuevoH += h
        tNuevoM += m

        var phraseContentM = Phrase(m.toString(),fuenteM)
        var phraseContentH = Phrase(h.toString(),fuenteH)
        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)
        var phraseContentT = Phrase(total.toString())
        var cellverticalM = PdfPCell(phraseContentM)
        var cellverticalH = PdfPCell(phraseContentH)
        var cellverticalT = PdfPCell(phraseContentT)
        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        m = findValue(repetidores, "sexo", "edad", "0", año.toString())
        h = findValue(repetidores, "sexo", "edad", "1", año.toString())
        total =  m + h
        totalM = m
        totalH = h
        totalT = total

        phraseContentM = Phrase(m.toString(),fuenteM)
        phraseContentH = Phrase(h.toString(),fuenteH)

        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)
        phraseContentT = Phrase(total.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE


        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        m = findValue(aprovados, "sexo", "edad", "0", año.toString())
        h = findValue(aprovados, "sexo", "edad", "1", año.toString())
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        phraseContentM = Phrase(m.toString(),fuenteM)
        phraseContentH = Phrase(h.toString(),fuenteH)

        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)
        phraseContentT = Phrase(total.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE


        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)

        m = findValue(reprovados, "sexo", "edad", "0", año.toString())
        h = findValue(reprovados, "sexo", "edad", "1", año.toString())
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        phraseContentM = Phrase(m.toString(),fuenteM)
        phraseContentH = Phrase(h.toString(),fuenteH)

        if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)
        phraseContentT = Phrase(total.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE


        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)



        //Totales

        phraseContentM = Phrase(totalM.toString(),fuenteM)
        phraseContentH = Phrase(totalH.toString(),fuenteH)

        /*if (m == 0) phraseContentM = Phrase("",fuenteM)
        if (h == 0) phraseContentH = Phrase("",fuenteH)*/
        phraseContentT = Phrase(totalT.toString())

        cellverticalM = PdfPCell(phraseContentM)
        cellverticalH = PdfPCell(phraseContentH)
        cellverticalT = PdfPCell(phraseContentT)

        cellverticalM.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalM.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalH.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalH.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        cellverticalT.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellverticalT.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        tablePrincipal.addCell(cellverticalH)
        tablePrincipal.addCell(cellverticalM)
        tablePrincipal.addCell(cellverticalT)


        val x = 30f + right  // coordenada X en puntos
        val y = 700f - up // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(tablePrincipal.totalWidth, tablePrincipal.totalHeight)
        tablePrincipal.writeSelectedRows(0, -1, 0f, tablePrincipal.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    fun deletePdf(nombre:String) {
        val ruta =  this.ruta+ "$nombre.pdf"
        val file = File(ruta)
        if (file.exists()) {
            file.delete()
        }

    }

    fun abrirdocumento(nombre:String,activity: Activity){
        val ruta =  this.ruta+ "$nombre.pdf"
        val file = File(ruta)
        val uri = FileProvider.getUriForFile(activity, "com.example.control_escolar.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun get_Day(fecha: String):Int {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd") // formato de la fecha en la cadena de texto
        val fecha = formatoFecha.parse(fecha) // convertir la cadena de texto en un objeto Date
        val calendar = Calendar.getInstance() // obtener una instancia de la clase Calendar
        calendar.time = fecha // establecer la fecha en el objeto Calendar
        val diaDelMes = calendar.get(Calendar.DAY_OF_MONTH) // obtener el día del mes como un número entero
        return diaDelMes
    }

    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, cell2:String, value1:String, value2: String):Int{
        val foundData = dataTable.find {it[cell1] == value1 && it[cell2] == value2}
        //Toast.makeText(context, "$cell1 = $value1 and $cell2 = $value2", Toast.LENGTH_LONG).show()
        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }
    }

    fun findStatusAsistenciaMes(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, cell2:String, value1:String, value2: String):Pair<Int, Int>{
        val foundData = dataTable.find {it[cell1] == value1 && it[cell2] == value2}
        //Toast.makeText(context, "$cell1 = $value1 and $cell2 = $value2", Toast.LENGTH_LONG).show()
        return if (foundData != null) {
            val entero = foundData["total"].toString().toInt()
            val texto = foundData["retardo"].toString().toInt()
            Pair(entero, texto)
        } else {
            Pair(0, 0)
        }
    }
    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, cell2:String, cell3: String,value1:String, value2: String, value3: String):Int{
        val foundData = dataTable.find {it[cell1] == value1 && it[cell2] == value2 && it[cell3] == value3}
        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }
    }

    fun findValuePartials(dataTable: MutableList<MutableMap<String, Any>>, cell1: String, cell2: String, cell3:String, value1: String, value2: String, value3: String): Pair<Int, Int> {
        val foundData = dataTable.find { it[cell1] == value1 && it[cell2] == value2 && it[cell3] == value3 }

        return if (foundData != null) {
            val intValue1 = foundData["total"].toString().toInt()
            val intValue2 = foundData["sumcalification"].toString().toInt()
            Pair(intValue1, intValue2)
        } else {
            Pair(0, 0)
        }
    }



    fun findValuePartials(dataTable: MutableList<MutableMap<String, Any>>, cell1: String, cell2: String, value1: String, value2: String):Int {
        val foundData = dataTable.find { it[cell1] == value1 && it[cell2] == value2 }

        return if (foundData != null) {
            val intValue1 = foundData["total"].toString().toInt()
            // Puedes calcular el segundo entero aquí si es necesario
            intValue1
        } else {
            0
        }
    }

    fun findValueActivity(dataTable: MutableList<MutableMap<String, Any>>, cell1: String, cell2: String, value1: String, value2: String):Int {
        val foundData = dataTable.find { it[cell1] == value1 && it[cell2] == value2 }
        return if (foundData != null) {
            val intValue1 = foundData["value"].toString().toInt()
            // Puedes calcular el segundo entero aquí si es necesario
            intValue1
        } else {
            0
        }
    }


    fun findValueAspects(dataTable: MutableList<MutableMap<String, Any>>, cell1: String,  value1: String): ArrayList<String> {
        var foundDataList = dataTable.filter { it[cell1] == value1}
        val name_aspects = ArrayList<String>()
        if (foundDataList.isNotEmpty()) {
            for (foundData in foundDataList) {
                name_aspects.add (foundData["name"].toString())// +" "+ foundData["value"].toString() +"%")

            }
        } else {
            name_aspects.add("")
        }

        return name_aspects
    }

    fun findValueAspects(dataTable: MutableList<MutableMap<String, Any>>): ArrayList<String> {
        val name_aspects = ArrayList<String>()
        if (dataTable.isNotEmpty()) {
            for (foundData in dataTable) {
                name_aspects.add (foundData["name"].toString())// +" "+ foundData["value"].toString() +"%")
            }
        } else {
            name_aspects.add("")
        }

        return name_aspects
    }

    fun quitarAcentos(texto: String): String {
        val textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
        val patron = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        return textoNormalizado.replace(patron, "")
    }

    fun roundDecimalFloat(number: Float, decimals: Int): Float {
        val factor = 10.0.pow(decimals).toFloat()
        return (number * factor).roundToInt() / factor
    }
    fun getNameMounth(mounth:Int):String{
        var name = ""
        val nombreDia = when (mounth) {
            1 -> name =  "Enero"
            2 -> name = "Febrero"
            3 -> name = "Marzo"
            4 -> name ="Abril"
            5 -> name ="Mayo"
            6 -> name ="Junio"
            7 -> name ="Julio"
            8 -> name ="Agosto"
            9 -> name ="Septiembre"
            10 -> name ="Octubre"
            11 -> name ="Noviembre"
            12 -> name ="Diciembre"

            else -> name ="Día inválido"
        }
        return  name
    }

    fun putPosition(up:Int, right: Int, writer: PdfWriter, table:PdfPTable){
        val x = 30f + right  // coordenada X en puntos
        val y = 700f - up // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
    }




}


