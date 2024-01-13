package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.control_escolar.Nombre_Escuela
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

data class AsistenciaBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var error = ""
    public  var mounth = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }

    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Asistencia " +
                    "(C_asistencia Text, Dia Text, " +
                    "Folio Integer, C_materia Integer, Fecha TEXT, Asistencia Integer, Sugerencias TEXT, Retardo Integer, "+
                    "Distraido INTEGER, Entrega_trabajos TExt, Argumentacion Text, Conducta text, Uniforme Text, Aseo Text, Utiles  Text, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()

        }

    }


    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Asistencia"
        db!!.execSQL(BorrarTabla)
    }


    public fun InsertarAsistencia(folio:String, c_materia:String, fecha:String, asistencia:Int, dia: String, status:Int, argumentacion:String):Boolean{
        try {
                val Insertar = "Insert into Asistencia (Folio, C_materia, Fecha, Asistencia, Dia, Retardo, Argumentacion)" +
                    " values ("+folio+", "+c_materia+", '"+fecha+"', "+asistencia+", '"+dia+"', "+status+", '$argumentacion')"
                db!!.execSQL(Insertar)
                error = Insertar
                return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }
    }


    public fun obtenerAll(fecha:String): Cursor {
        try {
            val condicion = "Alumno.Folio = Asistencia.Folio and Fecha  = '"+fecha+"'"
            val columnas = arrayOf("Nombre", "apellidop", "apellidom", "Alumno.Folio", "Foto", "Asistencia", "sexo", "N_lista","telefono_contacto", "Retardo", "Email_contacto", "Argumentacion")
            return db.query("Asistencia, Alumno", columnas, condicion, null, null, null, "Apellidop, Nombre")
        }catch (Ex:Exception){
            val condicion = "Alumno.Folio = Asistencia.Folio and Fecha  = '"+fecha+"'"
            val columnas = arrayOf("Nombre", "apellidop", "apellidom", "Alumno.Folio", "Foto", "Asistencia", "sexo", "N_lista","telefono_contacto", "Retardo", "Email_contacto", "Argumentacion")
            error = Ex.message.toString()
            return db.query("Asistencia, Alumno", columnas, condicion, null, null, null, "Apellidop, Nombre")
        }
    }

    public fun getAllTable():Cursor{
        val sQuery = " Select * from Asistencia order by fecha "
        return db.rawQuery(sQuery, null)
    }


    public fun getAsisten(fecha:String):Cursor{
        val sQuery = "Select Asistencia, Retardo, Asistencia.Folio, N_lista, Nombre " +
                "From Asistencia, Alumno " +
                "where Fecha = '$fecha' and Alumno.Folio = Asistencia.Folio " +
                "order by n_lista"
        return db.rawQuery(sQuery, null)
    }

    public fun getAttendanceByDay(date:String):Cursor{
        val sQuery = "SELECT N_lista, Nombre || ' ' || Apellidop || ' ' || Apellidom  AS Nombre_Completo, Asistencia, Retardo as Status, fecha " +
                "FROM Alumno, Asistencia " +
                "WHERE Alumno.Folio = Asistencia.Folio AND Fecha = '$date' " +
                "ORDER BY N_lista;"
        return db.rawQuery(sQuery, null)
    }

    public fun get_reporte_asistencia(fecha:String): Cursor {
        return try {
            val sQuery = "SELECT Count(sexo) as total, sexo " +
                    "FROM  Asistencia, Alumno " +
                    "where  Asistencia = 1 and fecha = '$fecha' and Alumno.Folio = Asistencia.Folio " +
                    "group by sexo " +
                    "order by sexo desc"
            error = sQuery
            db.rawQuery(sQuery, null)

        }catch (Ex:Exception){
            db.rawQuery("SELECT Count(sexo) as total FROM Alumno, Asistencia where Asistencia.Asistencia = 1 and Fecha  = '$fecha' and Alumno.Folio =  Asistencia.Folio  GROUP BY sexo", null)
        }
    }

    public  fun asistencia_x_dia(fecha:String):Cursor{
        val condicion = "Fecha  = '$fecha' and Alumno.Folio =  Asistencia.Folio"
        val columnas = arrayOf("N_lista", "Asistencia")
        return db.query("Asistencia, Alumno", columnas, condicion, null, null, null, "N_lista")
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    public  fun asistenciaMes(mes:Int, año:Int):Cursor{
        val (star, end, month) =  getStartEndOfMonth(mes, año)
        this.mounth = month
        val condicion = "Fecha Between '$star'  And  '$end' and Alumno.Folio = Asistencia.Folio"
        val columnas = arrayOf("Asistencia.Folio", "Asistencia", "Fecha", "Retardo", "N_lista")
        return db.query("Asistencia, Alumno", columnas, condicion, null, null, null, "N_lista, Fecha")
    }*/
    @RequiresApi(Build.VERSION_CODES.O)
    public  fun asistenciaMes(mes:Int, año:Int):MutableList<MutableMap<String, Any>>{
        val (star, end, month) =  getStartEndOfMonth(mes, año)
        val listaMes: MutableList<MutableMap<String, Any>> = mutableListOf()
        this.mounth = month
        val condicion = "Fecha Between '$star'  And  '$end' and Alumno.Folio = Asistencia.Folio"
        val columnas = arrayOf("Asistencia.Folio", "Asistencia", "Fecha", "Retardo", "N_lista", "Nombre")
        val cursor = db.query("Asistencia, Alumno", columnas, condicion, null, null, null, "N_lista, Fecha")
        if (cursor.moveToFirst()) {
            do {
                val newData1 = mutableMapOf<String, Any>()
                newData1["total"] = cursor.getString(1)
                newData1["fecha"] = cursor.getString(2)
                newData1["n_lista"] = cursor.getString(4)
                newData1["retardo"] = cursor.getString(3)
                newData1["nombre"] = cursor.getString(5)
                listaMes.add(newData1)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listaMes
    }
    @RequiresApi(Build.VERSION_CODES.O)
    public  fun asistenciaDias(mes:Int, año: Int):Cursor{
        val (star, end, month) =  getStartEndOfMonth(mes, año)
        this.mounth = month
        val condicion = "Fecha Between '$star'  And  '$end'"
        val columnas = arrayOf("Folio", "Asistencia", "Fecha")
        return db.query("Asistencia", columnas, condicion, null, "Fecha", null, "Fecha")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public  fun sexoxMes(mes:Int, año:Int):MutableList<MutableMap<String, Any>>{
        val listaSexo: MutableList<MutableMap<String, Any>> = mutableListOf()
        val (star, end, month) =  getStartEndOfMonth(mes, año)
        //val listaSexo = kotlin.collections.ArrayList<String>()
        this.mounth = month
        val condition = "Fecha Between '$star'  And  '$end' and Alumno.Folio = Asistencia.Folio and Asistencia = 1 "
        val columnas = arrayOf("Count(sexo)", "Fecha", "sexo")
        val cursor =  db.query("Asistencia, Alumno", columnas, condition, null, "fecha, sexo", null, "sexo, fecha")
        if (cursor.moveToFirst()){
            do {
                val newData1 = mutableMapOf<String, Any>()
                newData1["total"] = cursor.getString(0)
                newData1["fecha"] = cursor.getString(1)
                newData1["sexo"] = cursor.getString(2)
                listaSexo.add(newData1)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return  listaSexo
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun totalAlumnosMes(mes: Int, año: Int):Int{
        val (star, end, month) =  getStartEndOfMonth(mes, año)
        this.mounth = mounth
        val condition = "Fecha Between '$star'  And  '$end'"
        error =  condition
        val columnas = arrayOf("Distinct(Folio)")
        val total =  db.query("Asistencia", columnas, condition, null, null, null, "folio")
        return if (total.moveToFirst()){
            total.count
            //total.moveToLast()
            //total.getString(0).toInt()
        }
        else 0
    }


    public fun obtenerFaltas(fecha:String): Cursor {
        val columnas = arrayOf("Email_Contacto", "Nombre", "apellidop", "apellidom")
        return  db.query("Alumno, Asistencia", columnas, "Alumno.Folio = Asistencia.Folio and Asistencia = 0 and fecha = '"+fecha+"'", null, null, null, null)
        //cursor.close()
        //return cursor
    }


    public fun obtenerHorario(dia:String): Cursor {

        val columnas = arrayOf("C_dia", "Dia", "N_materia", "color")
        return  db.query("Materiadia, Materia", columnas, "Materiadia.c_materia = Materia.C_materia and Dia = '"+dia+"'", null, null, null, null)
        //cursor.close()
        //return cursor
    }

    public  fun borrarAsistencia(fecha:String):Boolean {
        try {
            val eliminar = "Delete from Asistencia  where fecha  = '$fecha'"

            db!!.execSQL(eliminar)
            error = eliminar//"Se elimino la Asistencia correctamente"
            return true

        } catch (Ex: Exception) {

            error = "Hubo un error al eliminar el la Asistencia"
            return false

        }
    }

    public  fun updateAtNormalAttendance(folio:Int, fecha:String, c_materia: Int):Boolean {
        try {
            val condition = "Update Asistencia set retardo = 0  where  folio = $folio and fecha = '$fecha' and c_materia =  $c_materia"

            db!!.execSQL(condition)
            error = "Se actualzo la asistencia correctamente"
            return true
        } catch (Ex: Exception) {
            error = "Hubo un error al actlizar la Asistencia"
            return false
        }
    }

    public fun updateArgumentAttendance(date:String, argument:String):Boolean{
        try {
            val condition = "Update Asistencia set Argumentacion = '$argument'  where   fecha = '$date'"

            db!!.execSQL(condition)
            error = "Se registro la argumentacion del dia $date"
            return true
        } catch (Ex: Exception) {
            error = "Hubo un error al argumentar el dia $date"
            //error = Ex.message.toString()
            return false
        }
    }

    public fun deleteTable(){
        try {
            val deleteTable = "DROP TABLE IF EXISTS Asistencia"
            db!!.execSQL(deleteTable)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }



    public fun onCreatetableAsistencia(){
        try {
            val createTable = "CREATE TABLE if not exists Asistencia " +
                    "(C_asistencia Text, Dia Text, " +
                    "Folio Integer, C_materia Integer, Fecha TEXT, Asistencia Integer, Sugerencias TEXT, Retardo Integer, "+
                    "Distraido INTEGER, Entrega_trabajos TExt, Argumentacion Text, Conducta text, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(createTable)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun diasMes():Pair<Int, Int>{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val primerDiaDelMes = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DATE, -1)
        val ultimoDiaDelMes = calendar.get(Calendar.DAY_OF_MONTH)
        return Pair(primerDiaDelMes, ultimoDiaDelMes)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getStartEndOfMonth(month: Int, year: Int): Triple<String, String, String> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val monthName = startDate.month.getDisplayName(TextStyle.FULL, Locale("es"))
        return Triple(startDate.format(formatter), endDate.format(formatter), monthName)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun getIntervalMonth(fechaTexto: String): String {
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fecha = formatoEntrada.parse(fechaTexto)

        val formatoSalida = SimpleDateFormat("d 'de' MMMM", Locale.getDefault())

        val calendario = Calendar.getInstance()
        calendario.time = fecha
        val primerDiaMes = calendario.getActualMinimum(Calendar.DAY_OF_MONTH)
        val ultimoDiaMes = calendario.getActualMaximum(Calendar.DAY_OF_MONTH)

        calendario.set(Calendar.DAY_OF_MONTH, primerDiaMes)
        val primerDiaTexto = formatoSalida.format(calendario.time)

        calendario.set(Calendar.DAY_OF_MONTH, ultimoDiaMes)
        val ultimoDiaTexto = formatoSalida.format(calendario.time)

        return "$primerDiaTexto al $ultimoDiaTexto"
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