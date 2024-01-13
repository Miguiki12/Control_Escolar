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



    public fun validar_curp(curp:String):Boolean {
        var valida = true
        try {
        if (curp.length > 0)
        {

        val sexo = curp.substring(10,11)
            val estado = curp.substring(11,13)
            var fecha_año = 0
            var dateFormat = SimpleDateFormat("yy")
            var DateTime = dateFormat.format(Date())
            if (isNumeric(curp.substring(4,6)))
            {
                fecha_año = DateTime.toInt()
                año = (curp.substring(4,6)).toInt()

                if (año == DateTime.toInt()){
                    año = 0
                }
                else
                {
                    edad = DateTime.toInt() - año;

                }
            }
            else
            {
                año = 0;
            }

            if (isNumeric(curp.substring(8,10)))
            {
                dia = curp.substring(8,10).toInt();
                if (dia > 31) dia = 0;
            }
            else
            {
                dia = 0;
            }

            if (isNumeric(curp.substring(6,8)))
            {

                mes = curp.substring(6,8).toInt()
                if (mes > 12) mes = 0
                else
                {
                     dateFormat = SimpleDateFormat("MM")
                     DateTime = dateFormat.format(Date())

                    if (DateTime.toInt() < mes) edad = edad - 1
                }
            }
            else
            {
                mes = 0
            }

            if (dia == 0 || mes == 0 || año == 0){
                error = "Curp no valida en la fecha de nacimiento"
                valida = false
            }
            else{
                var dateFormat = SimpleDateFormat("yyyy")
                var DateTime = dateFormat.format(Date())
                if (año > fecha_año){
                    fecha_año =  1900 + año
                    edad = DateTime.toInt() - fecha_año
                }
                else{
                    fecha_año = 2000 + año
                }
                FNacimiento = fecha_año.toString() + "-" + mes.toString() + "-" + dia.toString()

            }

            if (sexo != "H" && sexo != "M"){
                error = "Curp no valida en la letra H o M del sexo"
                valida = false
            }

            Sexo = sexo == "H"
            if (estados_curp(estado) == "NO"){
                error = "Error en la clave Renapo (Entidad federativa) de la curp que esta escribiendo"
                valida = false
            }
            else{
                entidad_federativa = estados_curp(estado)
            }

            return valida

        }
        }catch (Ex:Exception){
            error = Ex.message.toString()
            valida = false
        }
        return valida
    }


    fun calcularEdad(fechaNacimiento: String): Int {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")
        val fechaNac = formatoFecha.parse(fechaNacimiento)

        val calHoy = Calendar.getInstance()
        val calNac = Calendar.getInstance()
        calNac.time = fechaNac

        var edad = calHoy.get(Calendar.YEAR) - calNac.get(Calendar.YEAR)

        // Verificar si el mes de cumpleaños ya pasó en el año actual
        if (calHoy.get(Calendar.MONTH) < calNac.get(Calendar.MONTH)) {
            edad--
        } else if (calHoy.get(Calendar.MONTH) == calNac.get(Calendar.MONTH)) {
            // Si el mes es el mismo, verificar si el día de cumpleaños ya pasó
            if (calHoy.get(Calendar.DAY_OF_MONTH) < calNac.get(Calendar.DAY_OF_MONTH)) {
                edad--
            }
        }

        return edad
    }

    fun edadEstadistica(curp: String, dia: Byte, mes: Byte):Int {
        if (validar_curp(curp)){
        val cal = Calendar.getInstance()
        val año1 = "20" + curp.substring(4, 6)
        val año2 = cal.get(Calendar.YEAR).toString()
        var edad = año2.toInt() - año1.toInt()
        val mes1 = curp.substring(6, 8)
        val mes2 = mes.toInt()
        edad = if (mes2 > mes1.toInt()) edad else if (mes2 == mes1.toInt()) {
            val dia1 = curp.substring(8, 10)
            val dia2 = dia.toInt()
            if (dia2 >= dia1.toInt()) edad else edad - 1
        } else {
            edad - 1
        }

        if (edad < 4) {
            println("La fecha que ha registrado es incongruente, favor de verificarla.")
        }
        Estadistica = edad
        }
        return  edad
    }

}