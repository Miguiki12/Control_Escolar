package com.example.control_escolar

//import androidx.core.view.isVisible

import BDLayer.createExcel
import BDLayer.schoolBD
import LogicLayer.Formats
import LogicLayer.PathUtil
import LogicLayer.VibratePhone
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_act_seleccionar_escuela.*
import kotlinx.android.synthetic.main.view_show_bd.*
import kotlinx.android.synthetic.main.view_show_bd.view.*
import java.io.File
import java.util.*

//import com.huawei.hms.framework.common.PermissionMgr
//import com.huawei.hms.framework.common.util.ConstantHolder

//import java.util.jar.Manifest
//import com.huawei.hms.support.hianalytics.permission


class actSeleccionarEscuela : AppCompatActivity() {

    val adapter = EscuelasAdapter(1)
    lateinit var Indice: BD_Indice
    lateinit var schoolBD: schoolBD
    private val STORAGE_PERMISSION_CODE = 1
    private val REQUEST_CODE_PERMISSIONS = 100
    //lateinit var notificationReceiver: NotificationReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_seleccionar_escuela)
        recyclerView.layoutManager = LinearLayoutManager(this)
        Indice = BD_Indice(this)
        this.supportActionBar?.title = "Escuelas"
        CargarEscuelas()

        try{
            // Pedimos el permiso de almacenamiento
            getPermissions()
            imprimibles()
        }catch (Ex:Exception){
            //Toast.makeText(this,Ex.message.toString(),Toast.LENGTH_SHORT).show()
        }

    }
    override fun onResume() {
        super.onResume()
        // Tu código aquí se ejecutará cada vez que la actividad sea visible
        CargarEscuelas()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // Finaliza la aplicación
    }
    fun imprimibles(){
        val folderName = "Imprimibles"
        // Obtenemos la ruta de la memoria externa
        val externalStorageDirectory = "/sdcard/Documents/"//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        // Creamos una referencia a la carpeta que deseamos crear
        val folder = File(externalStorageDirectory, folderName)
        // Verificamos si la carpeta ya existe
        if (!folder.exists()) {
            // Creamos la carpeta si no existe
            if (folder.mkdirs()) {
                Toast.makeText(this, "Carpeta creada con éxito: $folder",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al crear la carpeta: $folder",Toast.LENGTH_SHORT).show()
            }
        } else {
            //Toast.makeText(this, "La carpeta ya existe: $folder",Toast.LENGTH_SHORT).show()
        }

    }


    fun CargarEscuelas(){
        var count = 0
        adapter.clearAll()
        var escuelas = Indice.obtenerTodasEscuelas()
        if(escuelas.moveToFirst()){
            while(count < escuelas.count){
                adapter.Clave.add(escuelas.getString(0))
                adapter.Nombre.add(escuelas.getString(1))
                adapter.Detalles.add(escuelas.getString(2)+" "+escuelas.getString(3)+"\n"+escuelas.getString(4)+"\n"+escuelas.getString(5))
                adapter.Imagen.add(R.drawable.escueladefault)
                adapter.Tipo.add(escuelas.getString(8))
                adapter.Turno.add(escuelas.getString(4))
                adapter.Alias.add(escuelas.getString(9))
                escuelas.moveToNext()
                count ++
            }
            recyclerView.adapter = adapter
        }
        //Toast.makeText(this, Indice.error, Toast.LENGTH_SHORT).show()
    }





    @SuppressLint("MissingInflatedId")
    private fun createSchool() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.viewdatosescuela,null)
        /**set view*/
        val Nombre = v.findViewById<EditText>(R.id.edit_datosNombre)
        val grado = v.findViewById<Spinner>(R.id.cbx_datosGrado)
        val grupo = v.findViewById<Spinner>(R.id.cbx_datosGrupo)
        val turno = v.findViewById<Spinner>(R.id.cbx_datosTurno)
        val ciclo = v.findViewById<Spinner>(R.id.cbx_datosCiclo)
        val tipo_escuela = v.findViewById<Spinner>(R.id.cbx_tipo_escuela)
        val txtciclo = v.findViewById<TextView>(R.id.text_ciclo_escuela)
        txtciclo.text = " CICLO  ${Formats.getCiclo()}"
        tipo_escuela.setSelection(0)
        //val botonCrear =  v.findViewById<Button>(R.id.btn_datosCrear)
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            if (Nombre.text.toString().isNotEmpty()){
                try {
                    var tipo = tipo_escuela.selectedItemPosition + 1
                    val alias = adapter.createAlias(Nombre.text.toString(),turno.selectedItem.toString(),grado.selectedItem.toString(), grupo.selectedItem.toString())
                    if (!Indice.isnotrepeated(alias)){
                        Indice.insertarDatosEscuela(Nombre.text.toString(),
                            alias,
                            grado.selectedItem.toString(),
                            grupo.selectedItem.toString(),
                            turno.selectedItem.toString(),
                            Formats.getCiclo(),//ciclo.selectedItem.toString(),
                            0,
                            tipo)
                        val BD_Escuelas = BD_Escuelas(this, alias)
                        //loadDateSchool(Nombre.text.toString(),grado.selectedItem.toString(),grupo.selectedItem.toString(),tipo_escuela.selectedItemPosition,turno.selectedItem.toString())
                        BD_Escuelas.OnCreateAll(this)
                        schoolBD = schoolBD(this,alias)
                        insertDataSchool(Nombre.text.toString(), grado.selectedItem.toString(),grupo.selectedItem.toString(),turno.selectedItem.toString(),ciclo.selectedItem.toString())
                        VibratePhone.vibrarTelefono(this, 1500)
                        adapter.clearAll()
                        CargarEscuelas()
                        //recyclerView.adapter = adapter
                        dialog.dismiss()
                    }else Toast.makeText(this, "Escuela ya existente, Verifique el turno, grado y grupo", Toast.LENGTH_SHORT).show()

                }
                catch (Ex:Exception){
                    Toast.makeText(this,Ex.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Escriba el nombre de la Escuela", Toast.LENGTH_SHORT).show()
            }
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }
    /**ok now run this */

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_sup_crearescuelas, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.btnm_crearescuela -> createSchool()
            R.id.btnm_help_school -> showHelp()

        }
        return  super.onOptionsItemSelected(item)
    }

    fun irPrincipal(){
        var inten = Intent(this, MainActivity2::class.java)
        startActivity((inten))
    }

    fun getPermissions(){
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val requestCode = 123 // Puedes usar cualquier número como código de solicitud
        if (ContextCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            imprimibles() // Llama a la función que crea la carpeta
        }
    }


    private fun showHelp() {
        val inflter = LayoutInflater.from(this)
        val v = inflter.inflate(R.layout.view_help_school,null)
        /**set view*/
        val addDialog = AlertDialog.Builder(this)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            dialog.dismiss()

        }

        addDialog.create()
        addDialog.show()
    }



    fun insertDataSchool(name:String, grado:String, grupo:String, turno:String, ciclo:String){
        try {
            schoolBD.sName = name
            schoolBD.sGrado = grado
            schoolBD.sGrupo = grupo
            schoolBD.sTurno = turno
            schoolBD.sCiclo = ciclo
            schoolBD.insertShortDataSchool()
        }catch (Ex:Exception){Toast.makeText(this,Ex.message.toString(), Toast.LENGTH_SHORT).show()}
   }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == adapter.requestCode && resultCode == Activity.RESULT_OK) {
            // Obtén la ruta del archivo seleccionado
            val selectedFileUri: Uri? = data?.data
            val path =  PathUtil().getPath(this,selectedFileUri!!)
            if(validateNameFile(path!!)){
                createExcel(this).readExcelPath(path!!, Nombre_Escuela.getName())
                Toast.makeText(this, "Asegúrese de que la información se haya transferido correctamente", Toast.LENGTH_LONG).show()
            }else Toast.makeText(this, "Archivo invalido", Toast.LENGTH_LONG).show()
        }
    }

    fun validateNameFile(name:String):Boolean {
        try {
            var isValite = false
            val piecesFile = name.split('/')//partimos la ruta del archivo en carpetas '/'
            val piecesName = piecesFile[piecesFile.count() - 1].split('_')//si el nombre es el correcto deberia partirce en posiciones
            if (piecesName.count() == 4) {//si el archivo se partio correctamente nos aseguramos de que cumpla los requisitos de que es un archivo creado por la misma app
                val grado = piecesName[2].toIntOrNull()//almacenamos el grado de la escuela
                val isXls = piecesName[3].split('.')//obtenemos la extencion del archivo
                if (Formats.isNumeric(grado.toString()) && isXls[1] == "xls") isValite = true//si hay un grado y el archivo es xls entonces procesdemos a insertar los datos en la bd
            }
            return isValite
        }catch (Ex:Exception){
            return  false
        }

    }
}







