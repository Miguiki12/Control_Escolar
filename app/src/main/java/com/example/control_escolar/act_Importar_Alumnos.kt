package com.example.control_escolar

import BDLayer.AlumnosBD
import BDLayer.Curp
import LogicLayer.Formats
import LogicLayer.VibratePhone
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_act_importar_alumnos.*
import kotlinx.android.synthetic.main.listaalumnos.view.*
import java.text.SimpleDateFormat
import java.util.*


class act_Importar_Alumnos : AppCompatActivity() {

    var listaalumnos = ArrayList<DatosAlumnos>()
    lateinit var Alumno: AlumnosBD
    var folio = "0"
    var espacio = ""
    var posicion = 0
    var empezar  = true
    lateinit var Nombre :  ArrayList<String>
    lateinit var apellidop :  ArrayList<String>
    lateinit var apellidom :  ArrayList<String>
    lateinit var entidad :  ArrayList<String>
    lateinit var edad :  ArrayList<Int>
    lateinit var fnacimiento: ArrayList<String>
    lateinit var Curps: ArrayList<String>
    lateinit var Numeros: ArrayList<String>
    lateinit var Correos: ArrayList<String>
    lateinit  var alumnos: Cursor
    lateinit var curp: Curp
    var agregar = "no"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_importar_alumnos)
        Alumno = AlumnosBD(this)
        Nombre = ArrayList()
        apellidop = ArrayList()
        apellidom = ArrayList()
        entidad = ArrayList()
        edad = ArrayList()
        Curps = ArrayList()
        curp = Curp()
        Numeros = ArrayList()
        Correos = ArrayList()
        fnacimiento = ArrayList()
        var  bundle = intent.extras
        agregar = bundle?.getString("agregar").toString()
        this.supportActionBar?.title = "Importar Alumnos"
        if (agregar == "si"){
            this.supportActionBar?.title = "Agregar Datos"
            CargarAlumnos()
        }

        lista_Importar.setOnItemClickListener{ adapterView, view, i, l ->
            posicion = i
            popupMenus(view.image_opcionesAlumno)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_importar, menu)

        return super.onCreateOptionsMenu(menu)
    }
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
                        if (agregar == "no") ImportarAlumnos()
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
            R.id.nav_Nombre_Completo -> Especificar()
            //R.id.nav_Nombre -> Solo_Nombre()
            R.id.nav_Curp -> pegarCurps()
            R.id.nav_Telefono -> pegarNumeros()
            R.id.nav_Correo -> pegarCorreo()
        }
        return  super.onOptionsItemSelected(item)
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
                //Toast.makeText(this, empezar.toString(), Toast.LENGTH_SHORT).show()
                Nombre_Completo(espacio.text.toString()[0], empezar)

                dialog.hide()
            }
            else Toast.makeText(this, "Especifique el simbolo entre nombre y apellido", Toast.LENGTH_LONG).show()
        }
    }



    private fun Nombre_Completo(espacio:Char, primeron:Boolean) {
        listaalumnos.clear()
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val nombres = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().split('\n')
        var cont = 0
        while (cont < nombres.count()){
            val nombre = nombres[cont].toString().split(espacio)

            if (primeron == true) {

                if (nombre.count() == 4 ) {
                    if (namereply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[0] + " "+nombre[1 ]+ " "+nombre[2] +" "+nombre[3] , "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[0] + " "+nombre[1 ])
                    apellidop.add(nombre[2])
                    apellidom.add(nombre[3])
                }
                if (nombre.count() == 3 ) {
                    if (namereply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[0] + " "+nombre[1] + " " +nombre[2], "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[0])
                    apellidop.add(nombre[1])
                    apellidom.add(nombre[2])
                }

                if (nombre.count() == 2 ) {
                    if (namereply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[0] + " "+nombre[1], "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[0])
                    apellidop.add(nombre[1])
                    apellidom.add("")
                }

            }
            if (primeron == false) {
                if (nombre.count() == 4 ) {
                    if (namereply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[2] + " "+nombre[3 ]+ " "+ nombre[0] + " "+nombre[1] , "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[2] + " "+nombre[3])
                    apellidop.add(nombre[0])
                    apellidom.add(nombre[1])
                }
                if (nombre.count() == 3 ) {
                    if (namereply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[2] + " "+nombre[0] + " "+nombre[1], "..",R.drawable.alumno, "0",1,"",""))
                    Nombre.add(nombre[2])
                    apellidop.add(nombre[0])
                    apellidom.add(nombre[1])
                }

                if (nombre.count() == 2 ) {
                    if (namereply(nombres[cont]) == false)   listaalumnos.add(DatosAlumnos(nombre[1] + " "+nombre[0], "..",R.drawable.alumno, "0",1,"",""))
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
        //Toast.makeText(this, listaalumnos.count().toString() , Toast.LENGTH_SHORT).show()
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



    private fun pegarCurps(){
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val curps = clipboardManager.primaryClip?.getItemAt(0)?.text.toString().split('\n')
        var cont = 0
        var sexo = 0
        while (cont < listaalumnos.count()){
            try {
                if (curps[cont].length > 0){
                    curp.validar_curp(curps[cont])
                    if (curp.Sexo) sexo = 1
                    else sexo = 0
                    Curps[cont] = curps[cont]
                    //Toast.makeText(this,Curps[cont], Toast.LENGTH_SHORT).show()
                    fnacimiento[cont] = curp.FNacimiento
                    edad[cont] = curp.edad
                    entidad[cont]  = curp.entidad_federativa
                    listaalumnos.get(cont).sexo = sexo
                    listaalumnos.get(cont).detalles  = curps[cont] +"\n"+ curp.entidad_federativa +" edad " +curp.edad
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

    private fun ImportarAlumnos(){
        var cont  = 0
        while (cont< listaalumnos.count()){
                try {
                    Alumno.InsertAlumno(Nombre[cont], apellidop[cont], apellidom[cont],listaalumnos[cont].sexo, edad[cont].toString(),entidad[cont], Curps[cont],fnacimiento[cont],Numeros[cont],Correos[cont])
                    VibratePhone.vibrarTelefono(this, 1000)

                    //Toast.makeText(this,Alumno.error, Toast.LENGTH_SHORT).show()
                    //Alumno.InsertAlumno(Nombre[cont], apellidop[cont], apellidom[cont], listaalumnos[cont].sexo)
                }catch (Ex:Exception){
                    Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
                }
            cont++
        }
        Reorganizar()
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
                    //Toast.makeText(this, listaalumnos[cont].nombre, Toast.LENGTH_SHORT).show()

                } catch (Ex: Exception) {
                    Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
                }
                cont ++
            }
    }

    private fun namereply(nombre:String):Boolean{
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
}