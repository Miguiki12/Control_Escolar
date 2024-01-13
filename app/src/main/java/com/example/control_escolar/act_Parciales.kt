package com.example.control_escolar

import BDLayer.*
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_act_activdad.*
import kotlinx.android.synthetic.main.activity_act_calificar.*
import kotlinx.android.synthetic.main.activity_act_materias.*
import kotlinx.android.synthetic.main.activity_act_parciales.*
import kotlinx.android.synthetic.main.lista_actividades.*
import kotlinx.android.synthetic.main.lista_actividades.view.*
import kotlinx.android.synthetic.main.listaactividad.view.*
import kotlinx.android.synthetic.main.view_buscar_por_materia.view.*
import kotlinx.android.synthetic.main.view_delete_partials.view.*
import kotlinx.android.synthetic.main.view_partial.*
import kotlinx.android.synthetic.main.view_print_partial.view.*
import kotlinx.android.synthetic.main.viewdatosactividad.view.*

class act_Parciales : AppCompatActivity(){
    lateinit var partialdb: PartialDB
    lateinit var TareasBD: TareasBD
    lateinit var evaluar: ASSESS
    lateinit var horizontalRecyclerView: RecyclerView
    lateinit var adapter:adapter_parciales
    lateinit var text_details_parcial: TextView
    var positioncalification = 0
    var positionpartial = 0
    val listDetails = ArrayList<DataDetailsCalification>()
    val partial = mutableListOf<Int>()
    val c_partial = mutableListOf<Int>()
    val begin = mutableListOf<String>()
    val end = mutableListOf<String>()
    val namematter = mutableListOf<String>()
    val colormatter = mutableListOf<Int>()
    val c_matter = mutableListOf<Int>()
    var listacalificar = ArrayList<DatosCalificar>()
    //var calificationspecial = ArrayList<DataDetailsCalificationSpecial>()
    var califications : MutableList<MutableMap<String, Any>> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_parciales)
        this.supportActionBar?.title = "Parciales"
        this.supportActionBar?.subtitle = Nombre_Escuela.getAlias()

        partialdb = PartialDB(this)
        TareasBD = TareasBD(this)
        partialdb.onCreateTableParciales()
        evaluar = ASSESS(this)
        //evaluar.deleteCalificaciones()
        //evaluar.onCreatetableCalificaciones()

        text_details_parcial = findViewById<TextView>(R.id.txt_details_Partial)
        horizontalRecyclerView = findViewById(R.id.horizontal_recycler_view)

        updatelistPartials()
        loadPartials()
        CargarAlumnos(1, 1)
        lista_Calificacion_evaluada.setOnItemClickListener {adapterView, view, i, l ->
            positioncalification = i
            try {showDetailsCalifications()}
            catch (Ex:Exception){Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_partial, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_create_partial -> createPartial()
            R.id.menu_delete_all_matter-> selectPartials()
            R.id.menu_printPdf_partial-> selectPartialsPrint(1)
            R.id.menu_printExcel_partial-> selectPartialsPrint(2)
            R.id.menu_terminate_partial -> calificatePartials()
        }
        return  super.onOptionsItemSelected(item)
    }

    private fun createPartial() {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.view_partial, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.show()
        /**set view*/

            val begin = view.findViewById<DatePicker>(R.id.dpicker_Begin_Partial)
            val end = view.findViewById<DatePicker>(R.id.dPicker_End_Partial)
            val btnFinish = view.findViewById<Button>(R.id.btn_Create_Partial)
            val period = view.findViewById<Spinner>(R.id.spinner_Partial)

            //cargamos los parciales registrados
            val parciales = partialdb.getDistinctPartials()
            //creamos 6 parciales en una lista
            val partials = ArrayList<Int>()
            for(i in 1 .. 6) {
                partials.add(i)
            }
            //eliminamos los parciales que ya estan creados
            partials.removeAll(parciales.toSet())
            //mostramos los parciales que no estan repetidos
            period.adapter =  ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,partials)

            //cargamos todas las materias
            val allmatters = loadMatters()
            val adapterMatter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,allmatters)
            //matter.adapter =  adapterMatter
            btnFinish.setOnClickListener {
                val date_begin = "${begin.year}-${begin.month + 1}-${begin.dayOfMonth}"
                val date_end = "${end.year}-${end.month + 1}-${end.dayOfMonth}"
                val periodo = period.selectedItemPosition
                var cont = 1
                while(cont < allmatters.size){
                    partialdb.sF_inicio = date_begin
                    partialdb.sF_fin = date_end
                    partialdb.iC_materia = allmatters[cont].toInt()
                    partialdb.iPeriodo = period.selectedItem.toString().toInt()
                    partialdb.insertPartial()
                    cont ++
                }
                loadPartials()
                dialog.hide()
            }
        }

    fun CargarAlumnos(partial:Int, c_matter:Int){
        try {
            evaluar.getCalifications(partial, c_matter, listacalificar)
            updatelist()
        }catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    fun loadPartials(){
        try {
            val partials = partialdb.getAllPartials()
            clearList()
            if (partials.moveToFirst()){
                do{
                    //cargamos los datos para mostrar en el reciclerView
                    partial.add(partials.getString(4).toInt())
                    begin.add(partials.getString(2))
                    end.add(partials.getString(3))
                    namematter.add(partials.getString(5))
                    colormatter.add(partials.getString(6).toInt())
                    c_matter.add(partials.getString(1).toInt())
                    c_partial.add(partials.getString(0).toInt())
                }while (partials.moveToNext())
                partials.close()
        }
            //Toast.makeText(this, "recargando", Toast.LENGTH_SHORT).show()
            updatelistPartials()
            setClick()
        }catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    fun loadMatters(): java.util.ArrayList<String> {
        var  arrayMatters = java.util.ArrayList<String>()
        try {
            var count = 0
            val matters = TareasBD.obtenerMAterias()
            if(matters.moveToFirst()){
                arrayMatters.add("Todas")
                while(count < matters.count){
                    arrayMatters.add(matters.getString(0))
                    namematter.add(matters.getString(1))
                    matters.moveToNext()
                    count ++
                }
            }
            matters.close()
        }catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
        //asignamos la lista obtenida a una global para poder obtener valores posteriormente

        return arrayMatters
    }


    fun deletePartial(){
        partialdb.deletePartial(2)
        Toast.makeText(this, partialdb.error, Toast.LENGTH_LONG).show()
        loadPartials()
    }

    @SuppressLint("MissingInflatedId")
    fun selectPartials(){
        val v = LayoutInflater.from(this).inflate(R.layout.view_delete_partials,null)
        val partials = partialdb.getDistinctPartials()

        var cont = 0
         while (cont < partials.count()){
             if (partials[cont] == 1) v.cbx_partial1.isEnabled = true
             if (partials[cont] == 2) v.cbx_partial2.isEnabled = true
             if (partials[cont] == 3) v.cbx_partial3.isEnabled = true
             if (partials[cont] == 4) v.cbx_partial4.isEnabled = true
             if (partials[cont] == 5) v.cbx_partial5.isEnabled = true
             if (partials[cont] == 6) v.cbx_partial6.isEnabled = true
             cont++
         }

        android.app.AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Ok"){
                    dialog,_->
                if (v.cbx_partial1.isChecked) partialdb.deletePartial(1)
                if (v.cbx_partial2.isChecked) partialdb.deletePartial(2)
                if (v.cbx_partial3.isChecked) partialdb.deletePartial(3)
                if (v.cbx_partial4.isChecked) partialdb.deletePartial(4)
                if (v.cbx_partial5.isChecked) partialdb.deletePartial(5)
                if (v.cbx_partial6.isChecked) partialdb.deletePartial(6)
                loadPartials()
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


    fun selectPartialsPrint(tipe:Int){
        val v = LayoutInflater.from(this).inflate(R.layout.view_print_partial, null)
        val partials = partialdb.getDistinctPartials()
        val group = v.rdg_print_partial
        var cont = 0
        var selection = 0
        //cargamos los periodos ya creados
        while (cont < partials.count()) {
            val radioButton = RadioButton(this)
            radioButton.text = "Parcial " + partials[cont].toString()
            radioButton.id = View.generateViewId()
            group.addView(radioButton)
            cont++
        }

        group.setOnCheckedChangeListener { radioGroup, checkedId ->
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton.text.toString()
            val option = selectedText.split(' ')
            selection = option[1].toInt()
        }
        android.app.AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Ok"){
                    dialog,_->
                if (tipe == 1 ) printPdfPartial(selection)
                if (tipe == 2) printExcelPartial(selection)
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

    fun calificatePartials() {
        val v = LayoutInflater.from(this).inflate(R.layout.view_print_partial, null)
        val partials = partialdb.getDistinctPartials()
        val group = v.rdg_print_partial
        var cont = 0
        var selection = 0
        //cargamos los periodos ya creados
        while (cont < partials.count()) {
            val radioButton = RadioButton(this)
            radioButton.text = "Parcial " + partials[cont].toString()
            radioButton.id = View.generateViewId()
            group.addView(radioButton)
            cont++
        }

        group.setOnCheckedChangeListener { radioGroup, checkedId ->
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton.text.toString()
            val option = selectedText.split(' ')
            selection = option[1].toInt()

        }

        val dialog = AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Ok") { dialog, _ ->
                evaluar.deleteCalification(selection)
                val suma = evaluar.getSumCalifications(selection)
                if (suma.moveToFirst()) {
                    do {
                        evaluar.insertCalificaciones(suma.getInt(1), suma.getInt(0), suma.getInt(2))
                    } while (suma.moveToNext())

                }
                suma.close()

                evaluar.deleteCalificaciones_finales()
                val totales = evaluar.getSumParcials()
                if (totales.moveToFirst()){
                   do {
                         evaluar.insertCalifications_Finals(totales.getInt(1),totales.getInt(0))
                   }while (totales.moveToNext())
                   totales.close()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun printPdfPartial(selection:Int){
        val intent = Intent(this, act_Actividad_Especial::class.java)
        intent.putExtra("c_actividad", "0")//enviamos 0 porque no nececitamos clave de mataria
        intent.putExtra("numpartial", selection.toString())
        startActivity(intent)
        /*val pdf = crearPDF(this)
        pdf.Parcial(selection)
        pdf.abrirdocumento("Parcial",this)*/
    }

    private fun printExcelPartial(selection: Int){
        val createexcel = createExcel(this)
        createexcel.printAllPartial(selection)
    }


    fun setClick(){
        adapter.setOnClickListener(object : adapter_parciales.OnClickListener {
            override fun onItemClick(position: Int) {
                positionpartial = position
                text_details_parcial.text = adapter.selectmatter
                text_details_parcial.setTextColor(adapter.backgroundcolormatter)
                listacalificar.clear()
                CargarAlumnos(partial[position], c_matter[position])
            }
        })
    }


    fun clearList(){
        partial.clear()
        begin.clear()
        end.clear()
        namematter.clear()
        colormatter.clear()
        c_matter.clear()
        c_partial.clear()
    }

    fun updatelistPartials(){
        adapter = adapter_parciales(c_partial, partial,begin, end, namematter, colormatter, c_matter)
        horizontalRecyclerView.adapter = adapter
        horizontalRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun updatelist(){
        lista_Calificacion_evaluada.adapter =  adapterCalificar(this, listacalificar)
    }

    fun loadDetailsCalificationsSpecial(){

    }

    fun loadDetailsCalifications(folio:Int, c_materia:Int){
        //llenamos las calificaciones que se dejaron en el parcial
        val details = TareasBD.obtenerTodasActividadesDetalles(c_materia, begin[positionpartial], end[positionpartial])
        //llenamos las calificaciones entregadas por el alumno
        califications.clear()
        califications = evaluar.getCalificationByStudent(folio,begin[positionpartial], end[positionpartial],c_materia)
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
    }

    /*fun loadDetailsCalificationsSpecial(v:View, folio: Int, c_materia: Int){
        calificationspecial.clear()
        val califications = evaluar.getCalificationSpecialByStudent(folio,begin[positionpartial],end[positionpartial],c_materia)
        if (califications.moveToFirst()){
            do {
                calificationspecial.add(
                    DataDetailsCalificationSpecial(
                    califications.getString(0),califications.getInt(1),califications.getInt(2),califications.getString(3)
                    ,califications.getString(4),califications.getInt(5),califications.getString(6),califications.getInt(7)))
            }while (califications.moveToNext())
            califications.close()
        }
        val adapterspecial = adapter_Califications_Specials(calificationspecial)
        v.horizontal_recycler_calification_special.adapter = adapterspecial
        v.horizontal_recycler_calification_special.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }*/

    fun showDetailsCalifications(){
        /*val v = LayoutInflater.from(this).inflate(R.layout.lista_actividades,null)
        v.barra_details_califications.text = "Calificación detallada para \n ${listacalificar[positioncalification].nombre}"
        listDetails.clear()
        loadDetailsCalificationsSpecial(v,listacalificar[positioncalification].folio.toInt(),c_matter[positionpartial])
        loadDetailsCalifications(listacalificar[positioncalification].folio.toInt(),c_matter[positionpartial])
        v.list_details_califications.adapter = adapter_Details_Calification(this,listDetails)*/
        var inten = Intent(this, act_details_califications::class.java)
        inten.putExtra("name", listacalificar[positioncalification].nombre)
        inten.putExtra("folio", listacalificar[positioncalification].folio)
        inten.putExtra("c_calification",listacalificar[positioncalification].c_calificacion.toString())
        inten.putExtra("c_matter",c_matter[positionpartial].toString())
        inten.putExtra("begin",begin[positionpartial])
        inten.putExtra("end",end[positionpartial])

        startActivity((inten))


        /*android.app.AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Ok"){
                    dialog,_->

                dialog.dismiss()
            }
            .create()
            .show()
        true*/
    }

    /*fun insertarchecboxIA(){
       val v = LayoutInflater.from(this).inflate(R.layout.view_delete_partials,null)
       for (i in 0 until partials.count()) {
           val checkBox = CheckBox(this)
           checkBox.textSize = 19f

           val params = LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.WRAP_CONTENT,
               LinearLayout.LayoutParams.WRAP_CONTENT
           )
           params.setMargins(0, 10.dpToPx(), 0, 0)
           checkBox.layoutParams = params


           checkBox.text = "Parcial "+partials[i]
           v.relative_delete_partials.addView(checkBox)
   }*/
    //darle margen a los textview
    /*fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }*/



}