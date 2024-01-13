package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.control_escolar.Nombre_Escuela

data class ParticipacionBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var iFolio = 0
    var dtFecha = ""
    var sDescripcion = ""
    var bMostrar = false
    var sPuntaje = "0"
    var iC_materia = 0
    var error = ""

    init {
        db = this.writableDatabase
        values = ContentValues()
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Participacion " +
                    "(C_Participacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Fecha Text, Descripcion Text, Mostrar Integer, Puntaje Integer, C_materia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()

        }

    }


    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Participacion"
        db!!.execSQL(BorrarTabla)
    }

    public fun deletetable(){
        val BorrarTabla = "DROP TABLE IF EXISTS Participacion"
        db!!.execSQL(BorrarTabla)
    }

    public fun Createtable(){
        val CrearTabla = "CREATE TABLE if not exists Participacion " +
                "(C_Participacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Folio Integer, Fecha Text, Descripcion Text, Mostrar Integer, Puntaje Integer, C_materia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
        db!!.execSQL(CrearTabla)
    }



    public fun Inserstake():Boolean{
        try {
                if(!isNumeric(sPuntaje.toString())) sPuntaje = "0"
                val Insertar = "Insert into Participacion (Folio, Fecha, Descripcion, Mostrar, Puntaje, C_materia)" +
                        " values ("+iFolio+",  '"+dtFecha+"', '"+sDescripcion+"',"+bMostrar+", "+sPuntaje+", "+iC_materia+")"
                db!!.execSQL(Insertar)
                //error = Insertar
                error = "Se registro correctamente "
                return true
        }catch (Ex:Exception){
            error = "Hubo un error al agregar participación"
            //error = Ex.message.toString()
            return  false
        }
    }


    public  fun deletestake(c_participacion:String):Boolean {
        try {
            val eliminar = "Delete from Participacion where C_participacion = "+c_participacion
                db!!.execSQL(eliminar)
            error = "Se elimino la Participacion correctamente"
            return true
        } catch (Ex: Exception) {
            error = "Hubo un error al eliminar el la Participacion"
            return false
        }
    }

    public fun get_stake(folio:String, c_materia:String):Cursor{
        try {
            //val condicion = "Materia.C_materia = Participacion.C_materia and Folio = $folio"
            val condicion = "Participacion.c_materia = Materia.c_materia and Participacion.C_materia = "+c_materia+" and Folio = $folio"
            val columnas = arrayOf("Fecha", "Descripcion", "Puntaje", "Participacion.c_materia", "N_Materia", "Color")
            error = condicion
            val c = db.query("Participacion, Materia", columnas, condicion, null, null, null, null)
            return  c
        }catch (Ex:Exception){
            error = Ex.message.toString()
            val condicion = "Participacion.c_materia = Materia.c_materia and Participacion.C_materia = "+c_materia+" and Folio = $folio"
            val columnas = arrayOf("Fecha", "Descripcion", "Puntaje", "c_materia", "N_Materia", "Color")
            error = condicion
            val c = db.query("Participacion, Materia", columnas, condicion, null, null, null, null)
            return c
        }
    }
    public fun get_stake(folio:String):Cursor{
        try {
            //val condicion = "Materia.C_materia = Participacion.C_materia and Folio = $folio"
            val condicion = "Participacion.c_materia = Materia.c_materia  and Folio = $folio"
            val columnas = arrayOf("Fecha", "Descripcion", "Puntaje", "Participacion.c_materia", "N_Materia", "Color", "c_participacion")
            error = condicion
            val c = db.query("Participacion, Materia", columnas, condicion, null, null, null, null)
            return  c
        }catch (Ex:Exception){
            error = Ex.message.toString()
            val condicion = "Participacion.c_materia = Materia.c_materia  Folio = $folio"
            val columnas = arrayOf("Fecha", "Descripcion", "Puntaje", "Participacion.c_materia", "N_Materia", "Color", "c_participacion")
            error = condicion
            val c = db.query("Participacion, Materia", columnas, condicion, null, null, null, null)
            return c
        }
    }


    public fun onCreatetableParticipacion(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Participacion " +
                    "(C_Participacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Fecha Text, Descripcion Text, Mostrar Integer, Puntaje Integer, C_materia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }


   public fun isNumeric(cadena: String): Boolean {
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