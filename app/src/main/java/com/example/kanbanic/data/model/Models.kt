package com.example.kanbanic.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String? = null
)

data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val columns: List<Column> = emptyList(),
    val members: List<String> = emptyList(), // List of user IDs
    val ownerId: String = "",
    val inviteCode: String = "",
    val background: String? = null // Hex color or image URL
)

data class Column(
    val id: String = "",
    val name: String = "",
    val order: Int = 0,
    val color: String? = null // Hex color for column background
)

data class Task(
    val id: String = "",
    val projectId: String = "",
    val columnId: String = "",
    val title: String = "",
    val description: String = "",
    val assigneeId: String? = null,
    val dueDate: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val importance: TaskImportance = TaskImportance.IMPORTANT,
    val color: String? = null, // Hex color for the card
    val positionIndex: Int = 0,
    val comments: List<Comment> = emptyList(),
    val activities: List<ActivityLog> = emptyList()
)

data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ActivityLog(
    val id: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH, URGENT
}

enum class TaskImportance {
    NOT_IMPORTANT, IMPORTANT, VERY_IMPORTANT
}
