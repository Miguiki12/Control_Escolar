package com.example.control_escolar

import BDLayer.SettingsBD
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class BD_Escuelas(context: Context, nombrebd:String):SQLiteOpenHelper(
    context, nombrebd, null, 1)  {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var error = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }

    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Escuela " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Grado TEXT, Grupo TEXT, Turno TEXT, ciclo TEXT,Status ITEGER, Domicilio Text, Colonia Text, Municipio Text, Telefono Text, Tipo Text, CCT Text, Foto Int)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Escuela"
        db!!.execSQL(BorrarTabla)
    }

    public fun onCreatetableEscuela(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Escuela " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Grado TEXT, Grupo TEXT, Turno TEXT, ciclo TEXT,Status ITEGER, Domicilio Text, Colonia Text, Municipio Text, Telefono Text, Tipo Text, CCT Text, Foto Int)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }

    }
    public fun onCreatetableAlumno(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Alumno " +
                    "(Folio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_lista Integer, Nombre TEXT, Apellidop TEXT, Apellidom TEXT, Sexo Integer, Domicilio TEXT, Colonia TEXT, Municipio Text, CP Text, Telefono Text, Email Text, "+
                    "Entidad_Federativa Text, Curp Text, F_nacimiento Text, Situacion Text, Condicionado Integer, Edad Integer, Estadistica Integer, Foto BLOB, Indigena Integer, " +
                    "Discapacidad Integer, Especifique Text, " +
                    "Email_Contacto Text, Tutor Text, Telefono_contacto Text, Ocupacion_contacto Text, Estudios_contacto Text, Tutor_2 Text, Telefono_contacto_2 Text, " +
                    "Email_Contacto_2 Text, Ocupacion_contacto_2 Text, Estudios_contacto_2 Text, F_registro Text, F_baja Text, Matricula Text)"

            db!!.execSQL(CrearTabla)
            val crearTriggerEliminarTarea = "CREATE TRIGGER IF NOT EXISTS eliminar_registros_alumnos " +
                    "AFTER DELETE ON Alumno " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    DELETE FROM Tarea WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Asistencia WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Calificaciones WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Calificacionestemp WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Calificaciones_Finales WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Participacion WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Reporte WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Justificacion WHERE Folio = OLD.Folio; " +
                    "    DELETE FROM Actividad_especial WHERE Folio = OLD.Folio; " +
                    "END;"

            db!!.execSQL(crearTriggerEliminarTarea)
            error = CrearTabla

        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }
    public fun onCreatetableMateria(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Materia " +
                    "(C_materia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_materia TEXT, Indice Int, Color Integer, Tipo Integer)"
            db!!.execSQL(CrearTabla)

            val crearTriggerEliminarTareas = "CREATE TRIGGER IF NOT EXISTS Materia " +
                    "AFTER DELETE ON Mateteria " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    DELETE FROM Tareas WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM MateriaDia WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM Tipo_Actividad WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM Asistencia WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM Reporte WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM Parciales WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM Participacion WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM Justificacion WHERE C_materia = OLD.C_materia; " +
                    "    DELETE FROM Configuracion WHERE C_materia = OLD.C_materia; " +

                    "END;"

            db!!.execSQL(crearTriggerEliminarTareas)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableMateriaDia(){
        try {
            val CrearTabla = "CREATE TABLE if not exists MateriaDia " +
                    "(C_dia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Dia TEXT, C_materia Integer, Hora_i Text, Hora_f Text, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableTipo_Actividad(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Tipo_Actividad " +
                    "(C_actividad INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Valor Integer, Diaria Integer, Calificar Integer, C_materia Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia)ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
            val crearTriggerEliminarTarea = "CREATE TRIGGER IF NOT EXISTS eliminar_registros_Tipo_Actividad " +
                    "AFTER DELETE ON Tipo_Actividad " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    DELETE FROM Tareas WHERE tipo = OLD.C_actividad; " +
                    "END;"

            db!!.execSQL(crearTriggerEliminarTarea)

        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableTareas(){
        try {

            val CrearTabla = "CREATE TABLE if not exists Tareas " +
                    "(id_tareas INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "C_actividades INTEGER, " +
                    "C_materia Integer, Nombre TEXT, Descripcion TEXT, Porcentaje Integer, F_inicio Text, " +
                    "F_entrega Text, F_tolerancia Text, tipo Integer, Terminada Integer, Encuenta Integer, " +
                    "FOREIGN KEY(C_materia) REFERENCES Materia(C_materia), FOREIGN KEY(tipo) REFERENCES Tipo_Actividad(C_Actividad) ON DELETE CASCADE ON UPDATE CASCADE)"

            db!!.execSQL(CrearTabla)

            val crearTriggerEliminarTarea = "CREATE TRIGGER IF NOT EXISTS eliminar_registros_relacionados " +
                    "AFTER DELETE ON Tareas " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    DELETE FROM Tarea WHERE C_tarea = OLD.C_actividades; " +
                    "END;"

            db!!.execSQL(crearTriggerEliminarTarea)

            /*db?.execSQL("CREATE TRIGGER actualizar_campo_relacionado AFTER INSERT ON Tareas " +
                    "BEGIN " +
                    "UPDATE Tareas SET C_actividades = id_tareas WHERE id_tareas = id_tareas; " +
                    "END;")*/

        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableTarea(){
        try {

            val CrearTabla = "CREATE TABLE if not exists Tarea " +
                    "(C_tarea INTEGER, " +
                    "Folio Integer, F_entrega String, Calificacion Integer, Porciento INTEGER, FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(C_tarea) REFERENCES Tareas(C_actividades) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableAsistencia(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Asistencia " +
                    "(C_asistencia Text, Dia Text, " +
                    "Folio Integer, C_materia Integer, Fecha TEXT, Asistencia Integer, Sugerencias TEXT, Retardo Integer, "+
                    "Distraido INTEGER, Entrega_trabajos TExt, Argumentacion Text, Conducta text, Uniforme Text, Aseo Text, Utiles  Text, FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }
    public fun onCreatetableCalificaciones(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Calificaciones " +
                    "(C_calificacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Calificacion Float, Periodo Integer,  FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }
    public fun onCreatetableCalificacionestemp(){
        try {
            db!!.execSQL("PRAGMA foreign_keys = ON")
            val CrearTabla = "CREATE TABLE if not exists Calificacionestemp " +
                    "(C_calificacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_parcial Integer, Folio Integer, Calificacion Integer, Observaciones Text, FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(C_parcial) REFERENCES Parciales(C_parcial) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
            db!!.execSQL("PRAGMA foreign_keys = OFF")
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }

    }
    public fun onCreatetableParciales(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Parciales " +
                    "(C_parcial INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_materia Integer, F_inicio Text, F_fin Text, Periodo Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }
    public fun onCreatetableCalificacines_Finales(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Calificaciones_Finales" +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Total Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableReportes(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Reporte " +
                    "(C_reporte INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Fecha Text, tipo Text, Descripcion Tex, "+
                    "Mostrar Integer, Puntaje Integer, Lugar Text, C_materia Integer, "+
                    "F_inicio Text, F_fin Text, Correctivo Text, Firma Text, Fecha_firma Text, "+
                    "Articulo Text, Articulo_vinculado text, Indidencia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreateTableParciales(){
        try {

            val createTable = "CREATE TABLE if not exists Parciales " +
                    "(C_parcial INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_materia Integer, F_inicio Text, F_fin Text, Periodo Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(createTable)
            val crearTriggerEliminarTarea = "CREATE TRIGGER IF NOT EXISTS eliminar_calificacionestemp " +
                    "AFTER DELETE ON Parciales " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    DELETE FROM Calificacionestemp WHERE C_parcial = OLD.C_parcial; " +
                    "END;"

            db!!.execSQL(crearTriggerEliminarTarea)

        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableParticipacion(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Participacion " +
                    "(C_Participacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Fecha Text, Descripcion Text, Mostrar Integer, Puntaje Integer, C_materia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableJustificacion(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Justificacion " +
                    "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Folio Integer, F_inicio Text, F_fin Text, Tipo Integer, C_reporte Integer, C_materia Interger, " +
                    "FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableConfiguracion(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Configuracion " +
                    "(C_materia Integer, Dia Integer, Mes Integer, Anticipacion Integer,  " +
                    "Decimales Integer, Condicionado Boolean,  Suspendido Boolean," +
                    " Cal_menor Integer, Email Text, Contrasena Text, Email_direccion Text, N_maestro Text, Estadistica1 Text, Estadistica2 Text, Estadistica3 Text,  Estadistica4 Text, Estadistica5 Text,  F_Estadistica Text, " +
                    " FOREIGN KEY(C_materia) REFERENCES Materia(C_materia) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableavisosPendientes(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Pendientes " +
                    "(Tipo Text, Realizado Boolean, Fecha Text, sQuery Text)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    public fun onCreatetableActividad_Especial(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Actividad_Especial " +
                    "(C_Actividades Int, Folio Int, Calificacion Int, f_entrega Text, Terminada Int, " +
                    " FOREIGN KEY(Folio) REFERENCES Alumno(Folio) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(C_actividades) REFERENCES Tareas(C_actividades) ON DELETE CASCADE ON UPDATE CASCADE)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }


    public fun OnCreateAll(context: Context){
        try {
            onCreatetableEscuela()
            onCreatetableAlumno()
            onCreatetableAsistencia()
            onCreatetableCalificaciones()
            onCreatetableCalificacines_Finales()
            onCreatetableConfiguracion()
            onCreatetableMateria()
            onCreatetableMateriaDia()
            onCreatetableParticipacion()
            onCreatetableReportes()
            onCreatetableTareas()
            onCreatetableTarea()
            onCreatetableTipo_Actividad()
            onCreatetableJustificacion()
            onCreatetableavisosPendientes()
            onCreateTableParciales()
            onCreatetableCalificacionestemp()
            onCreatetableActividad_Especial()

            SettingsBD(context).newSettings()
        }catch (Ex:Exception){
            Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    public fun getTable(table:String):Cursor{
        val sQuery = "Select * from $table"
        return db.rawQuery(sQuery, null)
    }

    public fun deletetable(nametable:String){
        val BorrarTabla= "DROP TABLE if exists "+nametable
        db!!.execSQL(BorrarTabla)

    }

    public fun insertTable(sQuery:String){
        try {
            db!!.execSQL(sQuery)
            error = "$databaseName\n  $sQuery"
        }catch (Ex:Exception){error = Ex.message.toString()}

    }
    public fun foundNameTable(nameTable:String):Int{
        var begin = 1
        when (nameTable) {
            "Alumno" -> {

            }"Materia" -> {

            }"TipoActividad" -> {

            }"Asistencia" -> {

                begin = 0
            }"Tareas" -> {
                begin = 1
            }"Actividad_Especial" -> {

                begin = 0
            }"Tarea" -> {
                begin = 0
            }
            "MateriaDia" -> {

            }"Parciales" -> {

            }"Participacion" -> {

            }"Reporte" -> {

            }"Calificacionestemp" -> {

            }"Justificacion" -> {

            }"Configuracion" -> {

                begin = 0
            }"Calificaciones" -> {

            }
            "Calificaciones_Finales" -> {

            }
            "Pendientes" -> {

                begin = 0
            }

            else -> {
                // Acciones por defecto si el nombre de la tabla no coincide con los casos anteriores
                println("Tabla no reconocida: $nameTable")
            }
        }
        return  begin
    }





}