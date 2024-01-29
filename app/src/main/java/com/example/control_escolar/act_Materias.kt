package com.example.control_escolar

import BDLayer.MateriasBD
import LogicLayer.VibratePhone
import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_act_materias.*
import kotlinx.android.synthetic.main.cardmaterias.view.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener


class act_Materias : AppCompatActivity(), AdapterView.OnItemClickListener {
    var mLayout: TextView? = null
    var mDefaultColor = 0
    var c_materia = "0"
    lateinit var materiasDB: MateriasBD
    lateinit var materias:Cursor
    private var  arrayList: ArrayList<ItemMateria>? = null
    private var  MateriasAdapter: AdapterMaterias? = null
    var posicion = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_act_materias)
        materiasDB = MateriasBD(this)
        //mLayout = findViewById(R.id.image_OpcionesMateria);
        mDefaultColor = ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary);
        arrayList =  ArrayList()
        /*arrayList = setDataList()
        MateriasAdapter = AdapterMaterias(applicationContext, arrayList!!)*/
        grid_Materias.adapter = MateriasAdapter
        CargarMaterias()
        grid_Materias.onItemClickListener = this
        btn_AgregarMateria.setOnClickListener{AgregarMateria()}
    }

    fun CargarMaterias(){
        try {
            var count = 0
            var view:View = View.inflate(this, R.layout.cardmaterias, null)
            val imagen =  findViewById<ImageView>(R.id.image_OpcionesMateria)
            materias = materiasDB.obtenerAll()
            arrayList!!.clear()
            if(materias.moveToFirst()){
                while(count < materias.count){

                    arrayList!!.add(ItemMateria(view,materias.getString(1), cargarDias(materias.getString(0)), materias.getString(0),materias.getString(2).toInt(), imagen))
                    //Toast.makeText(this, materias.getString(2).toString(), Toast.LENGTH_SHORT).show()
                    materias.moveToNext()
                    count ++
                }
                grid_Materias.adapter =  AdapterMaterias(applicationContext, arrayList!!)

            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }

        override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        try {
            posicion =  position
            mLayout = p1!!.lbl_claveMateria
            c_materia = p1!!.lbl_claveMateria.text.toString()
            popupMenus(p1!!.image_OpcionesMateria)
        }
        catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }



    private fun AgregarMateria() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.viewcardmaterias,null)
        /**set view*/
        val nombre = v.findViewById<EditText>(R.id.edit_NombreMateria)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("AGREGAR"){
                dialog,_->
            if (nombre.text.toString().length > 0){
                if(materiasDB.InsertMateria(nombre.text.toString())){
                    VibratePhone.vibrarTelefono(this, 500)
                    CargarMaterias()
                }
                Toast.makeText(this, materiasDB.error, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            else{
                Toast.makeText(this,"Escriba el nombre de la Materia", Toast.LENGTH_SHORT).show()
            }

        }
        addDialog.setNegativeButton("CANCELAR"){
                dialog,_->
            dialog.dismiss()


        }
        addDialog.create()
        addDialog.show()
    }

    private fun popupMenus(v:View): Boolean {
        try {
            val popupMenus = PopupMenu(this,v)
            if (Nombre_Escuela.get_tipo() > 1) popupMenus.inflate(R.menu.menu_materias)
            if (Nombre_Escuela.get_tipo() == 1) popupMenus.inflate(R.menu.menu_materias2)
            popupMenus.setOnMenuItemClickListener {
                val v = LayoutInflater.from(this).inflate(R.layout.viewcardmaterias,null)
                val name = v.findViewById<EditText>(R.id.edit_NombreMateria)
                val detalles = v.findViewById<EditText>(R.id.edit_TipoMateria)
                name.setText(arrayList!![posicion].nombre)
                detalles.setText(arrayList!![posicion].detalles)
                when(it.itemId){
                    R.id.editText->{
                        AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("Ok"){
                                    dialog,_->
                                if (materiasDB.ActualizarMateria(name.text.toString(), c_materia)){
                                    arrayList!![posicion].nombre = name.text.toString()
                                }
                                 Toast.makeText(this, materiasDB.error, Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
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
                            .setTitle("Borrar ${name.text.toString()}")
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setMessage("¿Seguro de borrar esta materia?")
                            .setPositiveButton("Yes"){
                                    dialog,_->
                                if (materiasDB.borrarMateria(c_materia)){
                                    arrayList!!.removeAt(posicion)
                                    MateriasAdapter = AdapterMaterias(applicationContext, arrayList!!)
                                    grid_Materias.adapter = MateriasAdapter
                                }
                                Toast.makeText(this,materiasDB.error,Toast.LENGTH_SHORT).show()
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
                    R.id.changeDay->{
                        var dias = ""
                        val v = LayoutInflater.from(this).inflate(R.layout.view_dias,null)
                        val lunes = v.findViewById<CheckBox>(R.id.cbx_Lunes)
                        val martes = v.findViewById<CheckBox>(R.id.cbx_Martes)
                        val miercoles = v.findViewById<CheckBox>(R.id.cbx_Miercoles)
                        val jueves = v.findViewById<CheckBox>(R.id.cbx_Jueves)
                        val viernes = v.findViewById<CheckBox>(R.id.cbx_Viernes)
                        val sabado = v.findViewById<CheckBox>(R.id.cbx_Sabado)
                        val diasselect = arrayList!![posicion].detalles.toString().split(' ') //detalles.text.toString().split(' ')
                        //val diasselect =  arrayOf( detalles.text.toString().split(' '))
                        var cont  = 0

                        while(cont < diasselect.count()) {
                            if (diasselect[cont].toString() == "lunes") lunes.isChecked = true
                            if (diasselect[cont].toString() == "martes") martes.isChecked = true
                            if (diasselect[cont].toString() == "miércoles") miercoles.isChecked = true
                            if (diasselect[cont].toString() == "jueves") jueves.isChecked = true
                            if (diasselect[cont].toString() == "viernes") viernes.isChecked = true
                            if (diasselect[cont].toString() == "sabado") sabado.isChecked = true
                            cont ++
                        }

                        AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("Ok"){
                                    dialog,_->
                                materiasDB.BorrarDias(c_materia)
                                if (lunes.isChecked) {
                                    materiasDB.DiasMateria("lunes", c_materia, "1")
                                    dias = "lunes"
                                }
                                if (martes.isChecked){
                                    materiasDB.DiasMateria("martes", c_materia, "2")
                                    dias += " martes"
                                }
                                if (miercoles.isChecked){
                                    materiasDB.DiasMateria("miércoles", c_materia, "3")
                                    dias += " miércoles"
                                }
                                if (jueves.isChecked){
                                    materiasDB.DiasMateria("jueves", c_materia,"4")
                                    dias += " jueves"
                                }
                                if (viernes.isChecked){
                                    materiasDB.DiasMateria("viernes", c_materia,"5")
                                    dias += " viernes"
                                }
                                if (sabado.isChecked){
                                    materiasDB.DiasMateria("sabado", c_materia,"6")
                                    dias += " sabado"
                                }
                                arrayList!![posicion].detalles = dias
                                //Toast.makeText(this, diasselect[0],Toast.LENGTH_SHORT).show()
                                MateriasAdapter = AdapterMaterias(applicationContext, arrayList!!)
                                grid_Materias.adapter = MateriasAdapter
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancel"){
                                    dialog,_->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.changeColor->{
                        openColorPicker()
                        //Toast.makeText(this,materiasDB.error,Toast.LENGTH_SHORT).show()

                        true
                    }
                    R.id.Calify->{
                        var inten = Intent(this, act_Aspectos::class.java)
                        inten.putExtra("nombremateria",arrayList!![posicion].nombre.toString())
                        inten.putExtra("c_materia",c_materia)
                        startActivity((inten))
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
            Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun openColorPicker() {
        val colorPicker = AmbilWarnaDialog(this, R.color.white, object : OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {}
            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                arrayList!![posicion].color = color
                mDefaultColor = color
                mLayout?.setBackgroundColor(mDefaultColor)
                materiasDB.ColorMateria(color.toString(),c_materia)
            }
        })
        colorPicker.show()
    }

    fun cargarDias(c_materias:String):String{
        var cont = 0
        var todos = ""
        val dias = materiasDB.obtenerDias(c_materias)
        if (dias.moveToFirst()){
            while(cont < dias.count){
                todos += " "+dias.getString(0)
                dias.moveToNext()
                cont++
            }
        }
        dias.close()
        return todos
    }

}