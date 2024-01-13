package com.example.control_escolar

import BDLayer.TareasBD
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.control_escolar.MainActivity2.Companion.MY_CHANNEL_ID

class AlarmNotification:BroadcastReceiver() {
    lateinit var pendientes: TareasBD
    companion object{
        const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, p1: Intent?) {
        createSimpleNotification(context)
    }

    private fun createSimpleNotification(context: Context) {
        pendientes = TareasBD(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)
        val notification = NotificationCompat.Builder(context, MainActivity2.MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_matter)
            .setContentTitle("No olvides")
            .setContentText("Actividaddes popr calificar")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Falta esta parte por proramar")
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        //Toast.makeText(context,pendingActivity(),Toast.LENGTH_SHORT).show()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun pendingActivity():String{
        val actividades = pendientes.getPending()
        var pending = ""
        var contador = 0
        if (actividades.moveToFirst()){
            while (contador < actividades.count){
                pending += " "+actividades.getString(2)
                actividades.moveToNext()
                contador++
            }
        }
        return pending
    }


}
