package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.control_escolar.Nombre_Escuela
import java.text.SimpleDateFormat

data class PartialDB(var context: Context): SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1){
    private val db: SQLiteDatabase
    private val values: ContentValues
    public var iC_materia = 0
    public var sF_inicio = ""
    public var sF_fin = ""
    public var iPeriodo = 0
    public var error = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }


    override fun onCreate(p0: SQLiteDatabase?) {
        try {
            val createTable = "CREATE TABLE if not exists Parciales " +
                    "(C_parcial INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_materia Integer, F_inicio Text, F_fin Text, Periodo Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(createTable)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun insertPartial():Boolean{
        try {
            val insert = "Insert into Parciales(c_materia, F_inicio, F_fin, Periodo) values ($iC_materia, '${convertdate(sF_inicio)}','${convertdate(sF_fin)}', $iPeriodo)"
            db!!.execSQL(insert)
            error = "Parcial creado Exitosamente"
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }

    fun createTable(){
        try {
            val createTable = "CREATE TABLE if not exists Parciales " +
                    "(C_parcial INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_materia Integer, F_inicio Text, F_fin Text, Periodo Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(createTable)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }

    fun getAllPartials():Cursor{
        try {
            val columnas = arrayOf("C_parcial", "Materia.c_materia","f_inicio","F_fin","Periodo", "N_materia", "color")
            val condition = "Parciales.C_materia = Materia.C_materia"
            return db.query("Parciales, Materia" , columnas, condition, null, null, null, "Periodo")
        }
        catch (Ex:Exception){
            val columnas = arrayOf("C_parcial", "Materia.c_materia","f_inicio","F_fin","Periodo","N_materia", "color")
            val condition = "Parciales.C_materia = Materia.C_materia"
            return db.query("Parciales, Materia", columnas, condition, null, null, null, "Periodo")
        }
    }

    fun getDistinctPartials():ArrayList<Int>{
        val partials = ArrayList<Int>()
        val sQuery = "Select Distinct(Periodo)" +
                "From Parciales " +
                "Order by Periodo"
        val cursor =  db!!.rawQuery(sQuery, null)
        if (cursor.moveToFirst()){
            do {
                partials.add(cursor.getInt(0))
            }while (cursor.moveToNext())
        }
        cursor.close()
        return partials
    }

    fun getDateParcial(parcial:Int):Pair<String, String>?{
        val sQuery = "Select distinct (Periodo) ,f_inicio, f_fin " +
                "from Parciales " +
                "where Periodo = $parcial"
        val cursor = db.rawQuery(sQuery, null)
        return if (cursor.moveToFirst()){
            Pair(cursor.getString(1),cursor.getString(2))
        } else null
    }

    fun deleteAllPartial():Boolean{
        try {
            val delete = "Delete from Parciales"
            db.execSQL(delete)
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }
    fun deletePartial(periodo:Int):Boolean{
        try {
            val delete = "Delete from Parciales where periodo = $periodo"
            db.execSQL(delete)
            error = delete
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }

    public fun onCreateTableParciales(){
        try {
            val createTable = "CREATE TABLE if not exists Parciales " +
                    "(C_parcial INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "C_materia Integer, F_inicio Text, F_fin Text, Periodo Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(createTable)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }


    fun convertdate(fecha:String ):String{
        val dateString = fecha
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = inputFormat.parse(dateString)
        val outputString = outputFormat.format(date)
        return  outputString
    }

}
