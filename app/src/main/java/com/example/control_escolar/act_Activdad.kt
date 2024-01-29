package com.example.control_escolar

import BDLayer.*
import LogicLayer.VibratePhone
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_act_activdad.*
import kotlinx.android.synthetic.main.listaactividad.view.*
import kotlinx.android.synthetic.main.view_buscar_por_materia.view.*
import kotlinx.android.synthetic.main.viewdatosactividad.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class act_Activdad : AppCompatActivity() {

    private var dateSelect: Calendar = Calendar.getInstance()
    var back = 0
    var next = 0

    var listaactivdad = ArrayList<DatosActivdad>()
    var listaseleccion = ArrayList<Int>()
    lateinit var notificationReceiver: NotificationReceiver
    lateinit var TareasBD: TareasBD
    lateinit var sendEMail : sendEMail
    var status = "write"
    var especial = 0
    var position_matter = 0
    var position_activity = 0
    var posicion = 0
    var nuevo = 0
    var arrayMaterias = ArrayList<String>()
    var arrayActividades = ArrayList<String>()
    var arrayValueActivitys = ArrayList<String>()
    lateinit  var pdf : crearPDF
    var terminada = false
    lateinit var materias: Cursor
    lateinit var actividades:Cursor
    lateinit var view: View
    var terminadas = 0
    var fecha = ""
    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        notificationReceiver =  NotificationReceiver()
        this.supportActionBar?.title = "Actividades"
        this.supportActionBar?.subtitle = Nombre_Escuela.getAlias()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_activdad)
        TareasBD = TareasBD(this)
        sendEMail = sendEMail(this)
        pdf = crearPDF(this)
        //CargarTareas()
        //putWeekDays()
        loadActivitysByFecha(today())
        txt_initial_actividad.isVisible = false
        //txt_initial_actividad.isVisible = nuevo == 0

        listaActividad.setOnItemClickListener {adapterView, view, i, l ->
            posicion = i
            try {Calificar()}catch (Ex:Exception){Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()}

        }
        listaActividad.setOnItemLongClickListener(AdapterView.OnItemLongClickListener { arg0, arg1, pos, id -> // TODO Auto-generated method stub
            posicion = pos
            popupMenus(arg1.image_opcionesActividad)
            true
        })

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation_add_types_activity)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_back->{
                    dateSelect.add(Calendar.DAY_OF_YEAR, -1)
                    //updateDateDisplay()
                    loadActivitysByFecha(updateDateDisplay())
                    true
                }
                R.id.nav_next->{
                    dateSelect.add(Calendar.DAY_OF_YEAR, 1)
                    loadActivitysByFecha(updateDateDisplay())
                    true
                }
                else-> false
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sup_actividad, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnm_Todas-> CargarTodasTareas()
            //R.id.btnm_Actividad_Especial-> goToActivity_Especial()
            R.id.btnm_crearactividad -> {agregarActividadNormal()
            status = "write"}
            R.id.btnm_buscar_materia -> BuscarpoMateria()
            R.id.btnm_Fecha -> CargarTareasFecha()
            R.id.btnm_solo_incompletas -> { terminadas = 0
                CargarTareas()}
            R.id.btnm_solo_terminadas->{ terminadas = 1
                CargarTareas()}
            R.id.btnm_help_activity-> showHelp()
            R.id.btnm_Actividad_Especial->agregarActividadEspecial()
            R.id.btnm_Actividad_Excel->exportActivitiesExcel(fecha)
        }
        return  super.onOptionsItemSelected(item)
    }
    private fun popupMenus(v: View) {
        try {
            val popupMenus = PopupMenu(this,v)//mostramos el menu correspondiente a la cituacion de la actividad
            if(listaactivdad[posicion].terminada == 0 && listaactivdad[posicion].especial == 0) popupMenus.inflate(R.menu.menu_actividad)
            if (listaactivdad[posicion].terminada == 1 && listaactivdad[posicion].especial == 0) popupMenus.inflate(R.menu.menu_actividad_2)
            if (listaactivdad[posicion].terminada == 1 && listaactivdad[posicion].especial == 1) popupMenus.inflate(R.menu.menu_actividad_2)
            if (listaactivdad[posicion].especial == 1 && listaactivdad[posicion].terminada == 0) popupMenus.inflate(R.menu.menu_actividad_3)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.Posponer->Posponer()
                    R.id.Eliminar_Actividad-> Eliminar_Actividad(listaactivdad[posicion].c_actividad)
                    R.id.menu_add_student-> addStudents()
                    R.id.Reenviar-> enviaractividadpdf(listaactivdad[posicion].fecha,listaactivdad[posicion].nombre,listaactivdad[posicion].tipo,listaactivdad[posicion].materia)
                    R.id.Modificar-> modificarActividad()
                    R.id.Terminar_Actividad->Terminar()
                    R.id.Reenviar_Calificacion-> enviarcalificacion(listaactivdad[posicion].c_actividad)
                }
                true
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }catch (Ex: Exception){
            Toast.makeText(v.context,Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun addStudents() {
        val intent = Intent(this, act_Actividad_Especial::class.java)
        //intent.putExtra("numpartial","0")
        intent.putExtra("c_actividad",listaactivdad[posicion].c_actividad)
        startActivityForResult(intent, 1)
    }

    fun CargarMaterias():ArrayList<String>{
        var  arrayMaterias = ArrayList<String>()
        try {

            materias = TareasBD.obtenerMAterias()
            if(materias.moveToFirst()){
                do{
                    arrayMaterias.add(materias.getString(1))

                }while (materias.moveToNext())
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        //asignamos la lista obtenida a una global para poder obtener valores posteriormente
        this.arrayMaterias = arrayMaterias
        return arrayMaterias
    }

    fun CargarAspectos():ArrayList<String>{
        var  arrayActividades = ArrayList<String>()
        try {

            var count = 0
            materias.moveToPosition(view.cbx_Materias_actividad.getSelectedItemPosition())
            //materias.moveToFirst()
            //Toast.makeText(this, materias.getString(0),Toast.LENGTH_SHORT).show()
            if (Nombre_Escuela.get_tipo() > 1) actividades = TareasBD.obtenerActividad(materias.getString(0))
            if (Nombre_Escuela.get_tipo() == 1) actividades = TareasBD.obtenerActividad("0")

            if(actividades.moveToFirst()){
                do{
                    arrayActividades.add(actividades.getString(1))
                    arrayValueActivitys.add(actividades.getString(2))
                }while (actividades.moveToNext())
            }
            val adaptadorA = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,arrayActividades)
            view.cbx_Aspectos_actividad.adapter = adaptadorA
            this.arrayActividades = arrayActividades
            //view.cbx_Aspectos_actividad.setSelection(1)
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return arrayActividades
    }



    fun CargarTareas(){
        try {
            var tareas = TareasBD.obtenerAll(terminadas)
            val totalEstudiantes = AlumnosBD(this).getStudentforQualify(fecha)
            listaactivdad.clear()
            if(tareas.moveToFirst()){
                do{
                    listaactivdad.add(DatosActivdad(tareas.getString(2), tareas.getString(4), tareas.getString(1),tareas.getString(3),
                        tareas.getString(0), tareas.getString(6).toInt(), tareas.getString(7).toInt(),tareas.getString(5).toInt(),tareas.getString(8).toInt(), tareas.getInt(9),totalEstudiantes.count))
                    nuevo += 1
                }while (tareas.moveToNext())
            }
            listaActividad.adapter =  adapterActividad(this, listaactivdad)
            tareas.close()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun CargarTareasFecha(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            try {
                var tareas = TareasBD.obtenerAllFecha(TareasBD.convertdate("$year-${monthOfYear+1}-$dayOfMonth"))
                fecha = TareasBD.convertdate("$year-${monthOfYear+1}-$dayOfMonth")
                val totalEstudiantes = AlumnosBD(this).getStudentforQualify(fecha)
                listaactivdad.clear()
                if(tareas.moveToFirst()){
                    do{
                        listaactivdad.add(DatosActivdad(tareas.getString(2), tareas.getString(4),
                            tareas.getString(1),tareas.getString(3),tareas.getString(0),
                            tareas.getString(6).toInt(), tareas.getString(7).toInt(), tareas.getString(5).toInt(), tareas.getString(8).toInt(), tareas.getInt(9),totalEstudiantes.count))
                    }while (tareas.moveToNext())
                }
                listaActividad.adapter =  adapterActividad(this, listaactivdad)
                tareas.close()
            }catch (Ex:Exception){
                Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }, year, month, day)
        dpd.show()
    }

    @SuppressLint("SuspiciousIndentation")
    fun loadActivitysByFecha(date:String){
        var tareas = TareasBD.obtenerAllFecha(TareasBD.convertdate(date))
        val totalEstudiantes = AlumnosBD(this).getStudentforQualify(fecha)
            try {
                fecha = TareasBD.convertdate(date)
                listaactivdad.clear()
                if(tareas.moveToFirst()){
                    do{
                        listaactivdad.add(DatosActivdad(tareas.getString(2), tareas.getString(4),
                            tareas.getString(1),tareas.getString(3),tareas.getString(0),
                            tareas.getString(6).toInt(), tareas.getString(7).toInt(), tareas.getString(5).toInt(), tareas.getString(8).toInt(), tareas.getInt(9),totalEstudiantes.count))
                            nuevo += 1
                    }while (tareas.moveToNext())
                }
                listaActividad.adapter =  adapterActividad(this, listaactivdad)
                //tareas.close()
            }catch (Ex:Exception){
                Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
        finally {
            //tareas.close()
        }

    }


    fun CargarTodasTareas(){
        try {
            var tareas = TareasBD.obtenerAll()
            val totalEstudiantes = AlumnosBD(this).getStudentforQualify(fecha)
            listaactivdad.clear()
            if(tareas.moveToFirst()){
                do{
                    listaactivdad.add(DatosActivdad(tareas.getString(2), tareas.getString(4), tareas.getString(1),
                    tareas.getString(3),tareas.getString(0), tareas.getString(6).toInt(), tareas.getString(7).toInt(), tareas.getString(5).toInt(),tareas.getString(8).toInt(), tareas.getInt(9),totalEstudiantes.count))
                }while (tareas.moveToNext())
            }
            listaActividad.adapter =  adapterActividad(this, listaactivdad)
            tareas.close()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun CargarTareasporMateria(c_materia:String){
        try {
            var tareas = TareasBD.obtenerAllbymaterias(terminadas, c_materia)
            val totalEstudiantes = AlumnosBD(this).getStudentforQualify(fecha)
            listaactivdad.clear()
            if(tareas.moveToFirst()){
                do{
                    listaactivdad.add(DatosActivdad(tareas.getString(2), tareas.getString(4),
                        tareas.getString(1),tareas.getString(3),tareas.getString(0),
                        tareas.getString(6).toInt(), tareas.getString(7).toInt(), tareas.getString(5).toInt(),tareas.getString(8).toInt(),tareas.getInt(9),totalEstudiantes.count))
                }while(tareas.moveToNext())
            }
            listaActividad.adapter =  adapterActividad(this, listaactivdad)
            tareas.close()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun AgregarActividad(requestCode: Int)
    {
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.viewdatosactividad, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        if (CargarMaterias().count() == 0) {
            Toast.makeText(this, "Debe crear materias para agregar una actividad", Toast.LENGTH_LONG).show()
            dialog.hide()
            Materias()
            return
        }
        if (CargarAspectos().count() == 0) {
            Toast.makeText(this, "Debe estructurar la calificacion para agregar actividades", Toast.LENGTH_LONG).show()
            dialog.hide()
            Encuadre()
            return
        }

        //cargamos las materias
        val adaptadorM = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,arrayMaterias)
        view.cbx_Materias_actividad.adapter =  adaptadorM


        //cargamos los aspectos de de la materia seleccionada
        view.cbx_Materias_actividad.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                try {
                    position_matter = p2
                    CargarAspectos()
                    //posicionamoes el tipo de actividad donde corresponde para actualizar el registro
                    view.cbx_Aspectos_actividad.setSelection(position_activity)
                    view.txt_detalle_porcentaje_Actividad.text = "% de "+ arrayValueActivitys[position_activity]
                    //view.txt_Porcentaje_Actividad.hint = arrayValueActivitys[position_activity]+"%"
                }
                catch (ex: Exception){
                     Toast.makeText(applicationContext, "Falta agregar el encuadre",Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //obtenemos las actividades de la materia seleccionada
        view.cbx_Aspectos_actividad.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                try {
                    if (view.switch_especificar_alumnos.isVisible) view.txt_detalle_porcentaje_Actividad.text = "de 100%"
                    else view.txt_detalle_porcentaje_Actividad.text = "% de "+ arrayValueActivitys[p2]
                }
                catch (ex: Exception){
                    //Toast.makeText(applicationContext, ex.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //para saber si la actividad es para todos o en general
        view.switch_especificar_alumnos.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                especial = 1
                val intent = Intent(this, act_Actividad_Especial::class.java)
                intent.putExtra("c_actividad", "0")
                startActivityForResult(intent, requestCode)
            } else {
                especial = 0
            }
        }

        //if (listaseleccion == null) view.switch_especificar_alumnos.isChecked = false

        view.btn_AgregarActividad.setOnClickListener {
        var fecha_entrega = ""
        try {
            val datePicker = view.dtp_Fecha_Actividad
            fecha_entrega = datePicker.year.toString() + "-" +(datePicker.month + 1).toString() +"-"+datePicker.dayOfMonth.toString()
            actividades.moveToPosition(view.cbx_Aspectos_actividad.getSelectedItemPosition())
            materias.moveToPosition(view.cbx_Materias_actividad.getSelectedItemPosition())
            if (view.edit_Nombre_Actividad.text.toString().isNotEmpty() && view.edit_Porcentaje_Actividad.text.toString().isNotEmpty()){
                if (status == "write"){

                    if(especial == 0){
                        if(TareasBD.InsertarTareas(view.edit_Nombre_Actividad.text.toString(), view.edit_Porcentaje_Actividad.text.toString(),
                            fecha_entrega,materias.getString(0),actividades.getString(0), especial)) {
                            VibratePhone.vibrarTelefono(this, 500)
                            dialog.hide()
                        }
                        Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
                    }

                    if (especial == 1){
                        if (view.edit_Porcentaje_Actividad.text.toString().toInt() > 0 && listaseleccion.isNotEmpty()) {
                            TareasBD.InsertarTareas(view.edit_Nombre_Actividad.text.toString(),view.edit_Porcentaje_Actividad.text.toString(),
                                fecha_entrega,materias.getString(0),actividades.getString(0),especial)
                            GuardarAcitivdadEspecial(TareasBD.getLastPrimaryKey(),fecha_entrega,listaseleccion)
                            VibratePhone.vibrarTelefono(this, 500)
                            dialog.hide()

                        }else Toast.makeText(this, "Para actividad especial se necesita especificar los alumnos y el valor de la misma", Toast.LENGTH_LONG).show()
                    }
                    }
                if (status == "update"){
                    TareasBD.actualizarTareas(view.edit_Nombre_Actividad.text.toString(),view.edit_Porcentaje_Actividad.text.toString(),
                        fecha_entrega, materias.getString(0),actividades.getString(0),listaactivdad[posicion].c_actividad)
                    VibratePhone.vibrarTelefono(this, 500)
                    dialog.hide()
                }
                    //createRemember()
                    //CargarTareas()
                    loadActivitysByFecha(fecha_entrega)
                    //Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
                    //ENVIAMOS CORREO ELECTRONICO DE AVISO DE ACTIVIDAD
                    if (view.cbx_Avisar.isChecked){
                        try {
                            if (especial == 0)
                                 enviaractividadpdf(fecha_entrega,view.edit_Nombre_Actividad.text.toString(),view.cbx_Aspectos_actividad.selectedItem.toString(),view.cbx_Materias_actividad.selectedItem.toString())

                            if (especial == 1)
                                enviarActividadEspecialPdf(fecha_entrega,view.edit_Nombre_Actividad.text.toString(),view.cbx_Aspectos_actividad.selectedItem.toString(),view.cbx_Materias_actividad.selectedItem.toString())
                                //Toast.makeText(this, sendEMail.error, Toast.LENGTH_SHORT).show()


                        }catch (Ex:Exception){
                            Toast.makeText(this,"Verifique que todos los alumno que selecciono tengan correo para informar de la actividad",Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            else Toast.makeText(this, "Indique todos los campos", Toast.LENGTH_SHORT).show()

        }catch (Ex:Exception)
        {
            Toast.makeText(this, "Favor de indicar todos los campos", Toast.LENGTH_SHORT).show()
            //Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        }
    }

    fun agregarActividadNormal(){
        AgregarActividad(1)
        val data = fecha.split('-')
        view.dtp_Fecha_Actividad.init(data[0].toInt(),data[1].toInt() - 1 ,data[2].toInt(),null)
        status = "write"
        especial = 0
        view.switch_especificar_alumnos.isVisible = false
        view.txt_especificar_porcentaje_Actividad.isVisible = false
        view.linear_porcentaje_Actividad.isVisible = false
    }

    fun modificarActividad(){
        AgregarActividad(1)
        status = "update"
        especial = 0

        var position = this.arrayMaterias.indexOfFirst {it == listaactivdad[posicion].materia}
        view.cbx_Materias_actividad.setSelection(position)//position_activity = this.arrayActividades.indexOfFirst { it == listaactivdad[posicion].tipo}
        position_activity = this.arrayActividades.indexOfFirst { it == listaactivdad[posicion].tipo}
        view.cbx_Aspectos_actividad.setSelection(position_activity)
        view.cbx_Materias_actividad.isEnabled = false
        view.cbx_Aspectos_actividad.isEnabled = false
        view.cbx_Avisar.isVisible = false
        view.switch_especificar_alumnos.isVisible = false
        view.btn_AgregarActividad.setText("Modificar")
        view.barra_Actividad.setText("Moidificar actividad Existente")
        view.edit_Nombre_Actividad.setText(listaactivdad[posicion].nombre)
        view.edit_Porcentaje_Actividad.setText(listaactivdad[posicion].porciento.toString())
        view.linear_porcentaje_Actividad.isVisible = false
        val data = listaactivdad[posicion].fecha.toString().split('-')
        view.dtp_Fecha_Actividad.init(data[0].toInt(),data[1].toInt() - 1 ,data[2].toInt(),null)
    }

    fun agregarActividadEspecial(){
        AgregarActividad(1)
        status = "write"
        val data = fecha.split('-')
        view.dtp_Fecha_Actividad.init(data[0].toInt(),data[1].toInt() - 1 ,data[2].toInt(),null)
        especial = 1
        view.switch_especificar_alumnos.isVisible = true
        view.switch_especificar_alumnos.setTextColor(Color.MAGENTA)
        view.btn_AgregarActividad.setText("Agregar")
        view.barra_Actividad.setText("Actividad Especial como Extraordinario o Recuperacion")
        view.barra_Actividad.setBackgroundColor(Color.MAGENTA)
        view.txt_detalle_porcentaje_Actividad.text = ""
        view.txt_especificar_porcentaje_Actividad.isVisible = false
    }

    fun select_cmateria():String{
        var id_materia = ""
        if (Nombre_Escuela.get_tipo() > 1){
            materias.moveToPosition(view.cbx_Materias_actividad.getSelectedItemPosition())
            id_materia = materias.getString(0)
        }
        else id_materia = "0"
        return id_materia
    }

    fun Calificar()
    {
        var inten = Intent(this, act_Calificar::class.java)
        inten.putExtra("nombre_actividad",listaactivdad[posicion].nombre)
        inten.putExtra("fecha_actividad",listaactivdad[posicion].fecha)
        inten.putExtra("c_actividad",listaactivdad[posicion].c_actividad)
        inten.putExtra("nombre_materia",listaactivdad[posicion].materia)
        inten.putExtra("tipo",listaactivdad[posicion].tipo)
        inten.putExtra("valor",listaactivdad[posicion].valor.toString())
        inten.putExtra("especial",listaactivdad[posicion].especial.toString())
        inten.putExtra("porcentaje",listaactivdad[posicion].porciento.toString())
        //Toast.makeText(this,listaactivdad[posicion].valor.toString(), Toast.LENGTH_SHORT).show()
        startActivity((inten))
    }

    fun Calificar_Todos(calificacion:String){
        try {
            TareasBD.borrarCalificaicion(listaactivdad[posicion].c_actividad)
            var count = 0
            //Toast.makeText(this, Nombre_Escuela.Alumnos.count.toString(), Toast.LENGTH_SHORT).show()
            if (Nombre_Escuela.Alumnos.moveToFirst()){
                while (count < Nombre_Escuela.Alumnos.count) {
                    TareasBD.Calificar_Todos(listaactivdad[posicion].c_actividad, Nombre_Escuela.Alumnos.getString(0),listaactivdad[posicion].fecha,calificacion,"0")
                    //Nombre_Escuela.Calificaciones[count]  = calificacion
                    Nombre_Escuela.Alumnos.moveToNext()
                    count++
                }
                Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
            }

        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    fun Posponer() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Obtener la fecha seleccionada
            val fechaSeleccionada = "$year-${monthOfYear + 1}-$dayOfMonth"
            listaactivdad[posicion].fecha = fechaSeleccionada
            if (TareasBD.Posponer(listaactivdad[posicion].fecha, listaactivdad[posicion].c_actividad)) {
                listaActividad.adapter = adapterActividad(this, listaactivdad)
            }
            Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
        }, year, month, day)
        dpd.show()
    }

    fun Terminar(){
        android.app.AlertDialog.Builder(this)
            .setTitle("Terminar Actividad")
            .setIcon(R.drawable.ic_importar_lista)
            .setMessage("¿Desea dar por terminada la actividad")
            .setPositiveButton("SI"){
                    dialog,_->
                dialog.dismiss()
                if(TareasBD.Terminarda("1",listaactivdad[posicion].c_actividad)){
                    enviarcalificacion(listaactivdad[posicion].c_actividad)
                    listaactivdad.removeAt(posicion)
                    listaActividad.adapter = adapterActividad(this, listaactivdad)
                }
                Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("NO"){
                    dialog,_->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun Eliminar_Actividad(c_actividad:String){
        android.app.AlertDialog.Builder(this)
            .setTitle("Eliminar "+listaactivdad[posicion].nombre)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setMessage("¿Seguro de realizar esta acción?")
            .setPositiveButton("Yes"){
                    dialog,_->
                if(TareasBD.borrarTareas(c_actividad)){
                    TareasBD.deleteActivityEspecial(c_actividad.toInt())
                    listaactivdad.removeAt(posicion)
                    listaActividad.adapter =  adapterActividad(this, listaactivdad)
                    Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No"){
                    dialog,_->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    fun enviarAdjuntoemail(rutaArchivo: String, numeroTelefono: String) {
        // Obtenemos la URI del archivo y la URI del número de teléfono de WhatsApp
        val uriArchivo = Uri.parse("file://$rutaArchivo")
        val uriNumero = Uri.parse("whatsapp://send?phone = $numeroTelefono")

        // Creamos el Intent para enviar el archivo como adjunto
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            putExtra(Intent.EXTRA_STREAM, uriArchivo)
            data = uriNumero
            type = "application/pdf"
        }
        // Iniciamos la actividad para enviar el archivo
        startActivity(intent)
    }



    fun createRemember(){
        notificationReceiver.createChannel(this)
        notificationReceiver.setDailyNotifications(this)
    }


    fun enviaractividadpdf(fecha:String, nombre:String, tipo:String, materia:String){
        if(pdf.pdf_Actividad_pendiente(fecha, "NOMBRE: "+  nombre.uppercase() +"\nTIPO: "
                    +tipo.uppercase() +"\nMATERIA: "+materia.uppercase()+"\n")){
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            val file = File(pdf.ruta +"Actividad.pdf")
            val uri = FileProvider.getUriForFile(this, "com.example.control_escolar.fileprovider", file)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Compartir archivo PDF"))
        }
        else Toast.makeText(this,"Error al crear el archivo para compartir", Toast.LENGTH_SHORT).show()
    }

    fun enviarActividadEspecialPdf(fecha:String, nombre:String, tipo:String, materia:String){
        //creamos el pdf para enviar
        if(pdf.pdf_Actividad_pendiente(fecha, "NOMBRE: "+  nombre.uppercase() +"\nTIPO: "
                    +tipo.uppercase() +"\nMATERIA: "+materia.uppercase()+"\n")){
            sendEMail.adjuntar("Actividad","")

        //recorremos todos los alumnos seleccionados a los que se les enviara la actividad especial
            GlobalScope.launch {
                for (i in 0 until listaseleccion.size) {
                    val folio = listaseleccion[i]
                    val email = foundEmail(folio)
                    if (email != null) sendEMail.sendwhitattach(
                        "Actividad Especial",
                        "Actividad de regularizacíon",
                        email
                    )
                }
            }
        }
        else Toast.makeText(this,"Error al crear el archivo para compartir", Toast.LENGTH_SHORT).show()
    }

    private fun foundEmail(folio:Int):String?{
        var email: String? = null
        if (Nombre_Escuela.Alumnos.moveToFirst()){
            do {
                if (Nombre_Escuela.Alumnos.getInt(0) == folio){
                    email = Nombre_Escuela.Alumnos.getString(16)
                    break
                }
            }while (Nombre_Escuela.Alumnos.moveToNext())
        }
        return email
    }


    fun enviarcalificacion(c_actividad:String){
        try {
            val nombre = listaactivdad[posicion].nombre
            val tipo = listaactivdad[posicion].tipo
            val materia = listaactivdad[posicion].materia
            val fecha = listaactivdad[posicion].fecha
            android.app.AlertDialog.Builder(this).setTitle("Enviar calificacion ").setIcon(R.drawable.ic_baseline_warning_24).setMessage("¿Cual medio necesitas usar para avisar la calificaciones?")
                .setPositiveButton("EMAIL") { dialog, _ ->
                    //enviamos las calificaciones por correo
                    Toast.makeText(this,"No cierre la aplicacion, la puede usar sin problema",Toast.LENGTH_LONG).show()
                    calificacionEmail(c_actividad, nombre, tipo, materia)
                    dialog.dismiss()
                }

                .setNegativeButton("WHATSAPP") { dialog, _ ->
                    //para enviar las calificaciones por whatsapp
                    calificacionWhatsapp(c_actividad)
                    dialog.dismiss()
                }
                .create()
                .show()
        }catch (Ex:Exception) {
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun calificacionEmail(c_actividad: String, nombre: String, tipo: String, materia: String){
        //currutina o subproceso
        var calificadas = TareasBD.obternercalificacion(c_actividad)
        //GlobalScope.launch {
        if (calificadas.moveToFirst()) {
        do{//para enviar las calificaciones por email
            if (pdf.calificacion_especifica(nombre,tipo,materia,calificadas.getString(5).toInt(), aviso_calificacion(calificadas.getString(5).toInt()),calificadas.getString(6))){
                sendEMail.adjuntar("Calificacion","")
                sendEMail.sendwhitattach("Calificacion Actividad ${nombre.uppercase()}","",calificadas.getString(4))
            }
        }while (calificadas.moveToNext())
        }else Toast.makeText(this, "No hay correos para avisar sobre las calificaciones de los alumnos", Toast.LENGTH_LONG).show()
        calificadas.close()
    }

    fun calificacionWhatsapp(c_actividad:String){
        val cursor = TareasBD.obternercalificacionReprobatoria(c_actividad)
        if (cursor.moveToFirst()){
            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_VIEW)
            do {
                val mensaje = "CALIFICACION DE LA ACTIVIDAD\n"+
                "\n"+
                "Alumno: ${cursor.getString(6)}\n"+
                "\n"+
                "Materia: ${cursor.getString(2)}\n"+
                "\n"+
                "Actividad: ${cursor.getString(3)}\n"+
                "\n"+
                "Nombre: ${cursor.getString(1)}\n"+
                "\n"+
                "Calificacion: ${cursor.getString(5)} / 100\n"+
                "\n"+
                "${aviso_calificacion(cursor.getInt(5))}\n"+
                "\n"+
                "\n"
                val url = "whatsapp://send?phone=" +cursor.getString(7)+ "&text= $mensaje"
                sendIntent.setData(Uri.parse(url))
                startActivity(sendIntent)
            }while (cursor.moveToNext())
        }
        cursor.close()
    }

    fun aviso_calificacion(valor: Int): String {
        var resultado = ""
        when (valor) {
            0 -> resultado = "No entregó o no hizo el trabajo."
            in 1..59 -> resultado = "Trabajo incompleto"
            in 60..70 -> resultado = "Corregir Errores"
            else -> resultado = "Actividad Terminada"
        }
        return resultado
    }

    fun BuscarpoMateria(){
        val builder = AlertDialog.Builder(this)
        view = layoutInflater.inflate(R.layout.view_buscar_por_materia, null)
        builder.setView(view)
        var seleccion = false
        val dialog = builder.create()
        dialog.show()

        if (terminadas == 1) view.cbx_Terminada.isChecked = true
        else view.cbx_Terminada.isChecked = false

        view.RMateria.setOnClickListener{
            view.cbx_buscar_materias.isVisible = true
            seleccion = true
        }

        view.RTodo.setOnClickListener{
            view.cbx_buscar_materias.isVisible = false
            seleccion = false
        }
        //Cargamos las materias
        val adaptadorM = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,CargarMaterias())
        view.cbx_buscar_materias.adapter =  adaptadorM

        view.btn_Buscar_Actividad.setOnClickListener{
            if (view.cbx_Terminada.isChecked) terminadas = 1
            else terminadas = 0
            if (seleccion){
                materias.moveToPosition(view.cbx_buscar_materias.selectedItemPosition)
                CargarTareasporMateria(materias.getString(0))
            }
            else CargarTareas()
            dialog.hide()
        }
    }


    private fun goToActivity_Especial(){
        var intent = Intent(this, act_Actividad_Especial::class.java)
        startActivity((intent))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Aquí puedes obtener la lista del Intent
                val extras = data?.extras
                if (extras != null) {
                    val lista = extras.getSerializable("seleccion_alumnos") as? ArrayList<Int>
                    listaseleccion = lista!!
                }
                if (listaseleccion.size == 0) view.switch_especificar_alumnos.isChecked = false
            }
        }
    }

    fun GuardarAcitivdadEspecial(c_actividad: Int,fecha: String, listaasistencia:ArrayList<Int>){
        try {
            var cont = 0
                while (cont < listaasistencia.count()){
                        TareasBD.InsertarActividadesEspeciales(c_actividad, listaasistencia[cont], 0,0)
                    cont ++
                }

        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHelp() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.view_help_activitys,null)
        /**set view*/
        val addDialog = android.app.AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            dialog.dismiss()

        }

        addDialog.create()
        addDialog.show()
    }

    private fun Encuadre(){
        var inten = Intent(this, act_Aspectos::class.java)
        inten.putExtra("nombremateria","En General")
        inten.putExtra("c_materia",0)
        startActivity((inten))
    }
    private fun Materias(){
        var inten = Intent(this, act_Materias::class.java)
        startActivity((inten))
    }

    fun getWeekdaysFromDate(dateText: String): List<Date> {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateText)
        val calendar = Calendar.getInstance()
        calendar.time = date

        val weekdays = mutableListOf<Date>()

        // Obtener el día de la semana correspondiente al lunes
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday = calendar.time
        weekdays.add(monday)

        // Obtener el día de la semana correspondiente al martes
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        val tuesday = calendar.time
        weekdays.add(tuesday)

        // Obtener el día de la semana correspondiente al miércoles
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        val wednesday = calendar.time
        weekdays.add(wednesday)

        // Obtener el día de la semana correspondiente al jueves
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        val thursday = calendar.time
        weekdays.add(thursday)

        // Obtener el día de la semana correspondiente al viernes
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        val friday = calendar.time
        weekdays.add(friday)
        return weekdays
    }
    fun putWeekDays(){
        val  days = getWeekdaysFromDate(today())
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        //var weekdayText = format.format(days[0])
        val nav_days = findViewById<BottomNavigationView>(R.id.navigation_add_types_activity)
        nav_days.menu.findItem(R.id.nav_lunes)?.title = format.format(days[0])
        nav_days.menu.findItem(R.id.nav_martes)?.title = format.format(days[1])
        nav_days.menu.findItem(R.id.nav_miercoles)?.title = format.format(days[2])
        nav_days.menu.findItem(R.id.nav_jueves)?.title = format.format(days[3])
        nav_days.menu.findItem(R.id.nav_viernes)?.title = format.format(days[4])
    }

    private fun today():String{
        var dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var date = dateFormat.format(Date())
        return date
    }

    private fun updateDateDisplay():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(dateSelect.time)

        // Obtener el nombre del día de la semana
        val calendar = Calendar.getInstance()
        calendar.time = dateSelect.time
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(dateSelect.time)

        val nav_days = findViewById<BottomNavigationView>(R.id.navigation_add_types_activity)
        fecha = formattedDate
        nav_days.menu.findItem(R.id.nav_today)?.title = "$dayName $formattedDate"

        return formattedDate

    }


    private fun exportActivitiesExcel(date:String){
        try {
            val excel = createExcel(this)
            //excel.getActivitysByDay(date)
            excel.getActivitysByDay("$date", TareasBD.getActivitysByDay(date),TareasBD.getCalificationsByDayM(date),8)
            //Toast.makeText(this,TareasBD.error, Toast.LENGTH_SHORT).show()
        }catch(Ex:Exception){ Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()}
    }
}









