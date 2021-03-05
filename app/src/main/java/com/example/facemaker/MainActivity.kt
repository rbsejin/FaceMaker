package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.facemaker.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


const val EXTRA_MESSAGE = "com.example.facemaker.MESSAGE"
const val PROJECT_ID = "project id"

class MainActivity : AppCompatActivity() {
//    private val projectDetailRequestCode = 1
//    private val newProjectActivityRequestCode = 2
//    private val headItemRequestCode = 3
//    private lateinit var projectAdapter: ProjectAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 프로필 정보 업데이트
        updateProfileUI()


//        ProjectManager.load(filesDir)
//
        val recyclerView = findViewById<RecyclerView>(R.id.project_list_recycler_view)
        val headerAdapter = ProjectHeaderAdapter { type -> headItemOnClick(type) }
        //projectAdapter = ProjectAdapter { project -> adapterOnClick(project) }
        recyclerView.adapter = ConcatAdapter(headerAdapter/*, projectAdapter*/)

        // delete to swipe
//        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
//            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//        ) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return projectAdapter.swapProejcts(
//                    viewHolder.adapterPosition,
//                    target.adapterPosition
//                )
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                projectAdapter.removeProject(viewHolder.adapterPosition)
//            }
//        }).apply {
//            attachToRecyclerView((recyclerView))
//        }
//
//        val bottomButton: View = findViewById(R.id.project_bottom)
//        bottomButton.setOnClickListener {
//            addButtonOnClick()
//        }

        val profile: ConstraintLayout = findViewById(R.id.profile_item)
        profile.setOnClickListener {
            // 로그아웃
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    startActivity(Intent(this, FirebaseUIActivity()::class.java))
                    finish()
                }

//            // 계정 삭제
//            AuthUI.getInstance()
//                .delete(this)
//                .addOnCompleteListener {
//                    // ...
//                }
        }
    }

    private fun updateProfileUI() {
        binding.profileUserNameText.text = Firebase.auth.currentUser.displayName
        binding.profileEmailText.text = Firebase.auth.currentUser.email

        Firebase.auth.currentUser.photoUrl?.let {
            Glide.with(applicationContext)
                .load(it)
                .circleCrop()
                .into(binding.profileImage)
        }
    }

    //    /* Opens ProjectDetailActivity when RecyclerView item is clicked. */
//    private fun adapterOnClick(project: Project) {
//        val intent = Intent(this, TaskListActivity()::class.java)
//        intent.putExtra(PROJECT_ID, project.id)
//        startActivityForResult(intent, projectDetailRequestCode)
//    }
//
    private fun headItemOnClick(type: Int) {
        when (type) {
//            0 -> {
//                // 오늘 할 일
//                val intent = Intent(this, TodayTaskListActivity()::class.java)
//                startActivityForResult(intent, headItemRequestCode)
//            }
//            1 -> {
//                // 중요
//                val intent = Intent(this, ImportantTaskListActivity()::class.java)
//                startActivityForResult(intent, headItemRequestCode)
//            }
//            2 -> {
//                // 계획된 일정
//                val intent = Intent(this, PlannedScheduleActivity()::class.java)
//                startActivityForResult(intent, headItemRequestCode)
//            }
        }
    }
//
//    private fun addButtonOnClick() {
//        val intent = Intent(this, AddProjectActivity::class.java)
//        startActivityForResult(intent, newProjectActivityRequestCode)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == newProjectActivityRequestCode && resultCode == Activity.RESULT_OK) {
//            data?.let { data ->
//                val name = data.getStringExtra(PROJECT_NAME)
//                name?.let {
//                    val projectId = ProjectManager.createId()
//                    val project = Project(projectId, name, Calendar.getInstance().time)
//                    ProjectManager.addProject(project)
//                }
//            }
//        } else if (requestCode == projectDetailRequestCode && resultCode == Activity.RESULT_OK) {
//            data?.let { data ->
//                val id = data.getIntExtra(REMOVED_PROJECT_ID, 0)
//                ProjectManager.removeProjectForId(id)
//            }
//        } else if (requestCode == headItemRequestCode && requestCode == Activity.RESULT_OK) {
//
//        }
//
//        val recyclerView: RecyclerView = findViewById(R.id.project_list_recycler_view)
//        (recyclerView.adapter as ConcatAdapter).adapters[1].notifyDataSetChanged()
//        ProjectManager.save(filesDir)
//    }
//
//    override fun onStop() {
//        ProjectManager.save(filesDir)
//        super.onStop()
//    }

    override fun onStart() {
        super.onStart()

        updateProfileUI()
    }
}