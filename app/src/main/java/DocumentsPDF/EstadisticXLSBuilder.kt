package DocumentsPDF

import BDLayer.AlumnosBD
import LogicLayer.Formats
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Environment
import androidx.core.content.FileProvider
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import java.io.File
import java.io.FileOutputStream

class EstadisticXLSBuilder(val context: Context) {

    private val ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/"
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


    public fun createBookEstadistic(date1: String, date2: String, date3: String, date4: String){
        val excelFileName = "Estadistica.xls"

        val dir = File(ruta)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(ruta, excelFileName)
        val fileOut = FileOutputStream(file)
        val workbook = HSSFWorkbook()

        exportEstadisticFormer(date1, workbook)

        exportEstadisticBegin(date1, workbook)
        clearTotals()

        exportEstadisticNextOne(date1,date2, workbook, 2)
        clearTotals()

        exportEstadisticNextOne(date2, date3, workbook, 3)
        clearTotals()

        exportEstadisticNextOne(date3, date4, workbook, 4)
        clearTotals()


        workbook.write(fileOut)
        fileOut.close()
        importEstadistica(excelFileName)
    }

    fun exportEstadisticFormer(date1: String, workbook: HSSFWorkbook) {

        val sheet = workbook.createSheet("CICLO ANTERIOR")

        val alumnos = AlumnosBD(context)
        var normal1 = alumnos.getEstadisticBySexo1(date1)
        var bajas = alumnos.getEstadisticUnsuscribe(date1)

        val inicio = normal1[0]["edad"].toString().toInt()
        val fin = normal1[normal1.size - 1]["edad"].toString().toInt()

        var listaEstadistica = AlumnosBD(context).getListStudentStadisticFormer()

        //llenamos los datos de los alumnos del excel
        val rowIndex = fillDatesStudent("CICLO ANTERIOR", listaEstadistica, sheet)
        //creamos los parametros de la tabla estadistica
        val labels = listOf(" ","REPETIDORES","NUEVO INGRESO", "ALTAS", "SUMAN", "BAJAS", "TOTAL")

        createTableSituation(labels, sheet, rowIndex + 6)


        //creamos los valores de las edades y los sexos
        createHeaderTableStadistic(sheet, rowIndex + 5, inicio, fin)

        //llenamos los datos de la estadistica
        val positionColumn = fillDateStadistic(normal1, bajas, sheet, rowIndex + 5, inicio, fin)

        //llenamos los totales
        fillTotalStadistic(sheet, rowIndex + 5, positionColumn)
        //llenamos los datos de la entidad_federativa
        createHeaderRowEntidad_Federativa(sheet)

        fillDataEntidad_Federativa(sheet)

        //cerramos todos los requerimientos solicitados
        listaEstadistica.close()

    }

    fun exportEstadisticBegin(date1: String, workbook: HSSFWorkbook) {

        val sheet = workbook.createSheet("ESTADISTICA INICIAL")

        val alumnos = AlumnosBD(context)
        var normal1 = alumnos.getEstadisticBySexo1(date1)
        var bajas = alumnos.getEstadisticUnsuscribe(date1)

        val inicio = normal1[0]["edad"].toString().toInt()
        val fin = normal1[normal1.size - 1]["edad"].toString().toInt()

        var listaEstadistica = AlumnosBD(context).getListStudentStadistic1(date1)

        //llenamos los datos de los alumnos del excel
        val rowIndex = fillDatesStudent("Estadistica Inicial", listaEstadistica, sheet)
        //creamos los parametros de la tabla estadistica
        val labels = listOf(" ","REPETIDORES","NUEVO INGRESO", "ALTAS", "SUMAN", "BAJAS", "TOTAL")

        createTableSituation(labels, sheet, rowIndex + 6)


        //creamos los valores de las edades y los sexos
        createHeaderTableStadistic(sheet, rowIndex + 5, inicio, fin)

        //llenamos los datos de la estadistica
        val positionColumn = fillDateStadistic(normal1, bajas, sheet, rowIndex + 5, inicio, fin)

        //llenamos los totales
        fillTotalStadistic(sheet, rowIndex + 5, positionColumn)
        //llenamos los datos de la entidad_federativa
        createHeaderRowEntidad_Federativa(sheet)

        fillDataEntidad_Federativa(sheet)

        //cerramos todos los requerimientos solicitados
        listaEstadistica.close()

    }


