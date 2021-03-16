package com.example.facemaker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.data.Step
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.StepItemBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TaskStepAdapter(
    private var currentTask: Task,
) :
    RecyclerView.Adapter<TaskStepAdapter.ViewHolder>() {
    private lateinit var parentContext: Context
    private val stepList: MutableList<Step> = currentTask.stepList

    class ViewHolder(val binding: StepItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun getCheckButton() : ToggleButton {
            return binding.itemIcon
        }

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
            holder.binding.itemIcon.visibility = View.INVISIBLE
            holder.binding.itemDeleteButton.visibility = RecyclerView.INVISIBLE
            holder.binding.itemNameText.hint = "다음단계"
            holder.binding.itemNameText.setText("")
        } else {
            holder.binding.itemIcon.visibility = View.VISIBLE
            holder.binding.itemIcon.isChecked = stepList[position].isCompleted
            holder.binding.itemDeleteButton.visibility = RecyclerView.VISIBLE
            holder.binding.itemNameText.setText(stepList[position].name)
        }

        holder.binding.itemNameText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                Log.d("focus", "$position 포커스 잃음")

                if (position == stepList.size) {
                    val text: String = holder.binding.itemNameText.text.toString()
                    if (text.isNotEmpty()) {
                        stepList.add(Step(text))
                    }
                } else {
                    val text: String = holder.binding.itemNameText.text.toString()
                    if (text.isEmpty()) {
                        stepList.removeAt(position)
                        notifyDataSetChanged()
                    } else {
                        stepList[position] = Step(text)
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

        // 체크 버튼 클릭했을 때
        holder.getCheckButton().setOnClickListener {
            if (position == stepList.size) {
                return@setOnClickListener
            }

            stepList[position].isCompleted = !stepList[position].isCompleted
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