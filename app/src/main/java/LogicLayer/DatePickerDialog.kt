package LogicLayer

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_act_configuracion.*
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialog(var context: Context, var fecha: String, private val color: Int) {

    constructor(context: Context, fecha: String) : this(context, fecha, 0) // Valor por defecto para el color

    @RequiresApi(Build.VERSION_CODES.N)
    public fun showCalendar(): String {
        var date = ""
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val initialDate = dateFormat.parse(fecha) ?: Date()

        // Obtiene el año, mes y día de la fecha inicial
        calendar.time = initialDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crea un DatePickerDialog y configúralo
        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                // Puedes hacer algo con la fecha seleccionada, como mostrarla en un TextView
                date = selectedDate
                fecha = selectedDate

            },
            year,
            month,
            day
        )

        try {
            // Obtén el DatePicker personalizado del DatePickerDialog
            val customDatePicker = datePickerDialog.datePicker

            // Configura el color de fondo del DatePicker
            customDatePicker.setBackgroundColor(color)

            // Mostrar el diálogo de selección de fecha
            datePickerDialog.show()
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

        return date
    }
}