    fun exportEstadisticNextOne(date1: String, date2: String, workbook: HSSFWorkbook, numberStadistic:Int) {


        val sheet = workbook.createSheet("$date1 -- $date2")

        val alumnos = AlumnosBD(context)

        val normal = alumnos.getEstadisticBySexo2(date1, date2)
        val bajas = alumnos.getEstadisticUnsuscribe2(date1, date2)
        var altas = alumnos.getEstadisticRegister(date1, date2)
        val yearStar = Formats.obtenerRangoDeAnios().split('-')
        val dateStart = "${yearStar[0]}-08-01"
        val listaEstadistica = alumnos.getListStudentStadistic2(date1, date2, dateStart)

        val inicio = normal[0]["edad"].toString().toInt()
        val fin = normal[normal.size - 1]["edad"].toString().toInt()

        //llenamos los datos de los alumnos del excel
        val rowIndex = fillDatesStudent2(date1, listaEstadistica, sheet)
        //creamos los parametros de la tabla estadistica

        val labels = listOf(" ", "ESTADISTICA$numberStadistic", "ALTAS", "SUMAN", "BAJAS", "TOTAL")
        createTableSituation(labels, sheet, rowIndex + 6)
        //creamos los valores de las edades y los sexos
        createHeaderTableStadistic(sheet, rowIndex + 5, inicio, fin)
        //llenamos los datos de la estadistica
        val positionColumn = fillDateStadistic2(normal, altas,bajas, sheet, rowIndex + 5, inicio, fin)

        //fillTotalStadistic(sheet, rowIndex + 5, positionColumn)

        //llenamos los totales
        fillTotalStadistic2(sheet, rowIndex + 5, positionColumn)


        //cerramos todos los requerimientos solicitados
        listaEstadistica.close()

    }


        private fun fillDatesStudent(beginDate:String, dateEstadistic: Cursor, sheet: HSSFSheet ): Int {

            // Agregar los nombres de las columnas del cursor como la primera fila

            createHeaderRow(sheet)

            var rowIndex = 1 // Comenzar después de la fila de encabezado


            if (dateEstadistic.moveToFirst()) {
                do {
                    val row: Row = sheet.createRow(rowIndex)
                    for (i in 0 until 11) {
                        val cell: Cell = row.createCell(i)
                        when (i) {
                            0 -> cell.setCellValue(rowIndex.toString())
                            6 -> cell.setCellValue(if (dateEstadistic.getString(i) == "1") "H" else "M")
                            /*8 ->  {//SI YA PASO LA FECHA DE ALTA QUE NO LA MUESTRE
                                if(Formats.compareDates(dateEstadistic.getString(5), beginDate) > 0 && dateEstadistic.getString(8) == "ALTA"){
                                    cell.setCellValue("NUEVO INGRESO")
                                }
                                else cell.setCellValue(dateEstadistic.getString(i))
                            }*/
                            else -> cell.setCellValue(dateEstadistic.getString(i))
                        }
                    }


                    rowIndex++
                } while (dateEstadistic.moveToNext())
            }

            return rowIndex
        }


    private fun fillDatesStudent2(beginDate:String, dateEstadistic: Cursor, sheet: HSSFSheet ): Int {

        // Agregar los nombres de las columnas del cursor como la primera fila

        createHeaderRow(sheet)

        var rowIndex = 1 // Comenzar después de la fila de encabezado


        if (dateEstadistic.moveToFirst()) {
            do {
                val row: Row = sheet.createRow(rowIndex)
                for (i in 0 until 11) {
                    val cell: Cell = row.createCell(i)
                    when (i) {
                        0 -> cell.setCellValue(rowIndex.toString())
                        6 -> cell.setCellValue(if (dateEstadistic.getString(i) == "1") "H" else "M")
                        8 ->  {//SI YA PASO LA FECHA DE ALTA QUE NO LA MUESTRE
                            if(Formats.compareDates(dateEstadistic.getString(5), beginDate) < 0 && dateEstadistic.getString(8) == "ALTA"){
                                cell.setCellValue("ALTA")
                            }
                            else cell.setCellValue("IN")
                        }
                        else -> cell.setCellValue(dateEstadistic.getString(i))
                    }
                }


                rowIndex++
            } while (dateEstadistic.moveToNext())
        }

        return rowIndex
    }


