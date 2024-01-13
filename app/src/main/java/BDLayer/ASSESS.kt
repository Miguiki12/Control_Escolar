package BDLayer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.example.control_escolar.DatosCalificar
import com.example.control_escolar.Nombre_Escuela
import com.example.control_escolar.R
import kotlin.math.pow
import kotlin.math.roundToInt


class ASSESS(var context: Context): SQLiteOpenHelper(
context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var error = ""
    public var df_inicio = ""
    public var df_fin = ""
    public var iC_parcial = 0
    public var iFolio = 0
    public var fCalification = 0.0f
    public var sObservaciones = ""

    var valueAsistence = 0.0f
    var calificacionAsistence = 0.0f
    val dtTotalActivitysin0: MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtSumGradedActivities : MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtPorcentageByActivitys : MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtPorcentageByFolio : MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtSumReports: MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtCalificationByStudent: MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtSumParticipation: MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtSumAbsence: MutableList<MutableMap<String, Any>> = mutableListOf()
    val dtTotalAbsence: MutableList<MutableMap<String, Any>> = mutableListOf()
    var listActivitys : MutableList<MutableMap<String, Any>> = mutableListOf()
    lateinit var listMatters: ArrayList<Int>

    init {
        db = this.writableDatabase
        values = ContentValues()

    }
    override fun onCreate(p0: SQLiteDatabase?) {
        try {
            val createTable = "CREATE TABLE if not exists Calificacionestemp " +
                    "(C_calificacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_parcial  Integer, Folio Int, Calificacion Int, Observaciones Text, FOREIGN KEY(C_parcial) REFERENCES Parciales(C_parcial), FOREIGN KEY(Folio) REFERENCES Alumno(Folio))"
            db!!.execSQL(createTable)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }


    public fun deleteCalificaciones() {
        val BorrarTabla = "DROP TABLE IF EXISTS Calificaciones"
        db!!.execSQL(BorrarTabla)
    }

    public fun onCreatetableCalificaciones(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Calificaciones " +
                    "(C_calificacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Calificacion Float, Periodo Integer,  FOREIGN KEY(Folio) REFERENCES Alumno(Folio))"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

     fun onCreateTable() {
        try {
            val createTable = "CREATE TABLE if not exists Calificacionestemp " +
                    "(C_calificacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_parcial  Integer, Folio Int, Calificacion Int, Observaciones Text, FOREIGN KEY(C_parcial) REFERENCES Parciales(C_parcial), FOREIGN KEY(Folio) REFERENCES Alumno(Folio))"
            db!!.execSQL(createTable)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
            val BorrarTabla = "DROP TABLE IF EXISTS Calificaciones"
            db!!.execSQL(BorrarTabla)
    }

    fun insertCalificationtemp(){

        val sQuery = "Insert into Calificacionestemp(C_parcial, Folio, Calificacion, Observaciones) values ($iC_parcial, $iFolio, $fCalification, '$sObservaciones')"
        db!!.execSQL(sQuery)
    }

    public fun detelePartial(parcia:String){
        val sQuery = "Delete from Calificacionestemp where c_Parcial = $parcia"
        db!!.execSQL(sQuery)
    }

    public fun insertCalificaciones(folio:Int, calificacion:Int, periodo:Int){

        val sQuery = "Insert into Calificaciones(Folio,Calificacion, Periodo) values($folio, $calificacion, $periodo)"
        db!!.execSQL(sQuery)

    }

    public fun insertCalifications_Finals(folio:Int, calificacion:Int):Boolean{
        return try{
            val sQuery =
                "Insert into Calificaciones_finales(Folio,Total) values($folio, $calificacion)"
            db!!.execSQL(sQuery)
            true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            false
        }
    }

    public fun updateEspecialCalification(calificacion:Int, c_calificacion:Int){
        val sQuery = "Update Calificacionestemp set Calificacion = $calificacion where c_calificacion = $c_calificacion"
        db!!.execSQL(sQuery)
    }




    fun getDatesByParcial(parcial:Int){
        val sQuery = "Select distinct (Periodo) as Periodo, f_inicio, f_fin " +
                "from Parciales " +
                "where Periodo = $parcial"
        val cursor = db.rawQuery(sQuery, null)
        if (cursor.moveToFirst()){
            df_inicio = cursor.getString(1)
            df_fin = cursor.getString(2)
        }
        cursor.close()
    }


    public fun getCalificactionsParciales():MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Calificacion, folio, Periodo, N_materia, Indice, Color " +
                "FROM parciales, Calificacionestemp, Materia " +
                "where Calificacionestemp.c_Parcial = Parciales.C_parcial and Materia.C_materia = Parciales.C_materia "+
                "order by indice"
        val cursor = db.rawQuery(sQuery, null)
        val califications : MutableList<MutableMap<String, Any>> = mutableListOf()
        if (cursor.moveToFirst()){
            do {
                val newData1 = mutableMapOf<String, Any>()
                newData1["total"] = cursor.getString(0)
                newData1["folio"] = cursor.getString(1)
                newData1["periodo"] = cursor.getString(2)
                newData1["n_materia"] = cursor.getString(3)
                califications.add(newData1)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return califications
    }

    public fun getAspectsByMatters():MutableList<MutableMap<String, Any>>{
        var sQuery = ""
        if (Nombre_Escuela.get_tipo() > 1) {
             sQuery = "SELECT Nombre, Valor, N_materia " +
                    "FROM TIPO_ACTIVIDAD, Materia " +
                    "where Materia.C_materia = Tipo_Actividad.C_materia " +
                    "GROUP BY Tipo_Actividad.C_MATERIA, C_ACTIVIDAD "
        }
        if (Nombre_Escuela.get_tipo() == 1) {
             sQuery = "SELECT Nombre, Valor, N_materia " +
                    "FROM TIPO_ACTIVIDAD, Materia "
                    ///"group by Nombre"
        }

        val cursor = db.rawQuery(sQuery, null)
        val aspects : MutableList<MutableMap<String, Any>> = mutableListOf()
        if (cursor.moveToFirst()){
            do {
                val newData1 = mutableMapOf<String, Any>()
                newData1["name"] = cursor.getString(0)
                newData1["value"] = cursor.getString(1)
                newData1["n_matter"] = cursor.getString(2)
                aspects.add(newData1)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return aspects
    }



    public fun getCalifications(partial:Int, c_matter:Int, list:ArrayList<DatosCalificar>) {
        val sQuery =
            "Select Parciales.C_parcial, Alumno.Folio, Calificacion, Nombre, Observaciones, foto, sexo, c_calificacion " +
                    "from Calificacionestemp, Alumno, Parciales " +
                    "where Parciales.Periodo = $partial and Parciales.C_materia = $c_matter and Parciales.c_Parcial = Calificacionestemp.C_parcial and Calificacionestemp.Folio = Alumno.Folio"

        val cursor = db.rawQuery(sQuery, null)
        if (cursor.moveToFirst()) {
            do {                                                                 //PictureLoader.loadPicture(context, cursor.getBlob(5))
                list.add(
                    DatosCalificar(cursor.getString(3), cursor.getString(4), R.drawable.alumno, cursor.getString(2),
                        cursor.getString(6).toInt(),cursor.getString(1),cursor.getString(0),"", cursor.getInt(7)))
            } while (cursor.moveToNext())
            //loadPicture(cursor.getBlob(5))
            cursor.close()
        }
    }

    public fun getCalificationByMatter(partial: Int, c_matter:Int):Cursor{
        val sQuery = "SELECT Alumno.Folio, Nombre || ' ' || Apellidop || ' ' || Apellidom AS NombreCompleto, Calificacion, N_materia " +
                "FROM parciales, Calificacionestemp, Materia, Alumno " +
                "WHERE Calificacionestemp.c_Parcial = Parciales.C_parcial " +
                "  AND Alumno.Folio = Calificacionestemp.Folio " +
                "  AND Materia.C_materia = Parciales.C_materia " +
                "  AND Materia.c_materia = $c_matter " +
                "  AND Periodo = $partial " +
                "ORDER BY Alumno.folio;"
        /*val sQuery = "SELECT Folio, Calificacion, N_materia " +
                     "FROM parciales, Calificacionestemp, Materia " +
                     "where Calificacionestemp.c_Parcial = Parciales.C_parcial and Materia.C_materia = Parciales.C_materia and Materia.c_materia = $c_matter and Periodo = $partial " +
                     "order by folio"*/

        return  db.rawQuery(sQuery, null)
    }

    public  fun getSumCalifications(periodo:Int):Cursor{
        val sQuery = "SELECT CAST(SUM(calificacion) AS REAL) / (SELECT COUNT(c_materia) FROM materia) AS Total, folio, Parciales.Periodo " +
                "FROM calificacionestemp, Parciales " +
                "where Parciales.Periodo = $periodo and Parciales.C_parcial = Calificacionestemp.C_parcial " +
                "GROUP BY folio"
        return db.rawQuery(sQuery, null)
    }

    public  fun deleteCalification(partial:Int){
        val sQuery = "Delete from Calificaciones where Periodo = $partial"

        db!!.execSQL(sQuery)
    }

    public  fun getSumParcials():Cursor{
        val sQuery = "SELECT SUM(calificacion) / (SELECT COUNT(DISTINCT periodo) FROM Parciales) AS total, Calificaciones.folio " +
                "FROM calificaciones, alumno " +
                "where Calificaciones.Folio = alumno.Folio " +
                "GROUP BY Calificaciones.folio"
        return db.rawQuery(sQuery, null)
    }

    public  fun deleteCalificaciones_finales(){
        val sQuery = "Delete from Calificaciones_finales"
        db!!.execSQL(sQuery)
    }


    private fun loadPicture(byteArray: ByteArray?): Bitmap {
        var fotoBytes = byteArray
        var bitmaptemp = BitmapFactory.decodeResource(context.resources, R.drawable.alumno)
        if (fotoBytes != null && fotoBytes.isNotEmpty()) {
            // Convierte el arreglo de bytes a un objeto Bitmap
            val bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.size)
            return bitmap
        } else {
            return bitmaptemp
        }
    }



    //obtenemos todos los puntos menos por reportes
    private fun getReports (){
        val sQuery = "Select sum(Puntaje), Folio, c_materia "+
                     "From Reporte "+
                     "where fecha BETWEEN '$df_inicio' and '$df_fin' "+
                     "group by folio, c_materia "+
                     "order by folio"
        fillValues(sQuery,dtSumReports)
    }

    private fun fillValues(sQuery: String, dataTable: MutableList<MutableMap<String, Any>>){
        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    //Toast.makeText(context,cursor.getString(0)+" " +cursor.getString(1)+" "+cursor.getString(2), Toast.LENGTH_SHORT).show()
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["total"] = cursor.getString(0)
                    newData1["folio"] = cursor.getString(1)
                    newData1["c_matter"] = cursor.getString(2)
                    dataTable.add(newData1)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()
        }
    }

    //obtenemos los puntos de la participacion del alumno
    private fun getParticipations(){
        val sQuery = "Select sum(Puntaje), Folio, c_materia " +
                "From Participacion " +
                "where fecha BETWEEN '$df_inicio' and '$df_fin' " +
                "group by folio, c_materia " +
                "order by folio"
        fillValues(sQuery, dtSumParticipation)
    }

    public fun getCalificactionByC_activity(c_actividad:Int):Int{
        var calification = 0
        val sQuery = "Select Calificacion " +
                "from Calificacionestemp " +
                "where c_calificacion = $c_actividad"
        val cursor = db!!.rawQuery(sQuery, null)

        if (cursor.moveToFirst()){
            calification = cursor.getInt(0)
        }
        return calification
    }

    /*public fun getDetailsAsistences(begin: String, end: String){
        df_inicio = begin
        df_fin = end
        getAbsenceByFolio()
        getTotalAttendance()
        get
    }*/

    @SuppressLint("SuspiciousIndentation")
    public fun getAbsenceByFolio():MutableList<MutableMap<String,Any>>{
        /*val sQuery = "Select  count(Asistencia.Dia), Asistencia, folio, Asistencia.c_materia, N_materia " +
                     "From Asistencia, MateriaDia, Materia " +
                     "where fecha BETWEEN '$df_inicio' and '$df_fin' and asistencia = 0 and Asistencia.dia = MateriaDia.Dia and MateriaDia.C_materia = Materia.C_materia " +
                     "group by Asistencia.dia, folio, Asistencia.c_materia, asistencia, c_dia " +
                     "order by folio "*/
        val sQuery = "Select  count(Asistencia.Dia), folio, N_materia "+
                "From Asistencia, MateriaDia, Materia " +
                "where fecha BETWEEN'$df_inicio' and '$df_fin' and asistencia = 0 and Asistencia.dia = MateriaDia.Dia and MateriaDia.C_materia = Materia.C_materia " +
                "group by folio,  N_materia " +
                "order by folio "
                fillValues(sQuery,dtSumAbsence)
        return  dtSumAbsence
    }

    public fun getAbsenceByMatter(c_matter:Int):Cursor{
        val sQuery  = "Select  folio, count(Asistencia.Dia), N_materia " +
                      "From Asistencia, MateriaDia, Materia " +
                      "where fecha BETWEEN'$df_inicio' and '$df_fin' and asistencia = 0 and Materia.C_materia = $c_matter " +
                      "and Asistencia.dia = MateriaDia.Dia and MateriaDia.C_materia = Materia.C_materia " +
                      "group by folio, asistencia " +
                      "order by folio"
        return db.rawQuery(sQuery, null)
    }


    private fun getTotalAttendanceByDays(day:String):Int{
        /*val sQuery = "SELECT MAX(Asistencia) AS maximo FROM "+
                     "(SELECT COUNT(*) AS Asistencia FROM Asistencia where Fecha BETWEEN '$df_inicio' and '$df_fin' GROUP BY Asistencia) AS subconsulta"*/

        val sQuery = "SELECT MAX(Asistencia) AS maximo, folio " +
                     "FROM (SELECT COUNT(*) AS Asistencia, folio " +
                     "FROM Asistencia where Fecha BETWEEN '$df_inicio' and '$df_fin'  and  Dia = '$day' and Asistencia = 1  GROUP BY Asistencia,folio) AS subconsulta"
        try {
           val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()){
                return cursor.getString(0).toInt()
                cursor.close()
            }
            else return  0

        }catch (Ex:Exception){
             return  0
        }

    }

    public fun getTotalAttendance():MutableList<MutableMap<String, Any>>{
       /* val sQuery = "SELECT COUNT (Asistencia.Asistencia), folio, N_materia " +
                "from Asistencia, MateriaDia, Materia " +
                "where Asistencia = 1 and Asistencia.Dia = MateriaDia.Dia and MateriaDia.C_materia = Materia.C_materia " +
                "Group by folio, N_materia"*/

        /*val sQuery = "SELECT MAX(Asistencia) AS maximo, folio, N_materia " +
                "FROM (SELECT COUNT (Asistencia.Asistencia) as Asistencia, folio, N_materia " +
                "from Asistencia, MateriaDia, Materia " +
                "where fecha between '2023-06-01' and '2023-06-30' and N_materia = '$name_matter' and Asistencia = 1 and Asistencia.Dia = MateriaDia.Dia and MateriaDia.C_materia = Materia.C_materia " +
                "Group by folio, N_materia, N_materia) AS subconsulta;"*/

        val sQuery = "SELECT COUNT (Asistencia.Asistencia)as total, folio, N_materia " +
                "from Asistencia, MateriaDia, Materia " +
                "where fecha between '$df_inicio' and '$df_fin' and Asistencia = 1 and Asistencia.Dia = MateriaDia.Dia and MateriaDia.C_materia = Materia.C_materia " +
                "Group by folio, N_materia " +
                "order by total desc"

        fillValues(sQuery, dtTotalAbsence)
        return  dtTotalAbsence
    }


//obtiene las calificaciones por cada actividad entregada por el alumno
    private fun getQualificationByJob() {
        val sQuery ="SELECT Tarea.c_tarea, Tarea.folio AS CuentaDeN_lista, Tarea.folio, tipo, c_materia, Calificacion,  Tarea.F_entrega\n" +
                "FROM Tarea, tareas\n" +
                "WHERE(((Tarea.F_entrega)Between '$df_inicio' And '$df_fin')) and Tareas.C_actividades = Tarea.C_tarea and tareas.Porcentaje = 0 \n" +
                "GROUP BY Tarea.folio, tipo, c_materia, Calificacion, tarea.F_entrega, Tarea.c_tarea \n" +
                "ORDER BY folio, c_materia, tipo"
        try {
            val cursor = db.rawQuery(sQuery, null)
            // Aquí puedes realizar las operaciones necesarias con el cursor
            cursor.close()
        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()
        }
    }

    fun getCalificationByStudent(folio:Int, begin:String, end:String, c_matter:Int):MutableList<MutableMap<String, Any>>{
        val sQuery = "Select calificacion, folio, c_actividades " +
                "from Tarea, Tareas " +
                "where Tarea.C_Tarea = Tareas.C_actividades " +
                "and  folio = $folio and Tareas.f_entrega between '$begin' and '$end' and Encuenta = 0 and Tareas.c_materia =  $c_matter " +
                "order by Tareas.c_materia, Tareas.tipo, Tareas.Porcentaje"
        error = sQuery
        fillValues(sQuery,dtCalificationByStudent)
        return dtCalificationByStudent
    }
    fun getCalificationSpecialByStudent(folio:Int, begin:String, end:String, c_matter:Int):Cursor{
        val sQuery = "Select Tareas.C_actividades, Calificacion, porcentaje ," +
                "Nombre, N_materia, color, Tareas.f_entrega, folio, Actividad_Especial.Terminada, Actividad_Especial.C_Actividades " +
                "from Actividad_Especial, Tareas, Materia " +
                "where Materia.C_materia = Tareas.C_materia and Tareas.C_actividades = Actividad_Especial.C_Actividades " +
                "and Tareas.f_entrega between '$begin' and '$end' and Folio = $folio and tareas.c_materia = $c_matter " +
                "group by folio, Tareas.C_actividades " +
                "order by folio, Actividad_Especial.Terminada"
        error = sQuery

        return db.rawQuery(sQuery, null)
    }

//obtiene la suma total de cada actividad por alumno agrupado por la misma
    private fun getQualification0ByActivity() {
        var sQuery ="SELECT Sum(Tarea.Calificacion)  AS SumOfCalificacion, Tarea.FOLIO, tareas.tipo, tareas.c_materia, Tarea.Calificacion " +
                "FROM Tareas, Tarea " +
                "WHERE(((Tareas.F_entrega) Between '$df_inicio' And '$df_fin') And ((tareas.C_actividades)=Tarea.C_tarea) And ((tareas.Porcentaje)=0)) " +
                "GROUP BY Tarea.folio, tareas.tipo, tareas.c_materia "+
                "ORDER BY Tarea.folio, tareas.c_materia, tareas.tipo"

        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["total"] = cursor.getString(0)
                    newData1["folio"] = cursor.getString(1)
                    newData1["type"] = cursor.getString(2)
                    newData1["c_matter"] = cursor.getString(3)
                    dtSumGradedActivities.add(newData1)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()
        }
    }


//obtenemos  el total por actividad  a
    private fun getQualificationWhitPorcentageByActivity() {
         val sQuery = "SELECT Tareas.Porcentaje  AS Porciento, Tarea.FOLIO, tareas.tipo, tareas.c_materia, Tarea.Calificacion " +
                      "FROM Tareas, Tarea "+
                      "WHERE(((Tareas.F_entrega) Between '$df_inicio' And '$df_fin') And ((tareas.C_actividades)=Tarea.C_tarea) And ((tareas.Porcentaje)>0 and Encuenta = 0)) "+
                      "GROUP BY Tarea.folio, tareas.tipo, tareas.c_materia, Tarea.Calificacion, Tareas.Porcentaje "+
                      "ORDER BY Tarea.folio, tareas.c_materia, tareas.tipo"

        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["porciento"] = cursor.getString(0)
                    newData1["folio"] = cursor.getString(1)
                    newData1["type"] = cursor.getString(2)
                    newData1["c_matter"] = cursor.getString(3)
                    newData1["calificacion"] = cursor.getString(4)
                    dtPorcentageByFolio.add(newData1)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()
        }
    }


//obtiene el porcentaje de las actividades con valor > 0 agrupadas por su tipo de actividad
    fun getPorcentageTotalByActivity(){

        val sQuery = "SELECT Sum(Tareas.Porcentaje)  AS SumOfCalificacion, tareas.tipo, tareas.c_materia "+
                     "FROM Tareas "+
                     "WHERE(((Tareas.F_entrega) Between '$df_inicio' And '$df_fin') And ((tareas.Porcentaje)> 0)) and Encuenta = 0 "+
                     "GROUP BY tareas.tipo, tareas.c_materia "+
                     "ORDER BY tareas.c_materia, tareas.tipo "
        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["total"] = cursor.getString(0)
                    newData1["type"] = cursor.getString(1)
                    newData1["c_matter"] = cursor.getString(2)
                    dtPorcentageByActivitys.add(newData1)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()
        }
    }

    /// <summary>
    /// obtiene el total de actividades  del parcial con porcentaje 0 agrupado por tipo de actividad
    /// </summary>

    fun getAllActivitys0(){
        val sQuery = "SELECT Count(Tareas.C_actividades) AS Total_actividad, tipo, c_materia "+
                     "FROM Tareas "+
                     "WHERE(((Tareas.F_entrega)Between '$df_inicio' and '$df_fin')) and Porcentaje = 0 "+
                     "GROUP BY tipo, c_materia "+
                     "ORDER BY c_materia, tipo "
        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["total"] = cursor.getString(0)
                    newData1["type"] = cursor.getString(1)
                    newData1["c_matter"] = cursor.getString(2)
                    dtTotalActivitysin0.add(newData1)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()
        }
    }

    fun getAllActivitys():MutableList<MutableMap<String,Any>>{
        val sQuery = "SELECT Count(Tareas.C_actividades) AS Total_actividad, Tipo_Actividad.Nombre, Materia.N_materia " +
                "FROM Tareas, Tipo_Actividad, Materia " +
                "WHERE(((Tareas.F_entrega)Between '$df_inicio' and '$df_fin')) and Tareas.tipo = Tipo_Actividad.C_actividad and Tareas.C_materia = Materia.C_materia and encuenta = 0 " +
                "GROUP BY Tipo_Actividad.C_actividad,Tareas.C_materia " +
                "order by Tipo_Actividad.Nombre desc"
        val activitys : MutableList<MutableMap<String,Any>> = mutableListOf()
        try {

            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["total"] = cursor.getString(0)
                    newData1["activity"] = cursor.getString(1)
                    newData1["matter"] = cursor.getString(2)
                    activitys.add(newData1)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            error = e.message.toString()
        }
        return  activitys
    }

    fun getAllActivitysDelivered():MutableList<MutableMap<String,Any>>{
        val sQuery = "SELECT Count(Tarea.C_tarea) AS Total_actividad, folio, Tipo_Actividad.Nombre, Materia.N_materia " +
                "FROM Tarea, Tareas, Tipo_Actividad, Materia " +
                "WHERE(((Tareas.F_entrega)Between '${df_inicio}' and '$df_fin')) " +
                "and Tareas.tipo = Tipo_Actividad.C_actividad and Tareas.C_materia = Materia.C_materia and Tareas.C_actividades = Tarea.C_tarea and encuenta = 0 and Tarea.Calificacion > 0 "+
                "GROUP BY N_materia, Tipo_Actividad.Nombre, folio " +
                "ORDER BY Tareas.c_materia, Tareas.tipo "
        val activitys : MutableList<MutableMap<String,Any>> = mutableListOf()
        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["total"] = cursor.getString(0)
                    newData1["folio"] = cursor.getString(1)
                    newData1["activity"] = cursor.getString(2)
                    newData1["matter"] = cursor.getString(3)
                    activitys.add(newData1)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            error = e.message.toString()
        }
        return  activitys
    }





    private fun getTypeActivitys() {
        val sQuery = "Select * from Tipo_Actividad"
        try {
            val cursor = db.rawQuery(sQuery, null)
            // Aquí puedes realizar las operaciones necesarias con el cursor
            cursor.close()
        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()
        }

    }

    public fun getAllMatters():Cursor {
        val sQuery = "Select C_materia, N_materia, Color, Tipo, Indice from Materia "

        return  db.rawQuery(sQuery, null)

    }

    private fun getActivitys(){
        val sQuery = "Select Distinct(C_actividad),Tareas.c_materia, valor " +
                "From Tareas, Tipo_Actividad " +
                "where Tareas.tipo = Tipo_Actividad.C_actividad "+
                "Group by c_actividad"
        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()){
                listActivitys = ArrayList()
                do{
                    val newData1 = mutableMapOf<String, Any>()
                    newData1["c_activity"] = cursor.getString(0)
                    newData1["name"] = cursor.getString(1)
                    newData1["value"] = cursor.getString(2)
                    listActivitys.add(newData1)
                }while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            error = e.message.toString()
            e.printStackTrace()
        }
    }

    private fun califityAsistence():Float{
        val sQuery="select Valor "+
                   "From Tipo_Actividad "+
                   "where calificar = 1"
        val cursor = db.rawQuery(sQuery, null)
        if(cursor.moveToFirst()){
            valueAsistence = cursor.getString(0).toFloat()

        }else {
            valueAsistence = 0.0f
        }
        cursor.close()
        return  valueAsistence
    }

    public fun totalValueActivitys():Int{
        val sQuery = "Select Sum (Valor) as Total " +
                "from Tipo_Actividad"
        val cursor = db.rawQuery(sQuery, null)
        return if (cursor.moveToFirst()){
            cursor.getInt(0)
        }else{
            0
        }
        cursor.close()
    }

    private fun getMatters(){
        val sQuery = "Select c_materia from Materia where indice > 0"
        try {
            val cursor = db.rawQuery(sQuery, null)
            if (cursor.moveToFirst()){

                do{
                    listMatters.add(cursor.getString(0).toInt())
                }while (cursor.moveToNext())
            }
            cursor.close()

        } catch (e: SQLiteException) {
            // Aquí puedes manejar la excepción en caso de que haya un error en la consulta SQL
            error = e.message.toString()
            e.printStackTrace()

        }

    }

   public fun getAssess(folio:String, c_parcial:Int, c_materia:String, name_matter:String){
       try {
           val decimal = SettingsBD(context).getRoundDecimal()
           var cont = 0
           var assessment = 0.0f

           while (cont < listActivitys.count()){

               val type =  listActivitys[cont]!!["c_activity"].toString()

               val value = listActivitys[cont]!!["value"].toString().toInt()

               val calification0 = findValue(dtSumGradedActivities, "folio",  "type", "c_matter",folio, type, c_materia) /// dtTotalActivitysin0
               val  totalactivitys = findValue(dtTotalActivitysin0,"type", type) /// dtTotalActivitysin0
               //dividimos la calificacion total de las actividades con % cero entre las actividades pendientes del parcial
               var qualification0 = 0.0f
               if (totalactivitys > 0)  qualification0 = (calification0 / totalactivitys.toFloat())

               //restamos 100 a la suma de las actividades con % obtenemos para obtener el porcentaje que tienen las actividades pendientes con % cero

               val porcentaje0 = (100 - findValuePorcent(dtPorcentageByActivitys, "type", type)) / 100.0f

               val total0 = qualification0 * porcentaje0

               val calificacion = findValuesWithPorcent(dtPorcentageByFolio,"folio", "type", "c_matter", folio, type, c_materia) + total0

               val valuecalification = (calificacion * value) / 100.0f

               assessment = (valuecalification + assessment).toFloat()

               cont++
           }


           iC_parcial = c_parcial
           iFolio = folio.toInt()
           //Toast.makeText(context, dtSumAbsence.count().toString(), Toast.LENGTH_SHORT).show()
           val faltas = getPercentAttendance(dtSumAbsence,"c_matter","folio",name_matter, folio)
           val totalParticipation = findValue(dtSumParticipation,"folio", "c_matter", folio, c_materia)
           val totalReports = findValue(dtSumReports,"folio", "c_matter", folio, c_materia)
           sObservaciones = "$faltas\nParticipacion = $totalParticipation, Reportes = $totalReports"
           fCalification = (assessment + calificacionAsistence + totalParticipation) - totalReports
           //hacemos el proceso de redondear de acuerdo al numero indicado en la tabla de configuración
           if (decimal > 0) fCalification = redondearCalificacion(fCalification.toInt(), decimal).toFloat()//redondea dependiendo del numero deseado
           if (decimal == 0)fCalification = fCalification.toInt() + 0f //si es cero quita los numeros despues del punto
           insertCalificationtemp()


       }catch (Ex:Exception){
           Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_SHORT).show()
       }

    }



 public fun findValuePorcent(dataTable:MutableList<MutableMap<String, Any>>, cell:String, value:String):Int{
        val foundData = dataTable.find { it[cell] == value }

        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }

    }
    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell:String, value:String):Int{
        val foundData = dataTable.find { it[cell] == value }


        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }

    }

    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, cell2:String, value1:String, value2: String):Int{
        val foundData = dataTable.find {it[cell1] == value1 && it[cell2] == value2}
        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }

    }

    fun findValue(dataTable:MutableList<MutableMap<String, Any>>, cell1:String, cell2:String, cell3:String,value1:String, value2:String, value3:String):Int{
        val foundData = dataTable.find { it[cell1] == value1 && it[cell2] == value2 && it[cell3] == value3 }

        if (foundData != null) {
            return foundData["total"].toString().toInt()
        } else {
            return 0
        }

    }


    fun cleanValues(){
        dtPorcentageByFolio.clear()
        dtPorcentageByActivitys.clear()
        dtSumGradedActivities.clear()
        dtTotalActivitysin0.clear()
        listActivitys.clear()
        dtSumReports.clear()
        dtSumReports.clear()
        dtSumParticipation.clear()
        //listMatters.clear()
    }

    fun getAllValues(){
        getActivitys()
        getMatters()
        getAllActivitys0()
        getPorcentageTotalByActivity()
        getQualification0ByActivity()
        getQualificationWhitPorcentageByActivity()
        getAbsenceByFolio()
        getTotalAttendance()
        getReports()
        getParticipations()
        valueAsistence = califityAsistence()
    }


    fun findValuesWithPorcent(dataTable: MutableList<MutableMap<String, Any>>, cell1: String, cell2:String, cell3: String, value1: String, value2:String, value3: String): Float {
        var foundDataList = dataTable.filter { it[cell1] == value1 && it[cell2] == value2 && it[cell3] == value3 }
        var totalporciento = 0.0f
        if (foundDataList.isNotEmpty()) {
            for (foundData in foundDataList) {
                totalporciento += (foundData["porciento"].toString().toInt() / 100.0f) * (foundData["calificacion"].toString().toInt())
                //Toast.makeText(context, "$cell1 = $value1, $cell2 = $value2, $cell3 = $value3, $totalporciento" , Toast.LENGTH_SHORT).show()
            }
        } else {
        }
        return totalporciento
    }

    @SuppressLint("SuspiciousIndentation")
    fun getPercentAttendance(dataTable: MutableList<MutableMap<String, Any>>, cell1: String, cell2:String, value1: String, value2:String): String {
        var foundDataList = dataTable.find { it[cell1] == value1 && it[cell2] == value2 }
        var totalporciento = 0.0f
        var details = ""
        //Toast.makeText(context, "$cell1 == $value1 && $cell2 == $value2",Toast.LENGTH_SHORT).show()
        //Toast.makeText(context, dataTable[3]["total"].toString(), Toast.LENGTH_SHORT).show()
        if (foundDataList != null) {
                totalporciento = (foundDataList["total"].toString().toFloat() / findValue(dtTotalAbsence,"c_matter", value1))
                totalporciento *= 100
                totalporciento = 100.0f - totalporciento

            calificacionAsistence = 0.0f
                if (valueAsistence > 0) calificacionAsistence = valueAsistence * (totalporciento)/100
            //Toast.makeText(context,"valor $valueAsistence, asistencia = $totalporciento/100, $calificacionAsistence", Toast.LENGTH_SHORT).show()
            details = "No.Clases ${findValue(dtTotalAbsence, "c_matter", value1)}, faltas ${foundDataList?.get("total").toString()}, Asistencia $totalporciento%"
        } else {
            details = "No hay Faltas "
        }
        return details
    }

    fun getPercentAttendance1(cell1: String, cell2:String, value1: String, value2:String): String {
        val total_asistencias = getTotalAttendance()
        val faltas = getAbsenceByFolio()

        var foundDataList = faltas.find { it[cell1] == value1 && it[cell2] == value2 }
        var totalporciento = 0.0f
        var details = ""

        if (foundDataList != null) {
            totalporciento = (foundDataList["total"].toString().toFloat() / findValue(total_asistencias,"c_matter", value1))
            totalporciento *= 100
            //totalporciento = 100.0f - totalporciento
            /*Toast.makeText(context,  "${foundDataList["total"].toString()} / ${findValue(dtTotalAbsence,"c_matter", value1)}," +
                  "  total = $totalporciento, folio = $value2, dia = ${value1}" , Toast.LENGTH_SHORT).show()*/
            //si tenemos la asistencia para evaluar, obtenemos su porcentaje para poder sumar en la evaluacion


            calificacionAsistence = 0.0f
            if (valueAsistence > 0) calificacionAsistence = valueAsistence * (totalporciento)/100
            //Toast.makeText(context,"valor $valueAsistence, asistencia = $totalporciento/100, $calificacionAsistence", Toast.LENGTH_SHORT).show()
            details = " ${foundDataList?.get("total").toString()}/${findValue(total_asistencias, "c_matter", value1)}=${roundDecimalFloat(totalporciento.toFloat(), 1)}%"
            //details = "No.Clases ${findValue(total_asistencias, "c_matter", value1)}, faltas ${foundDataList?.get("total").toString()}, Asistencia $totalporciento%"
        } else {
            details = "N/A"
        }

        return details
    }

    fun redondearCalificacion(numero: Int, indicador: Int): Int {
        val residuo = numero % 10
        return if (residuo > indicador) {
            numero + (10 - residuo)
        } else {
            numero - residuo
        }
    }

    fun roundDecimalFloat(number: Float, decimals: Int): Float {
        val factor = 10.0.pow(decimals).toFloat()
        return (number * factor).roundToInt() / factor
    }
}
