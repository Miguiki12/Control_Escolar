package com.example.control_escolar

import BDLayer.JustificarBD
import BDLayer.ParticipacionBD
import BDLayer.ReporteBD
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_act_historial.*
import kotlinx.android.synthetic.main.lsita_historial.view.*

class act_Historial : AppCompatActivity() {
    var list_record = ArrayList<datos_historial>()
    lateinit var stake: ParticipacionBD
    lateinit var  reportes : ReporteBD
    lateinit var justificar: JustificarBD
    var Folio = ""
    var C_materia = ""
    var Nombre = ""
    var posicion = 0
    var Tipo = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_historial)

        stake = ParticipacionBD(this)
        reportes = ReporteBD(this)
        list_record = ArrayList()
        justificar = JustificarBD(this)
        settitles()





        list_Record.setOnItemClickListener {adapterView, view, i, l ->
            posicion = i
            popupMenus(view.image_optiom_record)
        }

    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_historial, menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       // when(item.itemId){
            //R.id.nav_delete_record->

            //R.id.nav_search_record->

        return  super.onOptionsItemSelected(item)

    }
    private fun popupMenus(v: View) {
            val popupMenus = PopupMenu(this,v)
            popupMenus.inflate(R.menu.menu_histoiral_2)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                   R.id.delete_record->Delete_list()

                }
                true
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
    }


    fun settitles(){
        var  alumnoreportado = intent.extras
        Folio = alumnoreportado?.getString("folio").toString()
        C_materia = alumnoreportado?.getString("c_materia").toString()
        Nombre = alumnoreportado?.getString("nombre").toString()
        Tipo = alumnoreportado?.getString("tipo").toString()
        if (Tipo == "1"){
            this.supportActionBar?.title = "Participaciones de "
            load_stake(Folio)
        }
        if (Tipo == "2"){
            this.supportActionBar?.title = "Incidencias de "
            load_report(Folio)
        }
        if (Tipo == "3"){
            this.supportActionBar?.title = "Suspenciones de "
            load_justificar(Folio.toInt(),3)
        }
        this.supportActionBar?.subtitle = Nombre
    }

    fun load_stake(folio:String):Int{
        var count = 0
        val participacion = stake.get_stake(folio)
        list_record.clear()
        try {
            if(participacion.moveToFirst()){
                while(count < participacion.count){
                    list_record.add(datos_historial(participacion.getString(1),"Puntaje = " + participacion.getString(2),participacion.getString(0),participacion.getString(4),participacion.getString(6),participacion.getString(5).toInt(),0))
                    participacion.moveToNext()
                    count ++
                }
                list_Record.adapter =  adapter_record(this, list_record)
            }
            //Toast.makeText(this, stake.error, Toast.LENGTH_SHORT).show()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
        return participacion.count
    }

    fun load_report(folio:String):Int{
        var count = 0
        val incidencias = reportes.get_reporte(folio)
        list_record.clear()
        if(incidencias.moveToFirst()){
           while(count < incidencias.count){
                    list_record.add(datos_historial(get_name_report(incidencias.getString(2).toInt()),"Puntaje = " + incidencias.getString(3),incidencias.getString(1),incidencias.getString(4),incidencias.getString(0),incidencias.getString(5).toInt(),0))
                    incidencias.moveToNext()
                    count ++
           }
         list_Record.adapter =  adapter_record(this, list_record)
        }

        return incidencias.count
    }

    fun load_justificar(folio:Int, tipo:Int):Int{
        var tipo = ""
        val incidencias = justificar.getHistorialByStudent(folio)
        list_record.clear()
        if(incidencias.moveToFirst()){
            do{
                if (incidencias.getInt(4) == 2) tipo = "Justificado"
                else tipo = "Suspendido"
                list_record.add(
                    datos_historial(
                        tipo,
                        "Puntaje = 0",
                        incidencias.getString(2) +" - " +incidencias.getString(3),
                        "Todas",
                        incidencias.getString(0),
                        Color.BLUE,0))
            }while (incidencias.moveToNext())
            list_Record.adapter =  adapter_record(this, list_record)
        }

        return incidencias.count
    }



    fun Delete_list(){
        if (Tipo == "1") Delete_stake()
        if (Tipo == "2") Delete_record()
        if (Tipo == "3") delete_Justific()
    }

    fun Delete_record(){
        if (reportes.DeleteReporte(list_record[posicion].c_actividad)){
            list_record.removeAt(posicion)
            list_Record.adapter = adapter_record(this, list_record)
        }
        Toast.makeText(this,reportes.error, Toast.LENGTH_SHORT).show()

    }
    fun Delete_stake(){
        if (stake.deletestake(list_record[posicion].c_actividad)){
            list_record.removeAt(posicion)
            list_Record.adapter = adapter_record(this, list_record)
        }
        //Toast.makeText(this,reportes.error, Toast.LENGTH_SHORT).show()
    }
    fun delete_Justific(){
        if (justificar.deleteJustific(list_record[posicion].c_actividad.toInt())){
            list_record.removeAt(posicion)
            list_Record.adapter = adapter_record(this, list_record)
            confirmationDelete()
        }
    }

    fun get_name_report(seleccion:Int):String{
        var type = ""
        if (seleccion == 1) type = "Llamada de atencion"
        if (seleccion == 2) type = "Reporte"
        if (seleccion == 3) type = "Citatorio"
        if (seleccion == 4) type = "Suspendido"
        return type
    }

    fun confirmationDelete() {
        val intent = Intent()
        intent.putExtra("deleted", 3)
        setResult(RESULT_OK, intent)
        finish()
    }


}