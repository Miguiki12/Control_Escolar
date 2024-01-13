package com.example.control_escolar

import BDLayer.TareasBD
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_act_calificar.*
import kotlinx.android.synthetic.main.viewcalificartareas.view.*

class act_Calificar : AppCompatActivity() {
    var listacalificar = ArrayList<DatosCalificar>()
    var posicion = 0
    lateinit var TareasBD: TareasBD
    var especial = "0"
    var c_actividad = "0"
    var nombre_actividad = ""
    var fecha_actividad = ""
    var tipo = ""
    var valor = ""
    var nombre_materia = ""
    var porcentaje = ""
    //var sendEMail = sendEMail(this)
    lateinit var alumnos_calificados : Cursor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_calificar)
        var  bundle = intent.extras
        nombre_actividad = bundle?.getString("nombre_actividad").toString()
        fecha_actividad = bundle?.getString("fecha_actividad").toString()
        c_actividad = bundle?.getString("c_actividad").toString()
        tipo = bundle?.getString("tipo").toString()
        valor = bundle?.getString("valor").toString()
        especial =  bundle?.getString("especial").toString()
        porcentaje =  bundle?.getString("porcentaje").toString()
        nombre_materia = bundle?.getString("nombre_materia").toString()
        this.supportActionBar?.title = nombre_actividad
        this.supportActionBar?.subtitle =  nombre_materia +": "+ tipo + " - valor $valor de 100"
        TareasBD = TareasBD(this)
        //Toast.makeText(this, valor, Toast.LENGTH_SHORT).show()
        try {
            if (especial.toString() == "0"){
                if(CargarCalificados() == 0) CargarAlumnos()
            }
            else loadEspecials()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        listaCalificar.setOnItemClickListener {adapterView, view, i, l ->
            posicion = i
            //calificar()
            califcar_actividad()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sup_calificar, menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_all_set->{
                if (especial == "0") Calificar_Todos(c_actividad, "100")
                else Calificar_Todos_Especial(c_actividad, "100")
            }
            R.id.nav_no_one_set->{
                if (especial == "0") Calificar_Todos(c_actividad, "0")
                else Calificar_Todos_Especial(c_actividad, "0")
            }
                //hidekeyboard()
        }
        return  super.onOptionsItemSelected(item)
    }


    fun CargarCalificados():Int{
        alumnos_calificados = TareasBD.obtenerCalificacion(c_actividad)
        try {
            var count = 0
            listacalificar.clear()
            if(alumnos_calificados.moveToFirst()){
                do{
                    var calificacion = ""
                    if (!alumnos_calificados.getString(3).isNullOrEmpty()) calificacion = alumnos_calificados.getString(3)
                    listacalificar.add(DatosCalificar(alumnos_calificados.getString(2), "N_lista " +alumnos_calificados.getString(5), R.drawable.alumno, calificacion.toString(), alumnos_calificados.getString(6).toInt(), alumnos_calificados.getString(1), c_actividad,fecha_actividad,0))
                    count ++
                }while (alumnos_calificados.moveToNext())
                updatelist()
            }

        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return alumnos_calificados.count
    }


    /*fun CargarCalificados():Int{
        alumnos_calificados = TareasBD.obtenerCalificacion(c_actividad)
        var indices : List<Int>?
        var target = ""
        var count = 0
        try {
            if(alumnos_calificados.moveToFirst()){
                while (count< alumnos_calificados.count){
                    target = alumnos_calificados.getString(1)
                    indices = listacalificar.indices
                        .filter { listacalificar[it].folio == target }
                        .toList()
                    if (indices.isNotEmpty()) {
                        listacalificar[indices[0]].calificacion = alumnos_calificados.getString(3)
                    } else {
                        // Realiza alguna acción si la lista de índices está vacía, ya que no se encontró el elemento buscado
                    }
                    alumnos_calificados.moveToNext()
                    count++
                }
                updatelist()
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return alumnos_calificados.count
    }*/


    fun CargarAlumnos():Int{
        var tareas = Nombre_Escuela.Alumnos
        try {
            var count = 0
            listacalificar.clear()
            if(tareas.moveToFirst()){
                while(count < tareas.count){                                                                        //PictureLoader.loadPicture(this, Nombre_Escuela.Alumnos.getBlob(19))
                    listacalificar.add(DatosCalificar(tareas.getString(1), "N_lista " +tareas.getString(18), R.drawable.alumno,"", tareas.getString(4).toInt(), tareas.getString(0), c_actividad,fecha_actividad,0))
                    tareas.moveToNext()
                    count ++
                }
               updatelist()
            }
            //tareas.close()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return listacalificar.size
    }

    fun loadEspecials(){
        var tareas = TareasBD.getCalificationEspecials(c_actividad.toInt())
        try {
            var count = 0
            listacalificar.clear()
            if(tareas.moveToFirst()){
                while(count < tareas.count){
                    listacalificar.add(DatosCalificar(tareas.getString(1), "N_lista " +tareas.getString(18),
                        //PictureLoader.loadPicture(this,Nombre_Escuela.Alumnos.getBlob(19))
                        R.drawable.alumno,tareas.getString(19), tareas.getString(4).toInt(), tareas.getString(0), c_actividad,fecha_actividad,0))
                    tareas.moveToNext()
                    count ++
                }
                updatelist()
                tareas.close()
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun updatelist(){
        val currentPosition = listaCalificar.firstVisiblePosition
        listaCalificar.adapter =  adapterCalificar(this, listacalificar)
        listaCalificar.setSelection(currentPosition)
    }

    fun hidekeyboard(){
        val view = this.currentFocus
        if (view != null){
            val imm =  applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun Calificar_Todos(c_actividad:String, calificacion:String){
        try {
            TareasBD.borrarCalificaicion(c_actividad)
            var calificados = Nombre_Escuela.Alumnos
            if (alumnos_calificados.count > 0)  calificados = alumnos_calificados


            var count = 0
            if (calificados.moveToFirst()){
                do {
                    TareasBD.Calificar_Todos(c_actividad, calificados.getString(0),fecha_actividad,calificacion,"0")
                    listacalificar[count].calificacion = calificacion
                    count++
                }while (calificados.moveToNext())
                Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
                updatelist()
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun Calificar_Todos_Especial(c_actividad:String, calificacion:String){
        try {
            TareasBD.borrarCalificaicionEspecial(c_actividad)
            var count = 0
                while (count < listacalificar.count()) {
                    TareasBD.Calificar_Todos_Especial(c_actividad, listacalificar[count].folio,fecha_actividad,calificacion)
                    listacalificar[count].calificacion = calificacion
                    count++

            }
            updatelist()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun califcar_actividad(){
        val builder = AlertDialog.Builder(this)
        val v = layoutInflater.inflate(R.layout.viewcalificartareas, null)
        builder.setView(v)
        v.barra_Calificar_actividad.text = listacalificar[posicion].nombre
        v.edt_Calificar_actividad.setText(listacalificar[posicion].calificacion)
        //si es especial mostramos el porcentaje sobre el cual se calificara
        if (especial == "1") v.txt_sobre_cien.text = "$porcentaje%"
        //view.txt_valor_aspecto_calificar.text = "valor = $valor"
        val dialog = builder.create()
        dialog.show()

        //limpiamos el campo cuando al entrar al edit
        v.edt_Calificar_actividad.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                v.edt_Calificar_actividad.text.clear()
            }
        }
        //al presionar el boton siguiente
        v.btn_siguiente_calificar.setOnClickListener {
            val calificacion = v.edt_Calificar_actividad.text.toString()
            if (calificar(calificacion)){//abrimos la ventana de calificar haste que presione terminar
                if (posicion < listacalificar.size - 1){
                    posicion += 1
                    califcar_actividad()
                    dialog.hide()
                }
                else dialog.hide()
            }

        }
        //al presionar el boton terminar
        v.btn_Calificar_Actividad.setOnClickListener {
            val calificacion = v.edt_Calificar_actividad.text.toString()
            if (calificar(calificacion)) dialog.hide()
        }

    }


    fun calificar(calificacion:String):Boolean{
        if (calificacion.isNotEmpty() && calificacion.toInt() <= 100) {
            if (especial == "0") {
                TareasBD.borrarCalificaicionAlumno(
                    listacalificar[posicion].c_tarea,
                    listacalificar[posicion].folio
                )
                TareasBD.Calificar(
                    listacalificar[posicion].c_tarea,
                    listacalificar[posicion].folio,
                    fecha_actividad,
                    calificacion,
                    "0"
                )
            }
            if (especial == "1") {
                TareasBD.rateCalificationEspecial(
                    listacalificar[posicion].c_tarea,
                    listacalificar[posicion].folio,
                    calificacion.toInt()
                )
                //Toast.makeText(this, TareasBD.error, Toast.LENGTH_SHORT).show()
            }
            listacalificar[posicion].calificacion = calificacion
            updatelist()
            return  true
        }else{
            Toast.makeText(this,"Especifique la calificación del alumno y ferifique que no sea mayor a 100", Toast.LENGTH_LONG).show()
            return false
        }
    }

}


