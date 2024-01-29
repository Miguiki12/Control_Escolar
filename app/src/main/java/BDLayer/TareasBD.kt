package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.control_escolar.Nombre_Escuela
import java.text.SimpleDateFormat
import java.util.*

data class TareasBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase = this.writableDatabase
    private val values: ContentValues = ContentValues()
    var error = ""
    var iCalificacion: Int = 0
        set(value) {
            if (value <= 100 )
                 field = value
            else error = "la calificacion no puede exceder a 100"
        }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Tareas " +
                    "(id_tareas INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "C_actividades INTEGER, " +
                    "C_materia Integer, Nombre TEXT, Descripcion TEXT, Porcentaje Integer, F_inicio Text, " +
                    "F_entrega Text, F_tolerancia Text, tipo Integer, Terminada Integer, Encuenta Integer, " +
                    "FOREIGN KEY(C_materia) REFERENCES Materia(C_materia), FOREIGN KEY(tipo) REFERENCES Tipo_Actividad(C_Actividad))"

            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }



    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Tareas"
        db!!.execSQL(BorrarTabla)

    }




    fun InsertarTareas(nombre:String, porcentaje:String, f_entrega:String, c_materia:String, c_actividad:String, especial:Int): Boolean {
        try {
            if (validardatos(nombre, c_actividad, c_materia, porcentaje)) {
                val Insertar = "INSERT INTO Tareas (C_materia, Nombre, Porcentaje, F_entrega, tipo, Terminada, Encuenta) " +
                        "VALUES ($c_materia, '$nombre', $porcentaje, '${convertdate(f_entrega)}', $c_actividad, 0, $especial)"

                // Ejecutar la inserción
                db!!.execSQL(Insertar)

                // Obtener el último id_tareas insertado
                val query = "SELECT last_insert_rowid() AS lastId"
                val cursor = db.rawQuery(query, null)

                // Actualizar el campo C_actividades
                if (cursor.moveToFirst()) {
                    val lastId = cursor.getInt(0)
                    val updateQuery = "UPDATE Tareas SET C_actividades = $lastId WHERE id_tareas = $lastId"
                    db.execSQL(updateQuery)
                }

                error = "Se registró correctamente la Actividad"
                return true
            } else {
                error = "Indique todos los campos"
                return false
            }
        } catch (Ex: Exception) {
            error = Ex.message.toString()
            return false
        }
    }

    public fun actualizarTareas(nombre:String, porcentaje:String, fecha:String, c_materia: String, tipo: String, c_actividades:String):Boolean{
        try {
            val Actualizar =
                "Update Tareas set Nombre = '$nombre', F_entrega = '${convertdate(fecha)}', c_materia = $c_materia, tipo = $tipo, Porcentaje = $porcentaje where C_actividades = $c_actividades"


            db!!.execSQL(Actualizar)
            //error = Actualizar
            error = "Se actualizo el nombre de la actividad correctamente"
            return true

        }catch (Ex:Exception){
            error = Ex.message.toString()

            return  false
        }

    }

    fun getLastPrimaryKey(): Int {

        var lastId = 0

        val query = "SELECT MAX(C_actividades) FROM Tareas"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(0)
        }

        cursor.close()
        //db.close()

        return lastId
    }


    public fun getCalificationEspecials(c_tarea: Int): Cursor {
        val sQuery = " Select Alumno.Folio, Nombre, Apellidop, Apellidom, Sexo, Domicilio, " +
                "Colonia, Curp, Telefono, Email, Entidad_Federativa, Situacion, Condicionado, Edad, Tutor,Telefono_contacto, Email_contacto, F_nacimiento,N_lista, calificacion, f_baja, f_registro " +
                "from Alumno, Actividad_Especial " +
                "where Alumno.folio = Actividad_Especial.folio and c_actividades = $c_tarea " +
                "order by apellidop, apellidom"
        return db.rawQuery(sQuery, null)
    }

    public fun rateCalificationEspecial(c_actividades:String, folio:String, califcation:Int):Boolean {
        return try{
            val sQuery =
                "Update Actividad_Especial set Calificacion = $califcation, f_entrega = '${getCurrentDate()}' where C_actividades = $c_actividades and folio = $folio"

            db!!.execSQL(sQuery)
            error = "Se registro la calificacion de la actividad correctamente"
            true

        } catch (Ex: Exception) {
            error = Ex.message.toString()
            false
        }
    }

    public fun finishCalificationEspecial(c_actividad: String, folio: String, terminate:String):Boolean{
        return try{
            val sQuery = "Update Actividad_Especial set Terminada = $terminate where C_actividades = $c_actividad and folio = $folio"
            db!!.execSQL(sQuery)
            error = sQuery//"Se registro la calificacion de la actividad correctamente"
            true

        } catch (Ex: Exception) {
            error = Ex.message.toString()
            false
        }
    }

    public fun beginingCalificationEspecialByPartial(begin: String,end: String){
        val sQuery = "Update Actividad_Especial set Terminada = 0 where f_entrega between '$begin' and '$end'"
        db!!.execSQL(sQuery)
        error = "Se inicializaron correctamente las actividades especiales"
    }

    public fun InsertarActividadesEspeciales(c_actividad:Int, folio:Int, calificacion:Int, terminada:Int):Boolean {
        try {
            val Insertar = "Insert into ActividaD_Especial(C_actividades, Folio, Calificacion, F_entrega, Terminada) " +
                    "values ($c_actividad, $folio, $calificacion, '${getCurrentDate()}', $terminada)"
            db!!.execSQL(Insertar)
            error = "Se registro correctamente la Actividad"
            return true
        } catch (Ex: Exception) {
            error = Ex.message.toString()
            return false
        } as Nothing

    }

    public fun getCalificationsByDay(date:String):Cursor{
        val sQuery = "Select N_lista, Alumno.Nombre || ' ' || Apellidop || ' ' || Apellidom  AS Nombre_Completo, Calificacion, Tareas.Nombre, Tipo_Actividad.Nombre " +
                "from Tareas, Alumno, Tarea, Tipo_Actividad " +
                "where Tareas.C_actividades = Tarea.C_tarea and Alumno.Folio = Tarea.Folio and Tareas.f_entrega = '$date' and Tipo_Actividad.C_actividad = Tareas.tipo " +
                "group by Tareas.tipo, Tareas.Nombre, Alumno.Nombre " +
                "order by N_lista"
        return db!!.rawQuery(sQuery, null)
    }

    public fun getCalificationsByDayM(date:String):MutableList<MutableMap<String, Any>>{
        val sQuery = "Select C_actividades, N_lista, Alumno.Nombre || ' ' || Apellidop || ' ' || Apellidom  AS Nombre_Completo, Calificacion, Tareas.Nombre, Tipo_Actividad.Nombre " +
                "from Tareas, Alumno, Tarea, Tipo_Actividad " +
                "where Tareas.C_actividades = Tarea.C_tarea and Alumno.Folio = Tarea.Folio and Tareas.f_entrega = '$date' and Tipo_Actividad.C_actividad = Tareas.tipo " +
                "group by Tareas.tipo, Tareas.Nombre, Alumno.Nombre " +
                "order by N_lista"
        val cursor = db!!.rawQuery(sQuery, null)
        val listaMes: MutableList<MutableMap<String, Any>> = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val newData1 = mutableMapOf<String, Any>()
                newData1["total"] = cursor.getString(3)
                newData1["c_actividades"] = cursor.getString(0)
                newData1["n_lista"] = cursor.getString(1)
                newData1["nombre"] = cursor.getString(2)
                listaMes.add(newData1)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listaMes
    }

    public fun getCalificated():MutableList<MutableMap<String, Any>>{
        val sQuery = "Select count(folio) as total, c_tarea " +
                "from Tarea " +
                "group by c_tarea"
        val cursor = db!!.rawQuery(sQuery, null)
        val listaMes: MutableList<MutableMap<String, Any>> = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val newData1 = mutableMapOf<String, Any>()
                newData1["total"] = cursor.getString(0)
                newData1["c_tarea"] = cursor.getString(1)
                listaMes.add(newData1)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listaMes
    }

    public fun getCalificationByid(c_tarea: Int):MutableList<MutableMap<String, Any>>{
        val sQuery = "Select * " +
                "from Tarea " +
                "where c_tarea = $c_tarea"
                "group by c_tarea"
        val cursor = db!!.rawQuery(sQuery, null)
        val listaTare: MutableList<MutableMap<String, Any>> = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val newData1 = mutableMapOf<String, Any>()
                newData1["c_tarea"] = cursor.getString(0)
                newData1["Folio"] = cursor.getString(1)
                newData1["F_entrega"] = cursor.getString(2)
                newData1["Calificacion"] = cursor.getString(3)
                newData1["Porciento"] = cursor.getString(4)
                listaTare.add(newData1)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listaTare
    }



    public fun getActivitysByDay(fecha:String):Cursor{
        val sQuery = "SELECT c_actividades, Tareas.Nombre, Tipo_Actividad.Nombre as tipo, Materia.N_materia " +
                     "FROM Tareas, Tipo_Actividad, Materia " +
                     "WHERE Tareas.F_entrega = '$fecha'" +
                     "and Tareas.tipo = Tipo_Actividad.C_actividad and Tareas.C_materia = Materia.C_materia and encuenta = 0 " +
                     "GROUP BY Tipo_Actividad.Nombre, Materia.N_materia, Tareas.Nombre " +
                     "order by c_actividades"
        error = sQuery
        return db!!.rawQuery(sQuery, null)
    }


    public fun getActivitysEspecialsByStudent(c_actividad: String, folio: String):Int{
        var folio1 = 0
        try {
            val sQuery = "Select Folio " +
                         "from  Actividad_Especial " +
                         "where c_actividades = $c_actividad and Folio = $folio"
            error = sQuery
           val cursor = db.rawQuery(sQuery, null)
           if (cursor.moveToFirst()) folio1 = cursor.getInt(0)

        }catch (Ex:Exception){
            error = Ex.message.toString()
            folio1 = 0
        }
        return folio1
    }

    public fun obtenerAllbymaterias(terminadas: Int, c_materia:String): Cursor {

        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor > 0 and Terminada = " + terminadas + " and Materia.c_materia = "+c_materia
        val orden = "C_actividad , F_entrega"

        val columnas = arrayOf("C_actividades", "F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Porcentaje", "color", "Terminada", "Encuenta", "valor")
        return db.query("Tareas, Materia, Tipo_Actividad", columnas, condicion, null, null, null, null)

    }

    public fun obtenerbyC_actividad(c_tarea: String): Cursor {

        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor > 0 and C_tarea = " + c_tarea
        val orden = "C_actividad , F_entrega"
        val columnas = arrayOf("C_actividades", "F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Porcentaje", "color", "Terminada", "Encuenta", "valor")
        return db.query("Tareas, Materia, Tipo_Actividad", columnas, condicion, null, null, null, orden)

    }
    public fun obtenerAll(terminadas:Int): Cursor {

        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor > 0 and f_entrega BETWEEN date('now', '-7 days') AND date('now') and Terminada = " + terminadas
        val orden = "Tareas.F_entrega Desc , Tareas.C_materia, Tareas.Tipo"
        val columnas = arrayOf("C_actividades", "F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Porcentaje", "color", "Terminada", "Encuenta", "valor")
        return db.query("Tareas, Materia, Tipo_Actividad", columnas, condicion, null, null, null, orden)

    }
    public fun obtenerAll(terminadas:Int, c_materia: Int): Cursor {

        val condicion =  "Tareas.Tipo = C_actividad and tareas.C_materia = $c_materia  and Valor > 0 and Terminada = $terminadas"
        val orden = "Tareas.F_entrega Desc , Tareas.C_materia, Tareas.Tipo"
        val columnas = arrayOf("C_actividades", "F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Porcentaje", "color", "Terminada", "Encuenta", "valor")
        return db.query("Tareas, Materia, Tipo_Actividad", columnas, condicion, null, null, null, orden)

    }
    public fun obtenerAll(): Cursor {

        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor > 0  "
        val orden = "Tareas.F_entrega Desc , Tareas.C_materia, Tareas.Tipo"
        val columnas = arrayOf("C_actividades", "F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Porcentaje", "color", "Terminada", "Encuenta", "valor")
        return db.query("Tareas, Materia, Tipo_Actividad", columnas, condicion, null, null, null, orden)
    }

    public fun obtenerAllFecha(f_entrega:String): Cursor {
        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor > 0 and  F_entrega = '"+convertdate(f_entrega)+"'"
        val orden = "Tareas.F_entrega Desc , Tareas.C_materia, Tareas.Tipo"
        val columnas = arrayOf("C_actividades", "F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Porcentaje", "color", "Terminada", "Encuenta", "valor")
        return db.query("Tareas, Materia, Tipo_Actividad", columnas, condicion, null, null, null, orden)
    }




    public fun getPending(): Cursor {
        //val now = Date().date
        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor > 0 and Terminada = 0 and  F_entrega = '"+getCurrentDate()+"'"
        val orden = "Tareas.F_entrega Desc , Tareas.C_materia, Tareas.Tipo"
        val columnas = arrayOf("C_actividades", "F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Porcentaje", "color", "Terminada", "Encuenta", "valor")
        error = condicion
        return db.query("Tareas, Materia, Tipo_Actividad", columnas, condicion, null, null, null, orden)
    }

    public fun obternercalificacion(c_tarea: String): Cursor {

        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor >= 0 and" +
                " Tareas.C_actividades = Tarea.C_tarea and Tarea.Folio = Alumno.Folio and Email_contacto <>'' and Tarea.c_tarea = "+c_tarea
        val orden = "C_actividad , Tarea.F_entrega"

        val columnas = arrayOf("Tarea.F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Email_Contacto", "Calificacion","Alumno.Nombre")
        return db.query("Tareas, Materia, Tipo_Actividad, Alumno, Tarea", columnas, condicion, null, null, null, orden)

    }

    public fun obternercalificacionReprobatoria(c_tarea: String): Cursor {

        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Valor >= 0 and" +
                " Tareas.C_actividades = Tarea.C_tarea and Tarea.Folio = Alumno.Folio and Telefono_contacto <> '' and Tarea.Calificacion < 70 and Tarea.c_tarea = "+c_tarea
        val orden = "C_actividad , Tarea.F_entrega"

        val columnas = arrayOf("Tarea.F_entrega", "Tareas.Nombre", "N_Materia", "Tipo_Actividad.Nombre", "Email_Contacto", "Calificacion","Alumno.Nombre", "Telefono_Contacto")
        return db.query("Tareas, Materia, Tipo_Actividad, Alumno, Tarea", columnas, condicion, null, null, null, orden)

    }
    public fun obtenerCalificacion(c_actividad: String): Cursor {
        val condicion = "Alumno.Folio = Tarea.Folio and C_tarea = "+c_actividad
        val columnas = arrayOf("C_tarea",  "Alumno.Folio", "Nombre", "Calificacion", "F_entrega", "N_lista", "sexo","f_baja", "f_registro")
        return db.query("Tarea, Alumno", columnas, condicion, null, null, null, "N_lista")
    }


    /*public fun obtenerCalificacion(c_actividad: String): Cursor {
        val condicion = "SELECT C_tarea,  Alumno.Folio, Nombre, Calificacion, F_entrega, N_lista, sexo\n" +
                "FROM alumno\n" +
                "LEFT JOIN tarea ON alumno.folio = tarea.folio AND tarea.c_tarea = $c_actividad\n" +
                "WHERE alumno.Situacion<>'BAJA'\n" +
                "ORDER BY alumno.N_lista ASC;"
        //val columnas = arrayOf("C_tarea",  "Alumno.Folio", "Nombre", "Calificacion", "F_entrega", "N_lista", "sexo")
        return db.rawQuery(condicion, null)
    }*/


    public fun obtenerBajodesempeño(fecha:String): Cursor {
        val condicion = "Tareas.Tipo = C_actividad and tareas.C_materia = Materia.C_materia and Tareas.C_actividades = Tarea.C_tarea and Tarea.Folio = Alumno.Folio and Calificacion < 60 and Tareas.F_entrega = '"+convertdate(fecha)+"'"
        val columnas = arrayOf("Alumno.Nombre", "Calificacion", "N_materia", "Tipo_Actividad.Nombre", "Tareas.Nombre","Tareas.F_entrega", "Tarea.F_entrega")
        error = condicion
        return db.query("Tarea, Alumno, Materia, Tipo_Actividad, Tareas", columnas, condicion, null, null, null, null)
    }

    public fun obtenerActividad(c_materia:String): Cursor {

        val columnas = arrayOf("C_actividad", "Nombre", "valor")
        error = c_materia
        return db.query("Tipo_Actividad", columnas, "c_materia = "+c_materia, null, null, null, null)


    }
    public fun obtenerMAterias(): Cursor {
        val columnas = arrayOf("C_materia", "N_materia", "Color", "Tipo", "Indice")
        return db.query("Materia", columnas, null, null, null, null, null)
    }

    public fun obtenerDetallesCalificaciones(folio: Int, c_materia: Int):Cursor{
        val sQuery = "Select Tareas.Nombre, Calificacion, Tareas.Porcentaje, Tipo_Actividad.Valor, Tipo_Actividad.Nombre as actividad, N_materia, Folio, Encuenta, Tareas.F_entrega, color " +
                "from Tarea, Tareas, Tipo_Actividad, Materia " +
                "where Tarea.C_Tarea = Tareas.C_actividades and Tareas.tipo = Tipo_Actividad.C_actividad " +
                "and Tareas.C_materia = Materia.C_materia and folio = $folio and Tareas.f_entrega between '2023-06-01' and '2023-06-31' and Encuenta = 0 and Materia.c_materia = $c_materia " +
                "order by Tareas.c_materia, Tareas.tipo, Tareas.Porcentaje"
        return db.rawQuery(sQuery, null)
    }

    public fun obtenerTodasActividadesDetalles(c_materia:Int, begin:String, end:String):Cursor{
        val sQuery = "Select Tareas.Nombre, Tareas.Porcentaje, " +
                "Tipo_Actividad.Valor, Tipo_Actividad.Nombre as actividad, " +
                "N_materia, Encuenta, Tareas.F_entrega, color , c_actividades " +
                "from Tareas, Tipo_Actividad, Materia " +
                "where Tareas.tipo = Tipo_Actividad.C_actividad " +
                "and Tareas.C_materia = Materia.C_materia and Tareas.f_entrega between '$begin' and '$end' and Encuenta = 0 and Materia.c_materia = $c_materia " +
                "order by Tareas.c_materia, Tareas.tipo, Tareas.Porcentaje "
        return db.rawQuery(sQuery, null)
    }

    public fun onCreatetableTareas() {
        try {
            val CrearTabla = "CREATE TABLE if not exists Tareas " +
                    "(C_actividades INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_materia Integer, Nombre TEXT, Descripcion TEXT, Porcentaje Integer, F_inicio Text," +
                    " F_entrega Text, F_tolerancia Text, tipo Integer, Terminada Integer, Encuenta Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia), FOREIGN KEY(tipo) REFERENCES Tipo_Actividad(C_Actividad))"
            db!!.execSQL(CrearTabla)
        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableActividad_Especial(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Actividad_Especial " +
                    "(C_Actividades Int, Folio Int, Calificacion Int, f_entrega Text, Terminada Int, " +
                    " FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_actividades) REFERENCES Tareas(C_actividades))"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableTarea(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Tarea " +
                    "(C_tarea INTEGER, " +
                    "Folio Integer, F_entrega String, Calificacion Integer, Porciento INTEGER, FOREIGN KEY(Folio) REFERENCES Alumno(Folio))"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }


    public fun borrarTareas(c_actividad: String):Boolean{
        try {
            val borrar = "Delete from Tareas where C_actividades = "+ c_actividad

            db!!.execSQL(borrar)
            error = "La actvidad se elimino correctamente"
            //error = borrar
            return  true
        }catch (Ex:Exception){
            //error = Ex.message.toString()
            error = "No se elimino la actividad"
            return false
        }

    }



    public  fun Calificar_Todos(c_tarea:String, folio:String, f_entrega:String, calificacion:String, porciento:String):Boolean {
        try {
            val calificar = "Insert into Tarea (C_tarea, Folio, F_entrega, Calificacion, Porciento)" +
                    "values ("+c_tarea+", "+folio+", '"+convertdate(f_entrega)+"', "+calificacion+", "+porciento+")"

            error = "Se califico a todos los alumnos con exito"
            db!!.execSQL(calificar)


            return true

        } catch (Ex: Exception) {

            error = Ex.message.toString()//"Hubo un error al calificar la actividad"
            return false
        }
    }

    public  fun Calificar_Todos_Especial(c_tarea:String, folio:String, f_entrega:String, calificacion:String):Boolean {
        try {
            val calificar = "Insert into Actividad_Especial (C_actividades, Folio, F_entrega, Calificacion)" +
                    "values ("+c_tarea+", "+folio+", '"+convertdate(f_entrega)+"', "+calificacion+")"

            error = "Se califico a todos los alumnos con exito"
            db!!.execSQL(calificar)
            return true
        } catch (Ex: Exception) {

            error = Ex.message.toString()//"Hubo un error al calificar la actividad"
            return false
        }
    }


    public  fun Calificar(c_tarea:String, folio:String, f_entrega:String, calificacion:String, porciento:String):Boolean {
        return try {
            val calificar = "Insert into Tarea (C_tarea, Folio, F_entrega, Calificacion, Porciento)" +
                    "values ("+c_tarea+", "+folio+", '"+convertdate(f_entrega)+"', "+calificacion+", "+porciento+")"

            error = "Se califico a todos los alumnos con exito"
            db!!.execSQL(calificar)
            true

        } catch (Ex: Exception) {
            error = Ex.message.toString()
            false
        }
    }

    public fun Posponer(f_entrega: String, C_tareas:String):Boolean{
        try{
            val posponer = "Update Tareas set F_entrega = '"+convertdate(f_entrega)+"' where C_actividades = "+ C_tareas

            db!!.execSQL(posponer)
            error = posponer
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }

    /*public fun Cero():Boolean{
        try{
            val posponer = "Update Tareas set Encuenta = 0 where C_actividades < 15"

            db!!.execSQL(posponer)
            error = posponer
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }*/

    public fun Terminarda(terminar:String,c_tareas:String):Boolean{
        try{
            val posponer = "Update Tareas set Terminada = "+terminar+" where C_actividades = "+ c_tareas

            db!!.execSQL(posponer)
            error = "Actividad Terminada con exito"
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }
    public fun borrarCalificaicionAlumno(c_tarea: String, folio:String){
        val borrar = "Delete from Tarea where c_tarea = "+ c_tarea+" and Folio = "+folio
        error = borrar
        db!!.execSQL(borrar)
    }


    public fun borrarCalificaicion(c_tarea: String){
        val borrar = "Delete from Tarea where c_tarea = "+ c_tarea

        db!!.execSQL(borrar)
    }

    public fun borrarCalificaicionEspecial(c_tarea: String){
        val borrar = "Delete from Actividad_Especial where c_Actividades = "+ c_tarea

        db!!.execSQL(borrar)
    }

    public fun deleteActivityEspecial(c_tarea:Int){
        val borrar = "Delete from Actividad_Especial where c_Actividades = $c_tarea"

        db!!.execSQL(borrar)
    }

    public fun deleteActivityEspecialByStudent(c_tarea:Int, folio: String){
        val borrar = "Delete from Actividad_Especial where c_Actividades = $c_tarea and Folio = $folio"
        error = borrar
        db!!.execSQL(borrar)
    }

    public fun deleteTable(){
        try {
            val BorrarTabla = "DROP TABLE IF EXISTS Tareas"
            db!!.execSQL(BorrarTabla)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }
    fun validardatos(nombre:String, tipo:String, materia:String, porcentaje: String):Boolean{
        if (nombre.length > 0 && isNumeric(tipo) && isNumeric(materia) && isNumeric(porcentaje)) return true
        else return false
    }

    private fun formatdate(date:String):String{
        var dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var dia = dateFormat.format(date)
        return dia
    }

    fun convertdate(fecha:String ):String{
        val dateString = fecha
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = inputFormat.parse(dateString)
        val outputString = outputFormat.format(date)
        return  outputString
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun isNumeric(cadena: String): Boolean {
        val resultado: Boolean
        resultado = try {
            cadena.toInt()
            true
        } catch (excepcion: NumberFormatException) {
            false
        }
        return resultado
    }


    }




