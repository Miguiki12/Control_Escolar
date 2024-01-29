package BDLayer

import LogicLayer.Formats
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
    var sCiclo = Formats.obtenerRangoDeAnios().split('-')
    var sEstadistica1 = "${sCiclo[0]}-09-16"
    var sEstadistica2 = "${sCiclo[0]}-12-31"
    var sEstadistica3 = "${sCiclo[1]}-03-31"
    var sEstadistica4 = "${sCiclo[1]}-06-15"
    var sEstadistica5 = "${sCiclo[1]}-07-15"
    var sF_Estadistica = "${sCiclo[0]}-09-01"


    init {
        db = this.writableDatabase
        values = ContentValues()
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Configuracion " +
                    "(C_materia Integer, Dia Integer, Mes Integer, Anticipacion Integer,  Decimales Integer, Condicionado Boolean,  Suspendido Boolean, " +
                    "Cal_menor Integer, Email Text, Contrasena Text, Email_direccion Text, N_maestro Text, Estadistica1 Text, Estadistica2 Text, Estadistica3 Text, Estadistica4 Text, Estadistica5 Text, F_Estadistica Text, " +
                    "FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)
            //insertamos por default los valores en la tabla
            newSettings()
        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }

    public fun Createtable(){
        val CrearTabla = "CREATE TABLE if not exists Configuracion "+
                                    "(C_materia Integer, Dia Integer, Mes Integer, Anticipacion Integer,  Decimales Integer, Condicionado Boolean,  Suspendido Boolean, "+
                                    "Cal_menor Integer, Email Text, Contrasena Text, Email_direccion Text, N_maestro Text, Estadistica1 Text, Estadistica2 Text, Estadistica3 Text, Estadistica4 Text, Estadistica5 Text, F_Estadistica Text, " +
                                    "FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
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
                val Insertar = "Insert into Configuracion (C_materia, Dia, Mes, Anticipacion, Decimales, Condicionado, Suspendido, Cal_menor, Email, Contrasena, Email_direccion, N_maestro, Estadistica1, Estadistica2, Estadistica3, Estadistica4, Estadistica5, F_Estadistica)" +
                        " values ($iC_materia, $iDia,  $iMes, $iAnticipacion, $iDecimales, $iCondicionado, $ias_Suspendido, $iCal_menor, '$sEmail', '$sContrasena', '$sEmail_direccion', '$sN_maestro', '$sEstadistica1', '$sEstadistica2', '$sEstadistica3','$sEstadistica4', '$sEstadistica5', '$sF_Estadistica')"
                db!!.execSQL(Insertar)
                error = "Se registro correctamente "
                return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }

    fun updteAgeEstadistic(day:Int, mounth:Int, c_materia: Int){
        val sQuery = "Update Configuracion set Dia = $day , Mes = $mounth where c_materia = $c_materia"

        db.execSQL(sQuery)

    }


    fun updteDateEstadistic(date:String, c_materia: Int){
        val sQuery = "Update Configuracion set F_Estadistica = '$date' where c_materia = $c_materia"

        db.execSQL(sQuery)

    }

    public fun getAllSettings(): Cursor {
        try {
            val columnas = arrayOf("c_materia", "Dia", "Mes", "Anticipacion", "Condicionado", "Suspendido","Cal_menor", "Email", "Contrasena", "Email_direccion","Decimales", "N_maestro", "Estadistica1","Estadistica2", "Estadistica3", "Estadistica4", "Estadistica5")
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


    public  fun getEstadisticsDates(): Array<String>? {
        try {
            val dates: Array<String> = arrayOf("", "", "", "","")
            val sQuery = "SELECT Estadistica1, Estadistica2, Estadistica3, Estadistica4, Estadistica5 FROM configuracion"
            val cursor = db!!.rawQuery(sQuery, null)
            if (cursor.moveToFirst()) {
                 dates[0] = cursor.getString(0)
                 dates[1] = cursor.getString(1)
                 dates[2] = cursor.getString(2)
                 dates[3] = cursor.getString(3)
                 dates[4] = cursor.getString(4)
                return dates
            } else {
                return null
            }
        } catch (Ex: Exception) {
            Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_SHORT).show()
            return null
        }
    }

    fun getDateStadistic(c_materia: Int):String{
        val sQuery = "Select F_estadistica from Configuracion where c_materia = $c_materia"

        val cursor = db.rawQuery(sQuery, null)

        return if (cursor.moveToFirst()) cursor.getString(0)
        else ""

        cursor.close()

    }

    fun getDayandMounthEstadistic(c_materia:Int):Pair<Int, Int>{

        val sQuery = "Select Dia, Mes from Configuracion where c_materia = 0"
        var dia = 0
        var mes = 0
        val cursor = db.rawQuery(sQuery, null)
        if (cursor.moveToFirst()){

            dia = cursor.getInt(0)
            mes = cursor.getInt(1)

        }
        return Pair(dia, mes)
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
        return !(Formats.isNumeric(dia.toString()) && Formats.isNumeric(mes.toString()) && Formats.isNumeric(decimales.toString()))
    }








}