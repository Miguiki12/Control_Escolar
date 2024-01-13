package BDLayer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.control_escolar.Nombre_Escuela

data class JustificarBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var iFolio = 0
    var dtF_fin = ""
    var dtF_inicio = ""
    var iTipo = 0
    var iC_reporte = 0
    var iC_materia = 0
    var error = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Justificacion " +
                    "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Folio Integer, F_inicio Text, F_fin Text, Tipo Integer, C_reporte Integer, C_materia Interger, " +
                    "FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()

        }

    }


    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Justificacion"
        db!!.execSQL(BorrarTabla)
    }

    public fun deletetable(){
        val BorrarTabla = "DROP TABLE IF EXISTS Justificacion"
        db!!.execSQL(BorrarTabla)
    }

    public fun Createtable(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Justificacion " +
                    "(Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " Folio Integer, F_inicio Text, F_fin Text, Tipo Integer, C_reporte Integer, C_materia Interger, " +
                    "FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()

        }
    }


    public fun Insertjustify():Boolean{
        try {
                val Insertar = "Insert into Justificacion (Folio, F_inicio, F_fin, Tipo, C_reporte, C_materia)" +
                        " values ("+iFolio+",  '"+dtF_inicio+"', '"+dtF_fin+"',"+iTipo+", "+iC_reporte+", "+iC_materia+")"
                db!!.execSQL(Insertar)
                error = Insertar//"Se registro correctamente "
                return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }

    public fun Insertdiscontinued():Boolean{
        try {
            val Insertar = "Insert into Justificacion (Folio, F_inicio, F_fin, Tipo, C_reporte, C_materia)" +
                    " values ("+iFolio+",  '"+dtF_inicio+"', '"+dtF_fin+"', "+iTipo+", "+iC_reporte+", "+iC_materia+")"
            db!!.execSQL(Insertar)
            error = Insertar//"Se registro correctamente "
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }


    public fun getAlljustify(F_inicio:String): Cursor? {
        var cursor : Cursor? = null
        try {
            val condicion ="'"+F_inicio+"' between Justificacion.F_inicio and Justificacion.F_fin"
            //val condicion = "F_fin between '"+F_inicio+"' and '"+F_inicio+"'"
            val columnas = arrayOf("Folio", "F_inicio", "F_fin", "Tipo", "C_reporte", "C_materia")
            error = condicion
            cursor = db.query("Justificacion", columnas, condicion, null, null, null, null)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }
        return cursor
    }

    fun getHistorialByStudent(folio:Int):Cursor{
        val sQuery = "Select Id, folio, F_inicio, F_fin, tipo " +
                "from Justificacion " +
                "where folio = $folio"
        return db.rawQuery(sQuery, null)
    }


    @SuppressLint("SuspiciousIndentation")
    public  fun deletejustify(Folio: String, F_inicio: String, Tipo: String):Boolean {
        try {
            val eliminar = "Delete from Justifiacion where Folio = $Folio and F_inicio = '$F_inicio' and Tipo = $Tipo"
                db!!.execSQL(eliminar)
            error = "Se elimino la Asistencia correctamente"
            return true
        } catch (Ex: Exception) {
            error = "Hubo un error al eliminar el la Asistencia"
            return false
        }
    }
    fun deleteJustific(id:Int):Boolean{
       return try {
           val sQuery = "Delete from Justificacion where Id = $id"
           db!!.execSQL(sQuery)
           true
       }catch (Ex:Exception){
           false
       }
    }

    public  fun borrarAsistencia(fecha:String, c_materia: String):Boolean {
        try {
            val eliminar = "Delete from Asistencia  where C_actividad  = '" + fecha+"' and c_materia =  " +c_materia

            db!!.execSQL(eliminar)
            error = "Se elimino la Asistencia correctamente"

            return true

        } catch (Ex: Exception) {

            error = "Hubo un error al eliminar el la Asistencia"
            return false

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