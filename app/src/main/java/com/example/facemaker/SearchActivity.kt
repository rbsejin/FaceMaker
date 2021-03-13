package com.example.facemaker

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.facemaker.data.Task
import com.example.facemaker.databinding.ActivitySearchBinding
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


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var database: DatabaseReference
    private lateinit var taskAdapter: SearchTaskAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var searchView: SearchView

    private val tasks = mutableListOf<Task>()

    private var isShowCompletedTask = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        database = Firebase.database.reference
        auth = Firebase.auth

        taskAdapter = SearchTaskAdapter(TaskListener { task -> adapterOnClick(task) })
        binding.searchRecyclerView.adapter = taskAdapter

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun adapterOnClick(task: Task) {
        val intent = Intent(this, TaskDetailActivity()::class.java)
        intent.putExtra(TASK_ID, task.id)
        //startActivity(intent)
        startActivityForResult(intent, taskDetailRequestCode)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        searchView = menu!!.findItem(R.id.menu_search)!!.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.isIconified = false // searchView 를 펼친다.
        searchView.maxWidth = 800 // 최대 너비를 설정하지 않았을 경우 옵션버튼이 사라진다.


        // 검색창 이벤트 처리
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateList()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                updateList()
                return true
            }
        })


        // DB 변경 시 목록 업데이트
        database.child("tasks").orderByChild("userId")
            .equalTo(auth.currentUser.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tasks.clear()

                    for (taskSnapshot in snapshot.children) {
                        val task: Task = taskSnapshot.getValue<Task>() ?: continue
                        tasks.add(task)
                    }

                    updateList()
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.i(error.message)
                }
            })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.completed_tasks -> {
                isShowCompletedTask = !isShowCompletedTask
                updateList()
                taskAdapter.notifyDataSetChanged()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.completed_tasks)

        item?.let {
            if (isShowCompletedTask) {
                it.title = getString(R.string.hide_completion_tasks)
            } else {
                it.title = getString(R.string.show_completion_tasks)
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    // 검색한 내용을 DB에서 가져온다.
    private fun updateList() {
        if (searchView.query.isEmpty()) {
            taskAdapter.itemList = listOf()
        } else {
            taskAdapter.itemList =
                tasks.filter {
                    it.name.contains(searchView.query)
                            && (isShowCompletedTask || it.completionDateTime == null)
                }
        }

        taskAdapter.notifyDataSetChanged()
    }
}