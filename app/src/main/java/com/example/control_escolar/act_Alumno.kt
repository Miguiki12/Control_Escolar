package com.example.control_escolar

import BDLayer.AlumnosBD
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_act_alumno.*


class act_Alumno : AppCompatActivity() {

    var listaalumnos = ArrayList<DatosAlumnos>()
    lateinit var Alumno: AlumnosBD
    //val adapter = adapter_alumnos()
    var posicion = 0
    var folio = "0"
    var nombrealumno = ""
    lateinit  var alumnos:Cursor
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PERMISSION_CAMERA = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.title = "Alumnos"
        setContentView(R.layout.activity_act_alumno)
        Alumno = AlumnosBD(this)

        //CargarAlumnos()
           /* grid_alumno.setOnClickListener {

            }*/
            /*listaAlumnos.setOnItemClickListener { adapterView, view, i, l ->
                posicion = i
                folio = view.txt_Folio_Alumno.text.toString()
                nombrealumno = view.txt_Nombre_Alumno.text.toString()
                popupMenus(view.image_opcionesAlumno)
           }*/
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_inf_alumnos, menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_Agregar_Alumno-> AgregarAlumno()
            R.id.nav_importar_alumnos-> {
                var inten = Intent(this, act_Importar_Alumnos::class.java)
                inten.putExtra("agregar", "no")
                startActivity((inten))
            }
            R.id.nav_agregar_datos->{
                var inten = Intent(this, act_Importar_Alumnos::class.java)
                inten.putExtra("agregar", "si")
                startActivity((inten))
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
                //Toast.makeText(this, sexo_1.toString(), Toast.LENGTH_SHORT).show()
                if (nombre.text.length > 0 && apellidop.text.length > 0){
                /*if (Alumno.InsertAlumno(nombre.text.toString(), apellidop.text.toString(), apellidom.text.toString(), sexo_1)) {
                    Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()
                    //listaalumnos.add(DatosAlumnos(nombre.text.toString() + " " + apellidop.text.toString() + " " + apellidom.text.toString(),"",R.drawable.alumno,"", sexo_1))
                    //listaalumnos.clear()
                    CargarAlumnos()
                }*/

                }else Toast.makeText(this, "Nombre y apellido paterno son obligatorios", Toast.LENGTH_SHORT).show()

            }catch (Ex:Exception){
                Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        addDialog.create()
        addDialog.show()
    }
    /*fun CargarAlumnos(){
        var count = 0
        adapter.clearAll()
        alumnos = Alumno.obtenerAll()
        Nombre_Escuela.getAlumnos(alumnos)
        if(alumnos.moveToFirst()){
            while(count < alumnos.count){
                adapter.Folio.add(alumnos.getString(0))
                adapter.Nombre.add(alumnos.getString(1) + " "+ alumnos.getString(2)+" "+ alumnos.getString(3))
                adapter.Detalles.add("N_lista " + alumnos.getString(18))
                adapter.Imagen.add(R.drawable.escueladefault)
                adapter.sexo.add(alumnos.getString(4).toInt())
                alumnos.moveToNext()
                count ++
            }
            //grid_alumnos.adapter =
            grid_alumno.adapter = adapte
        }

        //Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()
    }*/

    fun CargarAlumnos(){
        var count = 0
        alumnos = Alumno.obtenerAll()
        Nombre_Escuela.getAlumnos(alumnos)
        listaalumnos.clear()
        if(alumnos.moveToFirst()){
            while(count < alumnos.count){
                listaalumnos.add(DatosAlumnos(alumnos.getString(1) + " " +alumnos.getString(2)+" " +alumnos.getString(3), "N_lista " + alumnos.getString(18),R.drawable.alumno, alumnos.getString(0), alumnos.getString(4).toInt(),alumnos.getString(15),alumnos.getString(16)))
                alumnos.moveToNext()
                count ++
            }
            //recycler_alumnos.adapter = adapter
            grid_alumno.adapter = adapterAlumnos(this,listaalumnos)
            //listaAlumnos.adapter =  adapterAlumnos(this, listaalumnos)
        }
    }

