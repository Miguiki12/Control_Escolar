package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.control_escolar.Nombre_Escuela

data class PendienteBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var error = ""
    var sTipo = ""
    var bRealizado = false
    var sFecha = ""
    var sQuery = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Pendientes " +
                    "(Tipo Text, Realizado Boolean, Fecha Text, sQuery Text)"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()

        }

    }


    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Pendientes"
        db!!.execSQL(BorrarTabla)
    }


    public fun Insertar_pending():Boolean{
        try {
                val Insertar = "Insert into Pendientes (Tipo, Realizado, Fecha, sQuery)" +
                    " values ('"+sTipo+"', "+bRealizado+", '"+sFecha+"', '"+sQuery+"')"
                db!!.execSQL(Insertar)
                error = Insertar
                return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }
    public fun Insert_check_today():Boolean{
        try {
            val Insertar = "Insert into Pendientes (Tipo, Realizado, Fecha, sQuery)" +
                    " values ('Asistencia', True, '"+sFecha+"', '')"
            db!!.execSQL(Insertar)
            error = Insertar
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }


    public fun get_asistent(fecha:String, Tipo:String): Cursor {

        try {
            val condicion = "Tipo = '"+Tipo+"' and Fecha  = '"+fecha+"'"
            val columnas = arrayOf("Tipo", "Realizado", "Fecha", "sQuery")
            return db.query("Pendientes", columnas, condicion, null, null, null, null)
        }catch (Ex:Exception){
            val condicion = "Tipo = '"+Tipo+"' and Fecha  = '"+fecha+"'"
            val columnas = arrayOf("Tipo", "Realizado", "Fecha", "sQuery")
            error = Ex.message.toString()
            return db.query("Pendientes", columnas, condicion, null, null, null, null)
        }

    }
    public fun get_check_asistence(fecha:String):Boolean  {
        try {
            val condicion = "Tipo = 'Asistencia' and Realizado = True and Fecha  = '"+fecha+"'"
            val columnas = arrayOf("Tipo", "Realizado", "Fecha", "sQuery")
            error = condicion
            val c = db.query("Pendientes", columnas, condicion, null, null, null, null)
            return c.moveToFirst()
        }catch (Ex:Exception){

            error = Ex.message.toString()
            return false
        }
    }

    public fun  check_pending_asistent(fecha:String){
        val update = "update Pendientes set Realizado = True where Tipo = 'Asistencia' and Fecha =  '$fecha'"
        db!!.execSQL(update)
    }

    public  fun borrarAsistencia(fecha:String):Boolean {
        try {
            val eliminar =
                "Delete from Pendientes where Fecha  = '" + fecha+"'"

            db!!.execSQL(eliminar)
            error = "Se elimino la Asistencia correctamente"
            return true
        } catch (Ex: Exception) {

            error = "Hubo un error al eliminar el la Asistencia"
            return false
        }
    }






    public fun deleteTable(){
        try {
            val BorrarTabla = "DROP TABLE IF EXISTS Pendientes"
            db!!.execSQL(BorrarTabla)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }



    public fun onCreatetable(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Pendientes " +
                    "(Tipo Text, Realizado Boolean, Fecha Text, sQuery Text)"
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