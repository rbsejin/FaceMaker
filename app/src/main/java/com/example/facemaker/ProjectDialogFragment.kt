package com.example.facemaker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment

class ProjectDialogFragment(private val projectName: String) : DialogFragment() {
    private lateinit var listener: ProjectCreationDialogListener

    interface ProjectCreationDialogListener {
        fun onDialogPositiveClick(projectName: String)
        fun onDialogNegativeClick(isDelete: Boolean)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_project, null)
            val projectNameText: EditText = view.findViewById(R.id.project_name_text)
            projectNameText.setText(projectName)

            val projectDialogTitle: TextView = view.findViewById(R.id.project_title_text)
            projectDialogTitle.text = tag

            val positiveButtonText: String = when (tag) {
                NEW_PROJECT_TAG -> getString(R.string.add)
                UPDATE_PROJECT_NAME_TAG -> getString(R.string.save)
                else -> throw ClassCastException("Unknown tag $tag")
            }

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(positiveButtonText,
                    DialogInterface.OnClickListener { dialog, id ->
                        val projectNameText: EditText = view.findViewById(R.id.project_name_text)
                        listener.onDialogPositiveClick(projectNameText.text.toString())
                    })
                .setNegativeButton(getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialog, id ->
                        val isDeleted = when (tag) {
                            NEW_PROJECT_TAG -> true
                            UPDATE_PROJECT_NAME_TAG -> false
                            else -> throw ClassCastException("Unknown tag $tag")
                        }

                        listener.onDialogNegativeClick(isDeleted)
                    })

            val alertDialog = builder.create()
            alertDialog.show() // show를 호출 안하면 positiveButton이 null이 됨.

            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.isEnabled = projectNameText.text.toString().isNotEmpty()

            projectNameText.addTextChangedListener {
                positiveButton.isEnabled =
                    projectNameText.text.toString().isNotEmpty()
            }

            projectNameText.setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        positiveButton.callOnClick()
                        true
                    }
                    else -> false
                }

                true
            }

            alertDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as ProjectCreationDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement ProjectCreationDialogListener")
            )
        }
    }

    companion object {
        const val NEW_PROJECT_TAG = "새 프로젝트"
        const val UPDATE_PROJECT_NAME_TAG = "프로젝트 이름 바꾸기"
    }
}

