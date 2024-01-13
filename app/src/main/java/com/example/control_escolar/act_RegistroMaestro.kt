package com.example.control_escolar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_act_registro_maestro.*

class act_RegistroMaestro : AppCompatActivity() {

    lateinit var Indice: BD_Indice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.title = "Registrar Maestro"
        setContentView(R.layout.activity_act_registro_maestro)
        Indice = BD_Indice(this)
        loadDataTeacher()

        btnRegistrarCliente.setOnClickListener {
            Registrar()
        }
    }


    fun Registrar(){

       // if(validarcontraseña(txtRPassword1.text.toString(), txtRPassword2.text.toString())  && validardatos()){
            if (registrarbd()) {
                //Registrardatos("Correo.txt", email.text.toString())
                //Registrardatos("Contraseña.txt", password.text.toString())
                //Toast.makeText(applicationContext, "El registro se realizo exitosamente", Toast.LENGTH_LONG).show()
                var inten = Intent(this, actSeleccionarEscuela::class.java)
                inten.putExtra("c_maestro",1)
                /*inten.putExtra("antemail", email.text.toString())
                inten.putExtra("antpassword", password.text.toString())
                inten.putExtra("antnombre", nombre.text.toString())
                inten.putExtra("antapellidos", apellidos.text.toString())
                inten.putExtra("antdireccion", direccion.text.toString())
                inten.putExtra("antcolonia", colonia.text.toString())
                inten.putExtra("antcelular", celular.text.toString())*/
                startActivity((inten))
                //LimpiarTodo()
                //finish()
            }

       // }
        //else Toast.makeText(applicationContext,"Asegurece de escribir todos los campos ", Toast.LENGTH_SHORT).show()



    }

    override fun onBackPressed() {
        super.onBackPressed()
        var inten =  Intent(this, MainActivity::class.java )
        startActivity(inten)
        finish()
    }


    fun loadDataTeacher(){
        try {
            val dataTeacher = Indice.obtenerdatosMaestro(Nombre_Escuela.getName())
            if (dataTeacher.moveToFirst()){
                txtRNombre.setText(dataTeacher.getString(1))
                txtRApellidos.setText(dataTeacher.getString(2))
                txtRCelular.setText(dataTeacher.getString(5))
                txtRDireccion.setText(dataTeacher.getString(3))
                txtRColonia.setText(dataTeacher.getString(4))
            }
        }catch (Ex:Exception){
            Toast.makeText(this,Ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    fun registrarbd():Boolean {
        var correct = false
        try{
            ///if(Indice.obtenerdatosMaestro(txtREmail.text.toString()) == false) {

               if (Indice.insertarDatosMaestro(
                    txtRNombre.text.toString(),
                    txtRApellidos.text.toString(),
                    txtRDireccion.text.toString(),
                    txtRColonia.text.toString(),
                    txtRCelular.text.toString(),
                    "",
                    "",
                )){
                val lastIdTeacher = Indice.getLastCteacher()
                Indice.updateCtecherEschool(Nombre_Escuela.getName(),lastIdTeacher)
                Toast.makeText(applicationContext,"Se registro los datos del Profesor correctamente",Toast.LENGTH_SHORT).show()
                correct = true
            /*}
            else{
                Toast.makeText(applicationContext, "Correo ya registrado", Toast.LENGTH_SHORT).show()
                return  false
            }*/
          }
        }
        catch (ex: Exception){
            Toast.makeText(applicationContext,ex.message.toString(), Toast.LENGTH_LONG ).show()
            correct = false
        }
        return  correct
    }

    fun validarcontraseña(pass1 : String, pass2 : String):Boolean{

        if(pass1.length > 0 && pass2.length > 0) {

            if (pass1 == pass2) {
                return true
            }
            else {
                Toast.makeText(applicationContext, "La contraseñas no coinciden", Toast.LENGTH_LONG)
                    .show()
                return false
            }
        }
        else{
            Toast.makeText(applicationContext, "Favor de escribir ambos campos de la contraseña", Toast.LENGTH_LONG).show()
            return false
        }
    }

    fun validardatos():Boolean{
        if(txtRNombre.text.length > 0 && txtRApellidos.text.length > 0 && txtRDireccion.text.length > 0 && txtRColonia.text.length > 0 && txtRCelular.text.length> 0)
        {
            return true
        }
        else{
            Toast.makeText(applicationContext,"Asegurece de escribir todos los campos ", Toast.LENGTH_SHORT).show()
            return  false
        }
    }
}