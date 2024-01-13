package com.example.control_escolar

import android.view.View
import android.widget.ImageView

class ItemMateria {

    var nombre:String? = null
    var detalles:String? = null
    var opciones: ImageView? =  null
    var color = 0
    var id:String? = null



    constructor(view: View, name: String?, detalles: String?, id:String?, color:Int,  opciones:ImageView?) {
        this.nombre = name
        this.detalles = detalles
        this.id = id
        this.color = color
        if (opciones != null) {
            this.opciones = opciones
        }else this.opciones  = view.findViewById(R.id.image_OpcionesMateria)



    }
}