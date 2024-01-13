package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.control_escolar.Nombre_Escuela

data class MateriasBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var error = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Materia " +
                    "(C_materia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_materia TEXT, Indice Int, Color Integer, Tipo Integer)"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()

        }

    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Materia"
        db!!.execSQL(BorrarTabla)

    }

    public fun InsertMateria(nombre:String):Boolean{
        try {
            val Insertar = "Insert into Materia (N_materia, Indice, Color, Tipo)" +
                    " values ('"+nombre+"', 0, 0, 0)"

            db!!.execSQL(Insertar)
            //error = Insertar
            error = "Se registro correctamente la materia"
            return true

        }catch (Ex:Exception){
            error = Ex.message.toString()

            return  false
        }

    }

    public fun ActualizarMateria(nombre:String, c_materia:String):Boolean{
        try {
            val Actualizar = "Update Materia set N_materia = '"+nombre+"' where C_materia = "+c_materia
            db!!.execSQL(Actualizar)

            error = "Se actualizo el nombre  correctamente  de la materia"
            return true

        }catch (Ex:Exception){
            error = Ex.message.toString()

            return  false
        }

    }

    public  fun ColorMateria(color:String, c_materia:String):Boolean{
        try {
            val actualizar = "Update  Materia set Color = "+color+"  where C_materia  = " + c_materia

            db!!.execSQL(actualizar)
            error = "Se actualizaron correctamente los datos de la materia"
            return true

        }catch (Ex:Exception){
        error = Ex.message.toString()
            //error = "Hubo un error al actualizar los datos del alumno"
        return  false
    }
    }

    public fun DiasMateria(dia:String, c_materia:String,hora_i:String):Boolean {
        try {
            val Insertar = "Insert Into MateriaDia(Dia, C_materia, Hora_i, Hora_f) values ('"+dia+"', "+c_materia+", '"+hora_i+"', '')"
            db!!.execSQL(Insertar)
            error = "Se actualizarón los dias de la materia"
            return true
        } catch (Ex: Exception) {
            //error = Ex.message.toString()
            error = "Hubo un error al actualizar los dias de la materia"
            return false
        }
    }

    public  fun BorrarDias(c_materia:String):Boolean{
        try {
            val borrar = "Delete from MateriaDia where c_materia = " + c_materia
            db!!.execSQL(borrar)
            error = "Se eliminaron todos los dias"
            return  true
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }

    }


    public fun obtenerDias(c_materia:String): Cursor {
        val columnas = arrayOf( "Dia", "Hora_i")
        return db.query("MateriaDia", columnas, "c_materia = "+ c_materia, null, null, null, "c_dia")
    }


    public fun obtenerMAterias(): Cursor {
        val columnas = arrayOf("C_materia", "N_materia", "Color", "Tipo", "Indice")
        return db.query("Materia", columnas, null, null, null, null, "C_materia")
    }



    public fun obtenerHorario(dia:String): Cursor {
        val condicion = "Materia.c_materia = MateriaDia.c_materia and Dia = '$dia'"
        val columnas = arrayOf( "N_materia", "Dia")
        return db.query("MateriaDia, Materia", columnas, condicion, null, null, null, "c_dia")
    }





    public fun obtenerAll(): Cursor {

        val columnas = arrayOf("C_materia", "N_materia", "Color", "Tipo", "Indice")
        return db.query("Materia", columnas, null, null, null, null, null)
    }

    public  fun borrarMateria(c_materia:String):Boolean {
        try {
            val eliminar = "Delete from Materia  where C_materia  = " + c_materia

            db!!.execSQL(eliminar)
            error = "Se elimino la materia correctamente"

            return true

        } catch (Ex: Exception) {

            error = "Hubo un error al eliminar el la materia"
            return false

        }
    }

    public fun deleteTable(){
        try {
            val BorrarTabla = "DROP TABLE IF EXISTS Materia"
            db!!.execSQL(BorrarTabla)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }

    public fun onCreatetableMateria(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Materia " +
                    "(C_materia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_materia TEXT, Indice Int, Color Integer, Tipo Integer)"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }


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