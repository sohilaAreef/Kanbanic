package com.example.kanbanic.data

import com.example.kanbanic.data.model.*

object MockDataRepository {
    private val projects = mutableListOf(
        Project(id = "1", name = "Makarya App", description = "Project management mobile application", 
            columns = listOf(Column("1", "To Do", 0), Column("2", "In Progress", 1), Column("3", "Done", 2)),
            members = listOf("u1", "u2")),
        Project(id = "2", name = "Freelance UI", description = "UI/UX design for client",
            columns = listOf(Column("1", "To Do", 0), Column("2", "In Progress", 1), Column("3", "Done", 2)),
            members = listOf("u1"))
    )

    private val users = listOf(
        User("u1", "Alice", "alice@example.com"),
        User("u2", "Bob", "bob@example.com"),
        User("u3", "Charlie", "charlie@example.com")
    )

    private val tasks = mutableListOf(
        Task("t1", "1", "1", "Design Landing Page", "Create UI for the main page", priority = TaskPriority.HIGH, importance = TaskImportance.VERY_IMPORTANT),
        Task("t2", "1", "1", "Setup Firebase", "Configure Auth and Firestore", priority = TaskPriority.URGENT, importance = TaskImportance.VERY_IMPORTANT),
        Task("t3", "1", "2", "Implement MVP", "Structure the app using MVP pattern", priority = TaskPriority.MEDIUM, importance = TaskImportance.IMPORTANT),
        Task("t4", "1", "3", "Fix Theme Colors", "Ensure theme matches the mockups", priority = TaskPriority.LOW, importance = TaskImportance.NOT_IMPORTANT)
    )

    fun getProjects() = projects.toList()

    fun addProject(name: String, description: String) {
        val newId = (projects.size + 1).toString()
        projects.add(Project(
            id = newId, 
            name = name, 
            description = description,
            columns = listOf(Column("1", "To Do", 0), Column("2", "In Progress", 1), Column("3", "Done", 2))
        ))
    }

    fun getProjectById(id: String) = projects.find { it.id == id }

    fun getTasksByProject(projectId: String) = tasks.filter { it.projectId == projectId }

    fun getUsers() = users
    fun getUsersByIds(ids: List<String>) = users.filter { it.id in ids }

    fun addTask(
        projectId: String, 
        columnId: String, 
        title: String, 
        description: String, 
        priority: TaskPriority = TaskPriority.MEDIUM, 
        importance: TaskImportance = TaskImportance.IMPORTANT,
        dueDate: Long? = null,
        assigneeId: String? = null,
        color: String? = null
    ) {
        val newId = "t${tasks.size + 1}"
        tasks.add(Task(
            id = newId, 
            projectId = projectId, 
            columnId = columnId, 
            title = title, 
            description = description, 
            priority = priority, 
            importance = importance,
            dueDate = dueDate,
            assigneeId = assigneeId,
            color = color
        ))
    }

    fun updateTaskColumn(taskId: String, newColumnId: String) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            tasks[index] = tasks[index].copy(columnId = newColumnId)
        }
    }

    fun addColumn(projectId: String, name: String) {
        val projectIndex = projects.indexOfFirst { it.id == projectId }
        if (projectIndex != -1) {
            val project = projects[projectIndex]
            val newColumn = Column(id = (project.columns.size + 1).toString(), name = name, order = project.columns.size)
            projects[projectIndex] = project.copy(columns = project.columns + newColumn)
        }
    }

    fun updateProjectBackground(projectId: String, color: String) {
        val index = projects.indexOfFirst { it.id == projectId }
        if (index != -1) {
            projects[index] = projects[index].copy(background = color)
        }
    }

    fun updateColumnColor(projectId: String, columnId: String, color: String) {
        val projectIndex = projects.indexOfFirst { it.id == projectId }
        if (projectIndex != -1) {
            val project = projects[projectIndex]
            val updatedColumns = project.columns.map { 
                if (it.id == columnId) it.copy(color = color) else it
            }
            projects[projectIndex] = project.copy(columns = updatedColumns)
        }
    }
}
