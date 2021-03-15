package com.example.facemaker

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.TaskDateItemBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class TaskDateAdapter(private var currentTask: Task) :
    Adapter<TaskDateAdapter.ViewHolder>() {
    private lateinit var parentContext: Context
    private lateinit var database: DatabaseReference

    class ViewHolder(val binding: TaskDateItemBinding, private val currentTask: Task) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup, currentTask: Task): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskDateItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, currentTask)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        parentContext = parent.context
        database = Firebase.database.reference
        return ViewHolder.from(parent, currentTask)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // bind
        holder.apply {
            when (position) {
                0 -> {
                    val dateFormat = SimpleDateFormat("yy년 MM월 dd일 HH시 mm분 알림")
                    if (currentTask.notification == null) {
                        holder.binding.taskDateItemName.text = "알림 설정"
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.INVISIBLE
                    } else {
                        holder.binding.taskDateItemName.text =
                            dateFormat.format(currentTask.notification)
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.VISIBLE
                    }

                    binding.taskDateItemIcon.setImageResource(R.drawable.baseline_notifications_none_24)
                }
                1 -> {
                    val dateFormat = SimpleDateFormat("yy년 MM월 dd일 까지")
                    if (currentTask.dueDate == null) {
                        holder.binding.taskDateItemName.text = "기한 설정"
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.INVISIBLE
                    } else {
                        holder.binding.taskDateItemName.text =
                            dateFormat.format(currentTask.dueDate)
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.VISIBLE
                    }

                    binding.taskDateItemIcon.setImageResource(R.drawable.baseline_calendar_today_24)
                }
                2 -> {
                    if (currentTask.repeatCycle == null) {
                        holder.binding.taskDateItemName.text = "반복"
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.INVISIBLE
                    } else {
                        holder.binding.taskDateItemName.text = currentTask.repeatCycle
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.VISIBLE
                    }

                    binding.taskDateItemIcon.setImageResource(R.drawable.baseline_repeat_24)
                }
                3 -> {
                    if (currentTask.myDay != null) {
                        holder.binding.taskDateItemName.text = "나의 하루에 추가됨"
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.VISIBLE


                    } else {
                        holder.binding.taskDateItemName.text = "나의 하루에 추가"
                        holder.binding.taskDateItemDelete.visibility = RecyclerView.INVISIBLE
                    }

                    binding.taskDateItemIcon.setImageResource(R.drawable.baseline_calendar_today_24)
                }

                else -> {
                    assert(false)
                }
            }
        }

        // 알림, 기한, 반복 아이템 클릭했을 때 이벤트 처리
        holder.itemView.setOnClickListener {
            val calendar = Calendar.getInstance()

            when (position) {
                // 미리 알림
                0 -> {
                    PopupMenu(holder.binding.root.context, holder.itemView).apply {
                        menuInflater.inflate(R.menu.notification_item_menu, menu)

                        setOnMenuItemClickListener {
                            val ret = when (it.itemId) {
                                R.id.notification_today_later_item -> {
                                    calendar.add(Calendar.HOUR_OF_DAY, 3)
                                    calendar.set(Calendar.MINUTE, 0)
                                    currentTask.notification = calendar.time
                                    setAlarm(calendar)
                                    true
                                }
                                R.id.notification_tomorrow_item -> {
                                    calendar.add(Calendar.DATE, 1)
                                    calendar.set(Calendar.HOUR_OF_DAY, 9)
                                    calendar.set(Calendar.MINUTE, 0)
                                    currentTask.notification = calendar.time
                                    setAlarm(calendar)
                                    true
                                }
                                R.id.notification_next_week_item -> {
                                    val week = 7
                                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                                    calendar.add(Calendar.DAY_OF_MONTH, week)
                                    calendar.set(Calendar.HOUR_OF_DAY, 9)
                                    calendar.set(Calendar.MINUTE, 0)
                                    currentTask.notification = calendar.time
                                    setAlarm(calendar)
                                    true
                                }

                                R.id.notification_direct_selection_item -> {
                                    if (currentTask.notification != null) {
                                        calendar.time = currentTask.notification
                                    }

                                    val datePickerDialog = DatePickerDialog(
                                        parentContext,
                                        { _, year, month, dayOfMonth ->
                                            calendar.set(Calendar.YEAR, year)
                                            calendar.set(Calendar.MONTH, month)
                                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            currentTask.notification = calendar.time

                                            val timePickerDialog = TimePickerDialog(
                                                parentContext,
                                                { _, hourOfDay, minute ->
                                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                    calendar.set(Calendar.MINUTE, minute)

                                                    currentTask.notification = calendar.time
                                                    setAlarm(calendar)

                                                    database.child("tasks/${currentTask.id}/notification")
                                                        .setValue(currentTask.notification)
                                                },
                                                calendar.get(Calendar.HOUR_OF_DAY),
                                                calendar.get(Calendar.MINUTE),
                                                false
                                            )
                                            timePickerDialog.show()
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    )
                                    datePickerDialog.show()

                                    true
                                }
                                else -> false
                            }

                            database.child("tasks/${currentTask.id}/notification")
                                .setValue(currentTask.notification)

                            ret
                        }
                        show()
                    }
                }
                // 기한 설정
                1 -> {
                    PopupMenu(holder.binding.root.context, holder.itemView).apply {
                        menuInflater.inflate(R.menu.due_date_item_menu, menu)
                        setOnMenuItemClickListener {
                            val ret = when (it.itemId) {
                                R.id.due_today_item -> {
                                    currentTask.dueDate = calendar.time
                                    true
                                }
                                R.id.due_tomorrow_item -> {
                                    calendar.add(Calendar.DATE, 1)
                                    currentTask.dueDate = calendar.time
                                    true
                                }
                                R.id.due_next_week_item -> {
                                    val week = 7
                                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                                    calendar.add(Calendar.DAY_OF_MONTH, week)
                                    currentTask.dueDate = calendar.time
                                    true
                                }
                                R.id.due_direct_selection_item -> {
                                    val DEFAULT_HOUR = 9

                                    val datePickerDialog = DatePickerDialog(
                                        holder.binding.root.context,
                                        { _, year, month, dayOfMonth ->
                                            calendar.set(Calendar.YEAR, year)
                                            calendar.set(Calendar.MONTH, month)
                                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            calendar.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR)
                                            calendar.set(Calendar.MINUTE, 0)
                                            currentTask.dueDate = calendar.time
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    )
                                    datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                                        currentTask?.let { task ->
                                            val calendar = Calendar.getInstance()
                                            calendar.set(Calendar.YEAR, year)
                                            calendar.set(Calendar.MONTH, month)
                                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            task.dueDate = calendar.time
                                        }
                                    }

                                    datePickerDialog.show()

                                    true
                                }
                                else -> false
                            }

                            database.child("tasks/${currentTask.id}/dueDate")
                                .setValue(currentTask.dueDate)

                            ret
                        }
                        show()
                    }
                }
                // 반복
                2 -> {
                    PopupMenu(holder.binding.root.context, holder.itemView).apply {
                        menuInflater.inflate(R.menu.repeat_cycle_item_menu, menu)
                        setOnMenuItemClickListener {
                            val ret = when (it.itemId) {
                                R.id.repeat_every_day_item -> {
                                    currentTask.repeatCycle = "매일"
                                    true
                                }
                                R.id.repeat_every_weekday_item -> {
                                    currentTask.repeatCycle = "평일"
                                    true
                                }
                                R.id.repeat_every_week_item -> {
                                    currentTask.repeatCycle = "매주"
                                    true
                                }
                                R.id.repeat_every_month_item -> {
                                    currentTask.repeatCycle = "매월"
                                    true
                                }
                                R.id.repeat_every_year_item -> {
                                    currentTask.repeatCycle = "매년"
                                    true
                                }
                                R.id.repeat_direct_selection_item -> {
                                    currentTask.repeatCycle = "사용자 지정"
                                    true
                                }
                                else -> false
                            }

                            // 반복 선택하면 기한 설정도 기본값(오늘)로 같이 설정
                            currentTask.repeatCycle?.let {
                                if (currentTask.dueDate == null) {
                                    currentTask.dueDate = calendar.time // 오늘로 설정
                                }
                            }

                            val childUpdates = hashMapOf<String, Any?>()
                            childUpdates["tasks/${currentTask.id}/dueDate"] = currentTask.dueDate
                            childUpdates["tasks/${currentTask.id}/repeatCycle"] = currentTask.repeatCycle
                            database.updateChildren(childUpdates)

                            ret
                        }
                        show()
                    }
                }
                // 나의 하루
                3 -> {
                    currentTask.myDay = Calendar.getInstance().time
                    database.child("tasks/${currentTask.id}/myDay").setValue(currentTask.myDay)
                }
                // else 에 들어올 수 없다.
                else -> {
                    assert(false)
                }
            }
        }

        holder.binding.taskDateItemDelete.setOnClickListener {
            when (position) {
                0 -> {
                    currentTask.notification = null
                    // deleteAlarm
                    // notification 이 취소되지 않는다... 임시로 AlarmReceiver 에서 처리함
                    val notificationManager: NotificationManager =
                        parentContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(0)

                    database.child("tasks/${currentTask.id}/notification").removeValue()
                }
                1 -> {
                    currentTask.dueDate = null
                    currentTask.repeatCycle = null


                    val childUpdates = hashMapOf<String, Any?>()
                    childUpdates["tasks/${currentTask.id}/dueDate"] = currentTask.dueDate
                    childUpdates["tasks/${currentTask.id}/repeatCycle"] = currentTask.repeatCycle
                    database.updateChildren(childUpdates)
                }
                2 -> {
                    currentTask.repeatCycle = null
                    database.child("tasks/${currentTask.id}/repeatCycle").removeValue()
                }
                3 -> {
                    currentTask.myDay = null
                    database.child("tasks/${currentTask.id}/myDay").removeValue()
                }
                else -> assert(false)
            }
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

    // 알람설정
    private fun setAlarm(calendar: Calendar) {
        val simpleDateFormat = SimpleDateFormat(
            "yyyy년 MM월 dd일 EE요일 a hh시 mm분",
            Locale.getDefault()
        )
        val dateTimeMessage = simpleDateFormat.format(calendar.time)
        Toast.makeText(parentContext, dateTimeMessage, Toast.LENGTH_SHORT).show()

        // Preference 에 설정한 값 저장
        val editor = parentContext.getSharedPreferences(
            "alarm",
            AppCompatActivity.MODE_PRIVATE
        ).edit()
        editor.putLong("nextNotifyTime", calendar.timeInMillis)
        editor.apply()

        val receiver = ComponentName(parentContext, DeviceBootReceiver::class.java)
        val alarmIntent = Intent(parentContext, AlarmReceiver::class.java)
        alarmIntent.putExtra("taskId", currentTask.id)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                parentContext,
                0,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val alarmManager: AlarmManager =
            parentContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }

        parentContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        );
    }

    fun update(task: Task) {
        currentTask = task
        notifyDataSetChanged()
    }
}