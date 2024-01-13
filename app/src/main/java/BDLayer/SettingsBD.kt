package BDLayer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.control_escolar.Nombre_Escuela

data class SettingsBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var iC_materia = 0
    var iDia = 0
    var iMes = 0
    var iAnticipacion = 0
    var iDecimales = 0
    var iCondicionado = 0
    var ias_Suspendido = 0
    var iCal_menor = 0
    var sEmail = ""
    var sContrasena = ""
    var sEmail_direccion = ""
    var sN_maestro = ""
    var error = ""
    var sEstadistica1 = ""
    var sEstadistica2 = ""
    var sEstadistica3 = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Configuracion " +
                    "(C_materia Integer, Dia Integer, Mes Integer, Anticipacion Integer,  Decimales Integer, Condicionado Boolean,  as_Suspendido Boolean, " +
                    "Cal_menor Integer, Email Text, Contrasena Text, Email_direccion Text, N_maestro Text, Estadistica1 Text, Estadistica2 Text, Estadistica3 Text, " +
                    "FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }

    public fun Createtable(){
        val CrearTabla = "CREATE TABLE if not exists Configuracion " +
                "(C_materia Integer, Dia Integer, Mes Integer, Anticipacion Integer,  Decimales Integer, Condicionado Boolean,  as_Suspendido Boolean, Cal_menor Integer, Email Text, Contrasena Text, Email_direccion Text, N_maestro Text, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
        db!!.execSQL(CrearTabla)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Configuracion"
        db!!.execSQL(BorrarTabla)
    }

    public fun deletetable(){
        val BorrarTabla = "DROP TABLE IF EXISTS Configuracion"
        db!!.execSQL(BorrarTabla)
    }

    public fun newSettings():Boolean{
        try {
                val Insertar = "Insert into Configuracion (C_materia, Dia, Mes, Anticipacion, Decimales, Condicionado, as_Suspendido, Cal_menor, Email, Contrasena, Email_direccion, N_maestro, Estadistica1, Estadistica2, Estadistica3)" +
                        " values ($iC_materia, $iDia,  $iMes, $iAnticipacion, $iDecimales, $iCondicionado, $ias_Suspendido, $iCal_menor, '$sEmail', '$sContrasena', '$sEmail_direccion', '$sN_maestro', '$sEstadistica1', '$sEstadistica2', '$sEstadistica3')"
                db!!.execSQL(Insertar)
                error = "Se registro correctamente "
                return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }


    public fun getAllSettings(): Cursor {
        try {
            val columnas = arrayOf("c_materia", "Dia", "Mes", "Anticipacion", "Condicionado", "as_Suspendido","Cal_menor", "Email", "Contrasena", "Email_direccion","Decimales", "N_maestro", "Estadistica1","Estadistica2", "Estadistica3")
            return db.query("Configuracion", columnas, null, null, null, null, "c_materia")
        }catch (Ex:Exception){
            val columnas = arrayOf("c_materia", "Dia", "Mes", "Anticipacion", "Condicionado", "as_Suspendido","Cal_menor", "Email", "Contrasena", "Email_direccion", "N_maestro")
            error = Ex.message.toString()
            return db.query("Configuracion", columnas, null, null, null, null, "c_materia")
        }
    }
    public fun getbymatter(): Cursor {
        try {
            val columnas = arrayOf("c_materia", "Dia", "Mes", "Anticipacion", "Condicionado", "as_Suspendido", "Cal_menor", "Email", "Contrasena", "Email_direccion")
            return db.query("Configuracion", columnas, "c_materia = "+iC_materia, null, null, null, null)
        }catch (Ex:Exception){
            val columnas = arrayOf("c_materia", "Dia", "Mes", "Anticipacion", "Condicionado", "as_Suspendido","Cal_menor", "Email", "Contrasena", "Email_direccion")
            error = Ex.message.toString()
            return db.query("Configuracion", columnas, null, null, null, null, "c_materia")
        }
    }


    public  fun getEstadisticsDates(): Triple<String?, String?, String?>? {
        try {
            val sQuery = "SELECT Estadistica1, Estadistica2, Estadistica3 FROM configuracion"
            val cursor = db!!.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                val date1 = cursor.getString(0)
                val date2 = cursor.getString(1)
                val date3 = cursor.getString(2)
                return Triple(date1, date2, date3)
            } else {
                return null
            }
        } catch (Ex: Exception) {
            Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_SHORT).show()
            return null
        }
    }


    public fun getEmails():Cursor{
        val sQuery = "Select Email, Contrasena, Email_direccion from Configuracion"
        return db.rawQuery(sQuery, null)
    }

    public fun getRoundDecimal():Int{
        val sQuery = "Select Decimales from Configuracion"
        val cursor = db.rawQuery(sQuery, null)
        return if (cursor.moveToFirst()){
            val redondeo = cursor.getInt(0)
            cursor.close()
            redondeo

        } else{
            cursor.close()
            0
        }
    }


    @SuppressLint("SuspiciousIndentation")
    public  fun deleteConfiguracion():Boolean {
        try {
            val eliminar = "Delete from Configuracion where c_materia = "+iC_materia
                db!!.execSQL(eliminar)
            error = "Se elimino la Configuracion correctamente"
            return true
        } catch (Ex: Exception) {
            error = "Hubo un error al eliminar el la Configuracion"
            return false
        }
    }
    public  fun deleteAll():Boolean {
        try {
            val eliminar = "Delete from Configuracion "
            db!!.execSQL(eliminar)
            error = "Se elimino la Configuracion correctamente"
            return true
        } catch (Ex: Exception) {
            error = "Hubo un error al eliminar el la Configuracion"
            return false
        }
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

    fun addColumnN_teacher(){
        val consulta = "ALTER TABLE Configuracion ADD COLUMN N_maestro TEXT"
        db.execSQL(consulta)
    }



}