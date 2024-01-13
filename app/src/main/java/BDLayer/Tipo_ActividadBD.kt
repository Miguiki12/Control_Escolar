package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.control_escolar.Nombre_Escuela

data class Tipo_ActividadBD(var context: Context):SQLiteOpenHelper(
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
            val CrearTabla = "CREATE TABLE if not exists Tipo_Actividad " +
                    "(C_actividad INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Valor Integer, Diaria Integer, Calificar Integer, C_materia Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }


    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Tipo_Actividad"
        db!!.execSQL(BorrarTabla)

    }


    public fun InsertarActividad(nombre:String, valor:String, c_materia:String, calificar:Int):Boolean{
        try {

            if (isNumeric(valor)){
                val Insertar = "Insert into Tipo_Actividad (Nombre, Valor, Diaria, Calificar, C_materia)" +
                    " values ('"+nombre+"', "+valor+", 0, $calificar, "+c_materia+")"

                db!!.execSQL(Insertar)
                //error = Insertar
                error = "Se registro correctamente el tipo de Actividad"
                return true
            }
            else{
                error = "El porcentaje de la materia tiene que ser numerico"
                return  false
            }
        }catch (Ex:Exception){
            error = Ex.message.toString()

            return  false
        }

    }

    public fun ActualizarActividad(nombre:String, porcentaje:String, c_actividad:String):Boolean{
        try {
            val Actualizar = "Update Tipo_Actividad set Nombre = '"+nombre+"', Valor = "+porcentaje+" where C_actividad = "+c_actividad

            db!!.execSQL(Actualizar)

            error = "Se actualizo el nombre de la actividad correctamente"
            return true

        }catch (Ex:Exception){
            error = Ex.message.toString()

            return  false
        }

    }


    public fun obtenerAll(c_materia:String): Cursor {

        val columnas = arrayOf("C_actividad", "Nombre", "valor")
        return db.query("Tipo_Actividad", columnas, "c_materia = "+c_materia, null, null, null, null)


    }

    public  fun borrarTipo_Actividad(c_actividad:String):Boolean {
        try {
            val eliminar =
                "Delete from Tipo_Actividad  where C_actividad  = " + c_actividad

            db!!.execSQL(eliminar)
            error = "Se elimino la actividad correctamente"

            return true

        } catch (Ex: Exception) {

            error = "Hubo un error al eliminar el la actividad"
            return false

        }
    }

    public fun deleteTable(){
        try {
            val BorrarTabla = "DROP TABLE IF EXISTS Tipo_Actividad"
            db!!.execSQL(BorrarTabla)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }

    public fun onCreatetableTipo_Actividad(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Tipo_Actividad " +
            "(C_actividad INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Nombre TEXT, Valor Integer, Diaria Integer, Calificar Integer, C_materia Integer, FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
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