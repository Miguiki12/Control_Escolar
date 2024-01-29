package BDLayer

import LogicLayer.Formats
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.widget.Toast
import com.example.control_escolar.Nombre_Escuela
import java.io.ByteArrayOutputStream

data class AlumnosBD(var context: Context):SQLiteOpenHelper(
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
            val CrearTabla = "CREATE TABLE if not exists Alumno " +
                    "(Folio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_lista Integer, Nombre TEXT, Apellidop TEXT, Apellidom TEXT, Sexo Integer, Domicilio TEXT, Colonia TEXT, Municipio Text, CP Text, Telefono Text, Email Text, "+
                    "Entidad_Federativa Text, Curp Text, F_nacimiento Text, Situacion Text, Condicionado Integer, Edad Integer, Estadistica Interger, Foto BLOB, Indigena Integer, " +
                    "Discapacidad Integer, Especifique Text, " +
                    "Email_Contacto Text, Tutor Text, Telefono_contacto Text, Ocupacion_contacto Text, Estudios_contacto Text, Tutor_2 Text, Telefono_contacto_2 Text, " +
                    "Email_Contacto_2 Text, Ocupacion_contacto_2 Text, Estudios_contacto_2 Text, F_registro Text, F_baja Text, Matricula Text)"
            db!!.execSQL(CrearTabla)
        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }


    public fun onCreateStudentTemp(db: SQLiteDatabase?) {
        try {
            val CrearTabla = "CREATE TABLE if not exists StudentTemp " +
                    "(Folio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_lista Integer, Nombre TEXT, Apellidop TEXT, Apellidom TEXT, Sexo Integer, Domicilio TEXT, Colonia TEXT, Municipio Text, CP Text, Telefono Text, Email Text, "+
                    "Entidad_Federativa Text, Curp Text, F_nacimiento Text, Situacion Text, Condicionado Integer, Edad Integer, Estadistica Interger, Foto BLOB, Indigena Integer, " +
                    "Discapacidad Integer, Especifique Text, " +
                    "Email_Contacto Text, Tutor Text, Telefono_contacto Text, Ocupacion_contacto Text, Estudios_contacto Text, Tutor_2 Text, Telefono_contacto_2 Text, " +
                    "Email_Contacto_2 Text, Ocupacion_contacto_2 Text, Estudios_contacto_2 Text, F_registro Text, F_baja Text, Matricula Text)"
            db!!.execSQL(CrearTabla)
        } catch (Ex: Exception) {
            error = Ex.message.toString()
        }
    }




    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int, newVersion: Int
    ) {
        val BorrarTabla = "DROP TABLE IF EXISTS Alumno"
        db!!.execSQL(BorrarTabla)
    }



    public fun InsertAlumno(nombre:String, apellidop:String, apellidom:String, sexo:Int, edad:Int, f_nacimiento:String,curp: String, entidad:String, situacion: String):Boolean{
        try {
            val Insertar = "Insert into Alumno (Nombre, Apellidop, apellidom, sexo, Domicilio, Colonia, Curp, Telefono, Email, Tutor, Situacion, Condicionado,Edad,Email_Contacto,Telefono_Contacto,F_nacimiento, F_registro, F_baja, Entidad_Federativa, Estadistica)" +
                    " values ('"+nombre+"', '"+apellidop+"', '"+apellidom+"', "+sexo+", '','','$curp','','','','$situacion', 0, $edad ,'','','$f_nacimiento', '${Formats.getCurrentDate()}','','$entidad', ${Formats.calcularEdad(f_nacimiento, "2023-09-01")})"

            db!!.execSQL(Insertar)
            //error = Insertar
            error = "Se registro correctamente el alumno"
            return true

        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }

    }
    public fun InsertAlumno(nombre:String, apellidop:String, apellidom:String, sexo:Int, edad:String, Entidad:String, curp:String, fnacimiento:String, telefono:String, email:String, f_estadistica:String, f_registro:String):Boolean{
        try {
            var anos = 0
            val fecha = Formats.convertdate(fnacimiento)
            if (isNumeric(edad)) anos = edad.toInt()
            else anos = 0
            val Insertar = "Insert into Alumno (Nombre, Apellidop, apellidom, sexo, Domicilio, Colonia, Curp, Telefono, Email, Tutor, Situacion, Condicionado,Edad,Email_Contacto,Telefono_Contacto,Entidad_Federativa, F_nacimiento, Estadistica, F_registro, F_baja)" +
                    " values ('"+nombre+"', '"+apellidop+"', '"+apellidom+"', "+sexo+", '','','"+curp+"','','','','NUEVO INGRESO', 0, "+anos+" ,'"+email+"','"+telefono+"','"+Entidad+"', '"+fecha+"'" +
                    ", ${Formats.calcularEdad(fecha, f_estadistica)}, '$f_registro', '')"

            db!!.execSQL(Insertar)
            error = Insertar
            //error = "Se registro correctamente el alumno"
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }

    }

    public fun InsertStudentTemp(nombre:String, apellidop:String, apellidom:String, sexo:Int, edad:String, Entidad:String, curp:String, fnacimiento:String, telefono:String, email:String, f_estadistica:String, f_registro:String):Boolean{
        try {
            var anos = 0
            val fecha = Formats.convertdate(fnacimiento)
            onCreateStudentTemp(db)
            if (isNumeric(edad)) anos = edad.toInt()
            else anos = 0
            val Insertar = "Insert into StudentTemp (Nombre, Apellidop, apellidom, sexo, Domicilio, Colonia, Curp, Telefono, Email, Tutor, Situacion, Condicionado,Edad,Email_Contacto,Telefono_Contacto,Entidad_Federativa, F_nacimiento, Estadistica, F_registro, F_baja)" +
                    " values ('"+nombre+"', '"+apellidop+"', '"+apellidom+"', "+sexo+", '','','"+curp+"','','','','NUEVO INGRESO', 0, "+anos+" ,'"+email+"','"+telefono+"','"+Entidad+"', '"+fecha+"'" +
                    ", ${Formats.calcularEdad(fecha, f_estadistica)}, '$f_registro', '')"

            db!!.execSQL(Insertar)
            error = Insertar
            //error = "Se registro correctamente el alumno"
            return true
        }catch (Ex:Exception){
            error = Ex.message.toString()
            return  false
        }

    }

    public fun agregar_datos(nombre:String, apellidop:String, apellidom:String, sexo:Int, edad:String,  Entidad:String, curp:String, fnacimiento:String, telefono: String, email: String, folio:Int):Boolean {
        val actualizar =
            "Update  Alumno set Nombre = '" + nombre + "', apellidop = '" + apellidop + "', apellidom = '" + apellidom + "', " +
                    "sexo = " + sexo + ", edad = " + edad + ", F_nacimiento = '" + Formats.convertdate(fnacimiento) + "', Estadistica = '${Formats.ageEstadisticWhitDate(Formats.convertdate(fnacimiento),1,9)}'" +
                    ", Curp = '"+curp+"', Entidad_Federativa = '"+Entidad+"', telefono_contacto = '"+telefono+"', Email_contacto = '"+email+"'  where Folio  = " + folio
        try {

                db!!.execSQL(actualizar)
                //error = "Se actualizaron correctamente los datos del alumno"
                error = actualizar
                return true
            /*}else{
                error = "Asegurese de indicar la edad del alumno"
                return false
            }*/

        } catch (Ex: Exception) {
            error = Ex.message.toString()
            //error = "Hubo un error al actualizar los datos del alumno"
            return false

        }
    }


    public  fun Domiciliar(domicilio:String, colonia:String, celular:String, email:String, entidad:String, n_lista:Int):Boolean{
        try {
            val actualizar = "Update  Alumno set Domicilio = '"+domicilio+"', Colonia = '"+colonia+"', Telefono = '"+celular+"', email = '"+email+"', Entidad_Federativa = '"+entidad+"'  where Folio  = " + n_lista

            db!!.execSQL(actualizar)
            error = "Se actualizaron correctamente los datos del alumno"
            return true

        }catch (Ex:Exception){
        //error = Ex.message.toString()
            error = "Hubo un error al actualizar los datos del alumno"
        return  false
    }
    }

    public fun updateSituacionBaja(condicionado:Int, folio:Int, indigena: Int, discapacitado:Int, especifique:String, f_baja:String, n_lista:Int ):Boolean {
        try {
            val lista = n_lista +100
            val actualizar =
                "Update  Alumno set Condicionado = $condicionado, Indigena = $indigena, discapacidad = $discapacitado, especifique = '$especifique', f_baja = '$f_baja', n_lista  = $lista  where Folio  = $folio"
            db!!.execSQL(actualizar)
            error = "Se actualizaron correctamente los datos del alumno"
            return true

        } catch (Ex: Exception) {
            //error = Ex.message.toString()
            error = "Hubo un error al actualizar los datos del alumno"
            return false
        }
    }
    public fun updateSituacionRegistro(situacion:String, condicionado:Int, folio:Int, indigena: Int, discapacitado:Int, especifique:String, f_registro:String, n_lista:Int ):Boolean {
        try {
            val lista = n_lista +100
            val actualizar =
                "Update  Alumno set Situacion = '$situacion', Condicionado = $condicionado, Indigena = $indigena, discapacidad = $discapacitado, especifique = '$especifique', F_registro = '$f_registro', f_baja = '', n_lista  = $lista  where Folio  = $folio"
            db!!.execSQL(actualizar)
            error = "Se actualizaron correctamente los datos del alumno"
            return true

        } catch (Ex: Exception) {
            //error = Ex.message.toString()
            error = "Hubo un error al actualizar los datos del alumno"
            return false
        }
    }

    fun updateEstadistica(folio:Int, estadistica:Int){
        val sQuery = "Update Alumno set estadistica = $estadistica where folio = $folio"
        db!!.execSQL(sQuery)
    }

    fun updateBaja(folio:Int){
        val sQuery = "Update Alumno set f_baja = '${Formats.getCurrentDate()}' where folio = $folio"
        db!!.execSQL(sQuery)
    }


    public fun setPicture(picture: Bitmap, folio: Int): Boolean {
        return try {
            val outputStream = ByteArrayOutputStream()
            picture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()
            val actualizar = "UPDATE Alumno SET foto = ? WHERE Folio = ?"
            val values = ContentValues().apply {
                put("foto", byteArray)
            }

            db?.update("Alumno", values, "Folio = ?", arrayOf(folio.toString()))

            error = "Se actualizaron correctamente los datos del alumno"
            true
        } catch (ex: Exception) {
            error = ex.message.toString()
            false
        }
    }



    public fun DatosPersonales(nombre:String, apellidop:String, apellidom:String, sexo:Int, edad:String, nacimiento:String, curp:String, folio:Int):Boolean {
        try {
            if (isNumeric(edad)) {
                val fecha = Formats.convertdate(nacimiento)
                var años = edad.toInt()
                if (edad.toInt() == 0 ) años = Formats.calcularEdad(fecha)

                val actualizar =
                    "Update  Alumno set nombre = '" + nombre + "', apellidop = '" + apellidop + "', apellidom = '" + apellidom + "', sexo = " + sexo + ", " +
                            "edad = " + años + ", F_nacimiento = '" + fecha + "', Estadistica = '${Formats.ageEstadisticWhitDate(fecha,1,9)}'," +
                            "Curp = '"+curp+"'  where Folio  = " + folio

                db!!.execSQL(actualizar)
                error = "Se actualizaron correctamente los datos del alumno"
                return true
            }else{
                error = "Asegurese de indicar la edad del alumno"
                return false
            }
        } catch (Ex: Exception) {
            //error = Ex.message.toString()
            error = "Hubo un error al actualizar los datos del alumno"
            return false
        }
    }

    public fun updatePersonalDates(nombre:String, apellidop:String, apellidom:String, sexo:Int,  edad:String, nacimiento:String, curp:String, folio:Int):Boolean {
        try {
            if (isNumeric(edad)) {
                var anos = edad.toInt()
                val fecha = Formats.convertdate(nacimiento)
                if (anos == 0) anos = Formats.calcularEdad(nacimiento)//si  tennemos fecha
                val actualizar =
                    "Update  Alumno set nombre = '" + nombre + "', apellidop = '" + apellidop + "', apellidom = '" + apellidom + "', sexo = " + sexo + ", " +
                            "edad = $anos, F_nacimiento = '$fecha', Estadistica = '${Formats.ageEstadisticWhitDate(fecha,1,9)}'," +
                            "Curp = '"+curp+"'  where Folio  = " + folio

                db!!.execSQL(actualizar)
                error = "Se actualizaron correctamente los datos del alumno"
                return true
            }else{
                error = "Asegurese de indicar la edad del alumno"
                return false
            }
        } catch (Ex: Exception) {
            //error = Ex.message.toString()
            error = "Hubo un error al actualizar los datos del alumno"
            return false
        }
    }

    public fun datoscontacto(nombre:String, telefono:String, email:String, ocupacion:String, estudios:String, folio:Int):Boolean {
        try {
            val actualizar =
                "Update  Alumno set Tutor = '$nombre', Telefono_contacto = '$telefono', Email_contacto = '$email', Ocupacion_contacto = '$ocupacion', Estudios_contacto = '$estudios'  where Folio  = $folio"

            db!!.execSQL(actualizar)
            error = "Se actualizaron correctamente los datos del Tutor"
            //error = actualizar
            return true

        } catch (Ex: Exception) {
            //error = Ex.message.toString()
            error = "Hubo un error al actualizar los datos del Tutor"
            return false

        }
    }

    public fun datoscontacto_2(nombre:String, telefono:String, email:String, ocupacion:String, estudios:String, folio:Int):Boolean {
        try {
            val actualizar =
                "Update  Alumno set Tutor_2 = '$nombre', Telefono_contacto_2 = '$telefono', Email_contacto_2 = '$email', Ocupacion_contacto_2 = '$ocupacion', Estudios_contacto_2 = '$estudios'  where Folio  = $folio"

            db!!.execSQL(actualizar)
            error = "Se actualizaron correctamente los datos del Tutor"
            //error = actualizar
            return true

        } catch (Ex: Exception) {
            //error = Ex.message.toString()
            error = "Hubo un error al actualizar los datos del Tutor"
            return false
        }
    }

    public fun obtenerAll(): Cursor {
        val columnas = arrayOf("Folio", "Nombre", "Apellidop", "Apellidom", "Sexo", "Domicilio",
            "Colonia", "Curp", "Telefono", "Email", "Entidad_Federativa", "Situacion", "Condicionado",
            "Edad", "Tutor","Telefono_contacto", "Email_contacto", "F_nacimiento","N_lista", "Foto",
            "Estudios_contacto","Ocupacion_contacto", "Tutor_2", "Telefono_contacto_2","Email_contacto_2",
            "Estudios_contacto_2", "Ocupacion_contacto_2", "Discapacidad", "Indigena", "Especifique","f_baja","f_registro")
        return db.query("Alumno", columnas, "Situacion <> 'BAJA'", null, null, null, "Apellidop COLLATE NOCASE , apellidom COLLATE NOCASE, Nombre COLLATE NOCASE")
    }


    public fun getStudentforQualify(date:String):Cursor{

        /*val sQuery = "SELECT Folio, N_lista, nombre,sexo, situacion, f_baja, f_registro " +
                "FROM Alumno " +
                "WHERE (f_baja >= '$date') " +
                "OR (f_baja = '' and f_registro <= '$date') " +
                "order by N_lista;"*/

        val sQuery = "SELECT Folio, N_lista, nombre,sexo, situacion, f_baja, f_registro " +
                     "FROM Alumno "+
                     "WHERE f_registro <= '$date' and f_baja ='' " +
                     "UNION " +
                     "SELECT Folio, N_lista, nombre,sexo, situacion, f_baja, f_registro " +
                     "FROM Alumno " +
                     "WHERE f_baja >= '$date' and f_registro <= '$date' " +
                     "ORDER BY N_lista;"
        return db.rawQuery(sQuery, null)
    }


    public fun getStudentForAsistence(date:String):Cursor{
        /*val sQuery = "SELECT Nombre, apellidop, apellidom, Alumno.Folio, Foto,  sexo, N_lista, telefono_contacto, Email_contacto " +
                "FROM Alumno " +
                "WHERE (f_baja >= '$date' OR (f_baja = '' AND f_registro <= '$date')) " +
                "ORDER BY N_lista;"*/

        val sQuery = "SELECT Nombre, apellidop, apellidom, Alumno.Folio, Foto,  sexo, N_lista, telefono_contacto, Email_contacto " +
                "FROM Alumno "+
                "WHERE f_registro <= '$date' and f_baja ='' " +
                "UNION " +
                "SELECT Nombre, apellidop, apellidom, Alumno.Folio, Foto,  sexo, N_lista, telefono_contacto, Email_contacto " +
                "FROM Alumno " +
                "WHERE f_baja >= '$date' and f_registro <= '$date' " +
                "ORDER BY N_lista;"
        //Toast.makeText(context,sQuery, Toast.LENGTH_SHORT).show()
        return db.rawQuery(sQuery, null)
    }



    public fun obtenerAllsinBajas(): Cursor {

        val columnas = arrayOf("Folio", "Nombre", "Apellidop", "Apellidom", "Sexo", "Domicilio",
            "Colonia", "Curp", "Telefono", "Email", "Entidad_Federativa", "Situacion", "Condicionado",
            "Edad", "Tutor","Telefono_contacto", "Email_contacto", "F_nacimiento","N_lista", "Foto",
            "Estudios_contacto","Ocupacion_contacto", "Tutor_2", "Telefono_contacto_2","Email_contacto_2",
            "Estudios_contacto_2", "Ocupacion_contacto_2", "Discapacidad", "Indigena", "Especifique", "f_baja","f_registro")
        return db.query("Alumno", columnas, null, null, null, null, "Apellidop COLLATE NOCASE , apellidom COLLATE NOCASE, Nombre COLLATE NOCASE")
    }

    public fun getListStudentStadistic1(date:String):Cursor{

        val sQuery = "Select N_lista, Nombre, Apellidop, Apellidom, Curp, F_registro, Sexo, Estadistica, Situacion, F_baja, Entidad_Federativa " +
                     "From Alumno " +
                     "where EDAD > 0 and f_registro <= '$date' " +
                     "order by Apellidop COLLATE NOCASE , apellidom COLLATE NOCASE, Nombre COLLATE NOCASE"
        //Toast.makeText(context, sQuery, Toast.LENGTH_SHORT).show()
        return db.rawQuery(sQuery, null)
    }

    public fun getListStudentStadisticFormer():Cursor{

        val sQuery = "Select N_lista, Nombre, Apellidop, Apellidom, Curp, F_registro, Sexo, Estadistica, Situacion, F_baja, Entidad_Federativa " +
                "From Alumno " +
                "order by Apellidop COLLATE NOCASE , apellidom COLLATE NOCASE, Nombre COLLATE NOCASE"
        return db.rawQuery(sQuery, null)
    }

    public fun getListStudentStadistic2(begin:String, end:String, star:String):Cursor{

        val sQuery = "Select N_lista, Nombre, Apellidop, Apellidom, Curp, F_registro, Sexo, Estadistica, Situacion, F_baja, Entidad_Federativa " +
                "From Alumno " +
                "where Estadistica > 0 and f_registro <= '$end' " +
                "except " +
                "Select N_lista, Nombre, Apellidop, Apellidom, Curp, F_registro, Sexo, Estadistica, Situacion, F_baja, Entidad_Federativa " +
                "From Alumno " +
                "where Estadistica > 0 and f_baja between '2023-08-01' and '$begin' " +
                "order by Apellidop COLLATE NOCASE , apellidom COLLATE NOCASE, Nombre COLLATE NOCASE"
        //Toast.makeText(context, sQuery, Toast.LENGTH_SHORT).show()
        return db.rawQuery(sQuery, null)
    }

    public fun getEstadisticEntidad_Federativa():Cursor{
        val sQuery = "SELECT COUNT (*) as total,Sexo , edad, Entidad_Federativa " +
                     "FROM Alumno " +
                     "where Edad > 0 " +
                     "GROUP BY EDAD, SEXO, ENTIDAD_FEDERATIVA " +
                     "ORDER BY edad, sexo, Entidad_Federativa"
        return db.rawQuery(sQuery, null)
    }



    public fun get_All_by_folio(): Cursor {
        val columnas = arrayOf("Folio", "Nombre", "Apellidop", "Apellidom", "Sexo", "Domicilio",
            "Colonia", "Curp", "Telefono", "Email", "Entidad_Federativa", "Situacion", "Condicionado", "Edad", "Tutor","Telefono_contacto", "Email_contacto", "F_nacimiento","N_lista")
        return db.query("Alumno", columnas, null, null, null, null, "Folio")
    }


    fun getEstadisticBySexoAge():MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, SITUACION " +
                "FROM Alumno " +
                "WHERE Edad > 0 " +
                "GROUP BY Sexo, edad, Situacion " +
                "ORDER BY edad, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }


    fun getEstadisticBySexo1(date: String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, Estadistica, SITUACION " +
                "FROM Alumno " +
                "where Edad > 0  and F_registro <= '$date' " +
                "GROUP BY Sexo, Estadistica, Situacion " +
                "ORDER BY Estadistica, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticRepit(date1: String?, date2:String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, SITUACION " +
                "FROM Alumno " +
                "where Edad > 0 and  Situacion = 'REPETIDOR' and F_registro between '$date1' and '$date2' " +
                "GROUP BY Sexo, edad " +
                "ORDER BY edad, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticBySexo2(date1: String?, date2:String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, folio, Estadistica, Situacion, f_baja,f_registro " +
                     "FROM Alumno " +
                     "where Edad > 0 and F_registro <= '$date1' "+
                     "except "+
                     "SELECT Sexo, folio, Estadistica, Situacion, f_baja,f_registro "+
                     "FROM Alumno " +
                     "where Edad > 0 and F_baja <>'' and F_baja <= '$date1' "+
                     "ORDER BY Estadistica, sexo;"

        /*val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, situacion " +
                     "FROM Alumno " +
                     "where Edad > 0 and f_baja = '' and F_registro <= '$date1' " +
                     "GROUP BY Sexo, edad " +
                     "ORDER BY edad, sexo"*/
        return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticUnsuscribe2(date1: String?, date2:String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, Estadistica, SITUACION " +
                "FROM Alumno " +
                "WHERE Edad > 0 and F_baja between '$date1' and '$date2' " +
                "GROUP BY Sexo, edad " +
                "ORDER BY edad, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticUnsuscribe(date: String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, Estadistica, SITUACION " +
                "FROM Alumno " +
                "WHERE Edad > 0 and F_baja <>'' and F_baja <= '$date' " +
                "GROUP BY Sexo, Estadistica " +
                "ORDER BY Estadistica, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticRegister(date1: String?, date2:String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, Estadistica, SITUACION " +
                "FROM Alumno " +
                "WHERE Edad > 0 AND Situacion IN ('ALTA') and F_registro between '$date1' and '$date2' " +
                "GROUP BY Sexo, Estadistica, Situacion " +
                "ORDER BY Estadistica, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }


    fun getEstadisticFinaly(date1: String?, date2:String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, SITUACION " +
                "FROM Alumno " +
                "WHERE Edad > 0 "+
                "GROUP BY Sexo, edad " +
                "ORDER BY edad, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticEtnicoAndDiscapacitado():MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, SITUACION " +
                "FROM Alumno " +
                "WHERE Edad > 0 "+
                "GROUP BY Sexo, edad " +
                "ORDER BY edad, sexo"
         return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticExistence(date1: String?, date2:String?):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, SITUACION " +
                "FROM Alumno " +
                "where Edad > 0 and  Situacion <> 'BAJA'" +
                "GROUP BY Sexo, edad " +
                "ORDER BY edad, sexo"
        return  fillData(db.rawQuery(sQuery,null))
    }

    fun getEstadisticApproved(calification:Int):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, Total as calificacion " +
                "FROM Alumno, calificaciones_finales " +
                "WHERE Edad > 0 AND Alumno.Folio = calificaciones_finales.Folio and Total > $calification and Situacion != 'BAJA' " +
                "GROUP BY Sexo, edad " +
                "ORDER BY edad, sexo "
        return fillData(db.rawQuery(sQuery, null))
    }

    fun getEstadisticFailed(calification:Int):MutableList<MutableMap<String, Any>>{
        val sQuery = "SELECT Sexo, COUNT(*) AS Total, edad, Total as calificacion " +
                "FROM Alumno, calificaciones_finales " +
                "WHERE Edad > 0 AND Alumno.Folio = calificaciones_finales.Folio and Total <= $calification and Situacion != 'BAJA' " +
                "GROUP BY Sexo, edad " +
                "ORDER BY edad, sexo"
        return fillData(db.rawQuery(sQuery, null))
    }


    private fun fillData(cursor: Cursor):MutableList<MutableMap<String, Any>>{
        val listaEstadistic: MutableList<MutableMap<String, Any>> = mutableListOf()
        if (cursor.moveToFirst()){
            do{
                val newData1 = mutableMapOf<String, Any>()
                newData1["sexo"] = cursor.getString(0)
                newData1["total"] = cursor.getString(1)
                newData1["edad"] = cursor.getString(2)
                newData1["situacion"] = cursor.getString(3)
                listaEstadistic.add(newData1)
            }while (cursor.moveToNext())
            cursor.close()
        }
        return  listaEstadistic
    }



    public  fun borrarAlumno(Folio:Int):Boolean {
        try {
            val eliminar = "Delete from Alumno  where Folio  = $Folio"

            db!!.execSQL(eliminar)
            error = "Se elimino correctamente los datos del alumno"

            return true

        } catch (Ex: Exception) {
            error = Ex.message.toString()
            //error = "Hubo un error al eliminar el alumno"
            return false

        }
    }

    public fun deleteStudentsTemp(){
        try {
            val sQuery = "Delete from StudentTemp"

            db.execSQL(sQuery)
        }catch (Ex:Exception){Toast.makeText(context, Ex.message.toString(), Toast.LENGTH_LONG).show()}


    }

    public fun ordenarporN_lista(){
        /*val columnas = arrayOf("Folio", "Nombre", "Apellidop")
        val orden = db.query("Alumno", columnas, "Situacion <>'BAJA'", null, null, null, "Apellidop COLLATE NOCASE , apellidom COLLATE NOCASE, Nombre COLLATE NOCASE")*/
        val sQuery = "select Folio, Nombre, Apellidop, N_lista " +
                "From Alumno " +
                "where Situacion <>'BAJA' " +
                "order by Apellidop COLLATE NOCASE , apellidom COLLATE NOCASE, Nombre COLLATE NOCASE"
        val orden = db.rawQuery(sQuery, null)
        var count = 1
        if (orden.moveToFirst()){
            do{
                val actializar = "update Alumno set N_lista = " +(count).toString() +" where Folio = "+ orden.getString(0)
                count ++
                //error = actializar
                //Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                db!!.execSQL(actializar)

            }while (orden.moveToNext())
        }
        orden.close()
    }

    public fun deleteTable(){
        try {
            val BorrarTabla = "DROP TABLE IF EXISTS Alumno"
            db!!.execSQL(BorrarTabla)
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }


    }


    public fun onCreateTableAlumno() {
        try {
            val crearTabla = "CREATE TABLE if not exists Alumno " +
                    "(Folio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_lista INTEGER, Nombre TEXT, Apellidop TEXT, Apellidom TEXT, Sexo INTEGER, Domicilio TEXT, Colonia TEXT, Municipio TEXT, CP TEXT, Telefono TEXT, Email TEXT, " +
                    "Entidad_Federativa TEXT, Curp TEXT, F_nacimiento TEXT, Situacion TEXT, Condicionado INTEGER, Edad INTEGER, Estadistica INTEGER, Foto BLOB, Email_Contacto TEXT, Tutor TEXT, Telefono_contacto TEXT)"
            error = crearTabla
            db!!.execSQL(crearTabla)

            // Realiza una copia temporal de los datos existentes
            val tempTable = "CREATE TABLE Alumno_temp AS SELECT * FROM Alumno"
            db!!.execSQL(tempTable)

            // Elimina la tabla original
            val dropTable = "DROP TABLE Alumno"
            db!!.execSQL(dropTable)

            // Crea la tabla nueva con el tipo de campo "Foto" como BLOB
            val createNewTable = "CREATE TABLE Alumno " +
                    "(Folio INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "N_lista INTEGER, Nombre TEXT, Apellidop TEXT, Apellidom TEXT, Sexo INTEGER, Domicilio TEXT, Colonia TEXT, Municipio TEXT, CP TEXT, Telefono TEXT, Email TEXT, " +
                    "Entidad_Federativa TEXT, Curp TEXT, F_nacimiento TEXT, Situacion TEXT, Condicionado INTEGER, Edad INTEGER, Estadistica INTEGER, Foto BLOB, Email_Contacto TEXT, Tutor TEXT, Telefono_contacto TEXT)"
            db!!.execSQL(createNewTable)

            // Copia los datos de la tabla temporal a la nueva tabla
            val copyData = "INSERT INTO Alumno SELECT * FROM Alumno_temp"
            db!!.execSQL(copyData)

            // Elimina la tabla temporal
            val dropTempTable = "DROP TABLE Alumno_temp"
            db!!.execSQL(dropTempTable)

            error = "Se actualizó correctamente la estructura de la tabla Alumno"
        } catch (ex: Exception) {
            error = ex.message.toString()
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