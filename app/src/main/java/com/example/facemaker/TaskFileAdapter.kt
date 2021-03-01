package com.example.facemaker

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskFileAdapter(
    private val currentTask: Task,
    private val onClickCapture: () -> Unit,
    private val onOpenFile: (String) -> Unit,
    private val onAddFile: () -> Unit
) :
    RecyclerView.Adapter<TaskFileAdapter.TaskFileViewHolder>() {
    private lateinit var parentContext: Context
    val fileList: MutableList<String> = currentTask.fileList
    private val addFile: String = "파일추가"

    class TaskFileViewHolder(
        itemView: View,
        private val onClickCapture: () -> Unit,
        private val onOpenFile: (String) -> Unit,
        private val onAddFile: () -> Unit
    ) :
        RecyclerView.ViewHolder(itemView) {
        val iconImageView = itemView.findViewById<ImageView>(R.id.item_icon)
        val nameTextView = itemView.findViewById<TextView>(R.id.item_name)
        val deleteButton: ImageButton = itemView.findViewById(R.id.item_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskFileViewHolder {
        parentContext = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return TaskFileViewHolder(view, onClickCapture, onOpenFile, onAddFile)
    }

    override fun onBindViewHolder(holder: TaskFileViewHolder, position: Int) {
        val taskFile = if (position == fileList.size) {
            holder.deleteButton.visibility = RecyclerView.INVISIBLE
            holder.iconImageView.setImageResource(R.drawable.baseline_file_present_24)
            addFile
        } else {
            holder.deleteButton.visibility = RecyclerView.VISIBLE
            holder.iconImageView.setImageResource(R.drawable.baseline_photo_black_24)
            fileList[position]
        }

        holder.nameTextView.text = taskFile.substringAfterLast('/')

        // 아이템뷰 클릭했을 때
        holder.itemView.setOnClickListener {
            if (position == fileList.size) {
                // 파일 추가 아이템 클릭했을 때 파일을 추가한다.

                val builder = AlertDialog.Builder(parentContext)
                builder.setTitle("업로드")
                    .setItems(arrayOf("장치 파일", "카메라"),
                        DialogInterface.OnClickListener { dialog, which ->
                            // The 'which' argument contains the index position
                            // of the selected item
                            when (which) {
                                0 -> {
                                    // 장치 파일
                                    //currentTask.fileList.add("file${fileList.size + 1}")
                                    onAddFile()
                                }
                                1 -> {
                                    // 카메라
                                    onClickCapture()
                                }
                            }
                        })
                builder.create().show()


            } else {
                // 파일 아이템 선택했을 때 파일을 연다
                onOpenFile(taskFile)
            }
        }

        // 삭제 버튼 클릭했을 때
        holder.deleteButton.setOnClickListener {
            // 파일 추가 아이템에서는 삭제버튼을 클릭 불가
            assert(position < fileList.size)

            // 파일 아이템 삭제한다.
            currentTask.fileList.removeAt(position)

            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return fileList.size + 1
    }
}