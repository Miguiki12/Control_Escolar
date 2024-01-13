package BDLayer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract
import android.text.BoringLayout
import com.example.control_escolar.Nombre_Escuela

data class ReporteBD(var context: Context):SQLiteOpenHelper(
    context, Nombre_Escuela.getName(), null, 1) {
    private val db: SQLiteDatabase
    private val values: ContentValues
    var error = ""
    public var  iC_reporte: Int
    public var  iFolio:Int
    public var  sFecha:String
    public var  iTipo:Int
    public var sDescripcion:String
    public var bMostrar: Boolean
    public  var dPuntaje: Int
    public var iMateria: Int
    public var sF_inicio: String
    public var sF_fin: String

    init {
        db = this.writableDatabase
        values = ContentValues()
        iC_reporte = 0
        iFolio = 0
        sFecha = ""
        iTipo = 0
        sDescripcion = ""
        bMostrar = true
        dPuntaje = 0
        iMateria = 0
        sF_inicio = ""
        sF_fin = ""
    }


    public override fun onCreate(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists Reporte "+
                    "(C_reporte INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Fecha Text, tipo Text, Descripcion Tex, "+
                    "Mostrar Integer, Puntaje Integer, Lugar Text, C_materia Integer, "+
                    "F_inicio Text, F_fin Text, Correctivo Text, Firma Text, Fecha_firma Text, "+
                    "Articulo Text, Articulo_vinculado text, Indidencia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)

        } catch (Ex: Exception) {
            error = Ex.message.toString()

        }

    }
    /*public fun onCreatetableReportes(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Reporte " +
                    "(C_reporte INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Fecha Text, tipo Text, Descripcion Tex, "+
                    "Mostrar Integer, Puntaje Integer, Lugar Text, C_materia Integer, "+
                    "F_inicio Text, F_fin Text, Correctivo Text, Firma Text, Fecha_firma Text, "+
                    "Articulo Text, Articulo_vinculado text, Indidencia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            db!!.execSQL(CrearTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }
    }*/

    public fun onCreatetableReportes(){
        try {
            val CrearTabla = "CREATE TABLE if not exists Reporte "+
                    "(C_reporte INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "Folio Integer, Fecha Text, tipo Text, Descripcion Tex, "+
                    "Mostrar Integer, Puntaje Integer, Lugar Text, C_materia Integer, "+
                    "F_inicio Text, F_fin Text, Correctivo Text, Firma Text, Fecha_firma Text, "+
                    "Articulo Text, Articulo_vinculado text, Indidencia Integer, FOREIGN KEY(Folio) REFERENCES Alumno(Folio), FOREIGN KEY(C_materia) REFERENCES Materia(C_materia))"
            //error = CrearTabla
            db!!.execSQL(CrearTabla)

        }catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }


    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Reporte"
        db!!.execSQL(BorrarTabla)
    }

    public fun deletetable(){
        try {
            val BorrarTabla = "DROP TABLE IF EXISTS Reporte"
            db!!.execSQL(BorrarTabla)
        }
        catch (Ex:Exception){
            error = Ex.message.toString()
        }

    }

    public fun WriteReport():Boolean
    {
        try {
            val sQuery = "Insert into Reporte (Folio,Fecha,tipo,Descripcion,Mostrar,Puntaje,C_materia) values("+iFolio+", '"+sFecha+"', "+iTipo+
                    ", '"+sDescripcion+"', "+bMostrar+", "+dPuntaje+", "+iMateria+")"

            db!!.execSQL(sQuery)
            error = "Se registro con exito "
            return  true
        }catch (Ex:Exception){
            error = "Hubo un problema al registrar la insidencia"
            error = Ex.message.toString()
            return false
        }
    }

    public fun WriteSuspencion():Boolean
    {
        try {
            val sQuery = "Insert into Reporte(Folio,Fecha,tipo,Descripcion,Mostrar,Puntaje,C_materia,F_inicio,F_fin) values(" + iFolio + ", '" + sFecha+ "', " + iTipo +
                    ", '" + sDescripcion + "', " + bMostrar + ", " + dPuntaje + ", " + iMateria + ", '"+sF_inicio+"','"+sF_fin+"')";

            db!!.execSQL(sQuery)
            error = "Se registro con exito "
            return  true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            //error = "Error al registrar el la insidencia"
            return false
        }
    }

    public fun updateReporte():Boolean
    {
        try {
            val sQuery = "Update Reporte set  N_lista = " + iFolio + ", Fecha = '" + sFecha +
                    "', Tipo = " + iTipo + ", Descripcion = '" + sDescripcion + "', Mostrar = " + bMostrar +
                    ", Puntaje = " + dPuntaje + ", C_materia = "+iMateria+"  where N_lista = " + iFolio + " and C_reporte = " + iC_reporte;

            db!!.execSQL(sQuery)
            return  true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }

    public fun UpdateReporte():Boolean
    {
        try {
            val sQuery = "Update Reporte set  N_lista = " + iFolio + ", Fecha = '" + sFecha+ "', Tipo = " + iTipo +
                    ", Descripcion = '" + sDescripcion + "', Mostrar = " + bMostrar + ", Puntaje = "+ dPuntaje +
                    ", C_materia = " + iMateria + ", F_inicio = '"+sF_inicio+"', F_fin = '"+sF_fin+
                    "'  where N_lista = " + iFolio + " and C_reporte = " + iC_reporte;

            db!!.execSQL(sQuery)
            return  true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return false
        }
    }

    public fun DeleteReporte(c_reporte:String):Boolean
    {
        try {
            val sQuery= "Delete from Reporte where C_reporte = $c_reporte"
            db!!.execSQL(sQuery)
            error = "Se elimino correctamente la incidencia"
            return  true
        }catch (Ex:Exception){
            error = "Hubo un problema al eliminar la incidencia"
            //error = Ex.message.toString()
            return false
        }
    }



    public fun get_reporte(folio:String):Cursor{
        try {

            val condicion = "Reporte.c_materia = Materia.c_materia  and Folio = $folio"
            val columnas = arrayOf("C_reporte","Fecha","Reporte.tipo","Puntaje","N_materia","Color","F_inicio","F_fin")
            error = condicion
            val c = db.query("Reporte, Materia", columnas, condicion, null, null, null, null)
            return  c
        }catch (Ex:Exception){
            error = Ex.message.toString()
            val condicion = "Participacion.c_materia = Materia.c_materia  and Folio = $folio"
            val columnas = arrayOf("C_reporte","Fecha","Reporte.tipo","Puntaje","N_materia","Color","F_inicio","F_fin")
            error = condicion
            val c = db.query("Reporte, Materia", columnas, condicion, null, null, null, null)
            return c
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