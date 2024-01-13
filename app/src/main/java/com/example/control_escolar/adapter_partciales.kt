package com.example.control_escolar

import BDLayer.ASSESS
import BDLayer.TareasBD
import BDLayer.createExcel
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class adapter_parciales(private val c_partial:List<Int>, private val partial: List<Int>, private val begin:List<String>, private val end:List<String>, private val  namematter:List<String>, private val colormatter:List<Int>, private  val c_matter:List<Int>) : RecyclerView.Adapter<adapter_parciales.ViewHolder>() {

    public var selectmatter = ""
    public var backgroundcolormatter = 0
    private var posicion = 0
    lateinit var evaluar : ASSESS

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_parciales, parent, false)
        evaluar = ASSESS(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partial = partial[position]
        val begin = begin[position]
        val end = end[position]
        val name = namematter[position]
        val color = colormatter[position]
        holder.bind(partial, begin, end, name, color)
    }

    override fun getItemCount() = partial.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val partialView: TextView = itemView.findViewById(R.id.txt_number_Partial)
        private val beginView: TextView = itemView.findViewById(R.id.txt_Inicio_Parcial)
        private val endView: TextView = itemView.findViewById(R.id.txt_Fin_Parcial)
        private val matterView: TextView = itemView.findViewById(R.id.txt_matter_Partial)
        //private val txt_details_parsial: TextView = itemView.findViewById(R.id.txt_details_Partial)
        init {
            val btn: ImageView = itemView.findViewById<ImageView>(R.id.image_Options_Partial)
            itemView.setOnClickListener{
                this
                selectmatter = "   "+ namematter[position] + " parcial " + partial[position]
                backgroundcolormatter = colormatter[position]
                posicion = adapterPosition
                listener?.onItemClick(adapterPosition)
                //Toast.makeText(itemView.context, begin[posicion].toString()+" "+end[posicion], Toast.LENGTH_SHORT).show()
            }
            btn.setOnClickListener{
                posicion = adapterPosition
                popupMenus(it)
            }
        }

        fun bind(partial: Int, begin:String, end:String, name:String, color:Int) {
            partialView.text = partial.toString()
            beginView.text = begin.toString()
            endView.text = end.toString()
            matterView.text = name
            matterView.setBackgroundColor(color)
        }
    }
    private fun popupMenus(v: View) {
            val popupMenus = PopupMenu(v.context,v)
            popupMenus.inflate(R.menu.menu_popu_partial)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.finish_partial->finishPartial(v)
                    R.id.share_matter_partial->printPartialbyMatter(v.context)
                    //R.id.finish_partial->
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

    private fun finishPartial(itemView:View) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Finalizar Parcial")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setMessage("¿Finalizar el parcial ${partial[posicion]} de la materia ${namematter[posicion]}? ")
            .setPositiveButton("Si"){
                    dialog,_->
                calificar(itemView)
                listener?.onItemClick(posicion)
                notifyDataSetChanged()
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

    fun printPartialbyMatter(context: Context){
        val createexcel = createExcel(context)
        createexcel.printPartial(namematter[posicion], partial[posicion], c_matter[posicion])
        //createexcel.printGrafica()
    }


//inteface del click
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
    private var listener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    public fun calificar(itemView: View){
        evaluar.df_inicio =  begin[posicion]//"2023-05-01"
        evaluar.df_fin = end[posicion]//"2023-05-31"
        val inicializar_especiales = TareasBD(itemView.context)
        //checamos que el encuedre cierre en 100%
        if (evaluar.totalValueActivitys() >= 100){
            evaluar.getAllValues()
            evaluar.detelePartial(c_partial[posicion].toString())
            if(Nombre_Escuela.Alumnos.moveToFirst())
                do {
                    evaluar.getAssess(Nombre_Escuela.Alumnos.getString(0), c_partial[posicion], c_matter[posicion].toString(),namematter[posicion])
                }while (Nombre_Escuela.Alumnos.moveToNext())
            evaluar.cleanValues()
            inicializar_especiales.beginingCalificationEspecialByPartial(begin[posicion], end[posicion])
        }else{
            Toast.makeText(itemView.context, "El porcentaje de encuadre para evaluar es menor a 100", Toast.LENGTH_SHORT).show()
        }
        inicializar_especiales.close()
    }

    /*fun CargarAlumnos(){
        try {
            evaluar.getCalifications(listacalificar)
            listaCalificar.adapter =  adapterCalificar(this, listacalificar)
        }catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }*/



}
