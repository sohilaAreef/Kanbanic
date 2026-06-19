package com.example.kanbanic.ui.board

import com.example.kanbanic.data.MockDataRepository
import com.example.kanbanic.data.model.*

class BoardPresenter : BoardContract.Presenter {
    private var view: BoardContract.View? = null

    override fun attachView(view: BoardContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadBoard(projectId: String) {
        val project = MockDataRepository.getProjectById(projectId)
        val tasks = MockDataRepository.getTasksByProject(projectId)

        if (project != null) {
            view?.showBoard(project, tasks)
        } else {
            view?.showError("Project not found")
        }
    }

    override fun updateTaskColumn(taskId: String, toColumnId: String, newIndex: Int) {
        MockDataRepository.updateTaskColumn(taskId, toColumnId)
        val project = MockDataRepository.getProjects().find { p -> 
            MockDataRepository.getTasksByProject(p.id).any { it.id == taskId }
        }
        project?.let { loadBoard(it.id) }
    }

    override fun addTask(
        projectId: String, 
        columnId: String, 
        title: String, 
        description: String, 
        priority: TaskPriority, 
        importance: TaskImportance,
        dueDate: Long?,
        assigneeId: String?,
        color: String?
    ) {
        MockDataRepository.addTask(projectId, columnId, title, description, priority, importance, dueDate, assigneeId, color)
        loadBoard(projectId)
    }

    override fun addColumn(projectId: String, name: String) {
        MockDataRepository.addColumn(projectId, name)
        loadBoard(projectId)
    }

    override fun inviteMember(projectId: String, email: String) {
        // Firebase: logic to send invite or add user email to project.members
        // For UI: show success
    }
    
    override fun addComment(taskId: String, text: String) {
        // Firebase: update task.comments
    }

    override fun updateProjectBackground(projectId: String, color: String) {
        MockDataRepository.updateProjectBackground(projectId, color)
        loadBoard(projectId)
    }

    override fun updateColumnColor(projectId: String, columnId: String, color: String) {
        MockDataRepository.updateColumnColor(projectId, columnId, color)
        loadBoard(projectId)
    }
}
