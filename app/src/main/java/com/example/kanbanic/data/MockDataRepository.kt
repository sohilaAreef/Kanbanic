package com.example.kanbanic.data

import com.example.kanbanic.data.model.*

object MockDataRepository {
    private val projects = mutableListOf(
        Project(
            id = "1", 
            name = "Makarya App", 
            description = "Project management mobile application",
            columns = listOf(
                Column("1", "To Do", 0), 
                Column("2", "In Progress", 1), 
                Column("3", "Done", 2)
            ),
            members = listOf("u1", "u2", "u3")
        ),
        Project(
            id = "2", 
            name = "Freelance UI", 
            description = "UI/UX design for client",
            columns = listOf(
                Column("1", "To Do", 0), 
                Column("2", "In Progress", 1), 
                Column("3", "Done", 2)
            ),
            members = listOf("u1")
        )
    )

    private val tasks = mutableListOf(
        Task("t1", "1", "1", "Design Landing Page", "Create UI for the main page", priority = TaskPriority.HIGH),
        Task("t2", "1", "1", "Setup Firebase", "Configure Auth and Firestore", priority = TaskPriority.URGENT),
        Task("t3", "1", "2", "Implement MVP", "Structure the app using MVP pattern", priority = TaskPriority.MEDIUM),
        Task("t4", "1", "3", "Fix Theme Colors", "Ensure theme matches the mockups", priority = TaskPriority.LOW)
    )

    private val users = mutableListOf(
        User(id = "u1", name = "Demo User", email = "demo@kanbanic.com"),
        User(id = "u2", name = "Alice", email = "alice@kanbanic.com"),
        User(id = "u3", name = "Bob", email = "bob@kanbanic.com")
    )

    fun getProjects() = projects.toList()

    fun getUsersByIds(ids: List<String>): List<User> = users.filter { it.id in ids }

    fun addProject(name: String, description: String) {
        val newId = (projects.size + 1).toString()
        projects.add(Project(
            id = newId,
            name = name,
            description = description,
            columns = listOf(
                Column("1", "To Do", 0), 
                Column("2", "In Progress", 1), 
                Column("3", "Done", 2)
            ),
            members = listOf("u1") // Default owner as member
        ))
    }

    fun getProjectById(id: String) = projects.find { it.id == id }

    fun getTasksByProject(projectId: String) = tasks.filter { it.projectId == projectId }

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

    fun updateTaskColor(taskId: String, color: String) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            tasks[index] = tasks[index].copy(color = color)
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

    // --- Auth (mock, swap for Firebase Auth later) ---

    fun isEmailTaken(email: String): Boolean =
        users.any { it.email.equals(email, ignoreCase = true) }

    fun registerUser(name: String, email: String): User {
        val newUser = User(id = "u${users.size + 1}", name = name, email = email)
        users.add(newUser)
        return newUser
    }

    fun getUserByEmail(email: String): User? =
        users.find { it.email.equals(email, ignoreCase = true) }
}
