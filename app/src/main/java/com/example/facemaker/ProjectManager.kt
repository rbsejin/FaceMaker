package com.example.facemaker

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

object ProjectManager {
    private val projectList = mutableListOf<Project>()

    fun addProject(project: Project) {
        projectList.add(project)
    }

    fun removeProject(project: Project): Boolean {
        return projectList.remove(project)
    }

    fun removeProjectForId(id: Long): Boolean {
        return projectList.removeAll { it.id == id }
    }

    fun getProjectForId(id: Long): Project? {
        return projectList.firstOrNull { it.id == id }
    }

    fun getProjectList(): List<Project> {
        return projectList
    }

    fun createId(): Long {
        if (projectList.size == 0) {
            return 1
        }

        return projectList[projectList.size - 1].id + 1
    }

    fun load(filesDir: File) {
        try {
            val file = File(filesDir, "data.json")
            val inputStream = FileInputStream(file)
            val stringBuilder = StringBuilder()

            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                bufferedReader.forEachLine {
                    stringBuilder.append(it)
                }

                inputStream.close()

                val gson = Gson()
                val output = stringBuilder.toString()
                val projectType = object : TypeToken<List<Project>>() {}.type
                val projectList = gson.fromJson<List<Project>>(output, projectType)

                this.projectList.addAll(projectList)
                assert(this.projectList.size == projectList.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save(filesDir: File) {
        // to json
        try {
            val projectList = ProjectManager.getProjectList()
            val gson = Gson()
            val strJson = gson.toJson(projectList)

            // write
            val file = File(filesDir, "data.json")
            val fileOutputStream = FileOutputStream(file)

            fileOutputStream.write(strJson.toByteArray())

            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}