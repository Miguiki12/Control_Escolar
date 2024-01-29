package DocumentsPDF

import BDLayer.AlumnosBD
import BDLayer.schoolBD
import LogicLayer.Formats
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.os.Environment
import android.widget.Toast
import com.example.control_escolar.Nombre_Escuela
import com.itextpdf.awt.geom.AffineTransform
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import java.io.FileOutputStream
import java.text.SimpleDateFormat

class createMatriculaPdf(var context: Context) {

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
    var tSinH = 0
    var tSinM = 0
    var tBajasM = 0
    var tBajasH = 0
    var tAltasM = 0
    var tAltasH = 0


    private val ruta =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .toString() + "/Imprimibles/"//Environment.DIRECTORY_DOCUMENTS+"/Imprimibles/" //"/storage/emulated/0/Documents/Imprimibles/Reporte_Asistencia.pdf"

    init {
        getDefault(context)
    }




    public fun getDefault(context: Context){
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

    fun addTable(color:BaseColor, up: Float, right: Float, large: Float, bottom: Float,title:String, content: String, writer: PdfWriter) {
        val table = PdfPTable(1)
        table.totalWidth = large * 72 / 1.54f

        // Crea una nueva celda para la primera fila (fondo gris y texto blanco)
        val cell1 = PdfPCell(Phrase(title, Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL, BaseColor.WHITE)))
        cell1.backgroundColor =  color//BaseColor(158, 158, 158)
        cell1.borderColor = color//BaseColor(191, 191, 191)
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

        putPosition(up,right,writer,table)


    }

