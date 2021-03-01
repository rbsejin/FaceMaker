package com.example.facemaker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class TaskStepAdapter(
    private val currentTask: Task,
) :
    RecyclerView.Adapter<TaskStepAdapter.TaskStepViewHolder>() {
    private lateinit var parentContext: Context
    private val stepList: MutableList<String> = currentTask.stepList

    class TaskStepViewHolder(
        itemView: View
    ) :
        RecyclerView.ViewHolder(itemView) {
        val iconImageView = itemView.findViewById<ImageView>(R.id.item_icon)
        val nameTextView = itemView.findViewById<EditText>(R.id.item_name)
        val deleteButton: ImageButton = itemView.findViewById(R.id.item_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskStepViewHolder {
        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.step_item, parent, false)

        return TaskStepViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskStepViewHolder, position: Int) {
        if (position == stepList.size) {
            holder.deleteButton.visibility = RecyclerView.INVISIBLE
            holder.iconImageView.setImageResource(R.drawable.baseline_add_24)
            holder.nameTextView.hint = "다음단계"
            holder.nameTextView.setText("")
        } else {
            holder.deleteButton.visibility = RecyclerView.VISIBLE
            holder.iconImageView.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            holder.nameTextView.setText(stepList[position])
        }

        holder.nameTextView.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                Log.d("focus", "$position 포커스 잃음")

                if (position == stepList.size) {
                    val text: String = holder.nameTextView.text.toString()
                    if (text.isNotEmpty()) {
                        stepList.add(text)
                        ProjectManager.save(parentContext.filesDir)
                        notifyDataSetChanged()
                    }
                } else {
                    val text: String = holder.nameTextView.text.toString()
                    if (text.isEmpty()) {
                        stepList.removeAt(position)
                        notifyDataSetChanged()
                    } else {
                        stepList[position] = text
                    }
                }
            } else {
                Log.d("focus", "$position 포커스 얻음")
            }
        }

        holder.nameTextView.setOnEditorActionListener { v, actionId, event ->
            holder.nameTextView.clearFocus()
            true
        }

        // 삭제 버튼 클릭했을 때
        holder.deleteButton.setOnClickListener {
            // 파일 추가 아이템에서는 삭제버튼을 클릭 불가
            assert(position < stepList.size)

            holder.nameTextView.clearFocus()
            // 파일 아이템 삭제한다.
            currentTask.stepList.removeAt(position)

            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return stepList.size + 1
    }
}