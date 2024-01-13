package com.example.control_escolar



import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class BD_Indice(var context: Context):SQLiteOpenHelper(
    context, "Indice.db", null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    public var Id = 0
    public var Nombre = ""
    public var Grado = ""
    public var Grupo = ""
    public var Turno = ""
    public var Ciclo = ""
    public  var error = ""
    public var status = 0
    public  var sQuery = ""
    public var sesion = "0"
    public var escuelas = ArrayList<Cursor>()


    companion object {

        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Indice"
    }

    init {
        db = this.writableDatabase
        values = ContentValues()

    }


    public override fun onCreate(db: SQLiteDatabase?) {

        try {
            val CrearTabla = "CREATE TABLE if not exists Escuelas " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Alias TEXT, Grado TEXT, Grupo TEXT, Turno TEXT, ciclo TEXT, Status INTEGER, c_maestro Integer, tipo_escuela Integer)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }

    public fun onCreatetableEscuelas(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Escuelas " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Alias TEXT,Grado TEXT, Grupo TEXT, Turno TEXT, ciclo TEXT, Status INTEGER, c_maestro Integer, tipo_escuela Integer)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }



    }
    public fun onCreatetableMaestro(){
        try {

            val CrearTabla = "CREATE TABLE  if not exists Maestro " +
                    "(idMaestro INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT, apellidos TEXT, direccion TEXT, colonia TEXT, celular TEXT,  email TEXT, password TEXT, recordar INTEGER)";
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }

    public fun deletetable(){
        val BorrarTabla= "DROP TABLE if exists Escuelas"
        db!!.execSQL(BorrarTabla)

    }
    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Escuelas"
        db!!.execSQL(BorrarTabla)

    }

    fun borrartabla(nombre:String){
        val BorrarTabla = "DROP TABLE IF EXISTS " + nombre
        db!!.execSQL(BorrarTabla)

    }


    fun updateEscuelas(Nombre: String, Alias:String, Grado: String, Grupo:String, Turno:String, Ciclo:String, id:String ):Boolean{
        try {
            val  updatetabla = "Update Escuelas set Nombre = '"+Nombre+"', Alias = '$Alias', Grado = '"+Grado+"',  Grupo = '"+Grupo+"', Turno = '"+Turno+"', Ciclo = '"+Ciclo+"' where id = "+id
            this.db!!.execSQL(updatetabla)
            //error = "Los datos de la escuela se actualizaron exitosamente"
            error = updatetabla
            return true
        }
        catch (Ex: Exception){
            error = Ex.message.toString()
            return  false
        }
    }


    fun insertarDatosEscuela(Nombre: String, Alias: String, Grado: String, Grupo:String, Turno:String, Ciclo:String,c_maestro: Int, tipo:Int):Boolean {
        try {
            val datos = "Insert into Escuelas(nombre, Alias, Grado, Grupo, Turno, Ciclo, Status, c_maestro, tipo_escuela)  " +
                    "values('"+Nombre +"', '"+Alias+"', '"+Grado+"', '"+ Grupo+"', '"+Turno+"','"+Ciclo+"', 0, "+c_maestro+", "+tipo+")"
            error = "Escuela registrada exitosamente"
            this.db!!.execSQL(datos)
            return true
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }


    fun insertarDatosMaestro(nombre: String, apellidos: String, direccion:String, colonia:String, celular:String, email: String, password:String):Boolean {
        return try {
            val datos = "Insert into Maestro(nombre, apellidos, direccion, colonia, celular, email, password, recordar)  " +
                    "values('"+nombre +"', '"+apellidos+"', '"+ direccion+"', '"+colonia+"','"+celular+"', '"+email+"', '"+password+"', 0)"
            this.db!!.execSQL(datos)
            true
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
            false
        }
    }


    fun updateSesionMaestro(nombre:String):Boolean{
        return try {
            val datos = "Update Maestro set recordar = 0 where email = '"+nombre+"'"

            error = datos
            this.db!!.execSQL(datos)
            true
        } catch (Ex:Exception){
            error = Ex.message.toString()
            false
        }


    }
    fun updateRecordarMaestro(email:String):Boolean{
        try {
            val datos = "Update Maestro set recordar = 1 where email = '"+email+"'"

            error = datos
            this.db!!.execSQL(datos)
            return true
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }
    public fun updateCtecherEschool(alias:String, c_teacher:Int):Boolean{
        return try {
            val datos = "Update Escuelas set c_maestro = $c_teacher where Alias = '$alias'"
            error = datos
            this.db!!.execSQL(datos)
            true
        } catch (Ex:Exception){
            error = Ex.message.toString()
            false
        }
    }

    fun deleteEscuela(Nombre: String, Grado:String, Grupo:String):Boolean
    {
        try {
            val datos = "Delete from Escuelas where Nombre = '"+Nombre+"' and Grado =  '"+Grado+"' and Grupo = '"+Grupo+"'"
            this.db!!.execSQL(datos)
            error = "Escuela borrada exitosamente"
            return  true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }

    fun deleteEscuela(Clave: String):Boolean
    {
        try {
            val datos = "Delete from Escuelas where id = "+Clave
            this.db!!.execSQL(datos)
            error = "Escuela borrada exitosamente"
            error = datos
            return  true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false

        }finally {
            //db!!.close()
        }
    }

    public fun getLastCteacher():Int{
        var lastIdTeacher = 0
        val consulta = "SELECT MAX(idMaestro) FROM Maestro"
        val cursor = db.rawQuery(consulta, null)
        if (cursor.moveToFirst()) {
            lastIdTeacher = cursor.getInt(0)
            // Haz algo con la última clave primaria
        }
        cursor.close()
        return lastIdTeacher
    }

    fun obtenerdatosEscuelas(alias: String): String {
        val columnas = arrayOf("id", "Nombre", "Grado", "Grupo","Turno", "Ciclo")
        val c = db.query("Escuelas", columnas, "Alias = '"+alias+"'", null, null, null, "id Asc")
        if (c.moveToFirst()) {
            Id = c.getString(0).toInt()
            this.Nombre = c.getString(1)
            Grado = c.getString(2)
            Grupo = c.getString(3)
            Turno = c.getString(4)
            Ciclo = c.getString(5)
            c.moveToLast()
            return c.getString(0)
        }
        else return "No hay datos"
        //return ""
    }

    fun obtenerdatosMaestro(alias: String): Cursor {
            val columnas = arrayOf("IdMaestro", "Maestro.Nombre", "Apellidos", "Direccion","Colonia", "Celular", "Email", "Password", "recordar")
            return db!!.query("Maestro , Escuelas", columnas, "Alias = '$alias'", null, null, null, null)
    }

    fun obtenerTodosMaestro(): Boolean {

        try {
            val columnas = arrayOf("Idclientes", "Nombre", "Apellidos", "Direccion","Colonia", "Celular", "Email", "Password", "recordar")
            val c = db.query("Maestro", columnas, null, null, null, null, null)
            if (c.moveToFirst()) {
                this.Nombre = c.getString(1)
                this.Id = c.getString(0).toInt()
                this.sesion = c.getString(8)
                return true
            }
            else return false

        }catch (Ex:Exception){
            return  false
        }
    }

    public fun obtenerTodasEscuelas(idmaestro:String): Cursor {

        val columnas = arrayOf("id", "Nombre", "Grado", "Grupo", "Turno", "Ciclo", "Status", "c_maestro", "tipo_escuela")
        return db.query("Escuelas", columnas, "c_maestro = "+ idmaestro, null, null, null, null)
    }
    public fun obtenerTodasEscuelas(): Cursor {
        val columnas = arrayOf("id", "Nombre", "Grado", "Grupo", "Turno", "Ciclo", "Status", "c_maestro", "tipo_escuela", "Alias")
        return db.query("Escuelas", columnas, null, null, null, null, "Turno")
    }

    public fun isnotrepeated(alias:String):Boolean{
        val columnas = arrayOf("id", "Nombre", "Grado", "Grupo", "Turno", "Ciclo", "Status", "c_maestro", "tipo_escuela", "Alias")
        if(db.query("Escuelas", columnas, "Alias = '$alias'", null, null, null, "Turno").moveToFirst())
            return true
        else return false

    }

    public fun borrar_BD(bd:String){
        try {
            context.deleteDatabase(bd)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    fun renameDatabaseFile(context: Context, dbName: String, newDbName: String): Boolean {
        val dbFile = context.getDatabasePath(dbName)
        if (dbFile.exists()) {
            val newDbFile = File(dbFile.parent, newDbName)
            return dbFile.renameTo(newDbFile)
        }
        return false
    }

    fun backupDatabase(backupPath: String) {
        try {
            val inputFile = context.getDatabasePath(DATABASE_NAME)
            val outputFile = File(backupPath)

            val input = FileInputStream(inputFile).channel
            val output = FileOutputStream(outputFile).channel

            output.transferFrom(input, 0, input.size())

            input.close()
            output.close()

            // ¡Respaldo exitoso!
        } catch (e: IOException) {
            e.printStackTrace()
            // Manejar la excepción en caso de un error
        }
    }

}
