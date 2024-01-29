package LogicLayer

import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

object Formats {
    fun convertdate(fecha:String ):String{
        val dateString = fecha
        val inputFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = inputFormat.parse(dateString)
        val outputString = outputFormat.format(date)
        return  outputString
    }

    fun ageEstadisticWhitDate(date: String, dia: Byte, mes: Byte):Int {
        var edad = 0
        if (date.isNotEmpty()){
            val fecha = date.split('-')
            val cal = Calendar.getInstance()
            val año1 = fecha[0]//"20" + fecha[0][2] + fecha[0][3]
            val año2 = cal.get(Calendar.YEAR).toString()
                edad = año2.toInt() - año1.toInt()
            val mes1 = fecha[1]
            val mes2 = mes.toInt()
            edad = if (mes2 > mes1.toInt()) edad else if (mes2 == mes1.toInt()) {
                val dia1 = fecha[2]
                val dia2 = dia.toInt()
                if (dia2 >= dia1.toInt()) edad else edad - 1
            } else {
                edad - 1
            }
            if (edad < 4) {
                println("La fecha que ha registrado es incongruente, favor de verificarla.")
            }

        }
        return  edad
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun calcularEdad(fechaNacimiento: String, fechaDeReferencia: String): Int {
        val fechaNac = LocalDate.parse(fechaNacimiento)
        val fechaRef = LocalDate.parse(fechaDeReferencia)

        val periodo = Period.between(fechaNac, fechaRef)

        return periodo.years
    }


    fun compareDate(fecha: String, fecha2:String):Boolean {

        val valor1 = fecha.replace("-","").toInt()
        val valor2 = fecha2.replace("-","").toInt()

        return valor1 > valor2

    }

    /**
    * Verifica si la fecha de nacimiento es válida.
    * @param fechaNacimiento La fecha de nacimiento en formato "yyyy-MM-dd".
    * @return `true` si la fecha de nacimiento es válida, `false` en caso contrario.
    */
    public fun isValidateBirthDate(fecha: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false

        return try {
            dateFormat.parse(fecha)
            true
        } catch (e: Exception) {
            false
        }
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

    fun obtenerRangoDeAnios(): String {
        val calendar = Calendar.getInstance()
        val mesActual = calendar.get(Calendar.MONTH) + 1 // Sumamos 1 ya que los meses en Calendar van de 0 a 11

        val anioActual = calendar.get(Calendar.YEAR)

        val rangoAnios: String

        if (mesActual >= Calendar.AUGUST && mesActual <= Calendar.DECEMBER) {
            // Estamos en agosto o después, el rango es "anioActual - anioActual + 1"
            rangoAnios = "$anioActual - ${anioActual + 1}"
        } else {
            // Estamos en enero a agosto, el rango es "anioActual - anioActual - 1"
            rangoAnios = "${anioActual - 1}-$anioActual"
        }

        return rangoAnios
    }


    fun getCiclo(): String {
        val calendario = Calendar.getInstance()
        val añoActual = calendario.get(Calendar.YEAR)
        val mesActual = calendario.get(Calendar.MONTH) + 1 // Sumar 1 porque en Calendar los meses van de 0 a 11

        val añoAnterior: Int
        val añoSiguiente: Int

        if (mesActual >= Calendar.SEPTEMBER) {
            añoAnterior = añoActual
            añoSiguiente = añoActual + 1
        } else {
            añoAnterior = añoActual - 1
            añoSiguiente = añoActual
        }

        val años = "$añoAnterior-$añoSiguiente"
        return años
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun get_Day(fecha: String):Int {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd") // formato de la fecha en la cadena de texto
        val fecha = formatoFecha.parse(fecha) // convertir la cadena de texto en un objeto Date
        val calendar = android.icu.util.Calendar.getInstance() // obtener una instancia de la clase Calendar
        calendar.time = fecha // establecer la fecha en el objeto Calendar
        val diaDelMes = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH) // obtener el día del mes como un número entero
        return diaDelMes
    }

    fun setDatePicker(date:String, v: DatePicker){
        val data = date.split('-')
        v.init(data[0].toInt(),data[1].toInt() - 1 ,data[2].toInt(),null)
    }


    fun compareDates(targetDate: String, dateActivity: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        try {
            val targetDateAsDate = sdf.parse(targetDate)
            val dateActivityAsDate = sdf.parse(dateActivity)

            if (dateActivityAsDate != null && targetDateAsDate != null) {
                // Comparar targetDate con dateActivity
                return dateActivityAsDate.compareTo(targetDateAsDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejo de errores, por ejemplo, si el formato de la fecha es incorrecto
        }
        return 0
    }









}