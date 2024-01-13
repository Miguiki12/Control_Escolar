package com.example.control_escolar

import BDLayer.ASSESS
import BDLayer.TareasBD
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_act_details_califications.*

class act_details_califications : AppCompatActivity() {
    val listDetails = ArrayList<DataDetailsCalification>()
    var listacalificar = ArrayList<DatosCalificar>()
    var calificationspecial = ArrayList<DataDetailsCalificationSpecial>()
    var califications : MutableList<MutableMap<String, Any>> = mutableListOf()
    lateinit var adapterspecial:adapter_Califications_Specials
    var folio = ""
    var c_matter = ""
    var begin = ""
    var end = ""
    var name = ""
    var c_calification = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_details_califications)
        this.supportActionBar?.hide()

        var  bundle = intent.extras
        c_matter =  bundle?.getString("c_matter").toString()
        begin =  bundle?.getString("begin").toString()
        end =  bundle?.getString("end").toString()
        folio =  bundle?.getString("folio").toString()
        name = bundle?.getString("name").toString()
        c_calification = bundle?.getString("c_calification").toString()
        //Toast.makeText(this, c_calification, Toast.LENGTH_SHORT).show()

        try {
            barra_details_califications.text = "Calificación detallada para \n $name"
            listDetails.clear()
            loadDetailsCalificationsSpecial(folio.toInt(),c_matter.toInt())
            loadDetailsCalifications(folio.toInt(),c_matter.toInt())
            list_details_califications1.adapter = adapter_Details_Calification(this,listDetails)
        }catch(Ex:Exception){Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()}
    }

    fun loadDetailsCalifications(folio:Int, c_materia:Int){
        val tareasBD = TareasBD(this)
        val evaluar = ASSESS(this)
        //llenamos las calificaciones que se dejaron en el parcial
        val details = tareasBD.obtenerTodasActividadesDetalles(c_materia, begin, end)
        //llenamos las calificaciones entregadas por el alumno
        califications.clear()
        califications = evaluar.getCalificationByStudent(folio,begin, end,c_materia)
        listDetails.clear()
        if (details.moveToFirst()){
            for (i in 0 until details.count){
                listDetails.add(DataDetailsCalification(
                    details.getString(0),
                    details.getString(3),
                    details.getString(6),
                    details.getString(4),
                    details.getString(8),
                    details.getInt(7),
                    details.getInt(1),
                    0,
                    details.getInt(2),
                    evaluar.findValue(califications,"c_matter", details.getString(8))))
                details.moveToNext()
            }
        }
        details.close()
        tareasBD.close()
        evaluar.close()
    }

    fun loadDetailsCalificationsSpecial(folio: Int, c_materia: Int){
        calificationspecial.clear()
        val evaluar = ASSESS(this)
        val califications = evaluar.getCalificationSpecialByStudent(folio,begin,end,c_materia)
        if (califications.moveToFirst()){
            do {
                calificationspecial.add(
                    DataDetailsCalificationSpecial(
                        califications.getString(0),califications.getInt(1),califications.getInt(2),califications.getString(3),califications.getString(4),
                        califications.getInt(5),califications.getString(6),califications.getInt(7), califications.getInt(8),califications.getInt(9)))
            }while (califications.moveToNext())
            califications.close()
            evaluar.close()
        }
        adapterspecial = adapter_Califications_Specials(c_calification.toInt(), begin, end,c_materia, calificationspecial)
        horizontal_recycler_calification_special1.adapter = adapterspecial
        horizontal_recycler_calification_special1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun setClick(){
        adapterspecial.setOnClickListener(object : adapter_Califications_Specials.OnClickListener {
            override fun onItemClick(position: Int) {
                //adapterspecial.c_actividad = c_calification.toInt()
            }
        })
    }
}