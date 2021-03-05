//package com.example.facemaker
//
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.ToggleButton
//import androidx.databinding.BindingAdapter
//import com.example.facemaker.data.Task
//
///*
//taskItem
// */
//
//@BindingAdapter("taskNameString") // value 는 xml 에서 쓴다.
//fun TextView.setTaskNameString(item: Task?) { // binding.task = task 으로 쓰인다.
//    item?.let {
//        text = item.name
//    }
//}
//
//@BindingAdapter("taskCheckButton")
//fun ToggleButton.setTaskCheckButton(item: Task?) {
//    item?.let {
//        isChecked = item.isDone
//    }
//}
//
//@BindingAdapter("taskImportantButton")
//fun ToggleButton.setTaskImportantButton(item: Task?) {
//    item?.let {
//        isChecked = item.isImportant
//    }
//}
//
//@BindingAdapter("headerArrowImage")
//fun ImageView.setHeaderArrowImage(item: Header?) {
//    item?.let {
//        val drawableResource = if (item.isOpen) R.drawable.ic_keyboard_arrow_down_24px else R.drawable.ic_keyboard_arrow_right_24px
//        setImageResource(drawableResource)
//    }
//}
//
//@BindingAdapter("headerNameAndCountText")
//fun TextView.setHeaderNameAndCountText(item: Header?) {
//    item?.let {
//        text = "${item.name} ${item.childCount}"
//    }
//}
