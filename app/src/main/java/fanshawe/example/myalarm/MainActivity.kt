package fanshawe.example.myalarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import fanshawe.example.myalarm.Constants.ALARM_CHANNEL_ID
import fanshawe.example.myalarm.Constants.ALARM_REQUEST_CODE
import fanshawe.example.myalarm.databinding.ActivityMainBinding

import fanshawe.example.myalarm.Permissions.hasNotificationPermission
import fanshawe.example.myalarm.Permissions.requestNotificationPermission

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var myBinding: ActivityMainBinding
    lateinit var picker: MaterialTimePicker
    lateinit var calender: Calendar
    lateinit var alarmManager : AlarmManager
    lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(myBinding.root)

        createNotificationChannel()

        myBinding.selectTime.setOnClickListener {
            showTimePicker()
        }
        myBinding.setAlarm.setOnClickListener {
            if (hasNotificationPermission(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                    }
                }
                setAlarm()
            } else {
                requestNotificationPermission(this)
            }
        }
        myBinding.cancelAlarm.setOnClickListener {
            cancelAlarm()
        }

    }

    private fun cancelAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, MyAlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, MyAlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.timeInMillis, pendingIntent)
        Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show()
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(supportFragmentManager, "Fanshawe")
        picker.addOnPositiveButtonClickListener{
            if (picker.hour > 12){
                myBinding.selectTime.text =
                    String.format("%02d", picker.hour - 12) + " : " + String.format("%02d", picker.minute) + "PM"
            }
            else{
                String.format("%02d", picker.hour) + " : " + String.format("%02d", picker.minute) + "AM"
            }
            calender = Calendar.getInstance()
            calender[Calendar.HOUR_OF_DAY] = picker.hour
            calender[Calendar.MINUTE] = picker.minute
            calender[Calendar.SECOND] = 0
            calender[Calendar.MILLISECOND] = 0
        }
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name: CharSequence = "MyAlarm"
            val description = "From MyAlarm App"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(ALARM_CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            requestNotificationPermission(this)
        }
    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Toast.makeText(this, "Granted!", Toast.LENGTH_SHORT).show()
    }
}