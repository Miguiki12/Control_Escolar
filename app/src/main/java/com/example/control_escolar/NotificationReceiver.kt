package com.example.control_escolar

import BDLayer.TareasBD
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val MY_CHANNEL_ID = "my_channel_id"
    }
    var school1 = ""
    var school2 = ""
    lateinit var pendienteBD: TareasBD
    lateinit var Indice:BD_Indice
    override fun onReceive(context: Context?, intent: Intent?) {
        pendienteBD = TareasBD(context!!)
        Indice = BD_Indice(context!!)
        //travelSchools(context)

        // Determina la hora de la notificación según el Intent recibido
        val notificationTime = if (intent?.action == "ACTION_1") "Matutino" else "Vespertino"

        // Crea la notificación
        val builder = NotificationCompat.Builder(context!!, MY_CHANNEL_ID)
            .setContentTitle("Pendientes de")
            .setContentText(Nombre_Escuela.getName() +" $notificationTime")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(reminder(context))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.ic_matter)

        // Lanza la notificación
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }

    public fun setDailyNotifications(context: Context) {


        //reminder()
        // Obtiene el servicio de AlarmManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Crea el Intent que se lanzará cuando se active la alarma de las 10:30am
        val intent1 = Intent(context, NotificationReceiver::class.java)
        intent1.action = "ACTION_1"
        val pendingIntent1 = PendingIntent.getBroadcast(context, 0, intent1, 0)

        // Crea el Intent que se lanzará cuando se active la alarma de las 2:30pm
        val intent2 = Intent(context, NotificationReceiver::class.java)
        intent2.action = "ACTION_2"
        val pendingIntent2 = PendingIntent.getBroadcast(context, 1, intent2, 0)

        // Establece la hora a la que se activará la alarma de las 10:30am
        val calendar1 = Calendar.getInstance()
        calendar1.timeInMillis = System.currentTimeMillis()
        calendar1.set(Calendar.HOUR_OF_DAY, 8)
        calendar1.set(Calendar.MINUTE, 30)
        calendar1.set(Calendar.SECOND, 0)

        // Establece la hora a la que se activará la alarma de las 2:30pm
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = System.currentTimeMillis()
        calendar2.set(Calendar.HOUR_OF_DAY, 14)
        calendar2.set(Calendar.MINUTE, 30)
        calendar2.set(Calendar.SECOND, 0)

        // Configura las alarmas para que se repitan todos los días a la misma hora
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar1.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent1
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar2.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent2
        )
    }

    public fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MY_CHANNEL_ID,
                "Control_Escolar",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = school1
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun start(context: Context) {
        createChannel(context)
        setDailyNotifications(context)
    }


    fun travelSchools(context: Context){
            var count = 0
            var escuelas = Indice.obtenerTodasEscuelas()
            if(escuelas.moveToFirst()){
                while(count < escuelas.count){
                    Nombre_Escuela.setName(escuelas.getString(1))
                    //Toast.makeText(context, escuelas.getString(4), Toast.LENGTH_SHORT).show()
                    if(escuelas.getString(4) == "Matutino") school1 += reminder(context)
                    if(escuelas.getString(4) == "Vespertino") school2 += reminder(context)
                    escuelas.moveToNext()
                    count++
                }
            }
    }

    fun reminder(context: Context):String{
        var pendientes =  pendienteBD.getPending()
        var sPendientes = ""
        var cont = 0
        if (pendientes.moveToFirst()){
            while (cont < pendientes.count){
                //Toast.makeText(context, pendientes.getString(4), Toast.LENGTH_SHORT).show()
                sPendientes += pendientes.getString(2) + " - "
                pendientes.moveToNext()
                cont ++
            }
        }
        if (sPendientes.isEmpty()) sPendientes = "No hay pendientes de este dia"


        return sPendientes
    }

}