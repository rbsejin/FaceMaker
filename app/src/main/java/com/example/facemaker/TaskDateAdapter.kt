package com.example.facemaker

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import java.text.SimpleDateFormat
import java.util.*

class TaskDateAdapter(private val currentTask: Task) :
    Adapter<TaskDateAdapter.TaskDateViewHolder>() {
    private lateinit var parentContext: Context

    class TaskDateViewHolder(itemView: View, private val currentTask: Task) :
        RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.task_date_item_icon)
        val nameTextView: TextView = itemView.findViewById(R.id.task_date_item_name)
        val deleteButton: ImageButton = itemView.findViewById(R.id.task_date_item_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskDateViewHolder {
        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_date_item, parent, false)
        return TaskDateViewHolder(view, currentTask)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: TaskDateViewHolder, position: Int) {

        // bind
        holder.apply {
            when (position) {
                0 -> {
                    val dateFormat = SimpleDateFormat("yy년 MM월 dd일 HH시 mm분 알림")
                    if (currentTask.notificationDateTime == null) {
                        holder.nameTextView.text = "알림 설정"
                        deleteButton.visibility = RecyclerView.INVISIBLE
                    } else {
                        nameTextView.text = dateFormat.format(currentTask.notificationDateTime)
                        deleteButton.visibility = RecyclerView.VISIBLE
                    }

                    icon.setImageResource(R.drawable.baseline_notifications_none_black_24)
                }
                1 -> {
                    val dateFormat = SimpleDateFormat("yy년 MM월 dd일 까지")
                    if (currentTask.deadline == null) {
                        nameTextView.text = "기한 설정"
                        deleteButton.visibility = RecyclerView.INVISIBLE
                    } else {
                        nameTextView.text = dateFormat.format(currentTask.deadline)
                        deleteButton.visibility = RecyclerView.VISIBLE
                    }

                    icon.setImageResource(R.drawable.baseline_calendar_today_black_24)
                }
                2 -> {
                    if (currentTask.repeatCycle == null) {
                        nameTextView.text = "반복"
                        deleteButton.visibility = RecyclerView.INVISIBLE
                    } else {
                        nameTextView.text = currentTask.repeatCycle
                        deleteButton.visibility = RecyclerView.VISIBLE
                    }

                    icon.setImageResource(R.drawable.baseline_repeat_black_24)
                }
                3 -> {
                    if (currentTask.todayTaskDate != null) {
                        nameTextView.text = "나의 하루에 추가됨"
                        deleteButton.visibility = RecyclerView.VISIBLE
                    } else {
                        nameTextView.text = "나의 하루에 추가"
                        deleteButton.visibility = RecyclerView.INVISIBLE
                    }

                    icon.setImageResource(R.drawable.baseline_calendar_today_24)
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
                    PopupMenu(parentContext, holder.itemView).apply {
                        menuInflater.inflate(R.menu.notification_item_menu, menu)
                        var isAlarm: Boolean = false

                        setOnMenuItemClickListener {
                            val ret = when (it.itemId) {
                                R.id.notification_today_later_item -> {
                                    calendar.add(Calendar.HOUR_OF_DAY, 3)
                                    calendar.set(Calendar.MINUTE, 0)
                                    currentTask.notificationDateTime = calendar.time
                                    setAlarm(calendar)
                                    true
                                }
                                R.id.notification_tomorrow_item -> {
                                    calendar.add(Calendar.DATE, 1)
                                    calendar.set(Calendar.HOUR_OF_DAY, 9)
                                    calendar.set(Calendar.MINUTE, 0)
                                    currentTask.notificationDateTime = calendar.time
                                    setAlarm(calendar)
                                    true
                                }
                                R.id.notification_next_week_item -> {
                                    calendar.add(Calendar.DATE, 7)
                                    calendar.set(Calendar.HOUR_OF_DAY, 9)
                                    calendar.set(Calendar.MINUTE, 0)
                                    currentTask.notificationDateTime = calendar.time
                                    setAlarm(calendar)
                                    true
                                }

                                R.id.notification_direct_selection_item -> {
                                    if (currentTask.notificationDateTime != null) {
                                        calendar.time = currentTask.notificationDateTime
                                    }

                                    val datePickerDialog = DatePickerDialog(
                                        parentContext,
                                        { _, year, month, dayOfMonth ->
                                            calendar.set(Calendar.YEAR, year)
                                            calendar.set(Calendar.MONTH, month)
                                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            currentTask.notificationDateTime = calendar.time
                                            notifyItemChanged(position)

                                            val timePickerDialog = TimePickerDialog(
                                                parentContext,
                                                { _, hourOfDay, minute ->
                                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                    calendar.set(Calendar.MINUTE, minute)

                                                    currentTask.notificationDateTime = calendar.time
                                                    notifyItemChanged(position)
                                                    setAlarm(calendar)
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

                            notifyItemChanged(position)
                            ret
                        }
                        show()
                    }
                }
                // 기한 설정
                1 -> {
                    if (currentTask.deadline != null) {
                        calendar.time = currentTask.deadline
                    }

                    PopupMenu(parentContext, holder.itemView).apply {
                        menuInflater.inflate(R.menu.deadline_item_menu, menu)
                        setOnMenuItemClickListener {
                            val ret = when (it.itemId) {
                                R.id.deadline_today_item -> {
                                    currentTask.deadline = calendar.time
                                    true
                                }
                                R.id.deadline_tomorrow_item -> {
                                    calendar.add(Calendar.DATE, 1)
                                    currentTask.deadline = calendar.time
                                    true
                                }
                                R.id.deadline_next_week_item -> {
                                    calendar.add(Calendar.DATE, 7)
                                    currentTask.deadline = calendar.time
                                    true
                                }
                                R.id.daedline_direct_selection_item -> {
                                    val datePickerDialog = DatePickerDialog(
                                        parentContext,
                                        { _, year, month, dayOfMonth ->
                                            calendar.set(Calendar.YEAR, year)
                                            calendar.set(Calendar.MONTH, month)
                                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                            calendar.set(Calendar.HOUR_OF_DAY, 9)
                                            calendar.set(Calendar.MINUTE, 0)
                                            currentTask.deadline = calendar.time
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

                            notifyItemChanged(position)
                            ret
                        }
                        show()
                    }
                }
                // 반복
                2 -> {
                    PopupMenu(parentContext, holder.itemView).apply {
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
                                if (currentTask.deadline == null) {
                                    currentTask.deadline = calendar.time // 오늘로 설정
                                }

                                notifyDataSetChanged()
                            }

                            ret
                        }
                        show()
                    }
                }
                // 나의 하루
                3 -> {
                    currentTask.todayTaskDate = Calendar.getInstance().time
                    notifyDataSetChanged()
                }
                // else 에 들어올 수 없다.
                else -> {
                    assert(false)
                }
            }
        }

        holder.deleteButton.setOnClickListener {
            when (position) {
                0 -> {
                    currentTask.notificationDateTime = null
                    // deleteAlarm
                    // notification 이 취소되지 않는다... 임시로 AlarmReceiver 에서 처리함
                    val notificationManager: NotificationManager =
                        parentContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(currentTask.id)
                }
                1 -> {
                    currentTask.deadline = null
                    currentTask.repeatCycle = null
                }
                2 -> currentTask.repeatCycle = null
                3 -> currentTask.todayTaskDate = null
                else -> assert(false)
            }

            // 기한 설정 제거하면 반복도 같이 제거
            notifyDataSetChanged()
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
        Toast.makeText(parentContext, dateTimeMessage, Toast.LENGTH_SHORT)
            .show()

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
                currentTask.id,
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

        parentContext.packageManager.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP);
    }
}