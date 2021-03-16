package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.facemaker.data.Project
import com.example.facemaker.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import timber.log.Timber


const val EXTRA_MESSAGE = "com.example.facemaker.MESSAGE"
const val TASK_LIST_TYPE = "task list type"
const val PROJECT_ID = "project id"
const val PROJECT_NAME = "project name"
const val ADD_PROJECT = "add project"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val projects = mutableListOf<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        database = Firebase.database.reference
        auth = Firebase.auth

        // 프로필 정보 업데이트
        updateProfileUI()

        val recyclerView = findViewById<RecyclerView>(R.id.project_list_recycler_view)
        val headerAdapter = ProjectHeaderAdapter { type -> headItemOnClick(type) }
        val projectAdapter = ProjectAdapter(projects) { project -> adapterOnClick(project) }
        recyclerView.adapter = ConcatAdapter(headerAdapter, projectAdapter)

        val projectListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Project object and use the values to update the UI

                projects.clear()

                for (projectSnapshot in dataSnapshot.children) {
                    val project = projectSnapshot.getValue<Project>()
                    if (project == null) {
                        if (BuildConfig.DEBUG) {
                            error("must not be null")
                        }

                        continue
                    }

                    // 추후 공유할 때 호스트 아이디가 아니라 참가자 아이디로 변경해야함
                    if (project.hostUserId == auth.currentUser.uid) {
                        projects.add(project)
                    }
                }

                projectAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Project failed, log a message
                Timber.w(databaseError.toException())
            }
        }
        database.child("projects").addValueEventListener(projectListener)

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

        binding.profileItem.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.addProjectButton.setOnClickListener {
            // taskListActivity 이동
            val intent = Intent(this, TaskListActivity::class.java)
            intent.putExtra("add project", true)
            startActivity(intent)
        }

        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun updateProfileUI() {
        binding.profileUserNameText.text = auth.currentUser.displayName
        binding.profileEmailText.text = auth.currentUser.email

        auth.currentUser.photoUrl?.let {
            Glide.with(applicationContext)
                .load(it)
                .circleCrop()
                .into(binding.profileImage)
        }
    }

    /* Opens ProjectDetailActivity when RecyclerView item is clicked. */
    private fun adapterOnClick(project: Project) {
        val intent = Intent(this, TaskListActivity()::class.java)
        intent.putExtra(PROJECT_ID, project.id)
        intent.putExtra(PROJECT_NAME, project.name)
        startActivity(intent)
    }

    private fun headItemOnClick(type: Int) {
        when (type) {
            0 -> {
                // 오늘 할 일
                val intent = Intent(this, MyDayActivity()::class.java)
                startActivity(intent)
            }
            1 -> {
                // 중요
                val intent = Intent(this, TaskListActivity()::class.java)
                startActivity(intent)
            }
            2 -> {
                // 계획된 일정
                val intent = Intent(this, PlannedActivity()::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        updateProfileUI()
    }
}