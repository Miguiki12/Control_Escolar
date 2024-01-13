package com.example.control_escolar

import BDLayer.*
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.Calendar.getInstance
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_act_alumno.*
import kotlinx.android.synthetic.main.activity_act_alumnos.*
import kotlinx.android.synthetic.main.activity_act_asistencia.*
import kotlinx.android.synthetic.main.card_argumentar_asistencia.view.*
import kotlinx.android.synthetic.main.listaasistencia.view.*
import kotlinx.android.synthetic.main.view_buscar_por_materia.view.*
import kotlinx.android.synthetic.main.view_reporte_asistencia.view.*
import kotlinx.android.synthetic.main.view_stake.view.*
import kotlinx.android.synthetic.main.view_suspender_justificar.view.*
import kotlinx.android.synthetic.main.viewdatosactividad.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class act_Asistencia : AppCompatActivity() {
    var listaasistencia = ArrayList<Asistencia>()
    var C_materias = ArrayList<String>()
    var checado = false
    lateinit var sendEMail: sendEMail
    private lateinit var myCalendar: Calendar
    lateinit var AsistenciaBD : AsistenciaBD
    lateinit var JustificarBD: JustificarBD
    lateinit var materiasBD: MateriasBD
    lateinit var participacionBD: ParticipacionBD
    lateinit var pendientesBD: PendienteBD
    lateinit var TareasBD: TareasBD
    lateinit var pdf : crearPDF
    var suspendido = 0
    var Semana = arrayOf("domingo","lunes", "martes", "miércoles", "jueves", "viernes", "sabado")
    var fecha = ""
    var posicion = 0
    var argumento = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
              setContentView(R.layout.activity_act_asistencia)
        materiasBD = MateriasBD(this)
        sendEMail = sendEMail(this)
        AsistenciaBD = AsistenciaBD(this)
        //AsistenciaBD = AsistenciaBD(this)
        TareasBD = TareasBD(this)
        pdf = crearPDF(this)
        participacionBD = ParticipacionBD(this)
        pendientesBD = PendienteBD(this)
        JustificarBD = JustificarBD(this)
        //JustificarBD.deletetable()
        //JustificarBD.Createtable()
        getStatusSuspendido()


        listaAsistencia.setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, pos, id -> // TODO Auto-generated method stub
            posicion = pos
            popupMenus(arg1.txt_NombreAsistencia)
            true
        })
        /*listaAsistencia.setOnItemClickListener { adapterView, view, i, l ->
            val currentPosition = listaAsistencia.firstVisiblePosition
            if (listaasistencia[i].cbxasistencia == false) listaasistencia[i].cbxasistencia = true
            else listaasistencia[i].cbxasistencia = false
            listaAsistencia.adapter = adapterAsistencia(this, listaasistencia, 0,suspendido)
            listaAsistencia.setSelection(currentPosition)
        }*/
        clickToList()
        myCalendar = getInstance()
        updateLabel(myCalendar)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigation_move_dates_activity)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_back->{
                    myCalendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
                    //updateDateDisplay()
                    CargarAsistencia(updateDateDisplay())
                    updateLabel(myCalendar)
                    true
                }
                R.id.nav_next->{
                    myCalendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    CargarAsistencia(updateDateDisplay())
                    updateLabel(myCalendar)
                    true
                }
                else-> false
            }

        }
    }



    fun clickToList(){
        try {
            listaAsistencia.setOnItemClickListener { adapterView, view, i, l ->
                //val currentPosition = listaAsistencia.firstVisiblePosition
                if (listaasistencia[i].cbxasistencia == false) listaasistencia[i].cbxasistencia =
                    true
                else listaasistencia[i].cbxasistencia = false
                listaAsistencia.adapter = adapterAsistencia(this, listaasistencia, 0, suspendido)
                ///.listaAsistencia.setSelection(currentPosition)
            }
        }catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateDisplay():String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(myCalendar.time)

        // Obtener el nombre del día de la semana
        val calendar = java.util.Calendar.getInstance()
        calendar.time = myCalendar.time
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(myCalendar.time)

        val nav_days = findViewById<BottomNavigationView>(R.id.navigation_move_dates_activity)
        fecha = formattedDate
        nav_days.menu.findItem(R.id.nav_today)?.title = "$dayName $formattedDate"
        return formattedDate
    }


    fun getStatusSuspendido(){
        val settingsBD = SettingsBD(this)
        val cursor = settingsBD.getAllSettings()
        if(cursor.moveToFirst()) suspendido = cursor.getInt(5)
        cursor.close()
        settingsBD.close()
    }

    fun CargarAsistencia(fecha:String):Int{
        var asis = false
        val asistencia = AsistenciaBD.obtenerAll(fecha)
        listaasistencia.clear()
        try {
            if(asistencia.moveToFirst()){
                argumento = ""
                if (asistencia.getString(11)  != null) argumento = asistencia.getString(11)

                do{
                    asis = asistencia.getString(5).toInt() == 1
                    listaasistencia.add(
                        Asistencia( asistencia.getString(0)+ " " +asistencia.getString(1)+" " +asistencia.getString(2),  //PictureLoader.loadPicture(this,asistencia.getBlob(4))
                            "N_lista " +asistencia.getString(7),R.drawable.alumno, asis, asistencia.getString(6).toInt(), asistencia.getString(3).toInt(),asistencia.getString(8), asistencia.getString(9).toInt(), asistencia.getString(10))
                    )
                }while (asistencia.moveToNext())
                listaAsistencia.adapter =  adapterAsistencia(this, listaasistencia, 0,suspendido)
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
        finally {
            asistencia.close()
            asistencia.close()
        }
        return asistencia.count
    }


    fun CargarAlumnos(){
        var count = 0
        listaasistencia.clear()
        try {
            if(Nombre_Escuela.Alumnos.moveToFirst()){
                while(count < Nombre_Escuela.Alumnos.count){
                    listaasistencia.add(
                        Asistencia(Nombre_Escuela.Alumnos.getString(1) + " " +Nombre_Escuela.Alumnos.getString(2)+" " +Nombre_Escuela.Alumnos.getString(3),
                            "N_lista " +Nombre_Escuela.Alumnos.getString(18),R.drawable.alumno//PictureLoader.loadPicture(this, Nombre_Escuela.Alumnos.getBlob(19))
                            , false, Nombre_Escuela.Alumnos.getString(4).toInt(), Nombre_Escuela.Alumnos.getString(0).toInt(),Nombre_Escuela.Alumnos.getString(15), 0,Nombre_Escuela.Alumnos.getString(16))
                    )
                    Nombre_Escuela.Alumnos.moveToNext()
                    count ++
                }
                listaAsistencia.adapter =  adapterAsistencia(this, listaasistencia, 0,suspendido)
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    fun CargarMaterias():ArrayList<String>{
        var  arrayMaterias = ArrayList<String>()
        try {
            var count = 0
            val materias = materiasBD.obtenerMAterias()
            if(materias.moveToFirst()){
                while(count < materias.count){
                    arrayMaterias.add(materias.getString(1))
                    C_materias.add(materias.getString(0))
                    materias.moveToNext()
                    count ++
                }
            }
            materias.close()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return arrayMaterias
    }
    fun loadStatus(){
        var count  = 0
        val status = JustificarBD.getAlljustify(fecha)
        if (status!!.moveToFirst()){
            while (count < status.count){
                listaasistencia[findstatus(status.getString(0).toInt())].status = status.getString(3).toInt()
                //Toast.makeText(this,status.getString(0) + " " +status.getString(1), Toast.LENGTH_SHORT).show()
                status.moveToNext()
                count++
            }
        }
        status.close()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (Nombre_Escuela.get_tipo() == 1) menuInflater.inflate(R.menu.menu_sup_asistencia, menu)
        if (Nombre_Escuela.get_tipo() == 2) menuInflater.inflate(R.menu.menu_sup_asistencia_2, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.Todos_asistieron -> TodosAsistieron()
            R.id.Reenviar_Faltas ->{
                //val attachment = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Matricula.pdf"
                //sendEMail.openGmailWithAttachment("Faltas","dia de hoy", attachment, this)//{sendEMail.getLogin()
            //}
                avisar_x_correo()
                enviarfaltasW()}
            R.id.Buscar_asistencia-> seleccionarfecha()
            R.id.nav_eliminar_Asistencia -> deleteAttendance()
            R.id.nav_Guardar_Asistencia->{
                GuardarAsistencia(fecha, myCalendar.time.day.toString().toInt())
                if (hoy(fecha) && !pendientesBD.get_check_asistence(fecha) && AsistenciaBD.obtenerFaltas(fecha).moveToFirst()){
                    enviarfaltasW()
                }
            }
            R.id.Bajo_Desempaño-> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    if (pdf.bajoDesempeño(TareasBD.obtenerBajodesempeño(fecha))) pdf.abrirdocumento("Bajo_desempeño",this)
                    //Toast.makeText(this,TareasBD.error, Toast.LENGTH_SHORT).show()
                }catch (Ex:Exception) {Toast.makeText(this,"No hay actividades de ese dia",Toast.LENGTH_SHORT).show()}
            }
            R.id.Imprimir_Asistencia->reporteAsistencia(this)
            R.id.Argumentar->putArgumentDay()
            R.id.Bitacora->sendEMail.openGmailWithAttachment("Enviar correo","cuerpo","",this)
        }
        return  super.onOptionsItemSelected(item)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun updateLabel(myCalendar: Calendar){
        val myformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myformat, Locale.getDefault())
        this.supportActionBar?.title = "Asistencia: ${Nombre_Escuela.getAlias()}"
        fecha = (sdf.format(myCalendar.time))
        this.supportActionBar?.subtitle = fechacompleta(fecha)
        //this.supportActionBar?.subtitle = Horairio(myCalendar.time.day.toString().toInt())
        if(CargarAsistencia(fecha) == 0) {
            listaasistencia.clear()
            CargarAlumnos()
        }
        loadStatus()
    }

    fun TodosAsistieron(){
       var cont = 0
        if(checado) checado = false
        else checado = true
        while(cont < listaasistencia.count()) {
           listaasistencia[cont].cbxasistencia = checado
           cont ++
       }
        listaAsistencia.adapter = adapterAsistencia(this, listaasistencia, 0,suspendido)
    }

    fun Horairio(dia:Int):String{
        var Materias = ""
        try {
            var cont = 0
            val materias = AsistenciaBD.obtenerHorario(Semana[dia])
            if (materias.moveToFirst()){
                while(cont < materias.count){
                    Materias += materias.getString(2) + " "
                    materias.moveToNext()
                    cont ++
                }
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return  Materias
    }

    fun GuardarAsistencia(fecha:String, dia: Int){
        try {
            var cont = 0
            var asis = 0
            if(AsistenciaBD.borrarAsistencia(fecha)){
                while (cont < listaasistencia.count()){
                    if (listaasistencia[cont].cbxasistencia == true) asis = 1
                    else asis = 0
                    AsistenciaBD.InsertarAsistencia(listaasistencia[cont].Folio.toString(), "1", fecha, asis, Semana[dia], listaasistencia[cont].status,argumento)
                    cont ++
                }
                Toast.makeText(this, "Se registro la asistencia del dia $fecha correctamente", Toast.LENGTH_SHORT).show()
            }
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    fun avisar_x_correo():Boolean{
        try {
            //creamos el archivo para adjuntar en formato pdf
            pdf.pdf_falta(fechacompleta(fecha))
            //Adjuntamos el archivo creado
            sendEMail.adjuntar("Falta","")
            //enviamos el el mensaje con el archivo adjunto
            //sendEMail.send("Falta ", "Falta del dia " + fecha, sendEMail.allMails())
           if (sendEMail.avisarFaltas(fechacompleta(fecha), AsistenciaBD.obtenerFaltas(fecha))) {
               //pdf.deletePdf("Falta")
               //registramos que ya enviamos el aviso por correo
               insertPending()
               Toast.makeText(this, "Se envio el correo correctamente", Toast.LENGTH_SHORT).show()
           }else Toast.makeText(this, sendEMail.error, Toast.LENGTH_LONG).show()

            return  true
        }
        catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
            sendEMail.send("Falta","Sele informa que su hijo falto a clases el dia"+ fechacompleta(fecha), "migvaz5@hotmail.com")
            return  false
        }
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    fun enviarfaltasW(){
        android.app.AlertDialog.Builder(this)
           .setTitle("Informar Faltas")
            .setIcon(R.drawable.ic_importar_lista)
            .setMessage("¿Desea Avisar de las Faltas de hoy?")
            .setPositiveButton("Whatsapp"){
                    dialog,_->
                dialog.dismiss()
            val sendIntent = Intent()
            val target = false
            val indices = listaasistencia.indices
                .filter { listaasistencia[it].cbxasistencia == target }
                .toList()
                //avisar_x_correo()
                var count = 0
                sendIntent.setAction(Intent.ACTION_VIEW)
            while (count < indices.count()) {
                if (listaasistencia[indices[count]].telefono.length > 0){
                    val url = "whatsapp://send?phone=" +listaasistencia[indices[count]].telefono + "&text= Falta del " +fechacompleta(fecha)  +" del alumno "+listaasistencia[indices[count]].nombre
                    sendIntent.setData(Uri.parse(url))
                    startActivity(sendIntent)
                }
                count++
            }
            }
            .setNegativeButton("Correo"){
                    dialog,_->
                avisar_x_correo()
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun findstatus(data:Int):Int{
        val indices = listaasistencia.indices
            .filter { listaasistencia[it].Folio == data }
            .toList()
        return indices.get(0)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun popupMenus(v: View) {
        try {
            val popupMenus = PopupMenu(this,v)
            popupMenus.inflate(R.menu.menu_tocuh_asistencia)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    //R.id.nav_Justificar->updatestatus(2)
                    R.id.nav_Retardo->updatestatus(1)
                    R.id.nav_Participacion->add_stake()
                    R.id.nav_limpiar->updatestatus(0)
                    R.id.nav_Justificar_Desde->Justificarfaltas()
                    R.id.nav_Reporte-> gotoReporte()
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


    @RequiresApi(Build.VERSION_CODES.N)
    private fun seleccionarfecha(){
        val datePicker = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            myCalendar.set(Calendar.YEAR, i)
            myCalendar.set(Calendar.MONTH, i2)
            myCalendar.set(Calendar.DAY_OF_MONTH, i3)
            if (i2 < getInstance().time.month){
                updateLabel(myCalendar)
            }
            if (i2  == getInstance().time.month){
                if(i3 <= getInstance().time.date){
                    updateLabel(myCalendar)
                }
                else Toast.makeText(applicationContext,  " No se puede crear una dia de asistencia posteriro, favor de verificar", Toast.LENGTH_SHORT).show()
            }
            if(i2 > getInstance().time.month){
                Toast.makeText(applicationContext, " No se puede crear una dia de asistencia posterior, favor de verificar", Toast.LENGTH_SHORT).show()
            }
        }
        DatePickerDialog(this, datePicker, myCalendar.get(java.util.Calendar.YEAR),myCalendar.get(java.util.Calendar.MONTH),myCalendar.get(java.util.Calendar.DAY_OF_MONTH),).show()
    }


    private fun hoy(today:String):Boolean{
        var dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var dia = dateFormat.format(Date())
        if (dia == today)  return true
        else return false
    }

    private fun betweendata(begin:String, end:String , today: String):Boolean{
        return today in begin..end
    }

    fun putArgumentDay(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.card_argumentar_asistencia, null)
        view.barra_Arguementar.text = view.barra_Arguementar.text.toString() + "\n${fechacompleta(fecha)}"
        view.txt_detalle_argumentacion.setText(argumento)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        view.btn_Argumentar.setOnClickListener{
            if (view.txt_detalle_argumentacion.text!!.isNotEmpty()){
                argumento = view.txt_detalle_argumentacion.text.toString()
                //AsistenciaBD.updateArgumentAttendance(fecha,view.txt_detalle_argumentacion.text.toString())
            }
            Toast.makeText(this, "Para registrar el argumento guarde la lista nuevemente", Toast.LENGTH_LONG).show()
            dialog.hide()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun Justificarfaltas(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.view_suspender_justificar, null)
        val data = fecha.split('-')
        view.dtp_Inicio.init(data[0].toInt(),data[1].toInt() - 1 ,data[2].toInt(),null)
        view.dtp_Fin.init(data[0].toInt(),data[1].toInt() - 1 ,data[2].toInt(),null)
        view.lbl_Titulo.text = listaasistencia[posicion].nombre
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        view.btn_Suspencion.setOnClickListener{
            val f_inicio =  convertData(view.dtp_Inicio.year, view.dtp_Inicio.month, view.dtp_Inicio.dayOfMonth)
            val f_fin =  convertData(view.dtp_Fin.year, view.dtp_Fin.month, view.dtp_Fin.dayOfMonth)
            if (view.rb_Justificado.isChecked) InsertJustify(2, f_inicio, f_fin, 1)
            if (view.rb_Suspendido.isChecked) Insertdiscontinued(3, f_inicio, f_fin)
            dialog.hide()
        }
        view.txt_Historial_Suspencion.setOnClickListener { gotoStakePrimarias()}
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun add_stake(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.view_stake, null)
        view.txt_title_stake.text = listaasistencia[posicion].nombre
        //Cargamos las materias
        val adapterMatter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,CargarMaterias())
        view.cbx_matter_stake.adapter =  adapterMatter
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        view.txt_histori_stake.setOnClickListener { gotoStake(view.cbx_matter_stake.selectedItemPosition) }
        view.btn_add_stake.setOnClickListener{
            try {
                if(participacionBD.isNumeric(view.txt_point_stake.text.toString())){
                    if (Insertstake(view.txt_point_stake.text.toString(), convertoday(), C_materias[view.cbx_matter_stake.selectedItemPosition].toInt())) {
                        Toast.makeText(this, participacionBD.error, Toast.LENGTH_SHORT).show()
                        dialog.hide()
                    }
                }else Toast.makeText(this,"Especifique el porcentaje ", Toast.LENGTH_SHORT).show()
            }catch (Ex:Exception){
                Toast.makeText(this,"verifique que los puntos sean numericos y especifique la materia",Toast.LENGTH_SHORT).show()
            }
        }
    }

    //@RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation", "NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    fun reporteAsistencia(context: Context){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.view_reporte_asistencia, null)
        var correct = false
        val date = fecha.split('-')
        view.barra_Reporte_Asistencia.text = view.barra_Reporte_Asistencia.text.toString()  + "\n"+ fechacompleta(fecha)
        builder.setView(view)
        val total = AsistenciaBD.get_reporte_asistencia(fecha)

        if (total.moveToFirst()){
            try {
                var total_mujeres = 0
                var total_hombres = 0
                //si no se ha modificado el sexo
                if (total.count == 1){
                        if (total.getString(1).toString() == "1"){
                            total_hombres = total.getString(0).toInt()
                            view.lbl_cantidad_hombre.text = total_hombres.toString()
                            view.lbl_cantidad_total.text = (total_hombres + total_mujeres).toString()
                        }
                        else{
                            total_mujeres = total.getString(0).toInt()
                            view.lbl_cantidad_mujeres.text = total_mujeres.toString()
                            view.lbl_cantidad_total.text = (total_hombres + total_mujeres).toString()
                        }
                }
                //si hay masculino y femenino
                if (total.count == 2){
                    total_hombres = total.getString(0).toInt()
                    view.lbl_cantidad_hombre.text = total_hombres.toString()
                    total.moveToNext()
                    total_mujeres = total.getString(0).toInt()
                    view.lbl_cantidad_mujeres.text = total_mujeres.toString()
                    view.lbl_cantidad_total.text = (total_hombres + total_mujeres).toString()
                }
                correct = true
            }
            catch (Ex:Exception){
                correct = false
                view.lbl_status_reporte_asistencia.text = "Especificado el sexo de los alumnos. Datos incorrectos"
                Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }

        val dialog = builder.create()
        dialog.show()

        view.btn_imprimir_reporte.setOnClickListener {
            try {
                if (pdf.asistencia_mes(
                        AsistenciaBD.getIntervalMonth(fecha),
                        AsistenciaBD.asistenciaMes(date[1].toInt(), date[0].toInt()),
                        AsistenciaBD.asistenciaDias(date[1].toInt(),date[0].toInt()),
                        AsistenciaBD.sexoxMes(date[1].toInt(), date[0].toInt()),
                        AsistenciaBD.totalAlumnosMes(date[1].toInt(), date[0].toInt()),
                        this
                    )
                )
                    pdf.abrirdocumento("Asistencia_Mes", this)
            }
            catch (Ex:Exception){Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()}
        }

        view.btn_mes_reporte.setOnClickListener{
            try {
                val excel =  createExcel(this)
                excel.printFaltasMes("Noviembre",
                    AsistenciaBD.asistenciaDias(date[1].toInt(),date[0].toInt()),
                    AsistenciaBD.asistenciaMes(date[1].toInt(),date[0].toInt()),
                    AsistenciaBD.totalAlumnosMes(date[1].toInt(), date[0].toInt()),
                    AsistenciaBD.sexoxMes(date[1].toInt(), date[0].toInt()))
                //if (pdf.reporteAsistencia(fechacompleta(fecha), fecha,total, this)) abrirdocumento()
                dialog.hide()
            }catch (Ex:Exception){
             Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
        view.btn_enviar_reporte.setOnClickListener {
            try {
                if (correct){
                    sendEMail.getLogin()
                    val createExcel = createExcel(this)
                    createExcel.getAssesstByDay(fecha)
                    if(pdf.reporteAsistencia(fechacompleta(fecha), fecha,total, this)){
                        sendEMail.adjuntar("Reporte_Asistencia","")
                        //sendEMail.avisarFaltas(fechacompleta(fecha),AsistenciaBD.obtenerFaltas(fecha))
                        sendEMail.sendwhitattach("Reporte_Asistencia","",sendEMail.correodirector)

                    }
                }else Toast.makeText(this,"No se puede crear el reporte con datos incorrectos", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, sendEMail.error, Toast.LENGTH_SHORT).show()
            }catch (Ex:Exception){
                Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
        //total.close()
    }


    fun updatestatus(status:Int){
        val currentPosition = listaAsistencia.firstVisiblePosition
        listaasistencia[posicion].status = status
        listaAsistencia.adapter = adapterAsistencia(this, listaasistencia, 0, suspendido)
        listaAsistencia.setSelection(currentPosition)
    }

    fun InsertJustify(tipo: Int, F_incio:String, F_fin:String, c_materia:Int){
        JustificarBD.iFolio = listaasistencia[posicion].Folio
        JustificarBD.dtF_inicio = F_incio
        JustificarBD.dtF_fin = F_fin
        JustificarBD.iC_materia = 0//C_materias[c_materia].toInt()
        JustificarBD.iTipo = tipo
        JustificarBD.iC_reporte = 0
        JustificarBD.Insertjustify()
        if (betweendata(F_incio, F_fin, fecha)) updatestatus(tipo)
    }

    fun Insertstake(puntaje: String,  Fecha:String, c_materia: Int):Boolean{
        participacionBD.iFolio = listaasistencia[posicion].Folio
        participacionBD.dtFecha = Fecha
        participacionBD.sDescripcion = "Participacion"
        participacionBD.iC_materia = c_materia
        participacionBD.sPuntaje = puntaje
        return participacionBD.Inserstake()
    }

    fun Insertdiscontinued(tipo: Int, F_incio:String, F_fin:String){
        JustificarBD.iFolio = listaasistencia[posicion].Folio
        JustificarBD.dtF_inicio = F_incio
        JustificarBD.dtF_fin = F_fin
        JustificarBD.iC_materia = tipo
        JustificarBD.iTipo = tipo
        JustificarBD.iC_reporte = 0
        if(JustificarBD.Insertdiscontinued()){
            sendEmailSuspencion(
                listaasistencia[posicion].nombre,
                "del $F_incio al $F_fin",
                listaasistencia[posicion].correo)}
        if (betweendata(F_incio, F_fin, fecha)) updatestatus(tipo)
    }

    fun updateNormalStatus(){
        if(AsistenciaBD.updateAtNormalAttendance(listaasistencia[posicion].Folio, fecha, 1)) {
            updatestatus(0)
        }
        Toast.makeText(this,AsistenciaBD.error, Toast.LENGTH_SHORT).show()
    }

    fun insertPending(){
        pendientesBD.sFecha = convertoday()
        pendientesBD.Insert_check_today()
    }

    fun gotoReporte(){
        var inten = Intent(this, act_Reportes::class.java)
        inten.putExtra("nombre", listaasistencia[posicion].nombre)
        inten.putExtra("folio", listaasistencia[posicion].Folio.toString())
        inten.putExtra("sexo", listaasistencia[posicion].sexo.toString())
        inten.putExtra("email", listaasistencia[posicion].correo)
        inten.putExtra("telefono", listaasistencia[posicion].telefono)
        startActivity((inten))
    }
    fun gotoStake(select:Int ){
        if (C_materias.isNotEmpty()){
        var inten = Intent(this, act_Historial::class.java)
        inten.putExtra("folio", listaasistencia[posicion].Folio.toString())
        inten.putExtra("c_materia", C_materias[select].toString())
        inten.putExtra("nombre", listaasistencia[posicion].nombre)
        inten.putExtra("tipo", "1")
        startActivity((inten))
        }else Toast.makeText(this,"Necesita especificar la materia", Toast.LENGTH_LONG).show()
    }
    fun gotoStakePrimarias(){
            var inten = Intent(this, act_Historial::class.java)
            inten.putExtra("folio", listaasistencia[posicion].Folio.toString())
            inten.putExtra("c_materia", 0)
            inten.putExtra("nombre", listaasistencia[posicion].nombre)
            inten.putExtra("tipo", "3")
            startActivityForResult((inten), 3)
    }
    fun sendEmailSuspencion(alumno:String, periodo:String,correo:String){
        pdf.Suspencion(alumno,periodo)
        sendEMail.adjuntar("Suspension","")
        sendEMail.sendwhitattach("Suspensión del alumno $alumno", "",correo)
        Toast.makeText(this, sendEMail.error, Toast.LENGTH_LONG).show()
        //pdf.abrirdocumento("Suspencion", this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun convertData(year:Int, month:Int, day:Int ):String{
        val otherCalendar = getInstance()
        otherCalendar.set(Calendar.YEAR, year)
        otherCalendar.set(Calendar.MONTH, month)
        otherCalendar.set(Calendar.DAY_OF_MONTH, day)
        val myformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myformat, Locale.UK)
        return  (sdf.format(otherCalendar.time))
    }

    fun convertdate(fecha:String ):String{
        val myformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myformat, Locale.UK)
        return  (sdf.format(fecha))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun get_Day(fecha: String):Int {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd") // formato de la fecha en la cadena de texto
        val fecha = formatoFecha.parse(fecha) // convertir la cadena de texto en un objeto Date
        val calendar = getInstance() // obtener una instancia de la clase Calendar
        calendar.time = fecha // establecer la fecha en el objeto Calendar
        val diaDelMes = calendar.get(Calendar.DAY_OF_MONTH) // obtener el día del mes como un número entero
        return diaDelMes
    }

    fun fechacompleta(fecha: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE dd 'de' MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(fecha)
        return outputFormat.format(date)
    }

    fun convertoday( ):String{
        val myformat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myformat, Locale.UK)
        return  (sdf.format(Date()))
    }

    /*fun abrirdocumento(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(File("/storage/emulated/0/Documents/Imprimibles/Reporte_Asistencia.pdf")), "application/pdf")
        startActivity(intent)
    }*/
    fun abrirdocumento(){
        val file = File("/storage/emulated/0/Documents/Imprimibles/Reporte_Asistencia.pdf")
        val uri = FileProvider.getUriForFile(this, "com.example.control_escolar.fileprovider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }
    fun deleteAttendance(){
              AsistenciaBD.borrarAsistencia(fecha)
              CargarAlumnos()
              Toast.makeText(this, AsistenciaBD.error, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                //si se elimina la suspencion o justificacion actualizamos el status del dia seleccionado
                updateNormalStatus()

            }
        }
    }

}