        private fun createHeaderRow(sheet: HSSFSheet) {
            // Crear el encabezado
            val headerRow = sheet.createRow(0)

            // Nombres de las columnas
            val columnNames = listOf("N_LISTA","NOMBRE","APELLIDO PATERNO","APELLIDO MATERNO","CURP","F_REGISTRO","SEXO","EDAD","SITUACIÓN","F_BAJA", "ENTIDAD_FEDERATIVA")

            // Crear celdas en el encabezado con los nombres de las columnas
            for ((index, columnName) in columnNames.withIndex()) {
                val cell: Cell = headerRow.createCell(index)
                cell.setCellValue(columnName)
            }
        }

    private fun createHeaderRowEntidad_Federativa(sheet: HSSFSheet) {
        // Crear el encabezado
        val headerRow: Row = sheet.getRow(0)

        // Nombres de las columnas
        val columnNames = listOf("TOTAL","SEXO", "EDAD","ENTIDAD_FEDERATIVA")
        var  numberColumn = 15
        // Crear celdas en el encabezado con los nombres de las columnas
        for (index in 0 until  columnNames.count()) {
            val cell: Cell = headerRow.createCell(numberColumn)
            cell.setCellValue(columnNames[index])
            numberColumn ++
        }
    }

    private fun fillDataEntidad_Federativa(sheet: HSSFSheet){

        val cursor = AlumnosBD(context).getEstadisticEntidad_Federativa()
        if (cursor.moveToFirst()) {


            for (index in 0 until cursor.count) {
                val headerRow: Row = sheet.getRow(index + 1)
                var cell: Cell = headerRow.createCell(15)
                cell.setCellValue(cursor.getString(0))
                cell = headerRow.createCell(16)
                cell.setCellValue(cursor.getString(1))
                cell = headerRow.createCell(17)
                cell.setCellValue(cursor.getString(2))
                cell = headerRow.createCell(18)
                cell.setCellValue(cursor.getString(3))
                cursor.moveToNext()
            }
        }

    }


    private fun createTableSituation(labels:List<String>, sheet: HSSFSheet, rowIndex: Int) {
        // Crear la fila ESTADISTICA
        var headerRow: Row = sheet.createRow(rowIndex)
        val cell0: Cell = headerRow.createCell(0)
        cell0.setCellValue("ESTADISTICA")

        // Crear las filas REPETIDORES, NUEVO INGRESO, ALTAS, SUMAN, BAJAS, TOTAL
        //val labels = listOf(" ","REPETIDORES","NUEVO INGRESO", "ALTAS", "SUMAN", "BAJAS", "TOTAL")
        for ((index, label) in labels.withIndex()) {
            headerRow = sheet.createRow(rowIndex + index + 1)
            val cell: Cell = headerRow.createCell(0)
            cell.setCellValue(label)
        }
    }


    fun createHeaderTableStadistic(sheet: HSSFSheet, rowIndex: Int, inicio: Int, fin: Int) {

            val existingRow: Row = sheet.getRow(rowIndex + 1)
            var numberColumn = 2
            //llenamos las edades
            for (i in inicio..fin) {
                val cell0: Cell = existingRow.createCell(numberColumn)
                cell0.setCellValue("AÑOS $i")
                numberColumn += 3
            }

            val cell0: Cell = existingRow.createCell(numberColumn)
            cell0.setCellValue("TOTALES")


            val existingRow2: Row = sheet.getRow(rowIndex + 2) //
            numberColumn = 2
            //llenamos los encabezados de sexo
            for (i in inicio..fin) {
                val cell1: Cell = existingRow2.createCell(numberColumn)
                cell1.setCellValue("NIÑO")

                val cell2: Cell = existingRow2.createCell(numberColumn + 1)
                cell2.setCellValue("NIÑA")

                val cell3: Cell = existingRow2.createCell(numberColumn + 2)
                cell3.setCellValue("TOTAL")
                numberColumn += 3
            }
            //el total de totales

            val cell1: Cell = existingRow2.createCell(numberColumn)
            cell1.setCellValue("NIÑO")

            val cell2: Cell = existingRow2.createCell(numberColumn + 1)
            cell2.setCellValue("NIÑA")

            val cell3: Cell = existingRow2.createCell(numberColumn + 2)
            cell3.setCellValue("TOTAL")

        }

