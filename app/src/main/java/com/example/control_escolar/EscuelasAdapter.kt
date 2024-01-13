package com.example.control_escolar

import BDLayer.GoogleDriveManager
import BDLayer.createExcel
import LogicLayer.Formats
import LogicLayer.ImagesForDocuments
import LogicLayer.ManagerImage
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_show_bd.view.*
import java.io.File
import java.io.FilenameFilter


class EscuelasAdapter(val requestCode: Int):RecyclerView.Adapter<EscuelasAdapter.ViewHolder>(){
    lateinit var Indice: BD_Indice
    public val Nombre =  ArrayList<String>()
    public val Detalles =  ArrayList<String>()
    public val Imagen =  ArrayList<Int>()
    public val Clave = ArrayList<String>()
    public val Tipo = ArrayList<String>()
    public var Turno = ArrayList<String>()
    public var Alias = ArrayList<String>()
    public  var posicion = 0
    lateinit var context : Context
    var selectedFilePath: String? = null

    inner class  ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var itemImagen:  ImageView
        var itemNombre: TextView
        var itemDetalles: TextView
        var itemOpciones: ImageView
        var itemClave: TextView
        var itemTipo: TextView


        init {
            context = itemView.context
            itemImagen = itemView.findViewById(R.id.image_Escuela)
            itemNombre = itemView.findViewById(R.id.lbl_NombreEscuela)
            itemDetalles = itemView.findViewById(R.id.lbl_DetallesEscuela)
            itemOpciones = itemView.findViewById(R.id.image_OpcionesEscuela)
            itemClave = itemView.findViewById(R.id.lbl_claveEscuela)
            itemTipo = itemView.findViewById(R.id.lbl_tipoEscuela)
            Indice = BD_Indice(itemView.context)
            itemOpciones.setOnClickListener {
                posicion = absoluteAdapterPosition
                Nombre_Escuela.setName(Alias[adapterPosition])
                popupMenus(it) }
            itemView.setOnClickListener{
                posicion = adapterPosition
                var inten = Intent(itemView.context, MainActivity2::class.java)
                inten.putExtra("Escuela", Nombre[adapterPosition].toString())
                inten.putExtra("Nivel", tipo_Escuela(Tipo[position].toInt()))
                val  detalles = Detalles[posicion].split("\n")
                val gradogrupo = detalles[0].split(" ")
                Nombre_Escuela.setName(Alias[adapterPosition])
                Nombre_Escuela.setAlias(Nombre[adapterPosition])
                Nombre_Escuela.set_tipo(Tipo[adapterPosition].toInt())
                Nombre_Escuela.setTurno(Turno[adapterPosition])
                Nombre_Escuela.setGrado(gradogrupo[0])
                Nombre_Escuela.setGrupo(gradogrupo[1])
                //Toast.makeText(itemView.context, Nombre_Escuela.getName(),Toast.LENGTH_SHORT ).show()
                itemView.context.startActivity((inten))
            }
        }
        private fun popupMenus(v:View) {
            try {
                val popupMenus = PopupMenu(itemView.context,v)
                popupMenus.inflate(R.menu.menu_escuelas)
                popupMenus.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.updateEscuela->{
                            val v = LayoutInflater.from(itemView.context).inflate(R.layout.viewdatosescuela,null)
                            val name = v.findViewById<EditText>(R.id.edit_datosNombre)
                            val grado = v.findViewById<Spinner>(R.id.cbx_datosGrado)
                            val grupo = v.findViewById<Spinner>(R.id.cbx_datosGrupo)
                            val turno = v.findViewById<Spinner>(R.id.cbx_datosTurno)
                            val ciclo = v.findViewById<Spinner>(R.id.cbx_datosCiclo)
                            val tipo = v.findViewById<Spinner>(R.id.cbx_tipo_escuela)
                            val textciclo = v.findViewById<TextView>(R.id.text_ciclo_escuela)
                            name.setText(Nombre[adapterPosition])
                            val  detalles = Detalles[posicion].split("\n")
                            val gradogrupo = detalles[0].split(" ")
                            var posicionGrado = (grado.adapter as ArrayAdapter<String>).getPosition(gradogrupo[0])
                            grado.setSelection(posicionGrado)
                            posicionGrado = (grupo.adapter as ArrayAdapter<String>).getPosition(gradogrupo[1])
                            grupo.setSelection(posicionGrado)
                            posicionGrado = (turno.adapter as ArrayAdapter<String>).getPosition(detalles[1])
                            turno.setSelection(posicionGrado)
                            posicionGrado = (ciclo.adapter as ArrayAdapter<String>).getPosition(detalles[2])
                            ciclo.setSelection(posicionGrado)
                            textciclo.text = "CICLO "+ Formats.getCiclo()
                            //name.isEnabled = false
                            tipo.isEnabled = false
                            //grado.setSelection(1)   // Detalles[adapterPosition]
                            //grado.get
                            AlertDialog.Builder(itemView.context)
                                .setView(v)
                                .setPositiveButton("Ok"){
                                        dialog,_->
                                    val alias = createAlias(name.text.toString(),turno.selectedItem.toString(),grado.selectedItem.toString(), grupo.selectedItem.toString())
                                    if (!Indice.isnotrepeated(alias)) {
                                        if (Indice.renameDatabaseFile(itemView.context, Nombre[adapterPosition], alias)){
                                            if (Indice.updateEscuelas(name.text.toString(),alias,grado.selectedItem.toString(),
                                                grupo.selectedItem.toString(),turno.selectedItem.toString(),Formats.getCiclo(),Clave[adapterPosition].toString())){
                                                //Indice.renameDatabaseFile(Alias[adapterPosition], alias)
                                                //actualizamos los datos de la lista
                                                Nombre[adapterPosition] = name.text.toString()
                                                Detalles[adapterPosition] = grado.selectedItem.toString() + " " + grupo.selectedItem.toString() + "\n" + turno.selectedItem.toString() + "\n" + ciclo.selectedItem.toString()
                                                //Tipo[adapterPosition] = tipo.selectedItem.toString()
                                                Turno[adapterPosition] = turno.selectedItem.toString()
                                                Alias[adapterPosition] = alias
                                                notifyDataSetChanged()
                                                dialog.dismiss()
                                            }
                                        }else Toast.makeText(itemView.context, "No se pudo renombrar la bd", Toast.LENGTH_SHORT).show()
                                    }else Toast.makeText(itemView.context, "Escuela ya existente, Verifique el turno, grado y grupo", Toast.LENGTH_SHORT).show()
                                }
                                .setNegativeButton("Cancel"){
                                        dialog,_->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                            true
                        }
                        R.id.exportar->{
                           val exportar = createExcel(itemView.context)
                           exportar.createExcelFile()


                            true
                        }
                        R.id.importar->{

                            showBd(v.context)
                            //openFolder()
                            //createExcel(itemView.context).readExcelFile()
                            true
                        }
                        R.id.delete->{
                            /**set delete*/
                            AlertDialog.Builder(itemView.context)
                                .setTitle("Borrar")
                                .setIcon(R.drawable.ic_baseline_warning_24)
                                .setMessage("¿Seguro de borrar esta escuela? "+Alias[posicion]+ " "+Clave[posicion])
                                .setPositiveButton("Yes"){
                                        dialog,_->
                                    if(Indice.deleteEscuela(Clave[posicion])){
                                        Indice.borrar_BD(Alias[posicion])
                                        ImagesForDocuments().deleteLogoSchool(Alias[posicion])//borramos el logo de la escuela
                                        ImagesForDocuments().deleteFolderStudents(Alias[posicion])//borramos las imagenes de los alumnos
                                        removList(posicion)
                                    }
                                    notifyDataSetChanged()
                                    dialog.dismiss()
                                }
                                .setNegativeButton("No"){
                                        dialog,_->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()
                            true
                        }
                        else-> true
                    }
                }
                popupMenus.show()
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenus)
                menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                    .invoke(menu,true)
            }catch (Ex: Exception){
                Toast.makeText(itemView.context,Ex.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v =  LayoutInflater.from(viewGroup.context).inflate(R.layout.cardescuelas, viewGroup,false)
        return  ViewHolder(v)

    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemNombre.text = Nombre[position]
        viewHolder.itemDetalles.text = Detalles[position]
        viewHolder.itemImagen.setImageResource(Imagen[position])
        viewHolder.itemClave.text = Clave[position]
        viewHolder.itemTipo.text = tipo_Escuela(Tipo[position].toInt())

        ManagerImage(context).loadImageSchool(viewHolder.itemImagen,Alias[position])
        //viewHolder.itemId

    }

    public  fun clearAll(){
        Nombre.clear()
        Detalles.clear()
        Imagen.clear()
        Clave.clear()
        Tipo.clear()
        Turno.clear()
        Alias.clear()
    }

    public fun removList(indice:Int){
        Nombre.removeAt(indice)
        Detalles.removeAt(indice)
        Imagen.removeAt(indice)
        Clave.removeAt(indice)
        Tipo.removeAt(indice)
        Turno.removeAt(indice)
        Alias.removeAt(indice)
    }

    public  fun updateList(nombre:String, detalles:String, imagen:Int,clave:String, tipo:String, turno:String, alias:String, position:Int){
        Nombre[position] = nombre
        Detalles[position] = detalles
        Imagen[position] = imagen
        Clave[position] = clave
        Tipo[position] = tipo
        Turno[position] = turno
        Alias[position] = alias
    }

    public  fun renameDatebase(){

    }

    override fun getItemCount(): Int {
        return  Nombre.size
    }

    fun tipo_Escuela(tipo:Int):String{
        var tipo_ = ""
        if (tipo == 1) tipo_ =  "Primaria"
        if (tipo == 2) tipo_ =  "Secundaria"
        if (tipo == 3) tipo_ =  "Preparatoria"
        if (tipo == 4) tipo_ =  "Universidad"
        return tipo_
    }

    public fun createAlias(nombre: String, turno: String, grado: String, grupo: String): String {

        return nombre + "_" + turno[0] + "_" + grado[0] + "_" + grupo[0]
    }

    private fun backupBD(context: Context){
        try {
            // Obtén el contexto
            val patch = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/"
            val databaseName = Nombre_Escuela.getName()
            val databasePath = File(patch, "Boletas.pdf").path
            Toast.makeText(context, patch +"Boletas.pdf", Toast.LENGTH_SHORT).show()

// Inicializa el GoogleDriveManager y realiza la copia de seguridad y subida
            val googleDriveManager = GoogleDriveManager(context)
            googleDriveManager.backupAndUploadDatabaseToDrive(databasePath)
        }catch (Ex:Exception){Toast.makeText(context, Ex.message.toString(),Toast.LENGTH_SHORT).show()}
    }


    fun showBd(context: Context) {
        try {
            val inflater = LayoutInflater.from(context)
            val v = inflater.inflate(R.layout.view_show_bd, null)
            val addDialog = AlertDialog.Builder(context)
            var nombreArchivo = ""
            addDialog.setView(v)

            val ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/DB/"

            val listViewArchivos: ListView = v.findViewById(R.id.listViewArchivos)

            // Actualizar la lista de archivos cada vez que se muestra la vista
            actualizarArchivosListView(context, listViewArchivos, ruta)

            listViewArchivos.setOnItemClickListener { _, _, position, _ ->
                nombreArchivo = obtenerArchivosXLS(ruta)[position]
                v.txt_title_shows_bd.text = "Base de datos a importar \n$nombreArchivo"
            }

            v.txt_select_file_bd.setOnClickListener {
                openFolder()
            }
            addDialog.setPositiveButton("Ok") { dialog, _ ->
                if (nombreArchivo.isNotEmpty()){
                    createExcel(context).readExcelFile(nombreArchivo, Nombre_Escuela.getName())
                    actualizarArchivosListView(context, listViewArchivos, ruta)
                    dialog.dismiss()
                }
                else Toast.makeText(context, "Seleccione el archivo a importar", Toast.LENGTH_LONG).show()
            }

            addDialog.create().show()
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarArchivosListView(context: Context, listView: ListView, rutaDirectorio: String) {
        //val archivos = obtenerArchivosXLS(rutaDirectorio)
        val archivos = obtenerArchivosXLS(rutaDirectorio)
        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, archivos)
        listView.adapter = adapter
    }


    private fun obtenerArchivosXLS(rutaDirectorio: String): Array<String> {
        val directorio = File(rutaDirectorio)
        val archivosXLS = directorio.list(FilenameFilter { _, nombre ->
            nombre.endsWith(".xls", ignoreCase = true)
        })
        return archivosXLS ?: emptyArray()
    }


    fun openFolder() {
        val folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/Imprimibles/DB/"
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.data = Uri.parse("file://$folderPath")
        intent.type = "application/vnd.ms-excel" // Tipo MIME para archivos XLS
        try {
            (context as Activity).startActivityForResult(intent, requestCode)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, e.printStackTrace().toString(), Toast.LENGTH_SHORT).show() // Manejar la excepción si no hay aplicaciones que puedan manejar la acción
        }
    }


}
