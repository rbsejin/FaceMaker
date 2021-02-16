package com.example.facemaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import java.text.SimpleDateFormat
import java.util.*

// id를 넘기지 않고 객체를 직접 넘기면 어떨까?
class TaskDateAdapter(private val currentTaskId: Long) :
    Adapter<TaskDateAdapter.TaskDateViewHolder>() {
    //private val taskDateList: List<TaskDateItem>
    // 객체가 아닌 id를 넘겨서 getTaskForId() 를 사용하게 되어
    // Task 를 찾는데 비용 발생하고, null 체크 문제까지 있다.
    private var currentTask: Task = TaskManager.getTaskForId(currentTaskId)!!
    private lateinit var parentContext: Context

    /*init {
        var notificationItem: TaskDateItem = if (currentTask.notificationDateTime != null) {
            TaskDateItem(
                R.drawable.baseline_notifications_none_black_24,
                currentTask.notificationDateTime.toString(), true
            )
        } else {
            TaskDateItem(
                R.drawable.baseline_notifications_none_black_24,
                "미리 알림", false
            )
        }

        var deadlineItem: TaskDateItem = if (currentTask.deadline != null) {
            TaskDateItem(
                R.drawable.baseline_calendar_today_black_24,
                currentTask.deadline.toString(), true
            )
        } else {
            TaskDateItem(
                R.drawable.baseline_calendar_today_black_24,
                "기한 설정", false
            )
        }

        var repeatCycle: TaskDateItem = if (currentTask.repeatCycle != null) {
            TaskDateItem(
                R.drawable.baseline_repeat_black_24,
                currentTask.repeatCycle.toString(), true
            )
        } else {
            TaskDateItem(
                R.drawable.baseline_repeat_black_24,
                "반복", false
            )
        }

        taskDateList = listOf(notificationItem, deadlineItem, repeatCycle)
    }*/

    class TaskDateViewHolder(itemView: View, private val currentTask: Task) :
        RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.task_date_item_icon)
        val contentTextView: TextView = itemView.findViewById(R.id.task_date_item_content)
        val deleteButton: ImageButton = itemView.findViewById(R.id.task_date_item_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskDateViewHolder {
        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_date_item, parent, false)
        return TaskDateViewHolder(view, currentTask)
    }

    override fun onBindViewHolder(holder: TaskDateViewHolder, position: Int) {        
        //holder.bind(taskDateList[position], position)

        // bind
        holder.apply {
            when (position) {
                0 -> {
                    val dateFormat = SimpleDateFormat("yy년 MM월 dd일 HH시 알림")
                    if (currentTask.notificationDateTime == null) {
                        holder.contentTextView.text = "알림 설정"
                        deleteButton.visibility = RecyclerView.INVISIBLE
                    } else {
                        contentTextView.text = dateFormat.format(currentTask.notificationDateTime)
                        deleteButton.visibility = RecyclerView.VISIBLE
                    }

                    icon.setImageResource(R.drawable.baseline_notifications_none_black_24)
                }
                1 -> {
                    val dateFormat = SimpleDateFormat("yy년 MM월 dd일 까지")
                    if (currentTask.deadline == null) {
                        contentTextView.text = "기한 설정"
                        deleteButton.visibility = RecyclerView.INVISIBLE
                    } else {
                        contentTextView.text = dateFormat.format(currentTask.deadline)
                        deleteButton.visibility = RecyclerView.VISIBLE
                    }

                    icon.setImageResource(R.drawable.baseline_calendar_today_black_24)
                }
                2 -> {
                    if (currentTask.repeatCycle == null) {
                        contentTextView.text = "반복"
                        deleteButton.visibility = RecyclerView.INVISIBLE
                    } else {
                    }

                    icon.setImageResource(R.drawable.baseline_repeat_black_24)
                }
                else -> {
                    assert(false)
                }
            }
        }

        // 알림, 기한, 반복 아이템 클릭했을 때 이벤트 처리
        holder.itemView.setOnClickListener {
            val calendar = Calendar.getInstance()
/*            val now: Long = System.currentTimeMillis()
            calendar.time = Date(now)*/

            when (position) {
                // 미리 알림
                0 -> {
                    PopupMenu(parentContext, holder.itemView).apply {
                        menuInflater.inflate(R.menu.date_item_menu, menu)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.today_later_item -> {
                                    calendar.add(Calendar.HOUR, 3)
                                    currentTask.notificationDateTime = calendar.time
                                    notifyDataSetChanged()
                                    true
                                }
                                R.id.tomorrow_item -> {
                                    calendar.add(Calendar.DATE, 1)
                                    calendar.set(Calendar.HOUR, 9)
                                    //calendar.set(Calendar.MINUTE, 0)
                                    currentTask.notificationDateTime = calendar.time
                                    notifyDataSetChanged()
                                    true
                                }
                                R.id.next_week_item -> {
                                    calendar.add(Calendar.DATE, 7)
                                    calendar.set(Calendar.HOUR, 9)
                                    currentTask.notificationDateTime = calendar.time
                                    notifyDataSetChanged()
                                    true
                                }
                                R.id.direct_selection_item -> {
                                    notifyDataSetChanged()
                                    true
                                }
                                else -> false
                            }
                        }
                        show()
                    }
                }
                // 기한 설정
                1 -> {
                    PopupMenu(parentContext, holder.itemView).apply {
                        menuInflater.inflate(R.menu.date_item_menu, menu)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.today_later_item -> {
                                    currentTask.deadline = calendar.time
                                    notifyDataSetChanged()
                                    true
                                }
                                R.id.tomorrow_item -> {
                                    calendar.add(Calendar.DATE, 1)
                                    currentTask.deadline = calendar.time
                                    notifyDataSetChanged()
                                    true
                                }
                                R.id.next_week_item -> {
                                    calendar.add(Calendar.DATE, 7)
                                    currentTask.deadline = calendar.time
                                    notifyDataSetChanged()
                                    true
                                }
                                R.id.direct_selection_item -> {
                                    notifyDataSetChanged()
                                    true
                                }
                                else -> false
                            }
                        }
                        show()
                    }
                }
                // 반복
                2 -> {
                }
                // else 에 들어올 수 없다.
                else -> {
                    assert(false)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}

data class TaskDateItem(
    @DrawableRes
    val image: Int?,
    var defaultContent: String,
    var canDelete: Boolean
)