package com.example.control_escolar

import LogicLayer.LogoSchool
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_act_photo.*


class act_Photo : AppCompatActivity() {
    private lateinit var imageBitmap: Bitmap
    private var currentRotationAngle = 0f
    lateinit var putImage:LogoSchool
    var takedImage = 0
    var takedPicture = 0


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_photo)
        val btnCamara = findViewById<Button>(R.id.btnCamara)
        val btnRotar = findViewById<Button>(R.id.btn_Rotar)
        var  bundle = intent.extras
        this.supportActionBar?.title = "Fotografia para el alumno"
        val nombre_alumno = bundle?.getString("nombre_alumno").toString()
        val folio = bundle?.getString("folio").toString()
        this.supportActionBar?.subtitle = nombre_alumno
        putImage = LogoSchool(this,this.contentResolver,image_Photo)

        btnCamara.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        btnSavePicture.setOnClickListener {
            //enviarListaDeVuelta()
            if (takedPicture == 1){
                putImage.copyBitmapToDestinationStudent(imageBitmap,folio)
                this.onBackPressed()
            }
            if (takedImage == 1){
                putImage.copyFileToDestinationStudent(folio,putImage.image)
                this.onBackPressed()
            }
            if (takedImage == 0 && takedPicture == 0) Toast.makeText(this, "Tome una foto o seleccione una imagen", Toast.LENGTH_SHORT).show()

        }

        btnCancelPicture.setOnClickListener {this.onBackPressed()}

        btnArchivo.setOnClickListener {
            putImage.openFolder()
            takedPicture = 0
            hideForFile()

            /*if (putImage.takedImage == 1){
                takedPicture = 0
                hideForFile()//ocultamos los botones para que no tome fotos
            }*/
        }

        btnRotar.setOnClickListener {
            try {
                currentRotationAngle += 90f // Aumentar el ángulo de rotación en 90 grados cada vez que se presiona el botón
                if (currentRotationAngle >= 360f) {
                    currentRotationAngle = 0f // Reiniciar el ángulo de rotación a 0 grados si alcanza 360 grados
                }
                val rotatedBitmap = rotateImage(imageBitmap, currentRotationAngle)
                val imageView = findViewById<ImageView>(R.id.image_Photo)
                imageView.setImageBitmap(rotatedBitmap)
            }
            catch (Ex:Exception){

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takedImage =  putImage.handleActivityResult(requestCode, resultCode, data)
    }


    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            imageBitmap = intent?.extras?.get("data") as Bitmap
            val scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, 1366, 768, true)
            //imageBitmap = scaledBitmap
            val imageView = findViewById<ImageView>(R.id.image_Photo)
            imageView.setImageBitmap(imageBitmap)
            btn_Rotar.isVisible = true
            hideForPicture()
            takedImage = 0//ponemos el status de imagen desde archivo en 0
            takedPicture = 1//ponemos el status de foto en 1
        }
    }



    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        imageBitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        return imageBitmap
    }

    fun hideForPicture(){
        btnArchivo.isVisible = false
    }

    fun hideForFile(){
        btnCamara.isVisible = false
        btn_Rotar.isVisible = false
    }


    fun enviarListaDeVuelta() {
        try {
            val intent = Intent()
            intent.putExtra("picture", imageBitmap)
            setResult(RESULT_OK, intent)
            finish()
        }
        catch (Ex:Exception){
            Toast.makeText(this, Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

}