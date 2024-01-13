package com.example.control_escolar

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class AdapterAspectos( var context: Context, var arrayList: ArrayList<ItemAspectos>): BaseAdapter()  {
    var Total = 0
    public var error = ""


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
        var view:View = View.inflate(context, R.layout.cardaspectos, null)

        var name : TextView = view.findViewById(R.id.lbl_NombreAspecto)
        var porciento : TextView = view.findViewById(R.id.lbl_PorcientoAspecto)
        var idaspectos: TextView = view.findViewById(R.id.lbl_idAspecto)
        var imagen: ImageView = view.findViewById(R.id.imagen_Aspectos)
        var ItemAspectos: ItemAspectos = arrayList.get(p0)

              name.text = ItemAspectos.nombre
              porciento.text = ItemAspectos.porciento
              idaspectos.text = ItemAspectos.id

              if (name.text.substring(0, 3).uppercase() == "EXA") imagen.setImageResource(R.drawable.exa)
              if (name.text.substring(0, 3).uppercase() == "PRO") imagen.setImageResource(R.drawable.pro)
              if (name.text.substring(0, 3).uppercase() == "TRA") imagen.setImageResource(R.drawable.tra)
              if (name.text.substring(0, 3).uppercase() == "TAR") imagen.setImageResource(R.drawable.tar)


        return view

    }
}