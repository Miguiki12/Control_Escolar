package com.example.control_escolar

import java.io.Serializable

class DatosActivdad(var nombre:String, var tipo:String, var fecha:String, var materia:String, var c_actividad:String, var color:Int, var terminada:Int,var porciento:Int, var especial:Int, var valor: Int,var totalAlumnos: Int): Serializable {

}

class DataDetailsCalification(var nombre:String, var tipo:String, var fecha:String, var materia:String, var c_actividad:String, var color:Int, var porciento:Int, var especial:Int, var valor: Int, var calificacion:Int): Serializable {

}

class DataDetailsCalificationSpecial(var c_actividad: String, var calificacion:Int, var porciento:Int ,var nombre:String,  var nombre_materia:String, var color:Int, var fecha:String,var folio:Int, var terminada:Int, var c_actividades:Int): Serializable {

}