package com.example.proyecto.ModelClasses

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.proyecto.MySQLConnection

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("notification_id", -1)
        val userId = intent.getIntExtra("user_id", -1)
        Toast.makeText(context, "Notification ID: $notificationId, User ID: $userId", Toast.LENGTH_SHORT).show()
        Log.d("NotificationActionReceiver", "Notification ID: $notificationId, User ID: $userId")
        if (notificationId != -1 && userId != -1) {
            val MySQLConnection = MySQLConnection(context)
            MySQLConnection.updateDataAsync(
                "UPDATE notificacion SET leida = 1 WHERE id = ?",
                *arrayOf(notificationId.toString())
            ) { success ->
                if (success) {
                    Toast.makeText(context, "Notificación marcada como leída", Toast.LENGTH_SHORT).show()
                    NotificationManagerCompat.from(context).cancel(notificationId)
                } else {
                    Toast.makeText(context, "Error al marcar la notificación como leída", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}