package com.example.control_escolar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class AdapterMaterias(var context: Context, var arrayList: ArrayList<ItemMateria>): BaseAdapter() {

    lateinit var Indice: BD_Indice
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(p0: Int): Any {
        return arrayList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view:View = View.inflate(context, R.layout.cardmaterias, null)
        var name :TextView = view.findViewById(R.id.lbl_Materia)
        var detalles :TextView = view.findViewById(R.id.lbl_DetallesMateria)
        var opciones: ImageView = view.findViewById(R.id.image_OpcionesMateria)
        var idmateria: TextView = view.findViewById(R.id.lbl_claveMateria)
        var ItemMateria: ItemMateria = arrayList.get(p0)

        name.text = ItemMateria.nombre
        detalles.text = ItemMateria.detalles
        idmateria.text = ItemMateria.id
        idmateria.setBackgroundColor(ItemMateria.color!!)
        opciones.setImageResource(R.drawable.more)
        return view
    }
}