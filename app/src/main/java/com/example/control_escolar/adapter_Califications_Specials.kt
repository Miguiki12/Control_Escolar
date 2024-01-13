package com.example.control_escolar

import BDLayer.ASSESS
import BDLayer.TareasBD
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView


class adapter_Califications_Specials(var c_actividad:Int, val begin:String,val end:String, val c_materia: Int, private var list_califications:ArrayList<DataDetailsCalificationSpecial>):RecyclerView.Adapter<adapter_Califications_Specials.ViewHolder>(){
    var posicion = 0
    inner class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val Date: TextView = itemView.findViewById(R.id.txt_date_detail_activity_special)
        val Name_matter: TextView = itemView.findViewById(R.id.txt_matter_detail_activity_special)
        val Name_Activity: TextView = itemView.findViewById(R.id.txt_name_detail_activity_special)
        val Calification: TextView = itemView.findViewById(R.id.txt_calification_detail_activity_special)
        val Status_actividad: TextView = itemView.findViewById(R.id.txt_status_activity_special)
        val Options: ImageView = itemView.findViewById(R.id.image_option_detail_activity_especial)

        init {
            val btn: ImageView = itemView.findViewById<ImageView>(R.id.image_option_detail_activity_especial)
            itemView.setOnClickListener{
                this
                posicion = adapterPosition
                listener?.onItemClick(adapterPosition)
            }
            btn.setOnClickListener{
                posicion = adapterPosition
                popupMenus(it)
            }

        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v =  LayoutInflater.from(viewGroup.context).inflate(R.layout.list_activity_special, viewGroup,false)
        //v.txt_status_activity_special.setTextColor((Color.RED))
        return  ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.Date.text = list_califications[position].fecha
        viewHolder.Name_Activity.text = list_califications[position].nombre
        viewHolder.Name_matter.text = list_califications[position].nombre_materia
        viewHolder.Calification.text = "${list_califications[position].calificacion.toString()} puntos de ${list_califications[position].porciento.toString()} %"
        viewHolder.Name_matter.setTextColor(list_califications[position].color)

        if (list_califications[position].terminada == 1) {
            viewHolder.Status_actividad.setTextColor(Color.RED)
            viewHolder.Status_actividad.text = "Ya Aplicada"
            viewHolder.Options.isVisible = false
        }
    }

    public  fun clearAll(){
        list_califications.clear()
    }

    override fun getItemCount(): Int {
        return  list_califications.count()
    }
    private fun popupMenus(v: View) {
        val popupMenus = PopupMenu(v.context,v)
        popupMenus.inflate(R.menu.menu_calification_special)
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.repleaceCalification->repleaceCalification(v)
                R.id.plasCalification-> sumCalification(v)
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

    //inteface del click
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
    private var listener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    private fun repleaceCalification(view: View){
        try {
            val asses = ASSESS(view.context)
            val especiales = TareasBD(view.context)
            val calificacion = list_califications[posicion].calificacion.toFloat()
            val porciento = list_califications[posicion].porciento.toFloat()
            val folio = list_califications[posicion].folio
            val c_actividades = list_califications[posicion].c_actividades
            val total = (porciento * (calificacion / 100))
            //update calification of student
            asses.updateEspecialCalification(total.toInt(), c_actividad)
            //update status finish  of table Actividad Especial
            especiales.finishCalificationEspecial(c_actividades.toString(), folio.toString(),"1")
            loadDetailsCalificationsSpecial(folio,c_materia,view.context)
            Toast.makeText(view.context, "Se realizaron los cambios en la calificación de forma exitosa ", Toast.LENGTH_SHORT).show()
            asses.close()
            especiales.close()
        }catch (Ex:Exception){Toast.makeText(view.context, Ex.message.toString(), Toast.LENGTH_SHORT).show()}
    }


    private fun sumCalification(view: View){
        try {
            val asses = ASSESS(view.context)
            val especiales = TareasBD(view.context)
            val calification = list_califications[posicion].calificacion.toFloat()
            val porciento = list_califications[posicion].porciento.toFloat()
            val folio = list_califications[posicion].folio
            val c_actividades = list_califications[posicion].c_actividades
            val oldcalification = asses.getCalificactionByC_activity(c_actividad)
            val total = oldcalification + (porciento * (calification / 100))
            asses.updateEspecialCalification(total.toInt(), c_actividad)
            especiales.finishCalificationEspecial(c_actividades.toString(), folio.toString(),"1")
            loadDetailsCalificationsSpecial(folio,c_materia,view.context)
            Toast.makeText(view.context, "Se realizaron los cambios en la calificación de forma exitosa ", Toast.LENGTH_SHORT).show()
            asses.close()
            especiales.close()
        }catch (Ex:Exception){Toast.makeText(view.context, Ex.message.toString(), Toast.LENGTH_SHORT).show()}
    }


    fun loadDetailsCalificationsSpecial(folio: Int, c_materia: Int, context:Context){
        val evaluar = ASSESS(context)
        clearAll()
        val califications = evaluar.getCalificationSpecialByStudent(folio,begin,end,c_materia)
        if (califications.moveToFirst()){
            do {
                list_califications.add(
                    DataDetailsCalificationSpecial(
                        califications.getString(0),califications.getInt(1),califications.getInt(2),califications.getString(3),califications.getString(4),
                        califications.getInt(5),califications.getString(6),califications.getInt(7), califications.getInt(8),califications.getInt(9)))
            }while (califications.moveToNext())
            califications.close()
            evaluar.close()
            notifyDataSetChanged()
        }


    }
}
