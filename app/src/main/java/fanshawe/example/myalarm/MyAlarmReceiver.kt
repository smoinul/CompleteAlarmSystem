package fanshawe.example.myalarm

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fanshawe.example.myalarm.Constants.ALARM_CHANNEL_ID
import fanshawe.example.myalarm.Constants.ALARM_REQUEST_CODE

class MyAlarmReceiver: BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(thisContext: Context?, thisIntent: Intent?) {
        val descriptionIntent:Intent = Intent(thisContext, AlarmDescription::class.java)
        thisIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(thisContext, ALARM_REQUEST_CODE, descriptionIntent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(thisContext!!, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_alarm_on_24)
            .setContentTitle("Fanshawe Alarm System")
            .setContentText("You have set this alarm through your developed app!")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(thisContext)
        notificationManager.notify(123, builder.build())

    }
}