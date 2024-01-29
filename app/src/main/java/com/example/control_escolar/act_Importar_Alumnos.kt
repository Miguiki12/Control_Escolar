package com.example.control_escolar

import BDLayer.AlumnosBD
import BDLayer.Curp
import BDLayer.SettingsBD
import LogicLayer.Formats
import LogicLayer.VibratePhone
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.PopupWindowCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_act_activdad.*
import kotlinx.android.synthetic.main.activity_act_importar_alumnos.*
import kotlinx.android.synthetic.main.listaalumnos.view.*
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import java.text.SimpleDateFormat
import java.util.*


class act_Importar_Alumnos : AppCompatActivity() {
    //var requestCode = 0
    //var requestCode = 0
    val REQUEST_CODE_ACTION_1 = 1
    val REQUEST_CODE_ACTION_2 = 2
    var listaalumnos = ArrayList<DatosAlumnos>()
    lateinit var Alumno: AlumnosBD
    var folio = "0"
    var espacio = ""
    var posicion = 0
    var empezar  = true
    var f_estadistica = ""
    var f_inscripcion = ""
    private val handler = Handler()
    var NOMBRE_COMPLETO = false
    var APELLIDO_PATERNO = false
    var CURP = false
    var NOMBRE = false

    lateinit var Nombre :  ArrayList<String>
    lateinit var NombreC :  ArrayList<String>
    lateinit var apellidop :  ArrayList<String>
    lateinit var apellidom :  ArrayList<String>
    lateinit var entidad :  ArrayList<String>
    lateinit var sexo :  ArrayList<String>
    lateinit var edad :  ArrayList<Int>
    lateinit var fnacimiento: ArrayList<String>
    lateinit var Curps: ArrayList<String>
    lateinit var Numeros: ArrayList<String>
    lateinit var Correos: ArrayList<String>
    lateinit var Calificaciines: kotlin.collections.ArrayList<String>
    lateinit  var alumnos: Cursor
    lateinit var curp: Curp
    var agregar = "no"
    private lateinit var navNombre: MenuItem
    private lateinit var navArchivo: MenuItem
    private lateinit var navCurp: MenuItem
    private lateinit var navTelefono: MenuItem
    private lateinit var navCorreo: MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_importar_alumnos)
        Alumno = AlumnosBD(this)
        Nombre = ArrayList()
        NombreC = ArrayList()
        apellidop = ArrayList()
        apellidom = ArrayList()
        entidad = ArrayList()
        edad = ArrayList()
        Curps = ArrayList()
        sexo = ArrayList()
        curp = Curp()
        Numeros = ArrayList()
        Correos = ArrayList()
        fnacimiento = ArrayList()
        Calificaciines = ArrayList()
        getDateStadistic()
        getDateRegister()
        var  bundle = intent.extras
        agregar = bundle?.getString("agregar").toString()


        lista_Importar.setOnItemClickListener{ adapterView, view, i, l ->
            posicion = i
            popupMenus(view.image_opcionesAlumno)
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation_date)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_date_estadistic->{

                    //val f_estadistica = SettingsBD(this).getDateStadistic(0)
                    //iniciamos el calendario apartir de esa fecha
                    changeDateStadistic(f_estadistica, 1)
                    true
                }
                R.id.nav_date_income->{

                    //iniciamos el calendario apartir de esa fecha
                    changeDateStadistic(f_inscripcion, 2)
                    true
                }
                else-> false
            }

        }

       howBegining()

        /*ViewCompat.setOnApplyWindowInsetsListener(myView) { _, insets ->
            // Aplicar ajustes de ventana si es necesario
            insets
        }*/

        /*val toolbar: Toolbar = findViewById(R.id.toolbar) ?: throw IllegalStateException("Toolbar cannot be null")
        setSupportActionBar(toolbar)
        // Puedes personalizar la Toolbar según tus necesidades
        supportActionBar?.setDisplayShowTitleEnabled(false) // Desactivar el título predeterminado*/

    }


    private fun deshabilitarElementosMenu() {

        navCurp.isEnabled = true
        navTelefono.isEnabled = true
        navCorreo.isEnabled = true
        navNombre.isEnabled = false
        navArchivo.isEnabled = false

    }

    private fun habilitarElementosMenu() {
        navCurp.isEnabled = false
        navTelefono.isEnabled = false
        navCorreo.isEnabled = false
    }

    fun howBegining(){

        if (agregar == "no"){
            this.supportActionBar?.title = "Importar Alumnos"
            this.supportActionBar?.subtitle = "Estadistica $f_estadistica"

        }

        if (agregar == "si"){
            this.supportActionBar?.title = "Agregar Datos"
            this.supportActionBar?.subtitle = ""


            CargarAlumnos()
        } else {

        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_importar, menu)
         navArchivo = menu?.findItem(R.id.nav_import_recreap)!!
         navNombre = menu?.findItem(R.id.nav_Nombre_Completo)!!
         navCurp = menu?.findItem(R.id.nav_Curp)!!
         navTelefono = menu?.findItem(R.id.nav_Telefono)
         navCorreo = menu?.findItem(R.id.nav_Correo)


        if (agregar == "no") habilitarElementosMenu()
        if (agregar == "si") deshabilitarElementosMenu()

        // Deshabilitar elementos


        return super.onCreateOptionsMenu(menu)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.nav_Importar_Lista-> {
                android.app.AlertDialog.Builder(this)
                    .setTitle("Importar Lista")
                    .setIcon(R.drawable.ic_importar_lista)
                    .setMessage("¿Esta seguro de importar esta lista de alumnos?")
                    .setPositiveButton("Yes"){
                            dialog,_->
                        if (agregar == "si") Actualizar_Datos()
                        if (agregar == "no") importarAlumnosActuales()
                        //Actualizar_Datos()

                        val intent = Intent()
                        //intent.putExtra("clave_datos", datos)
                        setResult(RESULT_OK, intent)
                        finish()
                        dialog.dismiss()
                        /*var inten = Intent(this, act_Alumno::class.java)
                        startActivity((inten))
                        dialog.dismiss()*/
                        }

                    .setNegativeButton("No"){
                            dialog,_->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            R.id.nav_import_recreap-> openFolder()
            R.id.nav_Nombre_Completo -> Especificar()
            R.id.nav_Curp -> pegarCurps()
            R.id.nav_Telefono -> pegarNumeros()
            R.id.nav_Correo -> pegarCorreo()
            R.id.action_import_file_former->{
                openFolderFormer()

            }
        }
        return  super.onOptionsItemSelected(item)
    }

    fun changeDateStadistic(date: String, change:Int){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = android.app.DatePickerDialog(
            this,
            android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                try {
                    val fecha = Formats.convertdate("$year-${monthOfYear+1}-$dayOfMonth")
                    this.supportActionBar?.subtitle = "Estadistica $fecha"
                    SettingsBD(this).updteDateEstadistic(fecha, 0)

                    if (change == 1) {
                        val bottomNavigation =
                            findViewById<BottomNavigationView>(R.id.navigation_date)
                        val navDateStadistic =
                            bottomNavigation.menu.findItem(R.id.nav_date_estadistic)
                        f_estadistica = fecha
                        navDateStadistic.title = "Estadistica $fecha"
                    }
                    if (change == 2) {
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation_date)
                        val navDateIncome = bottomNavigation.menu.findItem(R.id.nav_date_income)
                        f_inscripcion = fecha
                        navDateIncome.title = "Registro $fecha"
                    }

                } catch (Ex: Exception) {
                    Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
                }
            },
            year,
            month,
            day
        )
        Formats.setDatePicker(date, dpd.datePicker)
        dpd.show()
    }



    private fun showTooltip(anchorView: View, tooltipText: String) {
        try {

        val tooltipView = layoutInflater.inflate(R.layout.tooltip_layout, null)
        val tooltipTextView = tooltipView.findViewById<View>(R.id.tooltipTextView)
        TooltipCompat.setTooltipText(tooltipTextView, tooltipText)

        val popupWindow = PopupWindow(tooltipView, anchorView.width, anchorView.height)
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        PopupWindowCompat.showAsDropDown(popupWindow, anchorView, 0, -anchorView.height, Gravity.NO_GRAVITY)

        // Cerrar el tooltip después de un tiempo (por ejemplo, 2 segundos)
            handler.postDelayed({
                if (!isFinishing && !isDestroyed) {
                    popupWindow.dismiss()
                }
            }, 2000)

        }catch (Ex:Exception){Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_LONG).show()}
    }


    @SuppressLint("SuspiciousIndentation")
    fun getDateStadistic(){
      val estaditica =   SettingsBD(this).getDateStadistic(0)
        if (!estaditica.isNullOrEmpty()) f_estadistica = estaditica
        else{
            val ciclo = Formats.obtenerRangoDeAnios().split('-')
            f_estadistica = "${ciclo[0]}-09-01"
        }
        val bottomNavigation =  findViewById<BottomNavigationView>(R.id.navigation_date)
        val navDateStadistic = bottomNavigation.menu.findItem(R.id.nav_date_estadistic)
        navDateStadistic.title = "Estadistica $f_estadistica"


    }

    fun getDateRegister(){
        val ciclo = Formats.obtenerRangoDeAnios().split('-')
        f_inscripcion = "${ciclo[0]}-08-20"

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation_date)
        val navDateIncome = bottomNavigation.menu.findItem(R.id.nav_date_income)

        navDateIncome.title = "Insrcipcion $f_inscripcion"

    }


    fun CargarAlumnos(){
        var count = 0
        alumnos = Alumno.get_All_by_folio()
        Nombre_Escuela.getAlumnos(alumnos)
        listaalumnos.clear()
        if(alumnos.moveToFirst()){
            while(count < alumnos.count){
                listaalumnos.add(DatosAlumnos(alumnos.getString(1) + " " +alumnos.getString(2)+" " +alumnos.getString(3), alumnos.getString(7),R.drawable.alumno, alumnos.getString(0), alumnos.getString(4).toInt(),alumnos.getString(15),alumnos.getString(16)))
                //Toast.makeText(this,alumnos.getString(1)+"  "+ alumnos.getString(0), Toast.LENGTH_SHORT).show()
                Numeros.add(alumnos.getString(15))
                Curps.add(alumnos.getString(7))
                Nombre.add(alumnos.getString(1))
                apellidop.add(alumnos.getString(2))
                apellidom.add(alumnos.getString(3))
                Correos.add(alumnos.getString(16))
                edad.add(0)
                entidad.add("")
                fnacimiento.add("")
                alumnos.moveToNext()
                count ++

            }
            lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)
        }
    }


    private fun Especificar(){
        val builder = AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.view_importar_nombre, null)
        builder.setView(v)
        val dialog = builder.create()
        dialog.show()
        /**set view*/
        val Rapellidop = v.findViewById<RadioButton>(R.id.rdg_Primero_Apellido)
        val Rnombre = v.findViewById<RadioButton>(R.id.rdg_Primero_Nombre)
        val espacio = v.findViewById<EditText>(R.id.txt_espacio_nombre)
        val btn = v.findViewById<Button>(R.id.btn_Importar)
        val addDialog = AlertDialog.Builder(this)
        /*addDialog.setView(v)
        addDialog.create()
        addDialog.show()*/
        Rnombre.setOnClickListener{
            empezar = true
        }
        Rapellidop.setOnClickListener{
            empezar = false
        }
        btn.setOnClickListener{
            if (espacio.text.length > 0){
                procesNameComplete(espacio.text.toString()[0], empezar, null)
                dialog.hide()
                dialog.hide()
            }
            else Toast.makeText(this, "Especifique el simbolo entre nombre y apellido", Toast.LENGTH_LONG).show()
        }
    }



    private fun procesNameComplete(espacio:Char, primeron:Boolean, nombresCompleto:ArrayList<String>?) {
        listaalumnos.clear()
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        var nombres = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().split('\n')
        if (nombresCompleto != null) nombres = nombresCompleto

        var cont = 0
        while (cont < nombres.count()){
            val nombre = nombres[cont].toString().split(espacio)

            if (primeron == true) {

                if (nombre.count() == 4 ) {
                    if (nameReply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[0] + " "+nombre[1 ]+ " "+nombre[2] +" "+nombre[3] , "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[0] + " "+nombre[1 ])
                    apellidop.add(nombre[2])
                    apellidom.add(nombre[3])
                }
                if (nombre.count() == 3 ) {
                    if (nameReply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[0] + " "+nombre[1] + " " +nombre[2], "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[0])
                    apellidop.add(nombre[1])
                    apellidom.add(nombre[2])
                }

                if (nombre.count() == 2 ) {
                    if (nameReply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[0] + " "+nombre[1], "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[0])
                    apellidop.add(nombre[1])
                    apellidom.add("")
                }

            }
            if (primeron == false) {
                if (nombre.count() == 4 ) {
                    if (nameReply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[2] + " "+nombre[3 ]+ " "+ nombre[0] + " "+nombre[1] , "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[2] + " "+nombre[3])
                    apellidop.add(nombre[0])
                    apellidom.add(nombre[1])
                }
                if (nombre.count() == 3 ) {
                    if (nameReply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[2] + " "+nombre[0] + " "+nombre[1], "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[2])
                    apellidop.add(nombre[0])
                    apellidom.add(nombre[1])
                }

                if (nombre.count() == 2 ) {
                    if (nameReply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[1] + " "+nombre[0], "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[1])
                    apellidop.add(nombre[0])
                    apellidom.add("")
                }
            }
            Curps.add("")
            Numeros.add("")
            Correos.add("")
            edad.add(0)
            entidad.add("")
            fnacimiento.add(Formats.getCurrentDate())
            cont++
        }
        if (listaalumnos.isNotEmpty()) deshabilitarElementosMenu()
        //Toast.makeText(this, listaalumnos.count().toString() , Toast.LENGTH_SHORT).show()
        if (listaalumnos.isNotEmpty()) hindCards()
        lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)
    }


    private fun Solo_Nombre() {
        listaalumnos.clear()
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val nombres = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().split('\n')

        var cont = 0

        while (cont < nombres.count()){

            listaalumnos.add(DatosAlumnos(nombres[cont], "N_lista null",R.drawable.alumno, "0",1,"",""))
            cont++
        }

        lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)
    }

    private fun procesarCurps(){
        var cont = 0
        var sexo = 0
        while (cont < listaalumnos.count()){
            try {
                val tempCurp = Curps[cont]
                if (tempCurp.isNotEmpty()){

                    if (curp.getSex2(tempCurp)) sexo = 1
                    else sexo = 0

                    fnacimiento[cont] = curp.getBirthday(tempCurp)
                    edad[cont] = curp.getAges(tempCurp)
                    entidad[cont]  = curp.getEntidadFederativa(tempCurp)
                    listaalumnos.get(cont).sexo = sexo
                    listaalumnos.get(cont).detalles  = Curps[cont] +"\n"+ entidad[cont] +" edad " +edad[cont] +"\n"+Numeros[cont]+"\n"+Correos[cont]
                }else{
                    fnacimiento[cont] = getDateActualy()
                    edad[cont] = 0
                    Curps[cont] = "no asignada"
                }

            }catch (Ex:Exception){

            }
            cont++
        }

    }

    /**
    * Obtiene los años cumplidos a partir de una CURP válida.
    * @param curp La CURP válida.
    * @return La cantidad de años cumplidos o -1 si la CURP no es válida.
    */
    private fun pegarCurps(){
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val curps = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().split('\n')
        var cont = 0
        var sexo = 0
        while (cont < listaalumnos.count()){
            try {
                if (curps[cont].length > 0){
                    val tempCurp = curps[cont]
                    if (curp.getSex2(tempCurp)) sexo = 1
                    if (!curp.getSex2(tempCurp)) sexo = 0
                    Curps[cont] = tempCurp
                    fnacimiento[cont] = curp.getBirthday(tempCurp)
                    edad[cont] = curp.getAges(tempCurp)
                    entidad[cont]  = curp.getEntidadFederativa(tempCurp)
                    listaalumnos.get(cont).sexo = sexo
                    listaalumnos.get(cont).detalles  = tempCurp +"\n"+ entidad[cont] +" edad " +edad[cont]
                }else{
                    fnacimiento[cont] = getDateActualy()
                    edad[cont] = 0
                    Curps[cont] = "no asignada"
                }

            }catch (Ex:Exception){
               Toast.makeText(this, "No todos los alunmnos tienen curp", Toast.LENGTH_SHORT).show()
            }
            cont++
        }
        lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)
    }

    private fun pegarNumeros(){

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val numero = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().split('\n')
        var cont = 0

        while (cont < listaalumnos.count()){
            try {
                if (numero[cont].length > 0){
                    Numeros[cont] = numero[cont]
                    listaalumnos.get(cont).numero  = numero[cont]
                }
            }catch (Ex:Exception){
                Toast.makeText(this,Ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
            cont++

        }
        //Toast.makeText(this, listaalumnos.count().toString() , Toast.LENGTH_SHORT).show()

        lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)

    }

    private fun pegarCorreo(){

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val correo = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().split('\n')
        var cont = 0

        while (cont < listaalumnos.count()){
            try {
                if (correo[cont].length > 0){
                    Correos[cont] = correo[cont]
                    listaalumnos.get(cont).correo  = correo[cont]
                }
            }catch (Ex:Exception){
                //Toast.makeText(this,"No todos los alumnos tienen correo", Toast.LENGTH_SHORT).show()
            }
            cont++

        }
        //Toast.makeText(this, listaalumnos.count().toString() , Toast.LENGTH_SHORT).show()

        lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)

    }

    fun importarAlumnosActuales(){
        var cont  = 0
        while (cont < listaalumnos.count() ) {
            Alumno.InsertAlumno(
                Nombre[cont],
                apellidop[cont],
                apellidom[cont],
                listaalumnos[cont].sexo,
                edad[cont].toString(),
                entidad[cont],
                Curps[cont],
                fnacimiento[cont],
                Numeros[cont],
                Correos[cont],
                f_estadistica,
                f_inscripcion
            )
            cont++
        }
        Reorganizar()
        clearvalues()
        listaalumnos.clear()
        habilitarElementosMenu()
        VibratePhone.vibrarTelefono(this, 1000)
    }


    fun importarAlumnosAnteriores(){
        var cont = 0

        Alumno.deleteStudentsTemp()
        while (cont < listaalumnos.count() ) {
            Alumno.InsertStudentTemp(
                Nombre[cont],
                apellidop[cont],
                apellidom[cont],
                listaalumnos[cont].sexo,
                edad[cont].toString(),
                entidad[cont],
                Curps[cont],
                fnacimiento[cont],
                Numeros[cont],
                Correos[cont],
                f_estadistica,
                f_inscripcion
            )
            cont++
        }
        Toast.makeText(this, "Se registraron los alumnos Temporales", Toast.LENGTH_SHORT).show()
        clearvalues()
        listaalumnos.clear()
    }



    private fun ImportarAlumnos(requestCode: Int){
        var cont  = 0
        while (cont < listaalumnos.count() ){
            try {
                if (REQUEST_CODE_ACTION_1 == requestCode) {
                    Alumno.InsertAlumno(
                        Nombre[cont],
                        apellidop[cont],
                        apellidom[cont],
                        listaalumnos[cont].sexo,
                        edad[cont].toString(),
                        entidad[cont],
                        Curps[cont],
                        fnacimiento[cont],
                        Numeros[cont],
                        Correos[cont],
                        f_estadistica,
                        f_inscripcion
                    )
                }

                if (REQUEST_CODE_ACTION_2 == requestCode) {
                    Alumno.InsertStudentTemp(
                        Nombre[cont],
                        apellidop[cont],
                        apellidom[cont],
                        listaalumnos[cont].sexo,
                        edad[cont].toString(),
                        entidad[cont],
                        Curps[cont],
                        fnacimiento[cont],
                        Numeros[cont],
                        Correos[cont],
                        f_estadistica,
                        f_inscripcion
                    )

                }


                habilitarElementosMenu()

            } catch (Ex: Exception) {
                Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
            }


            cont++
        }
        if (requestCode == 2) {
            Toast.makeText(this, "Se registraron los alumnos Temporales", Toast.LENGTH_SHORT).show()
            clearvalues()
            listaalumnos.clear()
        }
        if (requestCode  == 1) {
            Reorganizar()
            clearvalues()
            listaalumnos.clear()
            VibratePhone.vibrarTelefono(this, 1000)
        }
    }

    private fun clearvalues() {

        Nombre.clear()
        apellidop.clear()
        apellidom.clear()
        NombreC.clear()
        Curps.clear()
        Correos.clear()
        Numeros.clear()
        Calificaciines.clear()
        edad.clear()
        fnacimiento.clear()
        entidad.clear()
        sexo.clear()

    }


    private fun Actualizar_Datos(){
        var cont = 0
        ///Toast.makeText(this, listaalumnos.count().toString(), Toast.LENGTH_SHORT).show()
            while(cont <  listaalumnos.count()) {
                try {
                    Alumno.agregar_datos(
                        Nombre[cont],
                        apellidop[cont],
                        apellidom[cont],
                        listaalumnos[cont].sexo,
                        edad[cont].toString(),
                        entidad[cont],
                        Curps[cont],
                        fnacimiento[cont],
                        Numeros[cont],
                        Correos[cont],listaalumnos[cont].folio.toInt())

                } catch (Ex: Exception) {
                    Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
                }
                cont ++
            }
    }

    private fun nameReply(nombre:String):Boolean{
        var find = false
        val target = nombre
        val indices = listaalumnos.indices
            .filter { listaalumnos[it].nombre == target }
            .toList()

        if (indices.count() == 0) find = false
        if (indices.count() > 0) find = true

        return  find
    }


    private fun popupMenus(v: View) {
        try {
            val popupMenus = PopupMenu(this, v)
            popupMenus.inflate(R.menu.menu_lista_importar)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.editText -> {
                        val v = LayoutInflater.from(this).inflate(R.layout.viewdatosalumno, null)
                        val nombre = v.findViewById<EditText>(R.id.txtNombre_alumno)
                        val apellidop = v.findViewById<EditText>(R.id.txtApellidop_alumno)
                        val apellidom = v.findViewById<EditText>(R.id.txtApellidom_alumno)
                        val sexo = v.findViewById<Spinner>(R.id.cbxSexo_alumno)
                        try {
                            nombre.setText(Nombre[posicion])
                        }catch (Ex:Exception){
                            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
                        }

                        apellidop.setText(this.apellidop[posicion])
                        apellidom.setText(this.apellidom[posicion])
                        //Toast.makeText(this, listaalumnos[posicion].sexo.toString(), Toast.LENGTH_SHORT).show()
                        if (listaalumnos[posicion].sexo != null){

                            if (listaalumnos[posicion].sexo == 1) sexo.setSelection(0)
                            else sexo.setSelection(1)
                        }
                        android.app.AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                var sexo_1 = 0
                                listaalumnos[posicion].nombre = nombre.text.toString() + " "+ apellidop.text.toString() +" "+ apellidom.text.toString()
                                Nombre[posicion] = nombre.text.toString()
                                this.apellidop[posicion] = apellidop.text.toString()
                                this.apellidom[posicion] = apellidom.text.toString()
                                if (sexo.getSelectedItem().toString() == "Masculino") sexo_1 = 1
                                if (sexo.getSelectedItem().toString() == "Femenino") sexo_1 = 0
                                listaalumnos[posicion].sexo = sexo_1



                                lista_Importar.adapter = adapterAlumnos(this, listaalumnos)
                                //Curp[posicion]
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    R.id.delete -> {
                        /**set delete*/
                        android.app.AlertDialog.Builder(this)
                            .setTitle("Eliminar "+ listaalumnos[posicion].nombre )
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setMessage("¿Seguro de borrar este registro?")
                            .setPositiveButton("Yes") { dialog, _ ->
                                listaalumnos.removeAt(posicion)
                                Nombre.removeAt(posicion)
                                this.apellidop.removeAt(posicion)
                                this.apellidom.removeAt(posicion)
                                if (posicion < Curps.count()){
                                    edad.removeAt(posicion)
                                    entidad.removeAt(posicion)
                                    Curps.removeAt(posicion)
                                }
                                lista_Importar.adapter = adapterAlumnos(this, listaalumnos)
                                dialog.dismiss()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else -> true
                }

            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        } catch (Ex: Exception) {
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun getDateActualy(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(currentDate)
    }

    fun Reorganizar(){
        try {
            Alumno.ordenarporN_lista()
            CargarAlumnos()

        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCode && resultCode == RESULT_OK && data != null) {
            val selectedFile: Uri? = data.data
            if (selectedFile != null) {
                val columnNames = readDataFromExcel(this, selectedFile)
            }
        }
    }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_ACTION_1 -> {
                // Realizar acción 1
                if (resultCode == RESULT_OK && data != null) {
                    val selectedFile: Uri? = data.data
                    if (selectedFile != null) {
                        val columnNames = readDataFromExcel(this, selectedFile)
                        lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)
                        // Realizar acciones específicas para la acción 1
                    }
                }
            }
            REQUEST_CODE_ACTION_2 -> {
                // Realizar acción 2
                if (resultCode == RESULT_OK && data != null) {
                    val selectedFile: Uri? = data.data
                    if (selectedFile != null) {
                        val columnNames = readDataFromExcel(this, selectedFile)
                        importarAlumnosAnteriores()

                        // Realizar acciones específicas para la acción 1
                    }
                }
            }
            else -> {
                // Manejar otros casos si es necesario
            }
        }
    }




    fun openFolder() {
        val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/DB/"
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.data = Uri.parse("file://$folderPath")
        intent.type = "application/vnd.ms-excel" // Tipo MIME para archivos XLS
        try {
            (this as Activity).startActivityForResult(intent, REQUEST_CODE_ACTION_1)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show() // Manejar la excepción si no hay aplicaciones que puedan manejar la acción
        }
    }


    fun openFolderFormer() {
        val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/DB/"
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.data = Uri.parse("file://$folderPath")
        intent.type = "application/vnd.ms-excel" // Tipo MIME para archivos XLS
        try {
            (this as Activity).startActivityForResult(intent, REQUEST_CODE_ACTION_2)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show() // Manejar la excepción si no hay aplicaciones que puedan manejar la acción
        }
    }



    private fun readDataFromExcel(context: Context, uri: Uri) {
        //val alumnosList = mutableListOf<Alumno>() // Clase Alumno para representar cada fila del archivo
        val columnNames = mutableListOf<String>()

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val workbook: Workbook = HSSFWorkbook(inputStream)

            // Solo leemos la primera hoja
            val sheet = workbook.getSheetAt(0)

            // Leemos los nombres de las columnas de la primera fila
            val firstRow = sheet.getRow(0)
            for (i in 0 until firstRow.physicalNumberOfCells) {
                columnNames.add(firstRow.getCell(i)?.stringCellValue ?: "")
                //Toast.makeText(this, firstRow.getCell(i)?.stringCellValue ?: "", Toast.LENGTH_SHORT).show()
            }
            //

            loadValues(sheet.lastRowNum)

            var contador = 0
            // Iteramos sobre cada fila a partir de la segunda fila
            for (rowIndex in 1..sheet.lastRowNum) {
                val currentRow = sheet.getRow(rowIndex)


                // Iteramos sobre cada celda de la fila
                for (colIndex in 0 until currentRow.physicalNumberOfCells) {

                    var cellValue = currentRow.getCell(colIndex).toString()
                    //si el valor es no es nunerico lo captamos como string
                    if (!Formats.isNumeric(cellValue)) cellValue = "$cellValue"


                    // Dependiendo de la columna, asignamos el valor a la propiedad correspondiente de Alumno
                    when (columnNames[colIndex].toUpperCase()) {
                        "APELLIDO_PATERNO" -> {
                            apellidop[contador] = cellValue
                            APELLIDO_PATERNO = true
                        }
                        "APELLIDO_MATERNO" -> apellidom[contador] = cellValue
                        "NOMBRE" -> {
                            Nombre[contador] = cellValue
                            NOMBRE = true
                        }
                        "NOMBRE_COMPLETO" -> {
                            NombreC[contador] = cellValue
                            NOMBRE_COMPLETO = true
                        }
                        "CURP" ->  {
                            Curps[contador] = cellValue
                            CURP = true
                        }
                        "TELEFONO" -> Numeros[contador] = cellValue
                        "CORREO" -> Correos[contador] = cellValue
                        "CALIFICACION" -> Calificaciines[contador] = cellValue
                        // Agrega más casos para otras columnas según sea necesario
                    }

                }
              //  listaalumnos.add(DatosAlumnos(NombreC[contador], Curps[contador]+"\n"+Numeros[contador]+"\n"+Correos[contador],R.drawable.alumno , "0",1,"",""))
                contador++
            }

            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }

        //checamos que tipo de archivo estamos procesando si es estandar de recreapp o si es personalizado
        selectTypeFile()
    }
    /**
     * CHEQUE QUE LA IMPORTACION DEL ARCHIVO TENGA POR SEPARADO CULUMNAS DE SAPELLIDO PATERNO Y MATERNO
     * DE LO CONTRARIO PROCESAMOS EL ARREGLO NOMBRE COMPLETO LLAMANDO LA FUNCION
    */
    private fun selectTypeFile(){
        var validation = true
        //si es archivo recreaap
        if (APELLIDO_PATERNO && NOMBRE_COMPLETO){
            validation = false
        }
        //si va a tener las 3 columnas
        if (!NOMBRE_COMPLETO && APELLIDO_PATERNO && NOMBRE){
        //if (NombreC.isEmpty() && apellidop.isNotEmpty() && Nombre.isNotEmpty()){
            validation = false
        }

        //si vamos a buscar en la columna Nombre completo
        if (!APELLIDO_PATERNO && NOMBRE_COMPLETO){
        //if (apellidop.isEmpty() && NombreC.isNotEmpty()){
            validation =  true
        }

        //cuando es un archivo personalizado
        if (validation){
            procesNameComplete(' ',false, NombreC)
            procesarCurps()
        }
        else{ //cuando es archivo de recreapp

            for(i in 0 until apellidop.count()){
                listaalumnos.add(DatosAlumnos(Nombre[i] + " "+apellidop[i] + " "+ apellidom[i], Curps[i]+"\n"+Numeros[i]+"\n"+Correos[i],R.drawable.alumno , "0",1,"",""))
                //listaalumnos.add(DatosAlumnos(Nombre[i] + " "+apellidop[i] + " "+ apellidom[i], "",R.drawable.alumno , "0",1,"",""))
            }
            procesarCurps()
            if (listaalumnos.isNotEmpty()) hindCards()
            //lista_Importar.adapter =  adapterAlumnos(this, listaalumnos)
        }
    }

    fun hindCards(){
        val card_coumna = findViewById<CardView>(R.id.card_paste_column)
        card_coumna.isVisible = false
        val card_import_list = findViewById<CardView>(R.id.card_import_list)
        card_import_list.isVisible = false
        val card_import_list_former = findViewById<CardView>(R.id.card_import_list_former)
        card_import_list_former.isVisible = false
    }

    fun loadValues(size:Int){
        for (i in 0 until size){
            Nombre.add("")
            apellidop.add("")
            apellidom.add("")
            NombreC.add("")
            Curps.add("")
            Correos.add("")
            Numeros.add("")
            Calificaciines.add("")
            edad.add(0)
            fnacimiento.add("")
            entidad.add("")
            sexo.add("")
        }

    }




}