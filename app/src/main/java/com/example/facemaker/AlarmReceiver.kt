//package com.example.facemaker
//
//import android.annotation.SuppressLint
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Context.VIBRATOR_SERVICE
//import android.content.Intent
//import android.os.Build
//import android.os.VibrationEffect
//import android.os.Vibrator
//import androidx.core.app.NotificationCompat
//import com.example.facemaker.data.Task
//
//
//class AlarmReceiver : BroadcastReceiver() {
//    @SuppressLint("WrongConstant")
//    override fun onReceive(context: Context?, intent: Intent?) {
//        val notificationManager: NotificationManager =
//            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val notificationIntent = Intent(context, MainActivity::class.java)
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//
//        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
//
//        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "default")
//
//        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
//
//        // OREO API 26 이상부터 채널 필요
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            val channel =
//                NotificationChannel("default", "알람 채널", NotificationManager.IMPORTANCE_HIGH)
//            channel.description = "정해진 시간에 알람을 설정합니다."
//            notificationManager.createNotificationChannel(channel)
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }
//
//        // id -> task 찾는다.
//        val taskId: Int = intent!!.getIntExtra("taskId", 0)
//        val task: Task = ProjectManager.getTaskForId(taskId) ?: return
//
//        // 알람 삭제시 notification 이 취소되지 않아 임시방편으로 여기서 취소.
//        if (task.notificationDateTime == null) {
//            notificationManager.cancel(task.id)
//            return
//        }
//
//        // 알림을 제거한다.
//        task.notificationDateTime = null
//        // view를 갱신해주지 않아서 뷰는 그대로고, 데이터만 null인 상태
//
//        builder.setAutoCancel(true)
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .setWhen(System.currentTimeMillis())
//            .setTicker("{Ticker}")
//            .setContentTitle("미리 알림")
//            .setContentText(task.name)
//            .setContentInfo("INFO")
//            .setContentIntent(pendingIntent)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//
//        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            vibrator.vibrate(1000);
//        }
//
//        notificationManager.notify(task.id, builder.build())
//    }
//}
//
