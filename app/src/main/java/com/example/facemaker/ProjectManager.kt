//package com.example.facemaker
//
//import com.example.facemaker.data.Project
//import com.example.facemaker.data.Task
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import java.io.*
//import java.util.*
//
//object ProjectManager {
//    private val projectList = mutableListOf<Project>()
//
//    fun addProject(project: Project) {
//        projectList.add(project)
//    }
//
//    fun removeProject(project: Project): Boolean {
//        return projectList.remove(project)
//    }
//
//    fun removeProjectForId(id: Int): Boolean {
//        return projectList.removeAll { it.id == id }
//    }
//
//    fun getProjectForId(id: Int): Project? {
//        return projectList.firstOrNull { it.id == id }
//    }
//
//    fun getProjectList(): List<Project> {
//        return projectList
//    }
//
//    fun getTaskForId(id: Int): Task? {
//        for (project in projectList) {
//            for (task in project.getTaskList()) {
//                if (task.id == id) {
//                    return task
//                }
//            }
//        }
//
//        return null
//    }
//
//    fun createId(): Int {
//        if (projectList.size == 0) {
//            return 1
//        }
//
//        return projectList[projectList.size - 1].id + 1
//    }
//
//    fun load(filesDir: File) {
//        try {
//            val file = File(filesDir, "data.json")
//            val inputStream = FileInputStream(file)
//            val stringBuilder = StringBuilder()
//
//            if (inputStream != null) {
//                val inputStreamReader = InputStreamReader(inputStream)
//                val bufferedReader = BufferedReader(inputStreamReader)
//
//                bufferedReader.forEachLine {
//                    stringBuilder.append(it)
//                }
//
//                inputStream.close()
//
//                val gson = Gson()
//                val output = stringBuilder.toString()
//                val projectType = object : TypeToken<List<Project>>() {}.type
//                val projectList = gson.fromJson<List<Project>>(output, projectType)
//
//                this.projectList.clear()
//                this.projectList.addAll(projectList)
//                assert(this.projectList.size == projectList.size)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun save(filesDir: File) {
//        // to json
//        try {
//            val projectList = ProjectManager.getProjectList()
//            val gson = Gson()
//            val strJson = gson.toJson(projectList)
//
//            // write
//            val file = File(filesDir, "data.json")
//            val fileOutputStream = FileOutputStream(file)
//
//            fileOutputStream.write(strJson.toByteArray())
//
//            fileOutputStream.flush()
//            fileOutputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun removeAt(position: Int): Boolean {
//        return projectList.removeAt(position) != null
//    }
//
//    fun getTodayTaskList(): MutableList<Task> {
//        val todayTaskList = mutableListOf<Task>()
//        for (project in projectList) {
//            for (task in project.getTaskList()) {
//                if (task.todayTaskDate == null) {
//                    continue
//                }
//
//                val calendar = Calendar.getInstance()
//                val currentYear = calendar.get(Calendar.YEAR)
//                val currentMonth = calendar.get(Calendar.MONTH)
//                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_MONTH)
//
//                Calendar.getInstance().time = task.todayTaskDate
//                val year = calendar.get(Calendar.YEAR)
//                val month = calendar.get(Calendar.MONTH)
//                val dayOfWeek = calendar.get(Calendar.DAY_OF_MONTH)
//
//                if (year == currentYear &&
//                    month == currentMonth &&
//                    dayOfWeek == currentDayOfWeek
//                ) {
//                    todayTaskList.add(task)
//                }
//            }
//        }
//
//        return todayTaskList
//    }
//
//    fun getImportantTaskList(): MutableList<Task> {
//        val importantTaskList = mutableListOf<Task>()
//        for (project in projectList) {
//            for (task in project.getTaskList()) {
//                if (task.isImportant) {
//                    importantTaskList.add(task)
//                }
//            }
//        }
//
//        return importantTaskList
//    }
//
//    fun getPlanedScheduleTaskList(): MutableList<Task> {
//        val taskList = mutableListOf<Task>()
//        for (project in projectList) {
//            val list = project.getTaskList().filter { it.deadline != null}
//            taskList.addAll(list)
//        }
//
//        return taskList
//    }
//
//    fun removeTaskForId(id: Int) {
//        for (project in projectList) {
//            for (task in project.getTaskList()) {
//                if (task.id == id) {
//                    project.removeTask(task)
//                    return
//                }
//            }
//        }
//    }
//}