    fun showStudentsSituation(color:BaseColor,cursor: Cursor,  beginDate:String, endDate:String, up:Float, right:Float, writer: PdfWriter):Float {

        val fontBold = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD, BaseColor.WHITE)
        val fontRegular = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL, BaseColor.WHITE) // Nuevo tamaño de fuente
        val fontSmall = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL, BaseColor.WHITE)
        val fontSmallM = Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, BaseColor(Color.MAGENTA))
        val fontSmallH = Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))
        val table = PdfPTable(10)
        val columnWidths = floatArrayOf(17f, 100f, 70f, 70f, 90f, 50f, 20f, 20f, 80f, 60f)//,35f,35f)
        table.setTotalWidth(columnWidths)
        table.isLockedWidth = true

        var phrase = Phrase("N_lista", fontSmall)
        var cellLista = PdfPCell(phrase)

        // Añadir celda combinada para el nombre
        val nombreCell = PdfPCell(Phrase("NOMBRE", fontBold))

        val apellidopCell = PdfPCell(Phrase("APELLIDOP", fontBold))
        val apellidomCell = PdfPCell(Phrase("APELLIDOM", fontBold))


        val curpCell = PdfPCell(Phrase("CURP", fontBold))

        phrase = Phrase("SEXO", fontSmall)
        val cellSexo = PdfPCell(phrase)
        cellSexo.rotation = 90
        var N_lista = 0
        phrase = Phrase("EDAD-E", fontSmall)
        val cellEdad = PdfPCell(phrase)
        cellEdad.rotation = 90
        phrase = Phrase("F_REGISTRO", fontSmall)
        val cellRegistro = PdfPCell(phrase)

        phrase = Phrase("SITUACION", fontBold)
        val cellSituacion = PdfPCell(phrase)

        phrase = Phrase("F_BAJA", fontSmall)
        val cellBaja = PdfPCell(phrase)




        cellLista.backgroundColor = color
        nombreCell.backgroundColor = color
        apellidopCell.backgroundColor = color
        apellidomCell.backgroundColor = color
        curpCell.backgroundColor = color
        cellSexo.backgroundColor = color
        cellEdad.backgroundColor = color
        cellRegistro.backgroundColor = color
        cellSituacion.backgroundColor = color
        cellBaja.backgroundColor = color





        table.addCell(cellLista)
        table.addCell(nombreCell)
        table.addCell(apellidopCell)
        table.addCell(apellidomCell)
        table.addCell(curpCell)
        table.addCell(cellRegistro)
        table.addCell(cellSexo)
        table.addCell(cellEdad)
        table.addCell(cellSituacion)
        table.addCell(cellBaja)
        try {
            var sexo = "M"
            var font = fontSmallM
            if (cursor.moveToFirst()) {
                do {

                    if (cursor.getInt(6) == 0) {
                        sexo = "M"
                        font = fontSmallM
                    }
                    if (cursor.getInt(6) == 1) {
                        sexo = "H"
                        font = fontSmallH
                    }
                    N_lista ++


                    val cellN_lista = PdfPCell(Phrase(N_lista.toString(), font))

                    val cellNombre = PdfPCell(Phrase(cursor.getString(1), font))

                    val cellApellidop = PdfPCell(Phrase(cursor.getString(2), font))

                    val cellApellidom = PdfPCell(Phrase(cursor.getString(3), font))


                    val cellCurp = PdfPCell(Phrase(cursor.getString(4), font))
                    val cellRegistro = PdfPCell(Phrase(cursor.getString(5), font))
                    val cellSexo = PdfPCell(Phrase(cursor.getString(6), font))
                    val cellEdad = PdfPCell(Phrase(cursor.getString(7), font))

                    var cellSituacion : PdfPCell//(Phrase(cursor.getString(8), font))
                    var cellBaja = PdfPCell(Phrase("", font))


                    if (cursor.getString(8) == "ALTA"){
                        cellSituacion = PdfPCell((Phrase(cursor.getString(8), font)))
                        cellRegistro.backgroundColor = BaseColor.YELLOW
                        cellSituacion.backgroundColor = BaseColor.YELLOW
                        //table.addCell(cellSituacion)
                    }else cellSituacion = PdfPCell(Phrase(cursor.getString(8), font))





                    val result = compareDates(cursor.getString(9), beginDate)
                    when {
                        result > 0 -> {
                            if (!cursor.getString(9).isNullOrEmpty()){
                                cellBaja = PdfPCell(Phrase("BAJA : "+cursor.getString(9), font))
                                cellBaja.backgroundColor = BaseColor.BLACK
                                //table.addCell(cellBaja)
                            }else cellBaja = PdfPCell(Phrase(cursor.getString(9), font))
                            // La fecha objetivo es anterior a la fecha actual
                        }
                        result < 0 -> {
                           // table.addCell(" ")
                            // La fecha objetivo es posterior a la fecha actual
                        }
                        else -> {
                            cellBaja =  PdfPCell(Phrase(" ", font))
                            // Las fechas son iguales

                        }
                    }


                    table.addCell(cellN_lista)
                    table.addCell(cellNombre)
                    table.addCell(cellApellidop)
                    table.addCell(cellApellidom)
                    table.addCell(cellCurp)
                    table.addCell(cellRegistro)
                    table.addCell(cellSexo)
                    table.addCell(cellEdad)
                    table.addCell(cellSituacion)
                    table.addCell(cellBaja)

                    //table.isExtendLastRow = true


                } while (cursor.moveToNext())
            }
        } catch (Ex: Exception) {
            Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_LONG).show()
        }
        putPosition(up,right,writer, table)
        table.calculateHeights()
        return table.totalHeight
    }

    fun showStudentsSituation2(color:BaseColor,cursor: Cursor,  beginDate:String, endDate:String, up:Float, right:Float, writer: PdfWriter):Float {

        val fontBold = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD, BaseColor.WHITE)
        val fontRegular = Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, BaseColor.WHITE) // Nuevo tamaño de fuente
        val fontSmall = Font(Font.FontFamily.HELVETICA, 6f, Font.NORMAL, BaseColor.WHITE)
        val fontSmallM = Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, BaseColor(Color.MAGENTA))
        val fontSmallH = Font(Font.FontFamily.HELVETICA, 7f, Font.NORMAL, BaseColor(Color.parseColor("#1A8AF9")))
        val table = PdfPTable(10)
        val columnWidths = floatArrayOf(17f, 100f, 70f, 70f, 90f, 50f, 20f, 20f, 45f, 65f)//,35f,35f)
        table.setTotalWidth(columnWidths)
        table.isLockedWidth = true
        var phrase = Phrase("N_lista", fontSmall)
        var cellLista = PdfPCell(phrase)
        cellLista.rotation = 90

        // Añadir celda combinada para el nombre
        val nombreCell = PdfPCell(Phrase("NOMBRE", fontBold))

        val apellidopCell = PdfPCell(Phrase("APELLIDOP", fontBold))
        val apellidomCell = PdfPCell(Phrase("APELLIDOM", fontBold))

        val curpCell = PdfPCell(Phrase("CURP", fontBold))

        phrase = Phrase("SEXO", fontSmall)
        val cellSexo = PdfPCell(phrase)
        cellSexo.rotation = 90
        var N_lista = 0
        phrase = Phrase("EDAD", fontSmall)
        val cellEdad = PdfPCell(phrase)
        cellEdad.rotation = 90
        phrase = Phrase("F_REGISTRO", fontSmall)
        val cellRegistro = PdfPCell(phrase)

        phrase = Phrase("SITUACIÓN", fontRegular)
        val cellSituacion = PdfPCell(phrase)

        phrase = Phrase("F_BAJA", fontSmall)
        val cellBaja = PdfPCell(phrase)




        cellLista.backgroundColor = color
        nombreCell.backgroundColor = color
        apellidopCell.backgroundColor = color
        apellidomCell.backgroundColor = color
        curpCell.backgroundColor = color
        cellSexo.backgroundColor = color
        cellEdad.backgroundColor = color
        cellRegistro.backgroundColor = color
        cellSituacion.backgroundColor = color
        cellBaja.backgroundColor = color


        table.addCell(cellLista)
        table.addCell(nombreCell)
        table.addCell(apellidopCell)
        table.addCell(apellidomCell)
        table.addCell(curpCell)
        table.addCell(cellRegistro)
        table.addCell(cellSexo)
        table.addCell(cellEdad)
        table.addCell(cellSituacion)
        table.addCell(cellBaja)

        try {
            var sexo = "M"
            var font = fontSmallM


            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(6) == 0) {
                        sexo = "M"
                        font = fontSmallM
                    }
                    if (cursor.getInt(6) == 1) {
                        sexo = "H"
                        font = fontSmallH
                    }

                    N_lista ++
                    val cellN_lista = PdfPCell(Phrase(N_lista.toString(), font))
                    val cellNombre = PdfPCell(Phrase(cursor.getString(1), font))
                    val cellApellidop = PdfPCell(Phrase(cursor.getString(2), font))
                    val cellApellidom = PdfPCell(Phrase(cursor.getString(3), font))
                    val cellCurp = PdfPCell(Phrase(cursor.getString(4), font))
                    val cellRegistro = PdfPCell(Phrase(cursor.getString(5), font))
                    val cellSexo = PdfPCell(Phrase(cursor.getString(6), font))
                    val cellEdad = PdfPCell(Phrase(cursor.getString(7), font))
                    var cellSituacion = PdfPCell(Phrase("IN", font))
                    var cellBaja = PdfPCell(Phrase(cursor.getString(9), font))



                    val resultAlta = compareDates(cursor.getString(5), beginDate)
                    when {
                        resultAlta > 0 -> {

                        }
                        resultAlta < 0 -> {
                            if (cursor.getString(5).isNotEmpty()){
                                if (cursor.getString(8) == "ALTA") {
                                    cellSituacion = PdfPCell(Phrase("ALTA", font))
                                    cellSituacion.backgroundColor = BaseColor.YELLOW
                                    cellRegistro.backgroundColor = BaseColor.YELLOW
                                }
                            }//else table.addCell(Phrase(cursor.getString(5), font))
                            // La fecha objetivo es posterior a la fecha actual
                        }
                        else -> {
                            //table.addCell(" ")
                            // Las fechas son iguales

                        }
                    }



                    val result = compareDates(cursor.getString(9), endDate)
                    when {
                        result > 0 -> {
                            if (cursor.getString(9).isNotEmpty()){
                                cellBaja = PdfPCell(Phrase("BAJA :" + cursor.getString(9), font))
                                cellBaja.backgroundColor = BaseColor.BLACK
                            }//else table.addCell(Phrase(cursor.getString(9), font))
                            // La fecha objetivo es anterior a la fecha actual
                        }
                        result < 0 -> {
                            cellBaja = PdfPCell(Phrase("", font))
                            //table.addCell(" ")
                            // La fecha objetivo es posterior a la fecha actual
                        }
                        else -> {

                            //table.addCell(" ")
                            // Las fechas son iguales
                        }
                    }


                    table.addCell(cellN_lista)
                    table.addCell(cellNombre)
                    table.addCell(cellApellidop)
                    table.addCell(cellApellidom)
                    table.addCell(cellCurp)
                    table.addCell(cellRegistro)
                    table.addCell(cellSexo)
                    table.addCell(cellEdad)
                    table.addCell(cellSituacion)// Establecer el color de fondo de la celda (puedes usar valores RGB)
                    table.addCell(cellBaja)

                } while (cursor.moveToNext())
            }
        } catch (Ex: Exception) {
            Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_LONG).show()
        }
        putPosition(up,right,writer, table)
        table.calculateHeights()
        return table.totalHeight
    }

    fun Estadistica(date1:String,date2:String,date3:String,date4:String, date5:String){
        getDefault(context)
        val alumnos = AlumnosBD(context)
        var normal1 = alumnos.getEstadisticBySexo1(date1)
        var bajas = alumnos.getEstadisticUnsuscribe(date1)
        val document = Document(PageSize.LETTER)
        val writer = PdfWriter.getInstance(document, FileOutputStream(ruta + "Estadistica.pdf"))
        val inicio = normal1[0]["edad"].toString().toInt()
        val fin = normal1[normal1.size-1]["edad"].toString().toInt()
        val numColumnas = ((fin + 1) - inicio)
        val Table = PdfPTable(numColumnas)
        Table.totalWidth = (70 * numColumnas).toFloat()//tamaño de las columnas
        Table.isLockedWidth = true
        document.open()


        var right = 0



        //****Estadiscica 1 inicial
        //añadimos las estasticas iniciales con repetidores
        var listaEstadistica = AlumnosBD(context).getListStudentStadistic1(date1)
        showStudentsSituation(BaseColor(Color.parseColor("#587966")),listaEstadistica, date1, date2,250.0f,35.0f, writer)

        val cb = writer.directContent
        cb.beginText()
        cb.setFontAndSize(BaseFont.createFont(),14f) // Establecer la fuente y el tamaño del texto
        cb.setTextMatrix(10f, 700f) // Establecer la posición de inicio del texto
        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "GRADO  ${Grado.uppercase()} GRUPO ${Grupo.uppercase()} DOCENTE ${Name_teacher.uppercase()} FECHA INICIO CILCO -- $date1", 50f, 750f, 0f)
        cb.endText()


        var situaciones =  tableSitutions()
        putPosition(50.0f,35.0f,  writer,situaciones)

        addTable(BaseColor(Color.parseColor("#587966")),178.0f,35.0f,9.1f,0f,"ESTADÍSTICA INICIAL - (AGOSTO, SEPTIEMBRE)","", writer)
        for(i in inicio .. fin){
            addTableEstadisct1(normal1, bajas,50.0f,160.0f + right,writer,i)
            right += 75//ancho de la tabla
        }
        //creamos tabla totales
        val totalsRepit = addTableTotales()//tabla para los totales
        fillTableStadisticRepit(totalsRepit)//llenamos los totales de la estadistica de repetidores
        putPosition(50.0f,160.0f+right,  writer,totalsRepit)


        right = 0



        document.newPage()

        //****Estadistica 2
        normal1 = alumnos.getEstadisticBySexo2(date1, date2)
        bajas = alumnos.getEstadisticUnsuscribe2(date1,date2)
        var altas = alumnos.getEstadisticRegister(date1, date2)
        val yearStar = Formats.obtenerRangoDeAnios().split('-')
        val dateStart = "${yearStar[0]}-08-01"
        //Toast.makeText(context, "DATE1 $date1 DATE2 $date2", Toast.LENGTH_LONG).show()
        listaEstadistica = AlumnosBD(context).getListStudentStadistic2(date1,date2, dateStart)
        showStudentsSituation2(BaseColor(Color.parseColor("#B5435B")),listaEstadistica, date1, date2,250.0f,35.0f, writer)

        var cb2 = writer.directContent
        cb2.beginText()
        cb2.setFontAndSize(BaseFont.createFont(),12f) // Establecer la fuente y el tamaño del texto
        cb2.setTextMatrix(10f, 700f) // Establecer la posición de inicio del texto
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT, "GRADO  ${Grado.uppercase()} GRUPO ${Grupo.uppercase()} DOCENTE ${Name_teacher.uppercase()} FECHA $date1 - $date2", 50f, 750f, 0f)

        cb2.endText()


        situaciones =  tableSitutions2(1)
        putPosition(50.0f,35.0f,  writer,situaciones)


        addTable(BaseColor(Color.parseColor("#B5435B")), 160.0f,35.0f,9.1f,0f,"ESTADÍSTICA SEMESTRAL - (SEPTIEMBRE A DICIEMBRE)","", writer)
        //añadimos las estadisticas de altas y bajas
        //96.0
        for(i in inicio .. fin){
            addTableEstadisct2(normal1, altas, bajas,50.0f ,135.0f + right,writer,i)
            right += 75
        }
        //creamos tabla totales
        var totalsRegisters = addTableTotales()//tabla para los totales
        fillTableStadisticRegister(totalsRegisters) //llenamos los totales de basjas-altas
        putPosition(50.0f,135.0f+right,writer,totalsRegisters)
        right = 0

        document.newPage()


        //***Estadistica 3
        normal1 = alumnos.getEstadisticBySexo2(date2, date3)
        bajas = alumnos.getEstadisticUnsuscribe2(date2,date3)
        altas = alumnos.getEstadisticRegister(date2, date3)
        listaEstadistica = AlumnosBD(context).getListStudentStadistic2(date2,date3, dateStart)
        showStudentsSituation2(BaseColor(Color.parseColor("#7B497D")),listaEstadistica, date2, date3,250.0f,35.0f, writer)

        cb2 = writer.directContent
        cb2.beginText()
        cb2.setFontAndSize(BaseFont.createFont(),12f) // Establecer la fuente y el tamaño del texto
        cb2.setTextMatrix(10f, 700f) // Establecer la posición de inicio del texto
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT, "GRADO  ${Grado.uppercase()} GRUPO ${Grupo.uppercase()} DOCENTE ${Name_teacher.uppercase()} FECHA $date2 - $date3", 50f, 750f, 0f)

        cb2.endText()

        situaciones =  tableSitutions2(2)
        putPosition(50.0f,35.0f,  writer,situaciones)

        addTable(BaseColor(Color.parseColor("#7B497D")),160.0f,35.0f,9.1f,0f,"ESTADÍSTICA SEMESTRAL - (ENERO, FEBRERO, MARZO)","", writer)
        //añadimos las estadisticas de altas y bajas
        //96.0
        for(i in inicio .. fin){
            addTableEstadisct2(normal1, altas, bajas,50.0f ,135.0f + right,writer,i)
            right += 75
        }
        //creamos tabla totales
        totalsRegisters = addTableTotales()//tabla para los totales
        fillTableStadisticRegister(totalsRegisters) //llenamos los totales de basjas-altas
        putPosition(50.0f,135.0f+right,writer,totalsRegisters)
        right = 0

        document.newPage()


        //***Estadistica 4
        normal1 = alumnos.getEstadisticBySexo2(date3, date4)
        bajas = alumnos.getEstadisticUnsuscribe2(date3,date4)
        altas = alumnos.getEstadisticRegister(date3, date4)
        listaEstadistica = AlumnosBD(context).getListStudentStadistic2(date3,date4, dateStart)
        showStudentsSituation2(BaseColor(Color.parseColor("#4F444A")),listaEstadistica, date3, date4, 250.0f,35.0f, writer)

        cb2 = writer.directContent
        cb2.beginText()
        cb2.setFontAndSize(BaseFont.createFont(),12f) // Establecer la fuente y el tamaño del texto
        cb2.setTextMatrix(10f, 700f) // Establecer la posición de inicio del texto
        cb2.showTextAligned(PdfContentByte.ALIGN_LEFT, "GRADO  ${Grado.uppercase()} GRUPO ${Grupo.uppercase()} DOCENTE ${Name_teacher.uppercase()} FECHA $date3 - $date4", 50f, 750f, 0f)
        cb2.endText()

        situaciones =  tableSitutions2(3)
        putPosition(50.0f,35.0f,  writer,situaciones)
        addTable(BaseColor(Color.parseColor("#4F444A")),160.0f,35.0f,9.0f,0f,"ESTADÍSTICA FINAL - (ABRIL, MAYO, JUNIO)","", writer)


        //111
        for(i in inicio .. fin){
            //addTableEstadisct2(finales, existencia,aprovados,reprovados, 50f,130.0f + right,writer,i)
            addTableEstadisct2(normal1, altas, bajas,50.0f ,135.0f + right,writer,i)
            right += 75
        }

        totalsRegisters = addTableTotales()//tabla para los totales
        fillTableStadisticRegister(totalsRegisters) //llenamos los totales de basjas-altas
        putPosition(50.0f,135.0f+right,writer,totalsRegisters)
        right = 0

        document.newPage()

        //****FINAL
        val finales = alumnos.getEstadisticBySexo2(date4, date5)
        val existencia = alumnos.getEstadisticExistence(date4, date5)
        val aprovados = alumnos.getEstadisticApproved(60)
        val reprovados = alumnos.getEstadisticFailed(60)

        listaEstadistica = AlumnosBD(context).getListStudentStadistic2(date4,date5, date1)
        showStudentsSituation2(BaseColor(Color.parseColor("#4F444A")),listaEstadistica, date4, date5, 250.0f,120.0f, writer)


        situaciones =  tableSitutionsFinal(0)
        putPosition(50.0f,35.0f,  writer,situaciones)

        val cb3 = writer.directContent
        cb3.beginText()
        cb3.setFontAndSize(BaseFont.createFont(),12f) // Establecer la fuente y el tamaño del texto
        cb3.setTextMatrix(10f, 700f) // Establecer la posición de inicio del texto
        cb3.showTextAligned(PdfContentByte.ALIGN_LEFT, "GRADO  ${Grado.uppercase()} GRUPO ${Grupo.uppercase()} DOCENTE ${Name_teacher.uppercase()} FECHA $date4 - $date5", 50f, 750f, 0f)

        cb3.endText()

        addTable(BaseColor(Color.parseColor("#4F444A")),160.0f,235.0f,9.0f,0f,"ESTADÍSTICA FINAL - (Junio y Julio)","", writer)


        for(i in inicio .. fin){
            addTableEstadisct3(finales, existencia,aprovados,reprovados, 50f,235.0f + right,writer,i)
            //addTableEstadisct2(normal1, altas, bajas,50.0f ,130.0f + right,writer,i)
            right += 75
        }

        totalsRegisters = addTableTotales()//tabla para los totales
        fillTableStadisticRegister(totalsRegisters) //llenamos los totales de basjas-altas
        putPosition(50.0f,235.0f+right,writer,totalsRegisters)
        right = 0


        document.add(Table)

        //document.newPage()

        document.close()
        normal1.clear()
        //repetidores.clear()

        altas.clear()
        bajas.clear()
        //finales.clear()
        //aprovados.clear()
        //reprovados.clear()
        alumnos.close()
        listaEstadistica.close()
    }

    fun tableSitutions():PdfPTable{
        val fontBold = Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL)
        // Crear una tabla principal con una fila y tres columnas
        val table = PdfPTable(1)
        table.totalWidth = 125f
        table.isLockedWidth = true

        // Elemento Phrase para la celda horizontal

        val phraseHorizontal = Phrase("SITUACIÓN")
        // Añadir el elemento Phrase a una celda de la tabla principal
        val cellHorizontal = PdfPCell(phraseHorizontal)
        cellHorizontal.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellHorizontal.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        table.addCell(cellHorizontal)

        val cellRepetidores = PdfPCell(Phrase("REPETIDORES",fontBold))
        val cellNI = PdfPCell(Phrase("NUEVO INGRESO",fontBold))
        //val cellSC = PdfPCell(Phrase("SIN COMUNICACION",fontBold))
        val cellAltas = PdfPCell(Phrase("ALTAS",fontBold))
        val cellSuman = PdfPCell(Phrase("SUMAN",fontBold))
        val cellBajas = PdfPCell(Phrase("BAJAS",fontBold))
        val cellTotal = PdfPCell(Phrase("TOTAL",fontBold))

        table.addCell(Phrase(" ",fontBold))
        table.addCell(cellRepetidores)
        table.addCell(cellNI)
        //table.addCell(cellSC)
        table.addCell(cellAltas)
        table.addCell(cellSuman)
        table.addCell(cellBajas)
        table.addCell(cellTotal)

        return  table

    }

    fun tableSitutions2(numEstadistic:Byte):PdfPTable{
        val fontBold = Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL)
        // Crear una tabla principal con una fila y tres columnas
        val table = PdfPTable(1)
        table.totalWidth = 100f
        table.isLockedWidth = true

        // Elemento Phrase para la celda horizontal

        val phraseHorizontal = Phrase("SITUACIÓN")
        // Añadir el elemento Phrase a una celda de la tabla principal
        val cellHorizontal = PdfPCell(phraseHorizontal)
        cellHorizontal.horizontalAlignment = PdfPCell.ALIGN_CENTER
        cellHorizontal.verticalAlignment = PdfPCell.ALIGN_MIDDLE
        table.addCell(cellHorizontal)



        val cellEstadistica = PdfPCell(Phrase("ESTADISTICA$numEstadistic",fontBold))
        val cellAltas = PdfPCell(Phrase("ALTAS", fontBold))
        val cellSuman = PdfPCell(Phrase("SUMAN",fontBold))
        val cellBajas = PdfPCell(Phrase("BAJAS",fontBold))
        val cellTotal = PdfPCell(Phrase("TOTAL",fontBold))

        table.addCell(Phrase(" ",fontBold))
        table.addCell(cellEstadistica)
        table.addCell(cellAltas)
        table.addCell(cellSuman)
        table.addCell(cellBajas)
        table.addCell(cellTotal)

        return  table


    }


    fun tableSitutionsFinal(numEstadistic:Byte):PdfPTable{
        val fontBold = Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL)
        // Crear una tabla principal con una fila y tres columnas
        val table = PdfPTable(1)
        table.totalWidth = 200f
        table.isLockedWidth = true

        // Elemento Phrase para la celda horizontal

        val phraseHorizontal = Phrase("APROVADO")
        // Añadir el elemento Phrase a una celda de la tabla principal
        val cellHorizontal = PdfPCell(phraseHorizontal)
        table.addCell(cellHorizontal)




        val cellAprovadpC = PdfPCell(Phrase("APROVADO CON CONDICIONES", fontBold))
        val cellSuman = PdfPCell(Phrase("SUMAN",fontBold))
        val cellRevocacion = PdfPCell(Phrase("REVOCACION DE GRADO",fontBold))
        val cellReprobado = PdfPCell(Phrase("REPROBADO",fontBold))
        val cellSuman2 = PdfPCell(Phrase("SUMAN",fontBold))
        val cellTotal = PdfPCell(Phrase("TOTAL",fontBold))

        table.addCell(Phrase(" ",fontBold))
        //table.addCell(cellEstadistica)
        table.addCell(cellAprovadpC)
        table.addCell(cellSuman)
        table.addCell(cellRevocacion)
        table.addCell(cellReprobado)
        table.addCell(cellSuman2)
        table.addCell(cellTotal)

        return  table


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

    fun fillTableStadisticRepit(table:PdfPTable){
        table.addCell(addCell(tRepetidoresH.toString(),1))
        table.addCell(addCell(tRepetidoresM.toString(),0))
        table.addCell(addCell((tRepetidoresH + tRepetidoresM).toString(),2))
        table.addCell(addCell(tNuevoH.toString(),1))
        table.addCell(addCell(tNuevoM.toString(),0))
        table.addCell(addCell((tNuevoH + tNuevoM).toString(),2))
        //table.addCell(addCell(tSinH.toString(),1))
        //table.addCell(addCell(tSinM.toString(),0))
        //table.addCell(addCell((tSinH + tSinM).toString(),2))
        table.addCell(addCell(tAltasH.toString(),1))
        table.addCell(addCell(tAltasM.toString(),0))
        table.addCell(addCell((tAltasH + tAltasM).toString(),2))


        val sumanH = tRepetidoresH + tNuevoH + tAltasH //+tSinH
        val sumanM = tRepetidoresM + tNuevoM + tAltasM //+tSinM
        val sumanT = sumanH + sumanM
        table.addCell(addCell((sumanH).toString(),1))
        table.addCell(addCell((sumanM).toString(),0))
        table.addCell(addCell((sumanT).toString(),2))


        table.addCell(addCell(tBajasH.toString(),1))
        table.addCell(addCell(tBajasM.toString(),0))
        table.addCell(addCell((tBajasH + tBajasM).toString(),2))


        val totalH = sumanH - tBajasH
        val totalM = sumanM - tBajasM
        val totalT = totalH + totalM
        table.addCell(addCell((totalH).toString(),1))
        table.addCell(addCell((totalM).toString(),0))
        table.addCell(addCell((totalT).toString(),2))

        clearTotals()
    }
    fun fillTableStadisticRegister(table:PdfPTable){
        table.addCell(addCell(tNuevoH.toString(),1))
        table.addCell(addCell(tNuevoM.toString(),0))
        table.addCell(addCell((tNuevoH + tNuevoM).toString(),2))
        table.addCell(addCell(tAltasH.toString(),1))
        table.addCell(addCell(tAltasM.toString(),0))
        table.addCell(addCell((tAltasH + tAltasM).toString(),2))
        var totalH = tAltasH + tNuevoH
        var totalM = tAltasM + tNuevoM
        var totalT = totalH + totalM
        table.addCell(addCell((totalH).toString(),1))
        table.addCell(addCell((totalM).toString(),0))
        table.addCell(addCell((totalT).toString(),2))

        table.addCell(addCell(tBajasH.toString(),1))
        table.addCell(addCell(tBajasM.toString(),0))
        table.addCell(addCell((tBajasH + tBajasM).toString(),2))
        totalH -= tBajasH
        totalM -= tBajasM
        totalT = totalH + totalM
        table.addCell(addCell((totalH).toString(),1))
        table.addCell(addCell((totalM).toString(),0))
        table.addCell(addCell((totalT).toString(),2))
        clearTotals()
    }

    fun fillTableConcept(){
        val tabla = PdfPTable(1) // 3 columnas
        tabla.widthPercentage = 100f // Ancho de la tabla al 100%

// Añade los títulos a la tabla

        val columnaConceptos = PdfPCell(Phrase("Conceptos"))


// Alinea los títulos al centro

        columnaConceptos.horizontalAlignment = Element.ALIGN_CENTER


// Agrega las celdas a la tabla
        tabla.addCell("REPETIDORES")
        tabla.addCell("NUEVO INGRESO")
        tabla.addCell("ALTAS")

// Añade más filas y datos según tus necesidades
// ...

// Cierra el documento

    }

    fun addTableEstadisct1(ingreso:MutableList<MutableMap<String, Any>>, bajas:MutableList<MutableMap<String, Any>>, up:Float, right: Float,writer: PdfWriter, año:Int) {

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


        m = findValue(ingreso, "sexo", "edad", "situacion","0", año.toString(), "REPETIDOR")
        h = findValue(ingreso, "sexo", "edad", "situacion","1", año.toString(), "REPETIDOR")

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


        //nuevo ingreso
        m = findValue(ingreso, "sexo", "edad", "situacion", "0", año.toString(), "NUEVO INGRESO")
        h = findValue(ingreso, "sexo", "edad", "situacion", "1", año.toString(), "NUEVO INGRESO")
        //total =  m + h
        //totalM += m
        //totalH += h
        //totalT += total


        //tNuevoM += m
        //tNuevoH += h


        /*phraseContentM = Phrase(m.toString(),fuenteM)
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
        tablePrincipal.addCell(cellverticalT)*/

        m += findValue(ingreso, "sexo", "edad", "situacion", "0", año.toString(), "SIN COMUNICACIÓN")
        h += findValue(ingreso, "sexo", "edad", "situacion", "1", año.toString(), "SIN COMUNICACIÓN")


        total = m + h
        totalM += m
        totalH += h
        totalT += total


        tNuevoH += h
        tNuevoM += m

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

        //
        /*phraseContentM = Phrase(m.toString(),fuenteM)
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
        tablePrincipal.addCell(cellverticalT)*/

        //altas
        m = findValue(ingreso, "sexo", "edad","situacion", "0", año.toString(), "ALTA")
        h = findValue(ingreso, "sexo", "edad", "situacion","1", año.toString(), "ALTA")

        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tAltasM += m
        tAltasH += h

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

        //SUMAN
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


        //BAJAS
        m = findValue(bajas, "sexo", "edad", "0", año.toString())
        h = findValue(bajas, "sexo", "edad","1", año.toString())

        total =  m + h
        totalM -= m
        totalH -= h
        totalT -= total

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


        //TOTAL
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


        putPosition(up,right, writer,tablePrincipal)


    }

    fun addTableEstadisct2(estadistica2:MutableList<MutableMap<String, Any>>, altas:MutableList<MutableMap<String, Any>>, bajas:MutableList<MutableMap<String, Any>>, up:Float, right: Float,writer: PdfWriter, año:Int) {

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


        //normales

        m = findValuesEstadistic(estadistica2, "sexo", "edad", "0", año.toString())
        h = findValuesEstadistic(estadistica2, "sexo", "edad", "1", año.toString())
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tNuevoM += m
        tNuevoH += h

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



        //altas
        m = findValue(altas, "sexo", "edad", "situacion","0", año.toString(), "ALTA")
        h = findValue(altas, "sexo", "edad", "situacion","1", año.toString(), "ALTA")
        total =  m + h
        totalM += m
        totalH += h
        totalT += total

        tAltasM += m
        tAltasH += h

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


        //suman

        phraseContentM = Phrase(totalM.toString(),fuenteM)
        phraseContentH = Phrase(totalH.toString(),fuenteH)

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


        //bajas

        m = findValue(bajas, "sexo", "edad", "0", año.toString())
        h = findValue(bajas, "sexo", "edad", "1", año.toString())
        total =  m + h
        totalM -= m
        totalH -= h
        totalT -= total

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


        putPosition(up,right,writer,tablePrincipal)


    }

    fun addTableEstadisct3(ingreso:MutableList<MutableMap<String, Any>>, repetidores:MutableList<MutableMap<String, Any>>, aprovados:MutableList<MutableMap<String, Any>>,
                           reprovados:MutableList<MutableMap<String, Any>>, up:Float, right: Float,writer: PdfWriter, año:Int) {

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


        putPosition(up,right,writer,tablePrincipal)
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

    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, cell2:String, cell3: String,value1:String, value2: String, value3: String):Int{
        val foundData = dataTable.find {it[cell1] == value1 && it[cell2] == value2 && it[cell3] == value3}
        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }
    }

    fun findValuesEstadistic(dataTable: MutableList<MutableMap<String, Any>>, cell1: String, cell2:String,  value1: String, value2:String ): Int {
        var foundDataList = dataTable.filter { it[cell1] == value1 && it[cell2] == value2}
        var suma = 0
        if (foundDataList.isNotEmpty()) {
            for (foundData in foundDataList) {
                suma ++
                //Toast.makeText(context, "$cell1 = $value1, $cell2 = $value2, $cell3 = $value3, $totalporciento" , Toast.LENGTH_SHORT).show()
            }
        } else {
            suma = 0
        }
        return suma
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
        tSinM = 0
        tSinH = 0
        tNuevoH = 0
        tNuevoM = 0
        tBajasH = 0
        tBajasM = 0
        tAltasH = 0
        tAltasM = 0
    }

    fun putPosition(up:Float, right: Float, writer: PdfWriter, table:PdfPTable){
        val x = right.toFloat()  // coordenada X en puntos
        val y = up.toFloat() // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()
        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)
        contentByte.restoreState()
    }


    fun putPositionRotation(up: Float, right: Float, angle: Float, writer: PdfWriter, table: PdfPTable) {
        val x = right.toFloat()  // coordenada X en puntos
        val y = up.toFloat() // coordenada Y en puntos
        val contentByte = writer.directContent
        contentByte.saveState()

        // Rotar alrededor del punto de coordenadas (x, y) con el ángulo especificado
        contentByte.transform(AffineTransform.getRotateInstance(Math.toRadians(angle.toDouble()), x.toDouble(), y.toDouble()))

        val template = contentByte.createTemplate(table.totalWidth, table.totalHeight)
        table.writeSelectedRows(0, -1, 0f, table.totalHeight, template)
        contentByte.addTemplate(template, x, y)

        contentByte.restoreState()
    }

    fun compareDates(targetDate: String, dateActivity: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        try {
            val targetDateAsDate = sdf.parse(targetDate)
            val dateActivityAsDate = sdf.parse(dateActivity)

            if (dateActivityAsDate != null && targetDateAsDate != null) {
                // Comparar targetDate con dateActivity
                return dateActivityAsDate.compareTo(targetDateAsDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejo de errores, por ejemplo, si el formato de la fecha es incorrecto
        }
        return 0
    }


    fun eliminarSaltosDeLinea(texto: String): String {
        return texto.replace("\n", "")
    }
}
