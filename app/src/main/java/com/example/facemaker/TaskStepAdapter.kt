package com.example.facemaker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.StepItemBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TaskStepAdapter(
    private var currentTask: Task,
) :
    RecyclerView.Adapter<TaskStepAdapter.ViewHolder>() {
    private lateinit var parentContext: Context
    private val stepList: MutableList<String> = currentTask.stepList

    class ViewHolder(val binding: StepItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StepItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == stepList.size) {
            holder.binding.itemDeleteButton.visibility = RecyclerView.INVISIBLE
            holder.binding.itemIcon.setImageResource(R.drawable.baseline_add_24)
            holder.binding.itemNameText.hint = "다음단계"
            holder.binding.itemNameText.setText("")
        } else {
            holder.binding.itemDeleteButton.visibility = RecyclerView.VISIBLE
            holder.binding.itemIcon.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            holder.binding.itemNameText.setText(stepList[position])
        }

        holder.binding.itemNameText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                Log.d("focus", "$position 포커스 잃음")

                if (position == stepList.size) {
                    val text: String = holder.binding.itemNameText.text.toString()
                    if (text.isNotEmpty()) {
                        stepList.add(text)
                    }
                } else {
                    val text: String = holder.binding.itemNameText.text.toString()
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

            Firebase.database.reference.child("tasks/${currentTask.id}/stepList").setValue(stepList)
        }

        holder.binding.itemNameText.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    holder.binding.itemNameText.clearFocus()
                    true
                }
                else -> false
            }

            Firebase.database.reference.child("tasks/${currentTask.id}/stepList").setValue(stepList)
            true
        }

        // 삭제 버튼 클릭했을 때
        holder.binding.itemDeleteButton.setOnClickListener {
            // 파일 추가 아이템에서는 삭제버튼을 클릭 불가
            assert(position < stepList.size)

            holder.binding.itemNameText.clearFocus()
            // 파일 아이템 삭제한다.
            stepList.removeAt(position)

            Firebase.database.reference.child("tasks/${currentTask.id}/stepList").setValue(stepList)
        }
    }

    override fun getItemCount(): Int {
        return stepList.size + 1
    }

    fun update(task: Task) {
        currentTask = task
        notifyDataSetChanged()
    }
}