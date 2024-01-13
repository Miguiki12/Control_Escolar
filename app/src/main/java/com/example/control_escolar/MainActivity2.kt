package com.example.control_escolar

import BDLayer.ASSESS
import BDLayer.AlumnosBD
import BDLayer.SettingsBD
import BDLayer.crearPDF
import LogicLayer.ManagerImage
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.control_escolar.AlarmNotification.Companion.NOTIFICATION_ID
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_act_escuela.*
import kotlinx.android.synthetic.main.fragment_asistencia.*
import kotlinx.android.synthetic.main.view_printables.view.*
import java.util.*

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

     private lateinit var drawer:DrawerLayout
     private lateinit var toggle: ActionBarDrawerToggle
     //lateinit var Indice: BD_Indice
     lateinit var alumnos: AlumnosBD
     lateinit var pdf: crearPDF
     lateinit var evaluar:ASSESS
     var email = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        pdf = crearPDF(this)
        evaluar = ASSESS(this)
        try {
            setMainmenu()
            setTitles()
        }catch (Ex:Exception){Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()}

        alumnos = AlumnosBD(this)

        grid_horario.setOnClickListener{ Horario() }
        grid_alumnos.setOnClickListener{Alumnos() }
        grid_asistencia.setOnClickListener{Asistencia()}
        grid_actividades.setOnClickListener{Actividad()}
        grid_materias.setOnClickListener{Materias()}
        grid_settings.setOnClickListener{Settings()}
        //cargamos los alumnos para utilizarlos en cualquier parte del programa
        try {
            Nombre_Escuela.getAlumnos(alumnos.obtenerAll())
        }catch (Ex:Exception){
            //Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        toggle.syncState()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //@RequiresApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_materias -> Materias()
            R.id.nav_item_asistencia -> Asistencia()
            R.id.nav_item_alumnos -> Alumnos()
            R.id.nav_item_actividades -> Actividad()
            //R.id.nav_item_cerrar_sesion ->cerrarSesion()
            R.id.nav_item_avisos-> Avisos()
            R.id.nav_item_calendario -> Horario()
            R.id.nav_item_configuracion-> Settings()
            R.id.nav_item_encuadre->Encuadre()
            //R.id.nav_item_teacher->Teacher()
            R.id.nav_item_imprimibles-> Imprimibles()
            R.id.nav_item_escuela -> Escuela()
            R.id.nav_item_parciales-> Parciales()
            R.id.nav_item_insidencias-> { //calificar()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + 15000, pendingIntent)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "MySuperChannel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "SUSCRIBETE"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    fun setMainmenu(){
        var toolbar: androidx.appcompat.widget.Toolbar =  findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        var ab = supportActionBar
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_main)
            ab.setDisplayHomeAsUpEnabled(true)
        }
        drawer =  findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)//aqui esta el problema
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navigationView:NavigationView = findViewById(R.id.nav_view)
        if (Nombre_Escuela.get_tipo() > 1){//si no es primaria se oculta el encuadre y se especifica como calificar en las materias de forma individual
            val menu: Menu = navigationView.menu
            val menuItem: MenuItem = menu.findItem(R.id.nav_item_encuadre)
            menuItem.isVisible = false // Ocultar el elemento de menú
        }
        navigationView.setNavigationItemSelectedListener(this)
        //cargamos la imagen de la escuela y el nombre de la misma
        val headerView: View = navigationView.getHeaderView(0)
        val imageView: ImageView = headerView.findViewById(R.id.nav_header_imageView)
        val nameSchool: TextView = headerView.findViewById(R.id.nav_header_textView)
        nameSchool.text = Nombre_Escuela.getAlias()
        ManagerImage(this).loadImageSchool(imageView, Nombre_Escuela.getName())

    }

    fun setTitles(){
        var  bundle = intent.extras
        val Nombre = bundle?.getString("Escuela")
        val Nivel = bundle?.getString("Nivel")
        email = bundle?.getString("email").toString()
        this.supportActionBar?.title = Nombre
        this.supportActionBar?.subtitle = Nivel
    }

    fun cerrarSesion(){
        //Indice.updateSesionMaestro(email)
        Nombre_Escuela.setName("")
        var intent = Intent(this, MainActivity::class.java)
        startActivity((intent))
    }
    private fun Alumnos() {
        var intent = Intent(this, act_Alumnos::class.java)
        startActivity((intent))
    }
    private fun Actividad() {
        var intent = Intent(this, act_Activdad::class.java)
        startActivity((intent))
    }
    private fun Materias(){
        var inten = Intent(this, act_Materias::class.java)
        startActivity((inten))
    }
    private fun Asistencia(){
        var inten = Intent(this, act_Asistencia::class.java)
        startActivity((inten))
    }
    private fun Avisos() {
        var intent = Intent(this, act_Avisos::class.java)
        startActivity((intent))
    }
    private fun Horario(){
        var intent = Intent(this, act_Horario::class.java)
        startActivity((intent))
    }
    private fun Settings(){
        var intent = Intent(this, act_Configuracion::class.java)
        startActivity((intent))
    }
    private fun Teacher(){
        var intent = Intent(this, act_RegistroMaestro::class.java)
        startActivity((intent))
    }
    private fun Escuela() {
        var intent = Intent(this, act_Escuela::class.java)
        startActivity((intent))
    }
    private fun Parciales() {
        var intent = Intent(this, act_Parciales::class.java)
        startActivity((intent))
    }
    private fun Encuadre(){
        var inten = Intent(this, act_Aspectos::class.java)
        inten.putExtra("nombremateria","En General")
        inten.putExtra("c_materia",0)
        startActivity((inten))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun Imprimibles() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.view_printables, null)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            if (v.rbt_Gafetes.isChecked) {
                try {
                    pdf.Credential()
                    pdf.abrirdocumento("Credenciales", this)
                }catch (Ex:Exception){Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()}

            }
            if (v.rbt_Matricula.isChecked) {
                pdf.Matricula()
                pdf.abrirdocumento("Matricula", this)
            }
            if (v.rbt_Parcial.isChecked) {
                pdf.getAllParcial(1)
                pdf.abrirdocumento("Parcial", this)
            }
            if (v.rbt_Estadistica.isChecked) {
                Estadistica()
            }
            if (v.rbt_Boletas.isChecked) {
                // Acciones para el RadioButton 1
                pdf.Boleta()
                pdf.abrirdocumento("Boletas", this)
            }
            /*if (v.rbt_Traslado.isChecked) {
                try {
                    // Obtén el contexto
                    val context = this // o requireContext() en un fragmento

// Ruta de la base de datos
                    val databaseName = Nombre_Escuela.getName()
                    val databasePath = File("/data/user/0/com.example.control_escolar/databases/", databaseName).path
                    Toast.makeText(this, this.getDatabasePath(Nombre_Escuela.getName()).toString(), Toast.LENGTH_SHORT).show()

// Inicializa el GoogleDriveManager y realiza la copia de seguridad y subida
                    val googleDriveManager = GoogleDriveManager(context)
                    googleDriveManager.backupAndUploadDatabaseToDrive(databasePath)
                }catch (Ex:Exception){Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()}
        }*/
        }

        addDialog.create()
        addDialog.show()
    }

    fun Estadistica(){
        try {
             val estadisticDates = SettingsBD(this).getEstadisticsDates()
             val normal1 = alumnos.getEstadisticBySexo1(estadisticDates?.first, estadisticDates?.second)
             val repetidores = alumnos.getEstadisticRepit(estadisticDates?.first, estadisticDates?.second)
             val normalizer2 = alumnos.getEstadisticBySexo2(estadisticDates?.first, estadisticDates?.second)
             //val altasbajas = alumnos.getEstadisticRegisterUnsuscribe(estadisticDates?.first, estadisticDates?.second)
             val altas = alumnos.getEstadisticRegister(estadisticDates?.first, estadisticDates?.second)
             val bajas = alumnos.getEstadisticUnsuscribe(estadisticDates?.first, estadisticDates?.second)
             val finales = alumnos.getEstadisticFinaly(estadisticDates?.second, estadisticDates?.third)
             val existencia = alumnos.getEstadisticExistence(estadisticDates?.second, estadisticDates?.third)
             val aprovados = alumnos.getEstadisticApproved(60)
             val reprovados = alumnos.getEstadisticFailed(60)
             //pdf.Estadistica()
             pdf.Estadistica(normal1,repetidores, normalizer2, altas, bajas, finales,existencia, aprovados, reprovados)
             pdf.abrirdocumento("Estadistica",this)
        }catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }




}