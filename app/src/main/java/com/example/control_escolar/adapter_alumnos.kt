package com.example.control_escolar

import BDLayer.AlumnosBD
import BDLayer.Curp
import LogicLayer.Formats
import LogicLayer.ManagerImage
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.view_datos_alumno.view.*
import kotlinx.android.synthetic.main.viewsituacionalumno.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class adapter_alumnos:RecyclerView.Adapter<adapter_alumnos.ViewHolder>(){


    lateinit var Alumno: AlumnosBD
    lateinit  var alumnos: Cursor
    public val Nombre =  ArrayList<String>()
    public val Detalles =  ArrayList<String>()
    public val Imagen =  ArrayList<Int>()
    public  val sexo  = ArrayList<Int>()
    public  val Folio = ArrayList<String>()
    public val status =  ArrayList<String>()
    private var p = 0
    var folio = 0
    public  var posicion = 0
    lateinit var contex: Context
    inner class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var itemImagen:  ImageView
        var itemNombre: TextView
        var itemDetalles: TextView
        var itemOpciones: ImageView
        var itemfolio: TextView
        var itemStaus: TextView

        init {
            itemImagen = itemView.findViewById(R.id.image_alumno)
            itemNombre = itemView.findViewById(R.id.lbl_Nombre_Alumno)
            itemDetalles = itemView.findViewById(R.id.lbl_Detalles_Alumno)
            itemOpciones = itemView.findViewById(R.id.image_Opciones_Alumno)
            itemfolio = itemView.findViewById(R.id.lbl_Folio_Alumno)
            itemStaus = itemView.findViewById(R.id.lbl_status_Alumno)
            contex = itemView.context
            Alumno = AlumnosBD(itemView.context)
            alumnos = Alumno.obtenerAllsinBajas()
            try {
                //Toast.makeText(contex, alumnos.count, Toast.LENGTH_SHORT).show()
            }catch (Ex:Exception){Toast.makeText(contex, Ex.message.toString(), Toast.LENGTH_SHORT).show()}

            itemOpciones.setOnClickListener {
                posicion = adapterPosition //folio = Folio[adapterPosition].toInt()
                folio = Folio[adapterPosition].toInt()
                popupMenus(it)
            }
                itemView.setOnClickListener{
                try {
                    posicion = adapterPosition
                    folio = Folio[adapterPosition].toInt()
                    datos_alumno()

                }catch (Ex:Exception){
                    Toast.makeText(itemView.context,Ex.message.toString(), Toast.LENGTH_SHORT).show()
                }

            }
        }
        @RequiresApi(Build.VERSION_CODES.N)
        private fun popupMenus(v:View) {
            try {
                val popupMenus = PopupMenu(itemView.context, v)
                popupMenus.inflate(R.menu.menu_opcionesalumno)
                p = 0
                popupMenus.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.domiciliar -> {
                            val v = LayoutInflater.from(itemView.context).inflate(R.layout.viewupdatealumno, null)
                            var titulo = v.findViewById<TextView>(R.id.txtDatos)
                            val direccion = v.findViewById<EditText>(R.id.edit_Direccion_alumno)
                            val colonia = v.findViewById<EditText>(R.id.edit_Colonia_alumno)
                            val celular = v.findViewById<EditText>(R.id.edit_Celular_alumno)
                            val email = v.findViewById<EditText>(R.id.edit_Email_alumno)
                            val entidad = v.findViewById<EditText>(R.id.edit_Entidad_alumno)
                            if (Nombre_Escuela.tipo_escuela  == 1){
                                celular.isVisible = false
                                email.isVisible = false
                            }
                            titulo.text =  Nombre[posicion]
                            alumnos.moveToPosition(posicion)
                            //alumnos.moveToPosition(posicion)
                            changesexocolor(titulo)
                            direccion.setText(alumnos.getString(5))
                            colonia.setText(alumnos.getString(6))
                            celular.setText(alumnos.getString(8))
                            email.setText(alumnos.getString(9))
                            entidad.setText(alumnos.getString(10))



                            android.app.AlertDialog.Builder(itemView.context)
                                .setView(v)
                                .setPositiveButton("Ok") { dialog, _ ->
                                    if(Alumno.Domiciliar(direccion.text.toString(), colonia.text.toString(), celular.text.toString(), email.text.toString(), entidad.text.toString(), folio)){
                                        CargarAlumnos(v.context)
                                        //notifyDataSetChanged()
                                    }
                                    Toast.makeText(itemView.context,Alumno.error,Toast.LENGTH_SHORT).show()
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
                        }R.id.situacion -> {

                        val v = LayoutInflater.from(itemView.context).inflate(R.layout.viewsituacionalumno, null)
                        var titulo = v.findViewById<TextView>(R.id.txt_Situacion)
                        val situacion = v.findViewById<Spinner>(R.id.cbxsituacion)
                        var con = 0
                        var indi = 0
                        var disc = 0
                        var situation_position = 0
                        v.txt_especificar_discapacidad.isVisible = false
                        v.chx_Discapacitado.setOnCheckedChangeListener { _, isChecked ->
                            v.txt_especificar_discapacidad.isVisible = isChecked
                            v.txt_especificar_discapacidad.text!!.clear()
                        }


                        v.text_indicator_date.setOnClickListener {
                            showCalendar(Formats.getCurrentDate(),colorSituation(situacion.getSelectedItem().toString()).first, v.text_indicator_date)
                        }


                        //mostramos un calendar dependiendo del cambio de situación del alumno
                        situacion.onItemSelectedListener = object:
                            AdapterView.OnItemSelectedListener{
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                    situation_position = p2

                                    if (situation_position == 3) {
                                        var date = ""
                                        if (p > 0)  date = showCalendar(Formats.getCurrentDate(),colorSituation(situacion.getSelectedItem().toString()).first, v.text_indicator_date)
                                        v.text_indicator_date.text = "${situacion.getSelectedItem()}:${date}"
                                        v.text_indicator_date.setTextColor(Color.RED)
                                        v.text_indicator_date.text = alumnos.getString(30)
                                        p ++
                                    }
                                    if (situation_position == 2){
                                        var date = ""
                                        if (p > 0) date = showCalendar(Formats.getCurrentDate(),colorSituation(situacion.getSelectedItem().toString()).first, v.text_indicator_date)
                                        v.text_indicator_date.text = "${situacion.getSelectedItem()}:${date}"
                                        v.text_indicator_date.setTextColor(colorSituation(situacion.getSelectedItem().toString()).first)
                                        v.text_indicator_date.text = alumnos.getString(31)
                                        p ++
                                    }
                                    if (situation_position == 0 || situation_position == 1 || situation_position == 4){
                                        v.text_indicator_date.setTextColor(colorSituation(situacion.getSelectedItem().toString()).first)
                                        v.text_indicator_date.text = alumnos.getString(31)
                                        p = 1
                                    }

                                }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }

                        }

                        alumnos.moveToPosition(posicion)
                        changesexocolor(titulo)
                        titulo.text = Nombre[posicion]

                        if (alumnos.getInt(12) == 1) v.cbx_Condicionado.isChecked = true

                        if (alumnos.getInt(28) == 1) v.chx_Indigena.isChecked = true

                        if (alumnos.getInt(27) == 1){
                            v.chx_Discapacitado.isChecked = true
                            v.txt_especificar_discapacidad.isVisible = true
                        }

                        v.txt_especificar_discapacidad.setText(alumnos.getString(29))

                        //si tenemos fecha de baja

                        //colocamos el spinner dependiendo de la situacion
                        if (alumnos.getString(30) != "" && alumnos.getString(30) != null) situacion.setSelection(colorSituation("BAJA").second)
                        else situacion.setSelection(colorSituation(alumnos.getString(11)).second)

                        android.app.AlertDialog.Builder(itemView.context)
                            .setView(v)
                            .setPositiveButton("Actualizar") { dialog, _ ->

                                if (v.cbx_Condicionado.isChecked) con = 1
                                else con = 0

                                if (v.chx_Indigena.isChecked) indi = 1
                                if (v.chx_Discapacitado.isChecked) disc = 1
                                var f_baja = Formats.convertdate(v.text_indicator_date.text.toString())
                                if (situacion.selectedItemPosition == 3){//si damos de baja
                                    //f_baja = v.text_indicator_date.text.toString()
                                    updateDateunsubscribe(itemView, situacion.getSelectedItem().toString(),con,folio.toString().toInt(),indi,disc,v.txt_especificar_discapacidad.text.toString(),f_baja, )
                                    p = 0
                                }
                                else{//cualquier otro status
                                    Alumno.updateSituacionRegistro(situacion.getSelectedItem().toString(), con, folio.toString().toInt(),indi, disc, v.txt_especificar_discapacidad.text.toString(), f_baja, folio)
                                    Toast.makeText(itemView.context, Alumno.error, Toast.LENGTH_SHORT).show()
                                    p = 0
                                    Reorganizar(v.context)
                                    //CargarAlumnos(v.context)
                                    //Reorganizar(v.context)
                                }
                                dialog.dismiss()
                            }
                            .setNegativeButton("Cancelar"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                        R.id.datos_personales ->{
                            val v = LayoutInflater.from(itemView.context).inflate(R.layout.viewdatospersonalesalumno, null)
                            var titulo = v.findViewById<TextView>(R.id.txt_Datos_Personales)
                            val nombre = v.findViewById<EditText>(R.id.edit_Nombre_alumno)
                            val apellidop = v.findViewById<EditText>(R.id.edit_Apellidop_alumno)
                            val apellidom = v.findViewById<EditText>(R.id.edit_Apellidom_alumno)
                            val sexo = v.findViewById<Spinner>(R.id.cbx_Sexo_alumno)
                            val edad = v.findViewById<EditText>(R.id.edit_Edad_alumno)
                            val curp = v.findViewById<EditText>(R.id.edit_Curp_alumno)
                            val nacimiento = v.findViewById<DatePicker>(R.id.date_FechanacimientoAlumno)
                            try {

                                alumnos.moveToPosition(posicion)
                                changesexocolor(titulo)
                                titulo.text = Nombre[posicion].toString()

                                nombre.setText(alumnos.getString(1))
                                apellidop.setText(alumnos.getString(2))
                                apellidom.setText(alumnos.getString(3))
                                edad.setText(alumnos.getString(13))
                                curp.setText(alumnos.getString(7))
                                if (alumnos.getString(4).toString().toInt() == 1) {
                                    sexo.setSelection(0)
                                } else {
                                    sexo.setSelection(1)
                                }

                                edad.setText(alumnos.getString(13))
                                val fecha = alumnos.getString(17).split('-')
                                nacimiento.updateDate(
                                    fecha[0].toInt(),
                                    fecha[1].toInt() - 1,
                                    fecha[2].toInt()
                                )

                                curp.setOnFocusChangeListener { view, hasFocus ->
                                    if (!hasFocus) {
                                        if (curp.text.isNotEmpty()) {
                                            val validarcurp = Curp()
                                            validarcurp.validar_curp(curp.text.toString())
                                            edad.setText(validarcurp.edad.toString())
                                            val fecha = validarcurp.FNacimiento.split('-')
                                            nacimiento.updateDate(
                                                fecha[0].toInt(),
                                                fecha[1].toInt() - 1,
                                                fecha[2].toInt()
                                            )
                                            if (validarcurp.Sexo) {
                                                sexo.setSelection(0)
                                            } else {
                                                sexo.setSelection(1)
                                            }
                                        } /*else {
                                            edad.setText(Nombre_Escuela.Alumnos.getString(13))
                                            val fecha = Nombre_Escuela.Alumnos.getString(17).split('-')
                                            nacimiento.updateDate(
                                                fecha[0].toInt(),
                                                fecha[1].toInt() - 1,
                                                fecha[2].toInt()
                                            )
                                        }*/

                                    }
                                }

                            }
                            catch (Ex:Exception){
                               //Toast.makeText(itemView.context, Ex.message.toString(), Toast.LENGTH_SHORT ).show()
                            }

                            var sexo_1 = 0
                            android.app.AlertDialog.Builder(itemView.context)
                                .setView(v)
                                .setPositiveButton("Ok") { dialog, _ ->
                                    curp.requestFocus()
                                    //edad.requestFocus()
                                    if (sexo.selectedItem.toString() == "Masculino") sexo_1 = 1
                                    if (sexo.selectedItem.toString() == "Femenino") sexo_1 = 0
                                    if(Alumno.updatePersonalDates(nombre.text.toString(), apellidop.text.toString(), apellidom.text.toString(), sexo_1, edad.text.toString(),
                                            nacimiento.year.toString()+"-"+(nacimiento.month +1).toString()+"-"+nacimiento.dayOfMonth.toString(), curp.text.toString() ,folio)){
                                        CargarAlumnos(v.context)
                                    }
                                    Toast.makeText(itemView.context, Alumno.error, Toast.LENGTH_SHORT).show()
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
                        R.id.datos_tutor_1-> {
                            val v = LayoutInflater.from(itemView.context).inflate(R.layout.viewdatostutor, null)
                            var titulo = v.findViewById<TextView>(R.id.txt_datos_Tutor)
                            var tutor = v.findViewById<TextInputEditText>(R.id.txt_nombre_tutor)
                            val telefono = v.findViewById<EditText>(R.id.txt_telefono_tutor)
                            val email = v.findViewById<EditText>(R.id.txt_email_tutor)
                            val estudio = v.findViewById<EditText>(R.id.txt_estudios_tutor_1)
                            val ocupacion = v.findViewById<EditText>(R.id.txt_ocupacion_tutor_1)
                            //val condicionado = v.findViewById<CheckBox>(R.id.cbxCondicionado)

                            alumnos.moveToPosition(posicion)
                            changesexocolor(titulo)
                            tutor.setText(alumnos.getString(14))
                            telefono.setText(alumnos.getString(15))
                            email.setText(alumnos.getString(16))
                            estudio.setText(alumnos.getString(20))
                            ocupacion.setText(alumnos.getString(21))
                            titulo.text = Nombre[posicion]
                            android.app.AlertDialog.Builder(itemView.context)
                                .setView(v)
                                .setPositiveButton("Ok") { dialog, _ ->

                                    if (Alumno.datoscontacto(tutor.text.toString(), telefono.text.toString(), email.text.toString(), ocupacion.text.toString(), estudio.text.toString(),folio)){

                                        CargarAlumnos(v.context)
                                        //notifyDataSetChanged()
                                    }
                                    Toast.makeText(itemView.context, Alumno.error,Toast.LENGTH_SHORT).show()
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
                        R.id.datos_tutor_2-> {
                            val v = LayoutInflater.from(itemView.context).inflate(R.layout.view_datos_tutor_2, null)
                            var titulo = v.findViewById<TextView>(R.id.txt_datos_Tutor_2)
                            var tutor = v.findViewById<TextInputEditText>(R.id.txt_nombre_tutor_2)
                            val telefono = v.findViewById<EditText>(R.id.txt_telefono_tutor_2)
                            val email = v.findViewById<EditText>(R.id.txt_email_tutor_2)
                            val estudio = v.findViewById<EditText>(R.id.txt_estudios_tutor_2)
                            val ocupacion = v.findViewById<EditText>(R.id.txt_ocupacion_tutor_2)
                            //val condicionado = v.findViewById<CheckBox>(R.id.cbxCondicionado)

                            alumnos.moveToPosition(posicion)
                            changesexocolor(titulo)
                            tutor.setText(alumnos.getString(22))
                            telefono.setText(alumnos.getString(23))
                            email.setText(alumnos.getString(24))
                            estudio.setText(alumnos.getString(25))
                            ocupacion.setText(alumnos.getString(26))
                            titulo.text = Nombre[posicion]
                            android.app.AlertDialog.Builder(itemView.context)
                                .setView(v)
                                .setPositiveButton("Ok") { dialog, _ ->

                                    if (Alumno.datoscontacto_2(tutor.text.toString(), telefono.text.toString(), email.text.toString(), ocupacion.text.toString(), estudio.text.toString(),folio)){

                                        CargarAlumnos(v.context)
                                        //notifyDataSetChanged()
                                    }
                                    Toast.makeText(itemView.context, Alumno.error,Toast.LENGTH_SHORT).show()
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
                            android.app.AlertDialog.Builder(itemView.context)
                                .setTitle("Eliminar")
                                .setIcon(R.drawable.ic_baseline_warning_24)
                                .setMessage("¿Seguro de Eliminar el alumno ${itemNombre.text}?")
                                .setPositiveButton("Yes"){
                                        dialog,_->

                                    if( Alumno.borrarAlumno(folio)){
                                        //listaalumnos.removeAt(posicion)
                                        //listaAlumnos.adapter = adapterAlumnos(this, listaalumnos)
                                        CargarAlumnos(v.context)
                                        notifyDataSetChanged()
                                    }

                                    Toast.makeText(itemView.context, Alumno.error, Toast.LENGTH_SHORT).show()
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
                        R.id.cambiar_foto -> {
                                //var intent = Intent(v.context, act_Photo::class.java)
                                //v.context.startActivity((intent))

                            val intent = Intent(v.context, act_Photo::class.java)
                            intent.putExtra("nombre_alumno", Nombre[posicion])
                            intent.putExtra("folio", Folio[posicion])
                            val activity = v.context as Activity
                            activity.startActivityForResult(intent, 3)
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
            }catch (Ex: Exception){
                Toast.makeText(itemView.context,Ex.message.toString(),Toast.LENGTH_SHORT).show()

            }
        }
        fun datos_alumno(){
            val builder = androidx.appcompat.app.AlertDialog.Builder(itemView.context)
            val v = LayoutInflater.from(itemView.context).inflate(R.layout.view_datos_alumno, null)
            builder.setView(v)
            val dialog = builder.create()

            alumnos.moveToPosition(posicion)

            v.alumno_nombre.text = alumnos.getString(1).toString()+" "+alumnos.getString(2).toString()+" "+alumnos.getString(3).toString()
            v.alumno_edad.text = "Edad: "+alumnos.getString(13)
            v.alumno_curp.text = "Curp: "+alumnos.getString(7)

            v.alumno_domicilio.text = "Domicilio: "+ alumnos.getString(5)
            v.alumno_colonia.text = "Col: "+alumnos.getString(6)
            v.alumno_telefono.text = "Tel: "+alumnos.getString(8)
            v.alumno_correo.text = "correo: "+alumnos.getString(9)
            v.alumno_municipio.text = "Mun: "+alumnos.getString(10)

            v.alumno_nombre_tutor.text = "Nombre: " + alumnos.getString(14)
            v.alumno_telefono_tutor.text ="Telefono: "+ alumnos.getString(15)
            v.alumno_correo_tutor.text = "Correo: "+alumnos.getString(16)




            /*if (alumnos.getBlob(19) != null) v.alumno_foto.setImageBitmap(PictureLoader.loadPicture(itemImagen.context, alumnos.getBlob(19)))
            else v.alumno_foto.setImageResource(R.drawable.alumno)*/
            //cargamos desde arcgivo
            loadImageStudent(v.alumno_foto,alumnos.getString(0))

            if (alumnos.getString(4).toString().toInt() == 1){
                v.alumno_sexo.text = "Masculino"
            }
            else{
                v.alumno_sexo.text = "Femenino"
                v.alumno_sexo.setTextColor(Color.MAGENTA)
            }
            if (alumnos.getString(12).toString().toInt() == 1) v.alumno_condicionoado.text = "Condicionado: Si"
            else v.alumno_condicionoado.text = "Condicionado: No"
             v.alumno_status.text = "Situacion: "+ alumnos.getString(11)

            //val fecha = Nombre_Escuela.Alumnos.getString(17).split('-')
            //nacimiento.updateDate(fecha[0].toInt(),fecha[1].toInt()-1,fecha[2].toInt())
            dialog.show()
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v =  LayoutInflater.from(viewGroup.context).inflate(R.layout.card_alumos, viewGroup,false)
        return  ViewHolder(v)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemNombre.text = Nombre[position]
        viewHolder.itemDetalles.text = Detalles[position]
        //PictureLoader.loadPicture(contex, Imagen[position],viewHolder.itemImagen)

        viewHolder.itemImagen.setImageResource(R.drawable.alumno)

        try {
            ManagerImage(viewHolder.itemView.context).loadImageStudents(viewHolder.itemImagen, Folio[position])
        }catch (Ex:Exception){}

        alumnos.moveToPosition(position)
        viewHolder.itemfolio.text = Folio[position]

        if (alumnos.getString(30) != ""){
            viewHolder.itemStaus.setTextColor(colorSituation("BAJA").first)
            viewHolder.itemStaus.text = "BAJA"
        } else{
            viewHolder.itemStaus.setTextColor(colorSituation(status[position]).first)
            viewHolder.itemStaus.text = status[position]
        }
        //posicion = position
        //Toast.makeText(viewHolder.itemView.context, Nombre[position] +" "+ sexo[position].toString(),Toast.LENGTH_SHORT).show()
        /*if (status[position] == "BAJA") viewHolder.itemStaus.setTextColor(colorSituation(status[position]))
        if (status[position] == "ALTA") viewHolder.itemStaus.setTextColor(colorSituation(status[position]))
        if (status[position] == "NUEVO INGRESO") viewHolder.itemStaus.setTextColor(colorSituation(status[position]))
        if (status[position] == "REPETIDOR") viewHolder.itemStaus.setTextColor(colorSituation(status[position]))
        if (status[position] == "SIN COMUNICACIÓN") viewHolder.itemStaus.setTextColor(colorSituation(status[position]))*/

        if (sexo[position] == 0){
            viewHolder.itemNombre.setTextColor(Color.MAGENTA)
        }
        else viewHolder.itemNombre.setTextColor(Color.parseColor("#1A8AF9"))
    }


    fun colorSituation(situation: String): Pair<Int, Int> {
         return   when (situation) {
                "BAJA" -> Pair(Color.RED, 3)
                "ALTA" -> Pair(Color.parseColor("#1CB0F6"), 2)
                "NUEVO INGRESO" -> Pair(Color.parseColor("#57CB05"), 1)
                "REPETIDOR" -> Pair(Color.parseColor("#FFC501"), 0)
                "SIN COMUNICACIÓN" -> Pair(Color.parseColor("#5A2389"), 4)
                else -> Pair(Color.BLACK,-1)// Valor predeterminado, puedes ajustar según tus necesidades
            }
    }


    public  fun clearAll(){
        Nombre.clear()
        Detalles.clear()
        Imagen.clear()
        Folio.clear()
        sexo.clear()
        status.clear()
    }

    override fun getItemCount(): Int {
        return  Nombre.size
    }

    fun changesexocolor(view: View){
        if (sexo[posicion] == 0) {
            view.setBackgroundColor(Color.MAGENTA)
        }
    }
    fun CargarAlumnos(v:Context){
        clearAll()
        alumnos = Alumno.obtenerAll()
        Nombre_Escuela.getAlumnos(alumnos)
        alumnos = Alumno.obtenerAllsinBajas()
        val bitmap = BitmapFactory.decodeResource(v.resources, R.drawable.alumno)
        if(alumnos.moveToFirst()){
            do{
                Folio.add(alumnos.getString(0))
                Nombre.add(alumnos.getString(1) + " "+ alumnos.getString(2)+" "+ alumnos.getString(3))
                Detalles.add("N_lista " + alumnos.getString(18))
                //Imagen.add(PictureLoader.bitmapToByteArray(bitmap))
                Imagen.add(R.drawable.alumno)
                sexo.add(alumnos.getString(4).toInt())
                status.add(alumnos.getString(11))
            }while(alumnos.moveToNext())
            //recycler_alumnos.adapter = adapter
        }
        notifyDataSetChanged()
        //Toast.makeText(v, , Toast.LENGTH_SHORT).show()
    }

    fun update_adapter(folio:String, nombre:String, detalles:String, sexo:Int){
        Folio[posicion] =  folio
        Nombre[posicion] = nombre
        Detalles[posicion] = detalles
        this.sexo[posicion] = sexo
    }

    fun updateDateunsubscribe(view: View, situcaion:String, condicionado:Int, folio:Int, indigena:Int, discapacitado:Int, especifique:String, f_baja:String){

        android.app.AlertDialog.Builder(view.context).setTitle("Dar de baja a  "+Nombre[posicion]).setIcon(R.drawable.ic_baseline_warning_24).setMessage("¿Dar de baja al alumno?").setPositiveButton("Yes") { dialog, _ ->
            Alumno.updateSituacionBaja(
                condicionado,
                folio,
                indigena,
                discapacitado,
                especifique,
                f_baja,
                folio
            )

            CargarAlumnos(view.context)
            Toast.makeText(view.context, Alumno.error, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
            .setNegativeButton("No"){
                    dialog,_->

                dialog.dismiss()
            }
            .create()
            .show()


    }
    fun Reorganizar(v:Context){
        try {
            Alumno.ordenarporN_lista()
            CargarAlumnos(v)

        }catch (Ex:Exception){
            //Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    fun doesFileExist(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }



    fun loadImageStudent(imageView: ImageView, nameStudent: String){
        val filePatch  =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Students/${Nombre_Escuela.getName()}/$nameStudent.jpg"
        //val filePatch  =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Students/${Nombre_Escuela.getName()}/1.jpg"
        if(doesFileExist(filePatch)){
            imageView.setImageBitmap(null)

            //val bitmap = BitmapFactory.decodeFile(filePatch)
            //imageView.setImageBitmap(bitmap)
            Glide.with(contex)
                .load(File(filePatch))
                .into(imageView)
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    public fun showCalendar(fecha:String, color:Int, textView: TextView): String {
        var date = ""
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val initialDate = dateFormat.parse(fecha) ?: Date()

        // Obtiene el año, mes y día de la fecha inicial
        calendar.time = initialDate
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crea un DatePickerDialog y configúralo
        val datePickerDialog = android.app.DatePickerDialog(
            contex,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                 textView.text =  selectedDate
                // Puedes hacer algo con la fecha seleccionada, como mostrarla en un TextView
                date = selectedDate
            },
            year,
            month,
            day
        )

        try {
            // Obtén el DatePicker personalizado del DatePickerDialog
            val customDatePicker = datePickerDialog.datePicker

            // Configura el color de fondo del DatePicker
            customDatePicker.setBackgroundColor(color)

            // Mostrar el diálogo de selección de fecha
            datePickerDialog.show()
        } catch (ex: Exception) {
            Toast.makeText(contex, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }

        return date
    }



    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // Aquí puedes obtener la lista del Intent
                val extras = data?.extras
                if (extras != null) {
                    val lista = extras.getSerializable("seleccion_alumnos") as? ArrayList<Int>
                    listaseleccion = lista ?: ArrayList()
                }
                if (listaseleccion.size == 0) view?.switch_especificar_alumnos?.isChecked = false
            }
        }
    }*/



}
