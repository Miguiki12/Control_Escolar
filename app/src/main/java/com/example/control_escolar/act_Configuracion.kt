package com.example.control_escolar

import BDLayer.SettingsBD
import LogicLayer.Formats
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_act_configuracion.*
import kotlinx.android.synthetic.main.custom_date_picker.*
import java.text.SimpleDateFormat
import java.util.*

class act_Configuracion : AppCompatActivity() {
    lateinit var setting: SettingsBD
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_configuracion)
        this.supportActionBar?.title = "Configuración"
        this.supportActionBar?.subtitle = Nombre_Escuela.getAlias()
        setting = SettingsBD(this)

        setting.deletetable()
        setting.onCreate(setting.writableDatabase)
        //setting.addColumnN_teacher()
        try {getSettings()}catch (Ex:Exception){}
        txt_example_pass.setOnClickListener { open_example_pass() }
        btn_select_estadistic_1.setOnClickListener {showDatePickerDialog(1,txt_select_estadistic_1.text.toString())}
        btn_select_estadistic_2.setOnClickListener {showDatePickerDialog(2,txt_select_estadistic_2.text.toString())}
        btn_select_estadistic_3.setOnClickListener {showDatePickerDialog(3,txt_select_estadistic_3.text.toString())}
        btn_select_estadistic_4.setOnClickListener {showDatePickerDialog(4,txt_select_estadistic_4.text.toString())}
        btn_select_estadistic_5.setOnClickListener {showDatePickerDialog(5,txt_select_estadistic_5.text.toString())}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_save_settings -> new_Settings()

        }
        return  super.onOptionsItemSelected(item)
    }

    fun getSettings():Int{
        val config = setting.getAllSettings()
        if (config.moveToFirst()){
            txt_day_setting.setText(config.getString(1))
            txt_mounth_setting.setText(config.getString(2))
            if (config.getString(4).toString().toInt() == 1) cbx_conditionated_setting.isChecked = true
            if (config.getString(5).toString().toInt() == 1) cbx_asist_setting.isChecked = true
            txt_email_teacher.setText(config.getString(7))
            txt_password_teacher.setText(config.getString(8))
            txt_email_direction.setText(config.getString(9))
            txt_decimals_setting.setText(config.getString(10))
            txt_name_teacher.setText(config.getString(11))
            txt_select_estadistic_1.text = config.getString(12)
            txt_select_estadistic_2.text = config.getString(13)
            txt_select_estadistic_3.text = config.getString(14)
            txt_select_estadistic_4.text = config.getString(15)
            txt_select_estadistic_5.text = config.getString(16)
        }
        config.close()
       return config.count
    }

    fun new_Settings(){

        if (setting.validations(txt_day_setting.text.toString(), txt_mounth_setting.text.toString(), txt_decimals_setting.text.toString())){
            Toast.makeText(this,"Poner las cantidades por lo menos en cero", Toast.LENGTH_SHORT).show()
        }
        else{
            var condicionado = 0
            var suspendido = 0
            if (cbx_conditionated_setting.isChecked) condicionado = 1
            if (cbx_asist_setting.isChecked) suspendido = 1
            setting.deleteAll()
            setting.iDia = txt_day_setting.text.toString().toInt()
            setting.iMes = txt_mounth_setting.text.toString().toInt()
            setting.iDecimales = txt_decimals_setting.text.toString().toInt()
            setting.iCondicionado = condicionado
            setting.ias_Suspendido = suspendido
            setting.sEmail = txt_email_teacher.text.toString()
            setting.sContrasena = txt_password_teacher.text.toString()
            setting.sEmail_direccion = txt_email_direction.text.toString()
            setting.sN_maestro = txt_name_teacher.text.toString()
            setting.sEstadistica1 = txt_select_estadistic_1.text.toString()
            setting.sEstadistica2 = txt_select_estadistic_2.text.toString()
            setting.sEstadistica3 = txt_select_estadistic_3.text.toString()
            setting.sEstadistica4 = txt_select_estadistic_4.text.toString()
            setting.sEstadistica5 = txt_select_estadistic_5.text.toString()
            setting.newSettings()
            Toast.makeText(this,setting.error, Toast.LENGTH_SHORT).show()
        }
    }
    fun open_example_pass(){
        val url = "https://www.youtube.com/watch?v=RpSQQIGTpTM"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showDatePickerDialog(position: Int, fecha: String) {
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
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                // Puedes hacer algo con la fecha seleccionada, como mostrarla en un TextView
                when (position) {
                    1 -> txt_select_estadistic_1.text = Formats.convertdate(selectedDate)
                    2 -> txt_select_estadistic_2.text = Formats.convertdate(selectedDate)
                    3 -> txt_select_estadistic_3.text = Formats.convertdate(selectedDate)
                    4 -> txt_select_estadistic_4.text = Formats.convertdate(selectedDate)
                    5 -> txt_select_estadistic_5.text = Formats.convertdate(selectedDate)
                }
            },
            year,
            month,
            day
        )

        try {
            // Obtén el DatePicker personalizado del DatePickerDialog
            val customDatePicker = datePickerDialog.datePicker
            when (position) {
                1 -> customDatePicker.setBackgroundColor(0xFFFFC501.toInt()) // Amarillo
                2 -> customDatePicker.setBackgroundColor(0xFF1CB0F6.toInt()) // Azul
                3 -> customDatePicker.setBackgroundColor(0xFF57CB05.toInt()) // Verde
            }
            // Mostrar el diálogo de selección de fecha
            datePickerDialog.show()
        } catch (ex: Exception) {
            Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

