package com.example.ss3_app_new

object TaskStorage {
    private val taskList: MutableList<Task> = mutableListOf()

    fun addTask(task: Task) {
        taskList.add(task)
    }

    fun updateTask(oldTask: Task, newTask: Task) {
        val index = taskList.indexOf(oldTask)
        if (index != -1) {
            taskList[index] = newTask
        }
    }

    fun deleteTask(task: Task) {
        taskList.remove(task)
    }

    fun getTasks(): List<Task> {
        return taskList
    }

    fun getTasksSortedByDate(): List<Task> {
        return taskList.sortedBy { it.dateTime }
    }

    fun clearTasks() {
        taskList.clear()
    }
}
