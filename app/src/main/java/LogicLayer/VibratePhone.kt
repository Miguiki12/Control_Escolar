package LogicLayer

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

object VibratePhone {

    // Dentro de una actividad o cualquier clase que tenga acceso al contexto
 public fun vibrarTelefono(context: Context, tiempo: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(tiempo, VibrationEffect.EFFECT_TICK))
        } else {
            // Deprecated en versiones más nuevas
            vibrator.vibrate(tiempo)
        }
    }
}