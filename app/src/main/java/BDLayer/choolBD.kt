package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap

data class schoolBD(var context: Context,var nameSchool:String): SQLiteOpenHelper(
    context, nameSchool, null, 1) {
    private val db: SQLiteDatabase = this.writableDatabase
    private var values: ContentValues
    public var sName = "0"
    public var sGrado = "0"
    public var sGrupo = "0"
    public var sTurno = "0"
    public var sCiclo = "0"
    public var sAlias = ""
    public var iStatus = 0
    public var sDomicilio = ""
    public var sColonia = ""
    public var sMunicipio = ""
    public var sTelefono = ""
    public var sTipo = ""
    public var sCct = ""
    public lateinit var Foto : Bitmap
    var error = ""

    init {
        values = ContentValues().apply {
            put("Nombre", sName)
            put("Grado", sGrado)
            put("Grupo", sGrupo)
            put("Turno", sTurno)
            put("ciclo", sCiclo)
            put("Status", iStatus)
            put("Domicilio", sDomicilio)
            put("Colonia", sColonia)
            put("Municipio", sMunicipio)
            put("Telefono", sTelefono)
            put("Tipo", sTipo)
            put("CCT", sCct)
            //put("Foto", R.drawable.mi_imagen)
        }
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Escuela " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Grado TEXT, Grupo TEXT, Turno TEXT, ciclo TEXT,Status ITEGER, Domicilio Text, Colonia Text, Municipio Text, Telefono Text, Tipo Text, CCT Text, Foto Int)"

            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }


    public fun createTable(){
        val CrearTabla = "CREATE TABLE if not exists Escuela " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Nombre TEXT, Grado TEXT, Grupo TEXT, Turno TEXT, ciclo TEXT,Status ITEGER, Domicilio Text, Colonia Text, Municipio Text, Telefono Text, Tipo Text, CCT Text, Foto Int)"
        db!!.execSQL(CrearTabla)
    }

    public fun insertShortDataSchool(){

        values = ContentValues().apply {
            put("Nombre", sName)
            put("Grado", sGrado)
            put("Grupo", sGrupo)
            put("Turno", sTurno)
            put("ciclo", sCiclo)
            put("Status", iStatus)
        }
        db.insert("Escuela", null, values)
    }

    public fun insertDataSchool():Boolean{
      return  try {
            values = ContentValues().apply {
                put("Nombre", sName)
                put("Grado", sGrado)
                put("Grupo", sGrupo)
                put("Turno", sTurno)
                put("ciclo", sCiclo)
                put("Status", iStatus)
                put("Domicilio", sDomicilio)
                put("Colonia", sColonia)
                put("Municipio", sMunicipio)
                put("Telefono", sTelefono)
                put("Tipo", sTipo)
                put("CCT", sCct)
            }
            db.insert("Escuela", null, values)
            error = "Se guardaron los datos correctamente"
          true
        }catch (Ex:Exception){

            error = "Error al guardar los dats "+ Ex.message.toString()
            false
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Escuela"
        db!!.execSQL(BorrarTabla)
    }

    public fun deleteSchool(){
        val sQuery = "Delete from Escuela"
        db!!.execSQL(sQuery)
    }

    public fun deletetable(){
        val BorrarTabla = "DROP TABLE IF EXISTS Escuela"
        db!!.execSQL(BorrarTabla)
    }




    public fun getAllData(): Cursor {
            val sQuery = "Select * From Escuela"
            return db.rawQuery(sQuery, null)
    }

    public fun getDataSchool():Cursor{
        val sQuery = "Select Nombre, grado, grupo, turno, ciclo,domicilio, Colonia, municipio, " +
                "telefono, cct " +
                "from Escuela "
        return db.rawQuery(sQuery, null)
    }

    public fun getNameTeacher():String{
        val sQuery = "Select N_maestro " +
                "from Configuracion "
        val cursor = db.rawQuery(sQuery, null)
        return if (cursor.moveToFirst()) cursor.getString(0)
        else ""
    }


    fun validations(dia:String, mes:String, decimales:String):Boolean{
        return !(isNumeric(dia.toString()) && isNumeric(mes.toString()) && isNumeric(decimales.toString()))
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