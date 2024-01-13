package com.example.control_escolar

import BDLayer.GoogleDriveRestBackup
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var Indice: BD_Indice
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        ElegirEscuela()
        /*val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .requestEmail()
            // Agrega más solicitudes de alcances si es necesario
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //ElegirEscuela()
        btn_Ingresologin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }*/

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                ElegirEscuela()
                // Aquí tienes el objeto GoogleSignInAccount que representa la cuenta del usuario
                handleSignedInAccount(account)
            } catch (e: ApiException) {
                // Error al iniciar sesión
                Toast.makeText(this,e.message.toString(), Toast.LENGTH_SHORT).show()
                // Puedes manejar el error aquí según tus necesidades
            }
        }
    }

    private fun handleSignedInAccount(account: GoogleSignInAccount?) {
        if (account != null) {
            // Aquí puedes usar el objeto GoogleSignInAccount para acceder a Google Drive API
            val accessToken = account.serverAuthCode // Obtén el token de acceso

            if (accessToken != null) {
                val googleDriveRestBackup = GoogleDriveRestBackup(accessToken)

                val backupFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/Boletas.pdf")
                val backupSuccessful = googleDriveRestBackup.backupDatabaseToDrive(backupFile)

                if (backupSuccessful) {
                    Toast.makeText(this, "Respaldado exitosamente en Google Drive", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Respaldado exitosamente en Google Drive", Toast.LENGTH_SHORT).show()
                }

                val displayName = account.displayName
            } else {
                Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show()
            }
        }
    }





    fun Registrar(view: View) {
        var inten = Intent(this, act_RegistroMaestro::class.java)
        startActivity((inten))
        finish()
    }

    /*fun ElegirEscuela(view: View) {
        var inten = Intent(this, actSeleccionarEscuela::class.java)
        if (cbx_IngresoRecordar.isChecked){
            if (txt_IngresoCorreo.text.isNotEmpty() && txt_IngresoContrasena.text.isNotEmpty())
                Indice.updateRecordarMaestro(txt_IngresoCorreo.text.toString())
            else Toast.makeText(this,"Esciba una cuenta correctamente", Toast.LENGTH_SHORT)
        }
        startActivity((inten))
        //finish()
    }*/

    fun ElegirEscuela() {
        var inten = Intent(this, actSeleccionarEscuela::class.java)
        //inten.putExtra("Maestro", "")//Indice.Nombre)
        startActivity((inten))
        //finish()
    }
}