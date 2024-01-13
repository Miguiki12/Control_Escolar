package BDLayer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import com.example.control_escolar.Nombre_Escuela
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class sendEMail(var context: Context) {
    var policy : StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    var properties : Properties = Properties()
    lateinit var Correos:ArrayList<String>
    public var todosloscorreo = ""
    public var correoMaestro = ""
    public var contraseñaMaestro = ""
    public var correodirector = ""
    var pdf = crearPDF(context)
    public lateinit var session: Session
    public var error  = ""
    lateinit var config : SettingsBD
    var _multipart = MimeMultipart()
    init{

        Correos = ArrayList()
        config = SettingsBD(context)
        StrictMode.setThreadPolicy(policy)
        //properties.put("mail.smtp.host", "smtp.googlemail.com")
        properties.put("mail.smtp.host", "smtp.gmail.com")
        properties.put("mail.smtp.socketFactory.port", "465")
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        properties.put("mail.smtp.auth", "true")
        properties.put("mail.smtp.port", "465")
        getLogin()
        session =  javax.mail.Session.getDefaultInstance(properties,
            object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(correoMaestro, contraseñaMaestro)
                }
            })
    }

    public fun getLogin(){
        val setting =  config.getEmails()
        if (setting.moveToFirst()){
            //Toast.makeText(context, setting.getString(0).toString(), Toast.LENGTH_SHORT).show()
            correoMaestro = setting.getString(0).toString()//"migvaz5@gmail.com"
            contraseñaMaestro = setting.getString(1)//setting.getString(1).toString()//"fbksdnrjprenjvia"
            correodirector = setting.getString(2).toString()
        }
    }


    public fun addAddressee(correo:String){
        Correos.add(String())
    }

    fun adjuntartodos(correo:String){
        if (correo.length > 0)  todosloscorreo += correo +","
    }


   public fun allMails():String{
        var count = 0
        var todos = ""
        try {
            if (Nombre_Escuela.Alumnos.moveToFirst()){
                while (count < Nombre_Escuela.Alumnos.count){
                    if (Nombre_Escuela.Alumnos.getString(16).length > 0) todos += Nombre_Escuela.Alumnos.getString(16) + ","
                     Nombre_Escuela.Alumnos.moveToNext()
                    count ++
                }
            }
        }catch (Ex:Exception){
               error = Ex.message.toString()
        }
        if (todos.length > 0) todos = todos.substring(0, todos.length-1 )
        return todos
    }


    public fun selectMails(emails:Cursor):String{
        var count = 0
        var todos = ""
        try {
            if (emails.moveToFirst()){
                while (count < emails.count){
                    if (emails.getString(0).length > 0) todos += emails.getString(0) + ","
                    emails.moveToNext()
                    count ++
                }
            }
        }catch (Ex:Exception){
            error = Ex.message.toString()
        }
        if (todos.length > 0) todos = todos.substring(0, todos.length-1 )
        else error = "No hay correos de destinatarios disponibles"

        return todos
    }

    public fun adjuntar(nameFile:String, routFile:String){
        // Crea un objeto Multipart para contener el archivo adjunto
        val ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString()+"/Imprimibles/"
        // Crea un objeto MimeBodyPart para el archivo adjunto
        val attachmentBodyPart = MimeBodyPart()

        // Obtiene la ruta del archivo que deseas adjuntar
        val file = File(ruta+nameFile+".pdf")

        // Establece el archivo adjunto utilizando un objeto FileDataSource
        val fileDataSource = FileDataSource(file)
        attachmentBodyPart.setDataHandler(DataHandler(fileDataSource))

        // Establece el nombre del archivo adjunto
        attachmentBodyPart.setFileName(file.name)

        while (_multipart.count > 0) {
            _multipart.removeBodyPart(0)
        }
        // Agrega el objeto MimeBodyPart al objeto Multipart
        _multipart.addBodyPart(attachmentBodyPart)

    }


    public  fun send(asunto:String, contenido:String, destinatario: String){
        try {
            if (session != null){
                var message : javax.mail.Message = MimeMessage(session)
                message.setFrom(InternetAddress(correoMaestro))
                message.setSubject(asunto)
                message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(destinatario))
                message.setContent(contenido, "text/html; charset=utf-8")
                Transport.send(message)
                error = "Correo enviado con exito"
            }
        }catch (Ex:java.lang.Exception){
            error = "No se pudo enviar el email"
            error = Ex.message.toString()
        }
    }

    public  fun sendwhitattach(asunto:String, contenido:String, destinatario: String):Boolean{
        var enviado = false
        try {
            if (session != null){
                var message : javax.mail.Message = MimeMessage(session)
                message.setFrom(InternetAddress(correoMaestro))
                message.subject = asunto
                message.setContent(_multipart)
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(destinatario))
                Transport.send(message)
                //error = destinatario
                error = "Mensaje enviado con exito"
                enviado =true
            }
        }catch (Ex:java.lang.Exception){
            error = destinatario
            error = Ex.message.toString()
            enviado = false
        }
        return enviado
    }

    public  fun openGmailWithAttachment( subject: String, body: String, attachment: String, activity: Activity) {
        val intentEmail = Intent(Intent.ACTION_SENDTO)
        intentEmail.data = Uri.parse("mailto:${allMails()}")
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Titulo del mail")
        intentEmail.putExtra(Intent.EXTRA_TEXT, "Hola, estoy esperando respuesta..")
        //intentEmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("migvaz5@hotmail.com","migvaz5@gmail.com"))
        activity.startActivity(Intent.createChooser(intentEmail, "raulvazquez@hotmail.com"))
    }


    /*fun openGmailWithAttachment(subject: String, body: String, attachmentPath: String, activity: Activity) {
        val intent = Intent(Intent.ACTION_SENDTO)
        //intent.data = Uri.parse("mailto:")
        intent.data = Uri.parse("mailto:${allMails()}")
        //intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("correo@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)

        // Añade el archivo adjunto
        val file = File(attachmentPath)
        if (file.exists()) {
            val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Especifica el paquete de la aplicación de Gmail
        intent.setPackage("com.google.android.gm")

        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Gmail no está instalado
            Toast.makeText(activity, "Gmail no está instalado", Toast.LENGTH_SHORT).show()
        }
    }*/

    /*fun openGmailWithAttachment(subject: String, body: String, attachmentPath: String, activity: Activity) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:${allMails()}")
        intent.putExtra(Intent.EXTRA_SUBJECT, "FALTA DEL DIA")
        val body1 = "Estimados padres o tutores:\n "+
        "\n"+
        "Le informamos que su hijo/a no asistió a la escuela el 2023-05-21 sin aviso previo. Esta falta se considera como injustificada y debe ser informada a la autoridad educativa competente.\n"+
        "Es importante recordar que la asistencia regular a clase es esencial para el buen rendimiento académico de su hijo/a y para mantener un ambiente de aprendizaje adecuado en la escuela. Le pedimos que informe a la escuela lo antes posible sobre las razones de la falta de su hijo/a y que tome las medidas necesarias para evitar futuras faltas injustificadas.\n"+
        "\n"
        intent.putExtra(Intent.EXTRA_TEXT, body1)

        // Añade el archivo adjunto
        val file = File(attachmentPath)
        if (file.exists()) {
            val uri: Uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Especifica el paquete de la aplicación de Gmail
        intent.setPackage("com.google.android.gm")

        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Gmail no está instalado
            Toast.makeText(activity, "Gmail no está instalado", Toast.LENGTH_SHORT).show()
        }
    }*/


    /*fun openGmailWithAttachment(subject: String, body: String, activity: Activity) {
        try {
            // Crea un Intent para enviar el correo electrónico
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")

            // Añade el destinatario, asunto y cuerpo
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, body)

            // Añade el archivo adjunto si hay uno
            if (_multipart.count > 0) {
                intent.putExtra(Intent.EXTRA_STREAM, _multipart)
            }

            // Especifica el paquete de la aplicación de Gmail
            intent.setPackage("com.google.android.gm")

            // Inicia la actividad para enviar el correo
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Gmail no está instalado
            Toast.makeText(activity, "Gmail no está instalado", Toast.LENGTH_SHORT).show()
        }
    }*/







    public fun avisarActividad(){

    }
    /*public fun avisarFaltas(fecha:String, lista:Cursor){

        GlobalScope.launch {
                  send(
                        "FALTA DEL DIA "+ fecha,
                        "Se le informa que su hijo(a) falto a clases el dia: "+fecha +"\n", selectMails(lista))
        }
    }*/
    public fun avisarFaltas(fecha:String, lista:Cursor):Boolean{
        var enviado = false
        try {
            if (session != null){
                var message : javax.mail.Message = MimeMessage(session)
                message.setFrom(InternetAddress(correoMaestro))
                message.subject = "Falta $fecha"
                message.setContent(_multipart)
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(selectMails(lista)))
                Transport.send(message)
                if (lista.count == 0) error = "No hay faltas por avisar"
                else error = "Mensaje enviado con exito"
                enviado = true
            }
        }catch (Ex:java.lang.Exception){
            error = "Correo y contraseña del profesor son incorrectos o  correos de tutores no registrados "
            //error = Ex.message.toString()
            enviado = false
        }
        return  enviado
    }

    public  fun avisarHorario(){

    }

    public  fun avisarTareasCalificadas(){

    }

}


