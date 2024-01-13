package com.example.control_escolar

import android.database.Cursor

class Nombre_Escuela {
    // Declaración de objeto compañero
    companion object {
        var Nombre = ""
        private var Alias = ""
        lateinit var Alumnos: Cursor

        var tipo_escuela = 1
        var turno_escuela = "Matutino"
        var grado_escuela = ""
        var grupo_escuela = ""

        fun set_tipo(tipo:Int){
            this.tipo_escuela = tipo
            //Tipo 1 = Primaria
            //Tipo 2 = Secuncadria
            //Tipo 3 = Preparatoria
            //Tipo 4 = Universidad
        }

        fun get_tipo():Int{
            return this.tipo_escuela
        }

        fun setName(nombre:String){
            this.Nombre = nombre

        }
        fun getName():String{
            return this.Nombre
        }
        fun setAlias(alias:String){
            this.Alias = alias

        }
        fun getAlias():String{
            return this.Alias
        }

        fun getAlumnos(alumnos:Cursor){
            this.Alumnos = alumnos
        }

        fun getTurno():String{
            return turno_escuela
        }

        fun setTurno(turno:String){
            this.turno_escuela = turno
        }
        fun getGrado():String{
            return grado_escuela
        }

        fun setGrado(grado:String){
            this.grado_escuela = grado
        }
        fun getGrupo():String{
            return grupo_escuela
        }

        fun setGrupo(grupo:String){
            this.grupo_escuela = grupo
        }

    }
}