package com.example.facemaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.facemaker.databinding.PlannedHeaderItemBinding

class PlannedHeaderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder private constructor(private val binding: PlannedHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.planedGroupClose.setOnClickListener {
                Toast.makeText(this.binding.root.context, "close", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind() {
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlannedHeaderItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 1
    }
}
