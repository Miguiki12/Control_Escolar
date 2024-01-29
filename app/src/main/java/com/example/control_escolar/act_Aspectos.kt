package com.example.control_escolar

import BDLayer.Tipo_ActividadBD
import LogicLayer.VibratePhone
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_act_aspectos.*
import kotlinx.android.synthetic.main.cardaspectos.view.*

class act_Aspectos : AppCompatActivity(), AdapterView.OnItemClickListener {


    private var  arrayList: ArrayList<ItemAspectos>? = null
    private var  AspectosAdapter: AdapterAspectos? = null
    lateinit var checkbox_Aspectos:Switch
    var posicion = 0
    lateinit var actividadDB: Tipo_ActividadBD
    var c_materia = "0"
    var Porcentaje = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_aspectos)
        actividadDB = Tipo_ActividadBD(this)
        var  bundle = intent.extras
        val antmateria = bundle?.getString("nombremateria")

        c_materia = bundle?.getString("c_materia").toString()
        if (Nombre_Escuela.get_tipo() == 1) c_materia = "0"

        this.supportActionBar?.title = antmateria + ": "+Nombre_Escuela.getAlias()
        this.supportActionBar?.subtitle = "Indique el encuadre"
        arrayList = ArrayList()
        CargarAspectos()
        grid_Aspectos.onItemClickListener = this
        /*checkbox_Aspectos = findViewById<Switch>(R.id.checkbox_aspectos)
        checkbox_Aspectos.setOnClickListener {
            if (checkbox_Aspectos.isChecked) {
                addInfo("Asistencia",1)
            } else {
                // El CheckBox no está marcado
                // Realiza las acciones necesarias
            }
        }*/

    }

    private fun AgregarAspectos() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.viewdatosaspectos,null)
        /**set view*/
        val nombre = v.findViewById<EditText>(R.id.edit_Nombreaspecto)
        val  porciento = v.findViewById<EditText>(R.id.edit_porcentajeaspectos)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            if (nombre.text.toString().length > 0){

                arrayList!!.add(ItemAspectos(nombre.text.toString(), porciento.text.toString(),"1"))
            }
            else{
                Toast.makeText(this,"Escriba el nombre del Aspecto a calificar", Toast.LENGTH_SHORT).show()
            }

        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()


        }
        addDialog.create()
        addDialog.show()
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

        try {
            popupMenus(p1!!.imagen_OpcionesAspectos)
            posicion =  position
        }
        catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    private fun popupMenus(v:View) {
        try {
            val popupMenus = PopupMenu(this,v)
            popupMenus.inflate(R.menu.menu_aspectos)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editText->{
                        val v = LayoutInflater.from(v.context).inflate(R.layout.viewdatosaspectos,null)
                        val name = v.findViewById<EditText>(R.id.edit_Nombreaspecto)
                        val porcentaje = v.findViewById<EditText>(R.id.edit_porcentajeaspectos)
                        name.setText(arrayList!![posicion].nombre)
                        porcentaje.setText(arrayList!![posicion].porciento)
                        Porcentaje -= arrayList!![posicion].porciento.toString().toInt()
                        AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("Ok"){
                                    dialog,_->
                                if (name.text.toString().length > 0 && porcentaje.text.toString().length > 0){
                                Porcentaje += porcentaje.text.toString().toInt()
                                //Toast.makeText(this, Porcentaje.toString()+" "+ porcentaje.text.toString(), Toast.LENGTH_SHORT).show()
                                if (Porcentaje <= 100){
                                    if (actividadDB.ActualizarActividad(name.text.toString(), porcentaje.text.toString(), arrayList!![posicion].id.toString())) {
                                        arrayList!![posicion].nombre = name.text.toString()
                                        arrayList!![posicion].porciento = porcentaje.text.toString()
                                    }
                                    Toast.makeText(this, actividadDB.error, Toast.LENGTH_SHORT).show()
                                    CargarAspectos()

                                }
                                else Toast.makeText(this, "El total del porecentaje no debe exceder a 100%", Toast.LENGTH_SHORT).show()

                                dialog.dismiss()
                                }else Toast.makeText(this, "Especifique todos los datos", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Cancel"){
                                    dialog,_->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.delete->{
                        /**set delete*/
                        AlertDialog.Builder(this)
                            .setTitle("Borrar")
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setMessage("¿Seguro de borrar esta actividad?")
                            .setPositiveButton("Si"){
                                    dialog,_->

                                if(actividadDB.borrarTipo_Actividad(arrayList!![posicion].id.toString())){
                                    Porcentaje -= arrayList!![posicion].porciento.toString().toInt()
                                    arrayList!!.removeAt(posicion)
                                    AspectosAdapter = AdapterAspectos(applicationContext, arrayList!!)
                                    grid_Aspectos.adapter = AspectosAdapter
                                }
                                Toast.makeText(this,actividadDB.error,Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else-> true
                }

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


    fun CargarAspectos(){
        try {
            var count = 0
            var total = 0
            val aspectos = actividadDB.obtenerAll(c_materia)
            arrayList!!.clear()
            if(aspectos.moveToFirst()){
                while(count < aspectos.count){
                    arrayList!!.add(ItemAspectos(aspectos.getString(1), aspectos.getString(2), aspectos.getString(0)))
                    total += aspectos.getString(2).toInt()
                    aspectos.moveToNext()
                    count ++
                }
                AspectosAdapter = AdapterAspectos(applicationContext, arrayList!!)
                grid_Aspectos.adapter =  AspectosAdapter//AdapterAspectos(applicationContext, arrayList!!)
                Porcentaje = total

            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addInfo(name:String) {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.create_aspect,null)
        var type = 0
        /**set view*/
        val Nombre = v.findViewById<EditText>(R.id.edit_Nombreaspecto)
        val porcentaje = v.findViewById<EditText>(R.id.edit_porcentajeaspectos)
        val aspectos = v.findViewById<Spinner>(R.id.spinner_aspect)
        aspectos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtiene el elemento seleccionado del Spinner
                val elementoSeleccionado = parent?.getItemAtPosition(position).toString()
                if (position < 4){
                    Nombre.setText(elementoSeleccionado)
                    Nombre.isVisible = false
                }
                else {
                    Nombre.setText("")
                    Nombre.isVisible = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Maneja el caso en el que no se ha seleccionado nada (opcional)
            }
        }
        Nombre.setText(name)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->

            if (Nombre.text.toString().length > 0 && porcentaje.text.toString().length > 0){
                    //checamos si la palabra sist esta en el nombre de la actividad a calificar, si es asi en tipo le asignamos 1 para saber que la asistencia se tomara en cuenta para evaluar
                  if (findAsistnecia(Nombre.text.toString())) {
                      type = 1
                  }

                  Porcentaje += porcentaje.text.toString().toInt()
                 if (Porcentaje <= 100){
                     if(actividadDB.InsertarActividad(Nombre.text.toString(), porcentaje.text.toString(), c_materia, type)){
                         VibratePhone.vibrarTelefono(this, 500)
                        CargarAspectos()
                     }
                 }else{
                     Porcentaje -= porcentaje.text.toString().toInt()
                     Toast.makeText(this, "El total del porecentaje no debe exceder a 100%", Toast.LENGTH_SHORT).show()
                 }
                dialog.dismiss()
            }
            else{
                Toast.makeText(this,"Escriba el tipo de Actividad y su porcentaje", Toast.LENGTH_SHORT).show()
            }

        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            //checkbox_Aspectos.isChecked = false
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sup_aspectos, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnm_crearaspectos -> addInfo("")
            R.id.btnm_help_aspectos -> showHelp()
        }
        return  super.onOptionsItemSelected(item)
    }


    fun findAsistnecia(word:String): Boolean {
        var encontrado = false
        val palabraBuscada = "sist"

        encontrado = word.contains(palabraBuscada)
        return encontrado
    }
    private fun showHelp() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.view_help_aspect,null)
        /**set view*/
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            dialog.dismiss()

        }

        addDialog.create()
        addDialog.show()
    }


}