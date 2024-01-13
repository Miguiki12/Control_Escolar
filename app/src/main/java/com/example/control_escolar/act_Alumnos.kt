package com.example.control_escolar

import BDLayer.AlumnosBD
import LogicLayer.VibratePhone
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_act_alumnos.*


class act_Alumnos : AppCompatActivity() {

    lateinit var Alumno: AlumnosBD
    val adapter = adapter_alumnos()
    lateinit  var alumnos: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_alumnos)
        val decoration = SpacingItemDecoration(1)
        recycler_alumnos.addItemDecoration(decoration)
        //recycler_alumnos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_alumnos.layoutManager = LinearLayoutManager(this)
        //grid_alumnos.adapter
        Alumno = AlumnosBD(this)
        //Alumno.onCreateTableAlumno()
        //grid_alumnos.adapter = adapter_alumnos
        this.supportActionBar?.title = "Alumnos"
        this.supportActionBar?.subtitle = Nombre_Escuela.getAlias()
        try {
            CargarAlumnos()
        }
        catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }


        recycler_alumnos.setOnClickListener {
            //datos_alumno()
        }
    }

    override fun onResume() {
        super.onResume()

        CargarAlumnos()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_inf_alumnos, menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_Agregar_Alumno-> AgregarAlumno()
            R.id.nav_importar_alumnos-> {
                val intent = Intent(this, act_Importar_Alumnos::class.java)
                intent.putExtra("agregar", "no")
                startActivityForResult(intent, 2)
            }
            R.id.nav_agregar_datos->{
                val intent = Intent(this, act_Importar_Alumnos::class.java)
                intent.putExtra("agregar", "si")
                startActivityForResult(intent, 2)
                /*var inten = Intent(this, act_Importar_Alumnos::class.java)
                inten.putExtra("agregar", "si")
                startActivity((inten))*/
            }
            R.id.nav_Reorganizar_Nlista -> Reorganizar()
        }
        return  super.onOptionsItemSelected(item)
    }

    fun AgregarAlumno(){
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.viewdatosalumno,null)
        /**set view*/
        val nombre = v.findViewById<EditText>(R.id.txtNombre_alumno)
        val apellidop = v.findViewById<EditText>(R.id.txtApellidop_alumno)
        val apellidom = v.findViewById<EditText>(R.id.txtApellidom_alumno)
        val sexo = v.findViewById<Spinner>(R.id.cbxSexo_alumno)
        var sexo_1 = 0
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") {
                dialog,_->
            try {
                if (sexo.getSelectedItem().toString() == "Masculino") sexo_1 = 1
                if (sexo.getSelectedItem().toString() == "Femenino") sexo_1 = 0
                if (nombre.text.length > 0 && apellidop.text.length > 0){

                    if (Alumno.InsertAlumno(nombre.text.toString(), apellidop.text.toString(), apellidom.text.toString(), sexo_1)) {
                        VibratePhone.vibrarTelefono(this, 100)
                        Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()
                        Reorganizar()
                        //listaalumnos.add(DatosAlumnos(nombre.text.toString() + " " + apellidop.text.toString() + " " + apellidom.text.toString(),"",R.drawable.alumno,"", sexo_1))
                        //listaalumnos.clear()
                        CargarAlumnos()
                    }

                }else Toast.makeText(this, "Nombre y apellido paterno son obligatorios", Toast.LENGTH_SHORT).show()

            }catch (Ex:Exception){
                Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        addDialog.create()
        addDialog.show()
    }



    //@SuppressLint("Range")
    fun CargarAlumnos(){
        var count = 0
        adapter.clearAll()
        alumnos = Alumno.obtenerAll()
        Nombre_Escuela.getAlumnos(alumnos)
        alumnos = Alumno.obtenerAllsinBajas()

        if(alumnos.moveToFirst()){
            while(count < alumnos.count){
                adapter.Folio.add(alumnos.getString(0))
                adapter.Nombre.add(alumnos.getString(1) + " "+ alumnos.getString(2)+" "+ alumnos.getString(3))
                adapter.Detalles.add("N_lista " + alumnos.getString(18))
                //adapter.Imagen.add( alumnos.getBlob(19))
                adapter.sexo.add(alumnos.getString(4).toInt())
                adapter.status.add(alumnos.getString(11))
                alumnos.moveToNext()
                count ++
            }
            recycler_alumnos.adapter = adapter
        }
    }
    fun Reorganizar(){
        try {
            Alumno.ordenarporN_lista()
            CargarAlumnos()

        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                    CargarAlumnos()
            }
        }else if (requestCode == 3 && resultCode == RESULT_OK) {
            val image = data?.getParcelableExtra("picture") as? Bitmap
            if (image != null) {
                //val byteArray: ByteArray = PictureLoader.bitmapToByteArray(image)
                //adapter.Imagen[adapter.posicion] = image
                Alumno.setPicture(image, adapter.Folio[adapter.posicion].toInt())
                adapter.notifyDataSetChanged()
                recycler_alumnos.adapter = adapter
            }
        }
    }



 }