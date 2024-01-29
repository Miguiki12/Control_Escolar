package BDLayer

import java.text.SimpleDateFormat
import java.util.*


class Curp {

    var Sexo = false
    var entidad_federativa = ""
    var año = 0
    var mes = 0
    var dia = 0
    var edad = 0
    var Estadistica = 0
    var error = ""
    var FNacimiento = ""


    private fun estados_curp(clave: String): String {
        return when (clave) {
            "AS" -> {
                "AGUASCALIENTES"
            }
            "BC" -> {
                "BAJA CALIFORNIA"
            }
            "BS" -> {
                "BAJA CALIFORNIA SUR"
            }
            "CC" -> {
                "CAMPECHE"
            }
            "CL" -> {
                "COAHUILA"
            }
            "CM" -> {
                "COLIMA"
            }
            "CS" -> {
                "CHIAPAS"
            }
            "CH" -> {
                "CHIHUAHUA"
            }
            "DF" -> {
                "DISTRITO FEDERAL"
            }
            "DG" -> {
                "DURANGO"
            }
            "GT" -> {
                "GUANAJUATO"
            }
            "GR" -> {
                "GUERRERO"
            }
            "HG" -> {
                "HIDALGO"
            }
            "JC" -> {
                "JALISCO"
            }
            "MC" -> {
                "MÉXICO"
            }
            "MN" -> {
                "MICHOACÁN"
            }
            "MS" -> {
                "MORELOS"
            }
            "NT" -> {
                "NAYARIT"
            }
            "NL" -> {
                "NUEVO LEÓN"
            }
            "OC" -> {
                "OAXACA"
            }
            "PL" -> {
                "PUEBLA"
            }
            "QT" -> {
                "QUERÉTARO"
            }
            "QR" -> {
                "QUINTANA ROO"
            }
            "SP" -> {
                "SAN LUIS POTOSÍ"
            }
            "SL" -> {
                "SINALOA"
            }
            "SR" -> {
                "SONORA"
            }
            "TC" -> {
                "TABASCO"
            }
            "TS" -> {
                "TAMAULIPAS"
            }
            "TL" -> {
                "TLAXCALA"
            }
            "VZ" -> {
                "VERACRUZ"
            }
            "YN" -> {
                "YUCATÁN"
            }
            "ZS" -> {
                "ZACATECAS"
            }
            "NE" -> {
                "NACIDO EN EL EXTRANJERO"
            }
            else -> "NO"
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



    public fun validar_curp(curp:String) {
        if (validarCurp(curp)){
             if (getSex2(curp)) Sexo = true
             if (!getSex2(curp)) Sexo = false

              edad = getAges(curp)
              entidad_federativa = getEntidadFederativa(curp)
              FNacimiento = getBirthday(curp)

        }

    }


    fun validarCurp(curp: String): Boolean {
        // Verificar que la longitud de la CURP sea la correcta
        if (curp.length != 18) {
            return false
        }


        // Verificar que los primeros 4 caracteres sean letras
        if (!curp.substring(0, 4).matches(Regex("[A-Z]{4}"))) {
            return false
        }

        // Verificar que los siguientes 6 caracteres sean dígitos
        if (!curp.substring(4, 10).matches(Regex("\\d{6}"))) {


            return false
        }

        // Verificar que el siguiente carácter sea una letra


        if (!curp[10].isLetter()) {
            return false
        }

        // Verificar que los siguientes 8 caracteres sean dígitos o letras
        if (!curp.substring(11, 18).matches(Regex("[0-9A-Z]{7}"))) {
            return false
        }

        // Si pasa todas las validaciones, la CURP es válida
        return true
    }

    /**
     * Obtiene la fecha de nacimiento a partir de una CURP válida.
     * @param curp La CURP válida.
     * @return La fecha de nacimiento en formato "yyyy-MM-dd" o una cadena vacía si la CURP no es válida.
     */
    fun getBirthday(curp: String): String {
        //if (validarCurp(curp)) {
        var fechaNacimiento = ""
        try {

            val año = curp.substring(4, 6).toInt()
            val mes = curp.substring(6, 8).toInt()
            val dia = curp.substring(8, 10).toInt()

            // Ajustar el año según el siglo (1900 o 2000)
            val añoCompleto = if (año >= 0 && año <= 21) 2000 + año else 1900 + año

            // Crear la fecha de nacimiento
            fechaNacimiento = "$añoCompleto-$mes-$dia"

            // Verificar si la fecha es válida
            if (esFechaValida(fechaNacimiento)) {
                return fechaNacimiento
            }
        }catch (Ex:Exception){
            fechaNacimiento = "No asignada"
        }
        //}

        return fechaNacimiento
    }

    /**
     * Obtiene los años cumplidos a partir de una CURP válida.
     * @param curp La CURP válida.
     * @return Los años cumplidos o -1 si la CURP no es válida.
     */
    fun getAges(curp: String): Int {
        //if (validarCurp(curp)) {
        var añosCumplidos = 0
        try{

            val añoNacimiento = curp.substring(4, 6).toInt()
            val mesNacimiento = curp.substring(6, 8).toInt()
            val diaNacimiento = curp.substring(8, 10).toInt()

            // Ajustar el año según el siglo (1900 o 2000)
            val añoCompleto = if (añoNacimiento >= 0 && añoNacimiento <= 21) 2000 + añoNacimiento else 1900 + añoNacimiento

            val fechaNacimiento = Calendar.getInstance()
            fechaNacimiento.set(añoCompleto, mesNacimiento - 1, diaNacimiento) // Meses en Calendar van de 0 a 11

            // Obtener la fecha actual
            val fechaActual = Calendar.getInstance()

            // Calcular la diferencia de años
            añosCumplidos = fechaActual.get(Calendar.YEAR) - fechaNacimiento.get(Calendar.YEAR)

            // Ajustar si aún no ha cumplido años en el año actual
            if (fechaActual.get(Calendar.DAY_OF_YEAR) < fechaNacimiento.get(Calendar.DAY_OF_YEAR)) {
                añosCumplidos--
            }

        }catch (Ex:Exception){
            añosCumplidos = 0
        }

            return añosCumplidos
        //}

        return -1
    }


    /**
     * Obtiene la entidad federativa a partir de una CURP válida.
     * @param curp La CURP válida.
     * @return La entidad federativa correspondiente o una cadena vacía si la CURP no es válida.
     */
    fun getEntidadFederativa(curp: String): String {
        if (validarCurp(curp)) {
            val claveEntidad = curp.substring(11, 13)
            return estados_curp(claveEntidad)
        }

        return ""
    }


    /**
     * Obtiene el sexo a partir de una CURP válida.
     * @param curp La CURP válida.
     * @return El sexo ("H" para hombre, "M" para mujer) o una cadena vacía si la CURP no es válida.
     */
    fun getSex(curp: String): String {
        if (validarCurp(curp)) {
            return curp.substring(10, 11)
        }

        return ""
    }

    fun getSex2(curp: String):Boolean {
        var valor = false
        try {
            val sexo = curp.substring(10, 11)
            if (sexo == "M") valor = false
            if (sexo == "H") valor = true
        }catch (Ex:Exception){
            valor = false
        }

        return valor
    }



    // Función auxiliar para verificar si una fecha es válida
    private fun esFechaValida(fecha: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false

        return try {
            dateFormat.parse(fecha)
            true
        } catch (e: Exception) {
            false
        }
    }





}