package com.example.control_escolar

import BDLayer.TareasBD
import BDLayer.crearPDF
import BDLayer.sendEMail
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_act_actividad_especial.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class act_Actividad_Especial : AppCompatActivity() {
    var listaActividadEspecial = ArrayList<Asistencia>()
    lateinit var TareasBD:TareasBD
    var checado = false
    var c_tarea = ""
    var partial = "0"
    lateinit var materias: Cursor
    lateinit var actividades:Cursor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_actividad_especial)
        this.supportActionBar?.hide()

        //this.supportActionBar?.title = "SELECCIONE LOS ALUMNOS"
        var  bundle = intent.extras
        c_tarea =  bundle?.getString("c_actividad").toString()
        partial =  bundle?.getString("numpartial").toString()
        if (partial == "null") partial = "0"//si la ventana es iniciada desde trabajo especial le indicamos que su valor es 0
        //Toast.makeText(this, partial, Toast.LENGTH_SHORT).show()
        TareasBD = TareasBD(this)
        TareasBD.onCreatetableActividad_Especial()
        try {
            CargarAlumnos()
            loadSelecteds()
        }catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }


        cbx_all_students_selected.setOnClickListener {
            TodosAsistieron()
        }

        listaActividad_Especial.setOnItemClickListener { adapterView, view, i, l ->
            if (listaActividadEspecial[i].cbxasistencia == false) listaActividadEspecial[i].cbxasistencia = true
            else listaActividadEspecial[i].cbxasistencia = false
            updatelist()
        }
        btn_finish_select.setOnClickListener {
            if (partial == "0") enviarListaDeVuelta()
            if (partial == "1") printPartial(partial.toInt())
        }
    }

    fun updatelist(){
        val currentPosition = listaActividad_Especial.firstVisiblePosition
        listaActividad_Especial.adapter = adapterAsistencia(this, listaActividadEspecial, 0, 0)
        listaActividad_Especial.setSelection(currentPosition)
    }

    fun TodosAsistieron(){
        var cont = 0
        if(checado) checado = false
        else checado = true
        while(cont < listaActividadEspecial.count()) {
            listaActividadEspecial[cont].cbxasistencia = checado
            //listaActividadEspecial[cont].cbxasistencia = true
            cont ++
        }
        listaActividad_Especial.adapter = adapterAsistencia(this, listaActividadEspecial, 0, 0)
    }


    fun CargarAlumnos(){
        var count = 0
        listaActividadEspecial.clear()
        try {
            if(Nombre_Escuela.Alumnos.moveToFirst()){
                while(count < Nombre_Escuela.Alumnos.count){
                    listaActividadEspecial.add(
                        Asistencia(Nombre_Escuela.Alumnos.getString(1) + " " +Nombre_Escuela.Alumnos.getString(2)+" " +Nombre_Escuela.Alumnos.getString(3),
                            "N_lista " +Nombre_Escuela.Alumnos.getString(18),R.drawable.alumno//PictureLoader.loadPicture(this,Nombre_Escuela.Alumnos.getBlob(19)
                            , false, Nombre_Escuela.Alumnos.getString(4).toInt(), Nombre_Escuela.Alumnos.getString(0).toInt(), Nombre_Escuela.Alumnos.getString(15), 0,Nombre_Escuela.Alumnos.getString(16))
                    )
                    Nombre_Escuela.Alumnos.moveToNext()
                    count ++
                }
                listaActividad_Especial.adapter =  adapterAsistencia(this, listaActividadEspecial, 0, 0)
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun loadSelecteds():Int{
        val selected = TareasBD.getCalificationEspecials(c_tarea.toInt())
        var indices : List<Int>
        var target = ""
        var count = 0
        try {
            if(selected.moveToFirst()){
                while (count< selected.count){
                    target = selected.getString(0)
                    indices = listaActividadEspecial.indices
                        .filter { listaActividadEspecial[it].Folio == target.toInt() }
                        .toList()
                    listaActividadEspecial[indices.get(0)].cbxasistencia = true
                    selected.moveToNext()
                    count++
                }
                listaActividad_Especial.adapter =  adapterAsistencia(this, listaActividadEspecial, 0, 0)
            }

        }catch (Ex:Exception){

            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        finally {
            selected.close()
        }
        return selected.count
    }

    fun enviarListaDeVuelta() {
        val intent = Intent()
        intent.putExtra("seleccion_alumnos", createListSelecction())
        setResult(RESULT_OK, intent)
        //listaActividadEspecial.clear()
        finish()
    }



    fun createListSelecction():ArrayList<Int>{
        var list = ArrayList<Int>()
        try {
            var cont = 0
            while (cont < listaActividadEspecial.count()){
                if (listaActividadEspecial[cont].cbxasistencia == true) {
                    list.add(listaActividadEspecial[cont].Folio)//añadimos los alumnos que estan seleccionados en la lista
                    //si no esta el alumno registrado
                    if (TareasBD.getActivitysEspecialsByStudent(c_tarea.toString(), listaActividadEspecial[cont].Folio.toString()) == 0){

                        if (c_tarea.toInt() > 0) TareasBD.InsertarActividadesEspeciales(c_tarea.toInt(),listaActividadEspecial[cont].Folio, 0,0)
                   }
                }else {//si el alumno no esta checado en la lista lo borramos en caso dado que se este deseleccionando
                    if (c_tarea.toInt() > 0) {
                        TareasBD.deleteActivityEspecialByStudent(c_tarea.toInt(), listaActividadEspecial[cont].Folio.toString())
                    }
                }
                cont ++
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return  list
    }

    fun printPartial(partial:Int){
        val pdf = crearPDF(this)
        val sendemail = sendEMail(this)
        val seleccionados = createListSelecction()
        //pdf.getAllParcial(partial)
        GlobalScope.launch {
            if (seleccionados.isNotEmpty()) {
                for (i in 0 until seleccionados.size) {
                    val id = foundFolio(seleccionados[i])
                    Nombre_Escuela.Alumnos.moveToPosition(id)
                    val folio = Nombre_Escuela.Alumnos.getInt(0)
                    val nombre = Nombre_Escuela.Alumnos.getString(1)
                    val apellidop = Nombre_Escuela.Alumnos.getString(2)
                    val apellidom = Nombre_Escuela.Alumnos.getString(3)
                    val curp = Nombre_Escuela.Alumnos.getString(7)
                    val correo = Nombre_Escuela.Alumnos.getString(16)
                    if (correo.isNotEmpty()) {
                        pdf.getParcialByStudent(1, folio, nombre, apellidop, apellidom, curp)
                        sendemail.adjuntar("Parcial", "")
                        sendemail.sendwhitattach("Parcial $partial de $nombre", "Detalles Parcial $partial", correo)
                    }
                }
            }
        }
        Toast.makeText(this, "No cierre la app hasta que termine de enviarse las calificaciones", Toast.LENGTH_LONG).show()
        //listaActividadEspecial.clear()
    }

    private fun foundFolio(folio:Int):Int{
        var id = 0
        if (Nombre_Escuela.Alumnos.moveToFirst()){
            do {
                if (Nombre_Escuela.Alumnos.getInt(0) == folio){

                    break
                }
                id ++
            }while (Nombre_Escuela.Alumnos.moveToNext())
        }
        return id
    }

}