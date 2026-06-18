package com.example.kanbanic.ui.board

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
        // Mocking project and tasks for UI preparation
        // In real Firebase: Use Firestore listener here (onSnapshot)
        val project = Project(
            id = projectId,
            name = "Kanbanic Project",
            columns = listOf(
                Column("1", "To Do", 0),
                Column("2", "In Progress", 1),
                Column("3", "Done", 2)
            ),
            inviteCode = "MAKAR-123"
        )

        val tasks = listOf(
            Task(
                id = "t1", 
                projectId = projectId, 
                columnId = "1", 
                title = "Design Landing Page", 
                description = "Create UI for the main page", 
                priority = TaskPriority.HIGH,
                activities = listOf(ActivityLog(text = "Project created", timestamp = System.currentTimeMillis()))
            ),
            Task("t2", projectId, "1", "Setup Firebase", "Configure Auth and Firestore", priority = TaskPriority.URGENT),
            Task("t3", projectId, "2", "Implement MVP", "Structure the app using MVP pattern", priority = TaskPriority.MEDIUM),
            Task("t4", projectId, "3", "Fix Theme Colors", "Ensure theme matches the mockups", priority = TaskPriority.LOW)
        )

        view?.showBoard(project, tasks)
    }

    override fun updateTaskColumn(taskId: String, toColumnId: String, newIndex: Int) {
        // Firebase: update task.columnId and task.positionIndex
        // Since it's UI only, we just reload or expect the listener to trigger showBoard
    }

    override fun addTask(projectId: String, columnId: String, title: String, description: String) {
        // Firebase: db.collection("tasks").add(...)
        loadBoard(projectId)
    }

    override fun addColumn(projectId: String, name: String) {
        // Firebase: update project.columns list
        loadBoard(projectId)
    }

    override fun inviteMember(projectId: String, email: String) {
        // Firebase: logic to send invite or add user email to project.members
        // For UI: show success
    }
    
    override fun addComment(taskId: String, text: String) {
        // Firebase: update task.comments
    }
}
