package com.example.kanbanic.ui.board

import com.example.kanbanic.data.model.Project
import com.example.kanbanic.data.model.Task

interface BoardContract {
    interface View {
        fun showBoard(project: Project, tasks: List<Task>)
        fun showAddTaskDialog(columnId: String)
        fun showTaskDetails(task: Task)
        fun showAddColumnDialog()
        fun showInviteMemberDialog()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadBoard(projectId: String)
        fun updateTaskColumn(taskId: String, toColumnId: String, newIndex: Int)
        fun addTask(
            projectId: String, 
            columnId: String, 
            title: String, 
            description: String, 
            priority: com.example.kanbanic.data.model.TaskPriority, 
            importance: com.example.kanbanic.data.model.TaskImportance,
            dueDate: Long? = null,
            assigneeId: String? = null,
            color: String? = null
        )
        fun addColumn(projectId: String, name: String)
        fun inviteMember(projectId: String, email: String)
        fun addComment(taskId: String, text: String)
        fun updateProjectBackground(projectId: String, color: String)
        fun updateColumnColor(projectId: String, columnId: String, color: String)
    }
}
