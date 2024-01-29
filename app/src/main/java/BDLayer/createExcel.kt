package BDLayer

import LogicLayer.Formats
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.control_escolar.BD_Escuelas
import com.example.control_escolar.Nombre_Escuela
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Font
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class createExcel(var context: Context) {

    private val ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/"
    private val rutaBD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/DB/"
    var Name_school = ""
    var Addres_school = ""
    var Tel_school = ""
    var Grado = ""
    var Grupo = ""
    var Turno = ""
    var Ciclo = ""
    var Colonia = ""
    var Name_Student = ""
    var Estado = ""
    var Name_teacher  =""
    var Cct = ""

    init {
        getDefault(context)
    }

    public fun getDefault(context: Context){
        try {
            val datas = schoolBD(context,Nombre_Escuela.getName())
            Name_teacher = datas.getNameTeacher()

            val data = datas.getDataSchool()
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
                datas.close()
            }

        }catch (Ex:Exception){
            //Toast.makeText(context, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    public fun createExcelFile() {
        try {
            val nameBD = Nombre_Escuela.getName()
            val excelFileName = "$nameBD.xls"
            val dir = File("$rutaBD")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File("$rutaBD", excelFileName)
            file.deleteOnExit()
            val fileOut = FileOutputStream(file)
            val workbook = HSSFWorkbook()

            val tablas = BD_Escuelas(context, Nombre_Escuela.getName())
            var cursor = tablas.getTable("Alumno")
            fillData("Alumno",cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Materia")
            fillData("Materia", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Tipo_Actividad")
            fillData("Tipo_Actividad", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Asistencia")
            fillData("Asistencia", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Tareas")
            fillData("Tareas", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Actividad_Especial")
            fillData("Actividad_Especial", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Tarea")
            fillData("Tarea", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("MateriaDia")
            fillData("MateriaDia", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Escuela")
            fillData("Escuela", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Parciales")
            fillData("Parciales", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Participacion")
            fillData("Participacion", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Reporte")
            fillData("Reporte", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Calificacionestemp")
            fillData("Calificacionestemp", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Justificacion")
            fillData("Justificacion", cursor, workbook)
            cursor.close()


            cursor = tablas.getTable("Configuracion")
            fillData("Configuracion", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Calificaciones")
            fillData("Calificaciones", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Calificaciones_Finales")
            fillData("Calificaciones_Finales", cursor, workbook)
            cursor.close()

            cursor = tablas.getTable("Pendientes")
            fillData("Pendientes", cursor, workbook)
            cursor.close()

            tablas.close()
            workbook.write(fileOut)
            fileOut.close()
            importBD(nameBD )




        } catch (ex: Exception) {
            Toast.makeText(context, "Error: ${ex.message}", Toast.LENGTH_LONG).show()
        }

    }

    fun readExcelFile(excelFileName:String, nameBD: String) {
        try {
            val file = File("$rutaBD", excelFileName)
            val fileInputStream = FileInputStream(file)
            val workbook = HSSFWorkbook(fileInputStream)

            val numberOfSheets = workbook.numberOfSheets
            //for (i in 0 until numberOfSheets) {
            for (i in 0 until numberOfSheets) {
                val sheet = workbook.getSheetAt(i)
                val sheetName = workbook.getSheetName(i)
                val bdName = excelFileName.toString().split('.')
                readSheet(sheet, sheetName, nameBD)
            }

            fileInputStream.close()
        } catch (e: Exception) {
            Toast.makeText(context, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun readExcelPath(path:String, nameBD:String) {
        try {
            val file = File(path)
            val fileInputStream = FileInputStream(file)
            val workbook = HSSFWorkbook(fileInputStream)
            val numberOfSheets = workbook.numberOfSheets
            //val pathBD = path.toString().split('/')
            //val nameBD = pathBD[pathBD.count() - 1].toString().split('.')
            //val nameBD = pathBD[pathBD.count() - 1].toString().split('.')

            for (i in 0 until numberOfSheets) {
                val sheet = workbook.getSheetAt(i)
                val sheetName = workbook.getSheetName(i)
                //Toast.makeText(context, sheetName, Toast.LENGTH_SHORT).show()
                readSheet(sheet, sheetName, nameBD)
            }

            fileInputStream.close()
        } catch (e: Exception) {
            Toast.makeText(context, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun readSheet(sheet: HSSFSheet, tableName: String, bdName:String) {
        val rows = sheet.iterator()
        val headerRow = rows.next() as HSSFRow
        val columnNames = mutableListOf<String>()//lista de nombre de columnas encontradas
        val db = BD_Escuelas(context, bdName) // Obtener tu instancia de base de datos

        // Obtener la primera fila que contiene los nombres de las columnas
        val columnBegin  = db.foundNameTable(tableName)
        // Iterar sobre las celdas de la fila de encabezado y obtener los nombres de las columnas
        for (i in columnBegin until headerRow.lastCellNum) {
            val cell = headerRow.getCell(i) as HSSFCell
            columnNames.add(cell.toString())
        }

        // Iterar sobre las filas de datos
       while (rows.hasNext()) {
           val listValues = mutableListOf<String>()
           val dataRow = rows.next() as HSSFRow


            // Iterar sobre las celdas de la fila de datos
            for (i in columnBegin until dataRow.lastCellNum) {
                var cellValue = dataRow.getCell(i).toString()
                //si el valor es no es nunerico lo captamos como string
                if (!Formats.isNumeric(cellValue)) cellValue = "'$cellValue'"
                listValues.add(cellValue)
            }
           // Crear la consulta de inserción con los nombres de las columnas
           val sQuery = "INSERT INTO $tableName (${columnNames.joinToString(", ")}) VALUES (${listValues.joinToString(", ")})"
           //limpiamos los valores encontradas
           listValues.clear()

           // Ejecutar la inserción
            db.insertTable(sQuery)

        }

    }

   private  fun fillData(nameSheet:String, cursor: Cursor, workbook:HSSFWorkbook){

        val sheet = workbook.createSheet(nameSheet)
        // Agregar los nombres de las columnas del cursor como la primera fila
        val headerRow: Row = sheet.createRow(0)
        for (i in 0 until cursor.columnCount) {
            val cell: Cell = headerRow.createCell(i)
            cell.setCellValue(cursor.getColumnName(i))
        }

        var rowIndex = 1 // Comenzar después de la fila de encabezado

        if (cursor.moveToFirst()) {
            do {
                val row: Row = sheet.createRow(rowIndex)
                for (i in 0 until cursor.columnCount) {
                    val cell: Cell = row.createCell(i)
                    cell.setCellValue(cursor.getString(i))
                }
                rowIndex++
            } while (cursor.moveToNext())
        }
    }


    private  fun fillDataDate(nameSheet:String, cursor: Cursor, workbook:HSSFWorkbook){

        val sheet = workbook.createSheet(nameSheet)
        // Agregar los nombres de las columnas del cursor como la primera fila
        val headerRow: Row = sheet.createRow(0)
        for (i in 0 until cursor.columnCount) {
            val cell: Cell = headerRow.createCell(i)
            cell.setCellValue(cursor.getColumnName(i))
        }

        var rowIndex = 1 // Comenzar después de la fila de encabezado

        if (cursor.moveToFirst()) {
            do {
                val row: Row = sheet.createRow(rowIndex)
                for (i in 0 until cursor.columnCount) {
                    val cell: Cell = row.createCell(i)
                    if (i == 3){ cell.setCellValue(getStatusAtendendences(cursor.getInt(i)))}
                    else {cell.setCellValue(cursor.getString(i))}
                }
                rowIndex++
            } while (cursor.moveToNext())
        }
    }

    private  fun fillCalifications(nameSheet:String, califications: Cursor, absence:Cursor, workbook:HSSFWorkbook){
        absence.moveToFirst()
        //val sheet = workbook.createSheet("${absence.getString(2)} $Grado - $Grupo")
        val sheet = workbook.createSheet("$nameSheet $Grado - $Grupo")
        // Agregar los nombres de las columnas del cursor como la primera fila
        val headerRow: Row = sheet.createRow(0)
        val cell0: Cell = headerRow.createCell(0)
        cell0.setCellValue("N_lista")
        val cell1: Cell = headerRow.createCell(1)
        cell1.setCellValue("ALUMNO")
        val cell2: Cell = headerRow.createCell(2)
        cell2.setCellValue("Calificación")
        val cell3: Cell = headerRow.createCell(3)
        cell3.setCellValue("Faltas")
        val cell4: Cell = headerRow.createCell(4)
        cell4.setCellValue("Participación")

        var rowIndex = 1 // Comenzar después de la fila de encabezado

        if (califications.moveToFirst()) {
            do {
                val row: Row = sheet.createRow(rowIndex)
                for (i in 0 until 3) {
                    val cell: Cell = row.createCell(i)
                    cell.setCellValue(califications.getString(i))
                }
                //insertamos las faltas del alumno
                val falta: Cell = row.createCell(3)
                val totalabsece = foundTotalAbsence(absence, califications.getInt(0))
                falta.setCellValue(totalabsece.toString())
                rowIndex++
            } while (califications.moveToNext())
        }
    }


   public fun getAssesstByDay(date:String){
       val excelFileName = "Asistencia $date.xls"

       val dir = File(ruta)
       if (!dir.exists()) {
           dir.mkdirs()
       }
       val file = File(ruta, excelFileName)
       val fileOut = FileOutputStream(file)
       val workbook = HSSFWorkbook()
       val asistencia = AsistenciaBD(context)
       val cursor = asistencia.getAttendanceByDay(date)

       //llenamos los datos del excel
       fillDataDate("Asistencia $date", cursor, workbook)
       cursor.close()
       asistencia.close()
       workbook.write(fileOut)
       fileOut.close()
       importCalifications(excelFileName)
   }


    public fun getActivitysByDay(date:String){
        val excelFileName = "Actividades $date.xls"

        val dir = File(ruta)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(ruta, excelFileName)
        val fileOut = FileOutputStream(file)
        val workbook = HSSFWorkbook()
        val asistencia = TareasBD(context)
        val cursor = asistencia.getCalificationsByDay(date)

        //llenamos los datos del excel
        fillData("Actividades $date", cursor, workbook)
        cursor.close()
        asistencia.close()
        workbook.write(fileOut)
        fileOut.close()
        importCalifications(excelFileName)
    }


    fun getActivitysByDay(nameSheet: String, actividades:Cursor, calificaciones:MutableList<MutableMap<String, Any>>, alumnos:Int){
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("$nameSheet $Grado - $Grupo")
        val headerRow: Row = sheet.createRow(0)
        val excelFileName = "Actividades $nameSheet.xls"
        var contadordias = 0
        val dir = File(ruta)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(ruta, excelFileName)
        val fileOut = FileOutputStream(file)
        var cont = 2



        //encabezado del la lista
        if (actividades.moveToFirst()){
            val cell0: Cell = headerRow.createCell(0)
            cell0.setCellValue("N_lista")
            val NombreCompleto: Cell = headerRow.createCell(1)
            NombreCompleto.setCellValue("Nombre Completo")
            //llenamos las culumnas del con las actividades del dia
            do {
                val cell: Cell = headerRow.createCell(cont)
                cell.setCellValue(actividades.getString(1).toString())
                cont++
            }while (actividades.moveToNext())
        }
        //inicialisamos las posiciones
        actividades.moveToFirst()
        cont = 1
        var contadorposicion = 0
        //asistencia por alumnos
        while (cont <= alumnos)
        {
            var y = 2//para movernos hacia la izquierda
            var Total = 0
            val headerRow: Row = sheet.createRow(cont)
            val n_lista = calificaciones[contadorposicion]!!["n_lista"].toString()
            val nombre = calificaciones[contadorposicion]!!["nombre"].toString()

            //insertamos los numeros de lista
            val lista: Cell = headerRow.createCell(0)
            lista.setCellValue(n_lista)
            //insertamos los nombres
            val listanombre: Cell = headerRow.createCell(1)
            listanombre.setCellValue(nombre)
            do{
                //obtenemos los datos buscados en la tabla de asistencia
                val calificacion = findValue(calificaciones, "c_actividades", "n_lista", actividades.getString(0), n_lista)
                var status = ""

                if (calificacion  <=100 && calificacion > 60){
                    status = "Actividad Entregada"
                }
                if (calificacion <= 60 && calificacion > 0) {
                    status = "Actividad Incompleta"

                }
                if (calificacion == 0 ) {
                    status = "Actividad no entregada"
                }

                val listaasitencia: Cell = headerRow.createCell(y)
                listaasitencia.setCellValue(calificacion.toString())
                /*val listaAsistencias: Cell = headerRow.createCell((y+1))
                listaAsistencias.setCellValue(totalAsistencia.toString())
                val listaFaltas: Cell = headerRow.createCell((y+2))
                listaFaltas.setCellValue(totalFaltas.toString())
                val listaRetardos: Cell = headerRow.createCell((y+3))
                listaRetardos.setCellValue(totalRetardos.toString())
                val listaJustificados: Cell = headerRow.createCell((y+4))
                listaJustificados.setCellValue(totalJustificados.toString())
                val listaSuspendidos: Cell = headerRow.createCell((y+5))
                listaSuspendidos.setCellValue(totalSuspendido.toString())
                val listaTotal: Cell = headerRow.createCell((y+6 ))
                listaTotal.setCellValue(Total.toString())*/
                y++
                contadorposicion ++
            }while (actividades.moveToNext())
            cont ++
            //contadorposicion = 0
            actividades.moveToFirst()
        }

        contadordias = 0
        contadorposicion = 2
        //cont++
        actividades.moveToFirst()



        actividades.close()
        //totalsexo.clear()
        //asistencia.clear()
        workbook.write(fileOut)
        fileOut.close()
        importCalifications(excelFileName)
    }



    @RequiresApi(Build.VERSION_CODES.N)
    fun printFaltasMes(nameSheet: String, dias:Cursor, asistencia:MutableList<MutableMap<String, Any>>,alumnos:Int, totalsexo: MutableList<MutableMap<String, Any>>){
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("$nameSheet $Grado - $Grupo")
        val headerRow: Row = sheet.createRow(0)
        val excelFileName = "Asistencia $nameSheet.xls"
        var contadordias = 0
        val dir = File(ruta)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(ruta, excelFileName)
        val fileOut = FileOutputStream(file)
        var cont = 2
        var contadorposicion = 0



        val fuenteMujer = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.MAGENTA)
    //encabezado del la lista
        if (dias.moveToFirst()){
            //val headerRow: Row = sheet.createRow(0)
            val cell0: Cell = headerRow.createCell(0)
            cell0.setCellValue("N_lista")
            val NombreCompleto: Cell = headerRow.createCell(1)
            NombreCompleto.setCellValue("Nombre Completo")
            //llenamos las culumnas del con los dias del mes
            do {
                val cell: Cell = headerRow.createCell(cont)
                cell.setCellValue(Formats.get_Day(dias.getString(2)).toString())
                cont++
            }while (dias.moveToNext())

            val totalAsistencia: Cell = headerRow.createCell(cont++)
            totalAsistencia.setCellValue("totalAsistencia")
            val totalFaltas: Cell = headerRow.createCell(cont++)
            totalFaltas.setCellValue("totalFaltas")
            val totalRetardos: Cell = headerRow.createCell(cont++)
            totalRetardos.setCellValue("totalRetardos")
            val totalJustificados: Cell = headerRow.createCell(cont++)
            totalJustificados.setCellValue("totalJustificados")
            val totalSuspendidos: Cell = headerRow.createCell(cont++)
            totalSuspendidos.setCellValue("totalSuspendidos")
            val totalTotal: Cell = headerRow.createCell(cont++)
            totalTotal.setCellValue("Total")
        }

        dias.moveToFirst()
        cont = 1

        //asistencia por alumnos
        while (cont <= alumnos)
        {
            var y = 2//para movernos hacia la izquierda
            var Total = 0
            var totalAsistencia = 0
            var totalFaltas = 0
            var totalJustificados = 0
            var totalSuspendido = 0
            var totalRetardos = 0
            val headerRow: Row = sheet.createRow(cont)
            val n_lista = asistencia[contadorposicion]!!["n_lista"].toString()
            val nombre = asistencia[contadorposicion]!!["nombre"].toString()


            //insertamos los numeros de lista
            val lista: Cell = headerRow.createCell(0)
            lista.setCellValue(n_lista)
            //insertamos los numeros el nombre
            val listanombre: Cell = headerRow.createCell(1)
            listanombre.setCellValue(nombre)
            do{
                //obtenemos los datos buscados en la tabla de asistencia
                val (lista2, lista) = findStatusAsistenciaMes(asistencia, "fecha", "n_lista", dias.getString(2), n_lista)
                var status = ""

                if (lista2 == 1){
                    Total++
                }
                if ( lista == 0 && lista2 == 0) {
                    status = "I"
                    totalFaltas ++
                }
                if ( lista == 0 && lista2 == 1) {
                    status = "O"
                    totalAsistencia ++
                }
                if ( lista == 1) {
                    status = "R"
                    totalRetardos++
                }
                if ( lista == 2) {
                    status = "J"
                    totalJustificados++
                }
                if ( lista == 3) {
                    status = "S"
                    totalSuspendido++
                }
                val listaasitencia: Cell = headerRow.createCell(y)
                listaasitencia.setCellValue(status)
                val listaAsistencias: Cell = headerRow.createCell((y+1))
                listaAsistencias.setCellValue(totalAsistencia.toString())
                val listaFaltas: Cell = headerRow.createCell((y+2))
                listaFaltas.setCellValue(totalFaltas.toString())
                val listaRetardos: Cell = headerRow.createCell((y+3))
                listaRetardos.setCellValue(totalRetardos.toString())
                val listaJustificados: Cell = headerRow.createCell((y+4))
                listaJustificados.setCellValue(totalJustificados.toString())
                val listaSuspendidos: Cell = headerRow.createCell((y+5))
                listaSuspendidos.setCellValue(totalSuspendido.toString())
                val listaTotal: Cell = headerRow.createCell((y+6 ))
                listaTotal.setCellValue(Total.toString())
                y++
                contadorposicion ++
            }while (dias.moveToNext())
            cont ++
            //contadorposicion = 0
            dias.moveToFirst()
        }

        contadordias = 0
        contadorposicion = 2
        //cont++
        dias.moveToFirst()

        val headerRowSexoMujer: Row = sheet.createRow(cont)
        val cellMujer: Cell = headerRowSexoMujer.createCell(0)

        val headerRowSexoHombre: Row = sheet.createRow(cont+1)
        val cellHombre: Cell = headerRowSexoHombre.createCell(0)

        cellMujer.setCellValue("M")
        cellHombre.setCellValue("H")
        //insertamos el total de sexo por dia
        while(contadordias < 2){
            do{
                if (contadordias == 0){
                    val cellTotalSexoMujer :Cell = headerRowSexoMujer.createCell(contadorposicion)
                    cellTotalSexoMujer.setCellValue(findValue(totalsexo,"fecha","sexo",dias.getString(2), contadordias.toString()).toString())
                }
                else{
                    val cellTotalSexoHombre :Cell = headerRowSexoHombre.createCell(contadorposicion)
                    cellTotalSexoHombre.setCellValue(findValue(totalsexo,"fecha","sexo",dias.getString(2), contadordias.toString()).toString())
                }
                //dias.moveToNext()
                contadorposicion++
            }while(dias.moveToNext())
            dias.moveToFirst()
            contadorposicion = 2
            cont++
            contadordias++
        }

        dias.close()
        totalsexo.clear()
        asistencia.clear()
        workbook.write(fileOut)
        fileOut.close()
        importCalifications(excelFileName)
    }


    fun insertStudents(workbook:HSSFWorkbook){
        val sheet = workbook.createSheet("Alumnos")
        val cursor = Nombre_Escuela.Alumnos

        // Agregar los nombres de las columnas del cursor como la primera fila
        val headerRow: Row = sheet.createRow(0)
        for (i in 0 until cursor.columnCount) {
            val cell: Cell = headerRow.createCell(i)
            cell.setCellValue(cursor.getColumnName(i))
        }

        var rowIndex = 1 // Comenzar después de la fila de encabezado

        if (cursor.moveToFirst()) {
            do {
                val row: Row = sheet.createRow(rowIndex)
                for (i in 0 until cursor.columnCount) {
                    val cell: Cell = row.createCell(i)
                    cell.setCellValue(cursor.getString(i))
                }
                rowIndex++
            } while (cursor.moveToNext())
        }

    }

    fun printPartial(matter:String, partial:Int, c_matter:Int){
        val excelFileName = "Parcial $partial.xls"

        val dir = File(ruta)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(ruta, excelFileName)
        val fileOut = FileOutputStream(file)
        val workbook = HSSFWorkbook()
        val assets = ASSESS(context)
        assets.getDatesByParcial(partial)
        val califications = assets.getCalificationByMatter(partial, c_matter)
        val absence = assets.getAbsenceByMatter(c_matter)
        //llenamos los datos del excel
        fillCalifications("$matter $partial", califications, absence, workbook)

        califications.close()
        assets.close()
        workbook.write(fileOut)
        fileOut.close()
        importCalifications(excelFileName)
    }
    fun printAllPartial(partial:Int){
        val excelFileName = "Parciales.xls"

        val dir = File(ruta)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(ruta, excelFileName)
        val fileOut = FileOutputStream(file)
        val workbook = HSSFWorkbook()
        val assets = ASSESS(context)
        //obtenemos las fechas correspondientes al parcial seleccionado
        assets.getDatesByParcial(partial)
        //obtenemos todas las materias
        val allmatter = assets.getAllMatters()
        if (allmatter.moveToFirst()) {
            do {
                val c_matter = allmatter.getInt(0)
                val namematter = allmatter.getString(1)
                val califications = assets.getCalificationByMatter(partial, c_matter)
                val absence = assets.getAbsenceByMatter(c_matter)
                //llenamos los datos del excel
                fillCalifications("$namematter $partial", califications, absence, workbook)
                califications.close()
            }while (allmatter.moveToNext())
        }

        allmatter.close()
        assets.close()
        workbook.write(fileOut)
        fileOut.close()
        importCalifications(excelFileName)
    }

    fun printGrafica() {
        val filePath = ruta+"FINALES.xls"

        try {
            val file = FileInputStream(File(filePath))
            val workbook = HSSFWorkbook(file)
            val sheet = workbook.getSheetAt(1) // Obtén la hoja que deseas modificar

            // Agrega datos en una celda específica
            val row = sheet.createRow(5) // Ejemplo: segunda fila
            val cell = row.createCell(8) // Ejemplo: primera columna
            cell.setCellValue("Nuevo dato")

            // Guarda el archivo Excel modificado
            val outputStream = FileOutputStream(filePath)
            workbook.write(outputStream)
            outputStream.close()
            file.close()

            println("Datos agregados correctamente.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


   private fun foundTotalAbsence(cursor:Cursor, folio:Int):Int{
       var total = 0
       if (cursor.moveToFirst()) {
           do {

               if (cursor.getInt(0) == folio){
                   total = cursor.getInt(1)
                   break
               }

           } while (cursor.moveToNext())
       }
        return total
   }




    fun abrirDocumentoXLS(nombre: String, activity: Activity) {
        val ruta = this.ruta + "$nombre.xls" // Cambia la extensión a .xls
        val file = File(ruta)
        val uri = FileProvider.getUriForFile(activity, "com.example.control_escolar.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.ms-excel") // Usa el tipo MIME para archivos XLS
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity.startActivity(intent)
    }


   private fun importBD(nameBD:String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/vnd.ms-excel" // Cambiar al tipo de archivo Excel adecuado
        val file = File("$rutaBD$nameBD.xls")
        val uri = FileProvider.getUriForFile(context, "com.example.control_escolar.fileprovider", file)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(shareIntent, "Compartir archivo Excel")) // Cambiar el título si es necesario
    }

    private fun importCalifications(name:String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/vnd.ms-excel" // Cambiar al tipo de archivo Excel adecuado
        val file = File(ruta + name)
        val uri = FileProvider.getUriForFile(context, "com.example.control_escolar.fileprovider", file)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(Intent.createChooser(shareIntent, "Compartir archivo Excel")) // Cambiar el título si es necesario
    }

    fun getStatusAtendendences(type:Int):String{
        var status = "asistencia"
        if (type  ==  1) status = "Retardo"
        if (type  ==  2) status = "Justificado"
        if (type  ==  3) status = "Suspedido"
        return  status
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

    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, cell2:String, value1:String, value2: String):Int{
        val foundData = dataTable.find {it[cell1] == value1 && it[cell2] == value2}
        //Toast.makeText(context, "$cell1 = $value1 and $cell2 = $value2", Toast.LENGTH_LONG).show()
        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }
    }


}