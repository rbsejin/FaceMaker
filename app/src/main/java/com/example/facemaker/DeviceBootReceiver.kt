package com.example.facemaker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*


class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            // on device boot complete, reset the alarm
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //


            // id -> task 찾는다.
            val taskId: Int = intent?.getIntExtra("taskId", 0)
            //val task: Task = ProjectManager.getTaskForId(taskId) ?: return


            val sharedPreferences =
                context.getSharedPreferences("alarm", Context.MODE_PRIVATE)
            val millis =
                sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().timeInMillis)

            val nextNotifyTime: Calendar = GregorianCalendar()
            nextNotifyTime.timeInMillis = sharedPreferences.getLong("nextNotifyTime", millis)

            manager?.setRepeating(
                AlarmManager.RTC_WAKEUP, nextNotifyTime.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )
        }
    }
}