        fun fillDateStadistic(ingreso: MutableList<MutableMap<String, Any>>, bajas: MutableList<MutableMap<String, Any>>, sheet: HSSFSheet,rowIndex: Int, inicio: Int, fin: Int): Int {

            var numberColumn = 2

            for (i in inicio..fin) {

                var h = 0
                var m = 0
                var total = 0
                var totalM = 0
                var totalH = 0
                var totalT = 0

                m = findValue(ingreso, "sexo", "edad", "situacion", "0", i.toString(), "REPETIDOR")
                h = findValue(ingreso, "sexo", "edad", "situacion", "1", i.toString(), "REPETIDOR")

                var existingRow: Row = sheet.getRow(rowIndex + 3)
                var cell: Cell = existingRow.createCell(numberColumn)
                var cell2: Cell = existingRow.createCell(numberColumn + 1)
                var cell3: Cell = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$h")
                cell2.setCellValue("$m")


                total = m + h
                totalM += m
                totalH += h
                totalT += total

                tRepetidoresM += m
                tRepetidoresH += h


                cell3.setCellValue("$total")

                m = findValue(ingreso,"sexo","edad","situacion","0",i.toString(),"NUEVO INGRESO")
                h = findValue(ingreso,"sexo","edad","situacion","1",i.toString(),"NUEVO INGRESO")

                existingRow = sheet.getRow(rowIndex + 4)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$h")
                cell2.setCellValue("$m")


                total = m + h
                totalM += m
                totalH += h
                totalT += total


                tNuevoH += h
                tNuevoM += m



                cell3.setCellValue("$total")

                m = findValue(ingreso, "sexo", "edad", "situacion", "0", i.toString(), "ALTA")
                h = findValue(ingreso, "sexo", "edad", "situacion", "1", i.toString(), "ALTA")

                existingRow = sheet.getRow(rowIndex + 5)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$h")
                cell2.setCellValue("$m")


                total = m + h
                totalM += m
                totalH += h
                totalT += total

                tAltasM += m
                tAltasH += h


                cell3.setCellValue("$total")

                //total sumas
                existingRow = sheet.getRow(rowIndex + 6)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$totalH")
                cell2.setCellValue("$totalM")


                cell3.setCellValue("$totalT")


                //BAJAS
                m = findValue(bajas, "sexo", "edad", "0", i.toString())
                h = findValue(bajas, "sexo", "edad", "1", i.toString())

                existingRow = sheet.getRow(rowIndex + 7)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$h")
                cell2.setCellValue("$m")

                total = m + h
                totalM -= m
                totalH -= h
                totalT -= total

                tBajasM += m
                tBajasH += h

                cell3.setCellValue("$total")

                //total por edad
                existingRow = sheet.getRow(rowIndex + 8)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)
                cell.setCellValue("$totalH")
                cell2.setCellValue("$totalM")
                cell3.setCellValue("$totalT")

                numberColumn += 3


            }
            return numberColumn
        }

        fun fillDateStadistic2(estadistica2: MutableList<MutableMap<String, Any>>, altas: MutableList<MutableMap<String, Any>>,bajas: MutableList<MutableMap<String, Any>>,sheet: HSSFSheet,rowIndex: Int,  inicio: Int, fin: Int):Int {


            var numberColumn = 2

            for (i in inicio .. fin) {


                //contenido de la tabla
                var h = 0
                var m = 0
                var total = 0
                var totalM = 0
                var totalH = 0
                var totalT = 0


                //normales

                m = findValuesEstadistic(estadistica2, "sexo", "edad", "0", i.toString())
                h = findValuesEstadistic(estadistica2, "sexo", "edad", "1", i.toString())

                var existingRow: Row = sheet.getRow(rowIndex + 3)
                var cell: Cell = existingRow.createCell(numberColumn)
                var cell2: Cell = existingRow.createCell(numberColumn + 1)
                var cell3: Cell = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$h")
                cell2.setCellValue("$m")

                total = m + h
                totalM += m
                totalH += h
                totalT += total

                tNuevoM += m
                tNuevoH += h

                cell3.setCellValue("$total")


                //altas
                m = findValue(altas, "sexo", "edad", "situacion", "0", i.toString(), "ALTA")
                h = findValue(altas, "sexo", "edad", "situacion", "1", i.toString(), "ALTA")

                existingRow = sheet.getRow(rowIndex + 4)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$h")
                cell2.setCellValue("$m")


                total = m + h
                totalM += m
                totalH += h
                totalT += total

                tAltasM += m
                tAltasH += h


                cell3.setCellValue("$total")



                //suman
                existingRow = sheet.getRow(rowIndex + 5)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$totalH")
                cell2.setCellValue("$totalM")
                cell3.setCellValue("$totalT")





                //BAJAS
                m = findValue(bajas, "sexo", "edad", "0", i.toString())
                h = findValue(bajas, "sexo", "edad", "1", i.toString())

                existingRow = sheet.getRow(rowIndex + 6)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)

                cell.setCellValue("$h")
                cell2.setCellValue("$m")

                total = m + h
                totalM -= m
                totalH -= h
                totalT -= total

                tBajasM += m
                tBajasH += h

                cell3.setCellValue("$total")

                //total por edad
                existingRow = sheet.getRow(rowIndex + 7)
                cell = existingRow.createCell(numberColumn)
                cell2 = existingRow.createCell(numberColumn + 1)
                cell3 = existingRow.createCell(numberColumn + 2)
                cell.setCellValue("$totalH")
                cell2.setCellValue("$totalM")
                cell3.setCellValue("$totalT")



                numberColumn += 3
            }

            return numberColumn

        }


        fun fillTotalStadistic(sheet: HSSFSheet, rowIndex: Int, numberColumn: Int) {


            var existingRow: Row = sheet.getRow(rowIndex + 3)
            var cell: Cell = existingRow.createCell(numberColumn)
            var cell2: Cell = existingRow.createCell(numberColumn + 1)
            var cell3: Cell = existingRow.createCell(numberColumn + 2)

            cell.setCellValue("$tRepetidoresH")
            cell2.setCellValue("$tRepetidoresM")

            var total = tRepetidoresH + tRepetidoresM
            cell3.setCellValue("$total")


            existingRow = sheet.getRow(rowIndex + 4)
            cell = existingRow.createCell(numberColumn)
            cell2 = existingRow.createCell(numberColumn + 1)
            cell3 = existingRow.createCell(numberColumn + 2)

            cell.setCellValue("$tNuevoH")
            cell2.setCellValue("$tNuevoM")

            total = tNuevoH + tNuevoM
            cell3.setCellValue("$total")


            existingRow = sheet.getRow(rowIndex + 5)
            cell = existingRow.createCell(numberColumn)
            cell2 = existingRow.createCell(numberColumn + 1)
            cell3 = existingRow.createCell(numberColumn + 2)

            cell.setCellValue("$tAltasH")
            cell2.setCellValue("$tAltasM")

            total = tAltasH + tAltasM
            cell3.setCellValue("$total")


            existingRow = sheet.getRow(rowIndex + 6)
            cell = existingRow.createCell(numberColumn)
            cell2 = existingRow.createCell(numberColumn + 1)
            cell3 = existingRow.createCell(numberColumn + 2)

            val sumaH = tRepetidoresH + tNuevoH + tAltasH
            val sumaM = tRepetidoresM + tNuevoM + tAltasM

            cell.setCellValue("$sumaH")
            cell2.setCellValue("$sumaM")

            val totalSuma = sumaH + sumaM
            cell3.setCellValue("$totalSuma")

            existingRow = sheet.getRow(rowIndex + 7)
            cell = existingRow.createCell(numberColumn)
            cell2 = existingRow.createCell(numberColumn + 1)
            cell3 = existingRow.createCell(numberColumn + 2)

            cell.setCellValue("$tBajasH")
            cell2.setCellValue("$tBajasM")

            total = tBajasH + tBajasM
            cell3.setCellValue("$total")


            existingRow = sheet.getRow(rowIndex + 8)
            cell = existingRow.createCell(numberColumn)
            cell2 = existingRow.createCell(numberColumn + 1)
            cell3 = existingRow.createCell(numberColumn + 2)

            val totalH = sumaH - tBajasH
            val totalM = sumaM - tBajasM
            val totalT = totalH + totalM

            cell.setCellValue("$sumaH")
            cell2.setCellValue("$sumaM")
            cell3.setCellValue("$totalT")


        }


    fun fillTotalStadistic2(sheet: HSSFSheet, rowIndex: Int, numberColumn: Int) {


        var existingRow: Row = sheet.getRow(rowIndex + 3)
        var cell: Cell = existingRow.createCell(numberColumn)
        var cell2: Cell = existingRow.createCell(numberColumn + 1)
        var cell3: Cell = existingRow.createCell(numberColumn + 2)


        cell.setCellValue("$tNuevoH")
        cell2.setCellValue("$tNuevoM")

        var total = tNuevoH + tNuevoM
        cell3.setCellValue("$total")


        existingRow = sheet.getRow(rowIndex + 4)
        cell = existingRow.createCell(numberColumn)
        cell2 = existingRow.createCell(numberColumn + 1)
        cell3 = existingRow.createCell(numberColumn + 2)

        cell.setCellValue("$tAltasH")
        cell2.setCellValue("$tAltasM")

        total = tAltasH + tAltasM
        cell3.setCellValue("$total")


        existingRow = sheet.getRow(rowIndex + 5)
        cell = existingRow.createCell(numberColumn)
        cell2 = existingRow.createCell(numberColumn + 1)
        cell3 = existingRow.createCell(numberColumn + 2)

        val sumaH = tRepetidoresH + tNuevoH + tAltasH
        val sumaM = tRepetidoresM + tNuevoM + tAltasM

        cell.setCellValue("$sumaH")
        cell2.setCellValue("$sumaM")

        val totalSuma = sumaH + sumaM
        cell3.setCellValue("$totalSuma")

        existingRow = sheet.getRow(rowIndex + 6)
        cell = existingRow.createCell(numberColumn)
        cell2 = existingRow.createCell(numberColumn + 1)
        cell3 = existingRow.createCell(numberColumn + 2)

        cell.setCellValue("$tBajasH")
        cell2.setCellValue("$tBajasM")

        total = tBajasH + tBajasM
        cell3.setCellValue("$total")


        existingRow = sheet.getRow(rowIndex + 7)
        cell = existingRow.createCell(numberColumn)
        cell2 = existingRow.createCell(numberColumn + 1)
        cell3 = existingRow.createCell(numberColumn + 2)

        val totalH = sumaH - tBajasH
        val totalM = sumaM - tBajasM
        val totalT = totalH + totalM

        cell.setCellValue("$sumaH")
        cell2.setCellValue("$sumaM")
        cell3.setCellValue("$totalT")


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





        fun importEstadistica(name: String) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/vnd.ms-excel" // Cambiar al tipo de archivo Excel adecuado
            val file = File(ruta + name)
            val uri = FileProvider.getUriForFile(context,"com.example.control_escolar.fileprovider", file )
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity( Intent.createChooser(shareIntent,"Compartir archivo Excel")) // Cambiar el título si es necesario
        }


        fun findValue( dataTable: MutableList<MutableMap<String, Any>>,cell1: String, cell2: String, cell3: String, value1: String,value2: String, value3: String): Int {
            val foundData =
                dataTable.find { it[cell1] == value1 && it[cell2] == value2 && it[cell3] == value3 }
            if (foundData != null) {
                return foundData["total"].toString().toInt()
            } else {
                return 0
            }
        }

        fun findValue( dataTable: MutableList<MutableMap<String, Any>>, cell1: String, cell2: String, value1: String, value2: String ): Int {
            val foundData = dataTable.find { it[cell1] == value1 && it[cell2] == value2 }

            if (foundData != null) {
                return foundData["total"].toString().toInt()
            } else {
                return 0
            }
        }

        fun findValuesEstadistic(dataTable: MutableList<MutableMap<String, Any>>, cell1: String,  cell2: String, value1: String, value2: String): Int {
            var foundDataList = dataTable.filter { it[cell1] == value1 && it[cell2] == value2 }
            var suma = 0
            if (foundDataList.isNotEmpty()) {
                for (foundData in foundDataList) {
                    suma++
                    //Toast.makeText(context, "$cell1 = $value1, $cell2 = $value2, $cell3 = $value3, $totalporciento" , Toast.LENGTH_SHORT).show()
                }
            } else {
                suma = 0
            }
            return suma
        }
    }