    private fun popupMenus(v: View) {
        try {
            val popupMenus = PopupMenu(this, v)
            popupMenus.inflate(R.menu.menu_opcionesalumno)
            popupMenus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.cambiar_foto->{
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        if (takePictureIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(takePictureIntent, CameraMetadata.REQUEST_AVAILABLE_CAPABILITIES_SECURE_IMAGE_DATA)
                        }
                    }
                    R.id.domiciliar -> {
                        val v = LayoutInflater.from(this).inflate(R.layout.viewupdatealumno, null)
                        var titulo = v.findViewById<TextView>(R.id.txtDatos)
                        val direccion = v.findViewById<EditText>(R.id.edit_Direccion_alumno)
                        val colonia = v.findViewById<EditText>(R.id.edit_Colonia_alumno)
                        val celular = v.findViewById<EditText>(R.id.edit_Celular_alumno)
                        val email = v.findViewById<EditText>(R.id.edit_Email_alumno)
                        val entidad = v.findViewById<EditText>(R.id.edit_Entidad_alumno)
                        titulo.text =  nombrealumno
                        alumnos.moveToPosition(posicion)
                        changesexocolor(titulo)

                        try {
                            direccion.setText(alumnos.getString(5))
                            colonia.setText(alumnos.getString(6))
                            celular.setText(alumnos.getString(8))
                            email.setText(alumnos.getString(9))
                            entidad.setText(alumnos.getString(10))

                        }catch (Ex:Exception){
                            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
                        }

                        android.app.AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                               if(Alumno.Domiciliar(direccion.text.toString(), colonia.text.toString(), celular.text.toString(), email.text.toString(), entidad.text.toString(), folio.toString().toInt())){
                                    CargarAlumnos()
                               }
                                Toast.makeText(this,Alumno.error,Toast.LENGTH_SHORT).show()
                                //Toast.makeText(itemView.context,Indice.error,Toast.LENGTH_SHORT).show()
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
                    R.id.situacion -> {
                    val v = LayoutInflater.from(this).inflate(R.layout.viewsituacionalumno, null)
                    var titulo = v.findViewById<TextView>(R.id.txt_Situacion)
                    val situacion = v.findViewById<Spinner>(R.id.cbxsituacion)
                    val condicionado = v.findViewById<CheckBox>(R.id.cbx_Condicionado)
                    val indigena = v.findViewById<CheckBox>(R.id.chx_Indigena)
                    val discapacitado = v.findViewById<CheckBox>(R.id.cbx_Condicionado)
                    val especifique = v.findViewById<CheckBox>(R.id.txt_especificar_discapacidad)
                    var con = 0
                    alumnos.moveToPosition(posicion)
                    changesexocolor(titulo)
                    titulo.text = nombrealumno
                    if (alumnos.getString(12).toString().toInt() == 1) condicionado.isChecked = true
                    else condicionado.isChecked = false

                    //if (alumnos.getString(28).toString().toInt() == 1) indigena.isChecked = true
                    //else indigena.isChecked = false

                    //if (alumnos.getString(29).toString().toInt() == 1) discapacitado.isChecked = true
                    //else discapacitado.isChecked = false

                    //especifique.text = alumnos.getString(29)

                    if(alumnos.getString(11).toString() == "REPETIDOR") situacion.setSelection(0)
                    if(alumnos.getString(11).toString() == "NUEVO INGRESO") situacion.setSelection(1)
                    if(alumnos.getString(11).toString() == "ALTAS") situacion.setSelection(2)
                    if(alumnos.getString(11).toString() == "BAJAS") situacion.setSelection(3)

                    var indi = 0
                    var disc = 0

                    if (indigena.isChecked) indi = 1
                    if (discapacitado.isChecked) disc = 1


                    android.app.AlertDialog.Builder(this)
                        .setView(v)
                        .setPositiveButton("Ok") { dialog, _ ->

                            if (condicionado.isChecked) con = 1
                            else con = 0

                        if(Alumno.updateSituacionRegistro(situacion.getSelectedItem().toString(), con, folio.toString().toInt(),indi, disc, especifique.text.toString(), "" , folio.toInt())){
                            CargarAlumnos()
                        }
                        Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()
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
                    R.id.datos_personales ->{
                        val v = LayoutInflater.from(this).inflate(R.layout.viewdatospersonalesalumno, null)
                        var titulo = v.findViewById<TextView>(R.id.txt_Datos_Personales)
                        val nombre = v.findViewById<EditText>(R.id.edit_Nombre_alumno)
                        val apellidop = v.findViewById<EditText>(R.id.edit_Apellidop_alumno)
                        val apellidom = v.findViewById<EditText>(R.id.edit_Apellidom_alumno)
                        val sexo = v.findViewById<Spinner>(R.id.cbx_Sexo_alumno)
                        val edad = v.findViewById<EditText>(R.id.edit_Edad_alumno)
                        val curp = v.findViewById<EditText>(R.id.edit_Curp_alumno)
                        val nacimiento = v.findViewById<DatePicker>(R.id.date_FechanacimientoAlumno)

                        titulo.text = nombrealumno
                        alumnos.moveToPosition(posicion)
                        changesexocolor(titulo)
                        try {
                            nombre.setText(alumnos.getString(1))
                            apellidop.setText(alumnos.getString(2))
                            apellidom.setText(alumnos.getString(3))
                            edad.setText(alumnos.getString(13))
                            curp.setText(alumnos.getString(7))
                            if (alumnos.getString(4).toString().toInt() == 1){
                                sexo.setSelection(0)
                            }
                            else{
                                sexo.setSelection(1)
                                titulo.setBackgroundColor(Color.MAGENTA)
                            }
                            edad.setText(alumnos.getString(13))
                            val fecha = alumnos.getString(17).split('-')
                            nacimiento.updateDate(fecha[0].toInt(),fecha[1].toInt()-1,fecha[2].toInt())

                        }catch (Ex:Exception){

                        }
                        //Toast.makeText(this, alumnos.getString(17), Toast.LENGTH_SHORT).show()



                        var sexo_1 = 0
                        android.app.AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("Ok") { dialog, _ ->
                                if (sexo.getSelectedItem().toString() == "Masculino") sexo_1 = 1
                                if (sexo.getSelectedItem().toString() == "Femenino") sexo_1 = 0
                                if(Alumno.DatosPersonales(nombre.text.toString(), apellidop.text.toString(), apellidom.text.toString(), sexo_1, edad.text.toString(),
                                        nacimiento.year.toString()+"-"+(nacimiento.month +1).toString()+"-"+nacimiento.dayOfMonth.toString(), curp.text.toString() ,folio.toString().toInt())){
                                    CargarAlumnos()
                                }
                                Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()
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
                    R.id.datos_tutor_1 -> {
                        val v = LayoutInflater.from(this).inflate(R.layout.viewdatostutor, null)
                        var titulo = v.findViewById<TextView>(R.id.txt_datos_Tutor)
                        var tutor = v.findViewById<TextInputEditText>(R.id.txt_nombre_tutor)
                        val telefono = v.findViewById<EditText>(R.id.txt_telefono_tutor)
                        val email = v.findViewById<EditText>(R.id.txt_email_tutor)
                        val estudio = v.findViewById<EditText>(R.id.txt_estudios_tutor_1)
                        val ocupacion = v.findViewById<EditText>(R.id.txt_ocupacion_tutor_1)


                        alumnos.moveToPosition(posicion)
                        changesexocolor(titulo)
                        tutor.setText(alumnos.getString(14))
                        telefono.setText(alumnos.getString(15))
                        email.setText(alumnos.getString(16))
                        titulo.text = nombrealumno

                        android.app.AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("SI") { dialog, _ ->

                                if (Alumno.datoscontacto(tutor.text.toString(), telefono.text.toString(), email.text.toString(), ocupacion.text.toString(), estudio.text.toString(), folio.toInt())){

                                    //CargarAlumnos()
                                    }
                                Toast.makeText(this, Alumno.error,Toast.LENGTH_SHORT).show()
                                /*if(Alumno.Situacion(situacion.getSelectedItem().toString(), con, folio.toString().toInt())){
                                    CargarAlumnos()
                                }
                                Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()*/

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
                    R.id.datos_tutor_2 -> {
                        val v = LayoutInflater.from(this).inflate(R.layout.view_datos_tutor_2, null)
                        var titulo = v.findViewById<TextView>(R.id.txt_datos_Tutor_2)
                        var tutor = v.findViewById<TextInputEditText>(R.id.txt_nombre_tutor_2)
                        val telefono = v.findViewById<EditText>(R.id.txt_telefono_tutor_2)
                        val email = v.findViewById<EditText>(R.id.txt_email_tutor_2)
                        val estudio = v.findViewById<EditText>(R.id.txt_estudios_tutor_2)
                        val ocupacion = v.findViewById<EditText>(R.id.txt_ocupacion_tutor_2)
                        //val condicionado = v.findViewById<CheckBox>(R.id.cbxCondicionado)

                        alumnos.moveToPosition(posicion)
                        changesexocolor(titulo)
                        tutor.setText(alumnos.getString(14))
                        telefono.setText(alumnos.getString(15))
                        email.setText(alumnos.getString(16))
                        titulo.text = nombrealumno

                        android.app.AlertDialog.Builder(this)
                            .setView(v)
                            .setPositiveButton("SI") { dialog, _ ->

                                if (Alumno.datoscontacto_2(tutor.text.toString(), telefono.text.toString(), email.text.toString(), ocupacion.text.toString(), estudio.text.toString(), folio.toInt())){

                                    //CargarAlumnos()
                                }
                                Toast.makeText(this, Alumno.error,Toast.LENGTH_SHORT).show()
                                /*if(Alumno.Situacion(situacion.getSelectedItem().toString(), con, folio.toString().toInt())){
                                    CargarAlumnos()
                                }
                                Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()*/

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
                    R.id.eliminar_alumno->{
                        /**set delete*/
                        android.app.AlertDialog.Builder(this)
                            .setTitle("Eliminar")
                            .setIcon(R.drawable.ic_baseline_warning_24)
                            .setMessage("¿Seguro de Eliminar el alumnno "+nombrealumno+ "?")
                            .setPositiveButton("Yes"){
                                    dialog,_->

                               if( Alumno.borrarAlumno(folio.toString().toInt())){
                                   //listaalumnos.removeAt(posicion)
                                   //listaAlumnos.adapter = adapterAlumnos(this, listaalumnos)
                                   CargarAlumnos()
                               }

                                    Toast.makeText(this, Alumno.error, Toast.LENGTH_SHORT).show()
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
                }
                    true
                }
                popupMenus.show()
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenus)
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (Ex: Exception){
                Toast.makeText(v.context, Ex.message.toString(), Toast.LENGTH_SHORT).show()

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

    private fun copyTextToClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Porta papeles", "Texto copiado al portapapeles")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, clipboard.text, Toast.LENGTH_LONG).show()
    }

    fun changesexocolor(view: View){
        if (alumnos.getString(4).toString().toInt() == 0) {
            view.setBackgroundColor(Color.MAGENTA)
        }
    }
    /*fun student(){
        val students = arrayListOf(
            Student("Juan Perez", R.drawable.raul),
            Student("Maria Rodriguez", R.drawable.miguel),
            Student("Luis Hernandez", R.drawable.carlos),
            Student("Ana Chavez", R.drawable.beto)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.students_recycler_view)
        recyclerView.adapter = adapterStudent(students)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }*/

    /*fun take_picture(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permissiON.CAMERA), REQUEST_PERMISSION_CAMERA)
        } else {
            take_picture()
        }
    }*/
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imageView = findViewById<ImageView>(R.id.image_vie)
            //listaalumnos[posicion].foto.
            imageView.setImageBitmap(imageBitmap)
        }
    }*/


}