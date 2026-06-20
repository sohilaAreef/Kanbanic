package com.example.kanbanic.data

import com.example.kanbanic.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

object FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private const val PROJECTS_COLLECTION = "projects"
    private const val TASKS_COLLECTION = "tasks"
    private const val USERS_COLLECTION = "users"

    fun getProjects(onSuccess: (List<Project>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection(PROJECTS_COLLECTION)
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if (e != null) {
                    onFailure(e)
                    return@addSnapshotListener
                }
                val projects = snapshot?.toObjects(Project::class.java) ?: emptyList<Project>()
                onSuccess(projects)
            }
    }

    fun addProject(name: String, description: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val docRef = db.collection(PROJECTS_COLLECTION).document()
        val project = Project(
            id = docRef.id,
            name = name,
            description = description,
            ownerId = userId,
            members = listOf(userId),
            columns = listOf(
                Column("1", "To Do", 0),
                Column("2", "In Progress", 1),
                Column("3", "Done", 2)
            )
        )
        docRef.set(project)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getProjectById(projectId: String, onSuccess: (Project?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(PROJECTS_COLLECTION).document(projectId)
            .addSnapshotListener { snapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                if (e != null) {
                    onFailure(e)
                    return@addSnapshotListener
                }
                val project = snapshot?.toObject(Project::class.java)
                onSuccess(project)
            }
    }

    fun getTasksByProject(projectId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(TASKS_COLLECTION)
            .whereEqualTo("projectId", projectId)
            .addSnapshotListener { snapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                if (e != null) {
                    onFailure(e)
                    return@addSnapshotListener
                }
                val tasks = snapshot?.toObjects(Task::class.java) ?: emptyList<Task>()
                onSuccess(tasks)
            }
    }

    fun addTask(
        projectId: String,
        columnId: String,
        title: String,
        description: String,
        priority: TaskPriority,
        importance: TaskImportance,
        dueDate: Long?,
        assigneeId: String?,
        color: String?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = db.collection(TASKS_COLLECTION).document()
        val task = Task(
            id = docRef.id,
            projectId = projectId,
            columnId = columnId,
            title = title,
            description = description,
            priority = priority,
            importance = importance,
            dueDate = dueDate,
            assigneeId = assigneeId,
            color = color
        )
        docRef.set(task)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateTaskColumn(taskId: String, toColumnId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(TASKS_COLLECTION).document(taskId)
            .update("columnId", toColumnId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun addColumn(projectId: String, name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(PROJECTS_COLLECTION).document(projectId).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot ->
                val project = snapshot.toObject(Project::class.java) ?: return@addOnSuccessListener
                val newColumn = Column(
                    id = (project.columns.size + 1).toString(),
                    name = name,
                    order = project.columns.size
                )
                db.collection(PROJECTS_COLLECTION).document(projectId)
                    .update("columns", project.columns + newColumn)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateProjectBackground(projectId: String, color: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(PROJECTS_COLLECTION).document(projectId)
            .update("background", color)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateColumnColor(projectId: String, columnId: String, color: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(PROJECTS_COLLECTION).document(projectId).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot ->
                val project = snapshot.toObject(Project::class.java) ?: return@addOnSuccessListener
                val updatedColumns = project.columns.map {
                    if (it.id == columnId) it.copy(color = color) else it
                }
                db.collection(PROJECTS_COLLECTION).document(projectId)
                    .update("columns", updatedColumns)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun inviteMember(projectId: String, email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(USERS_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                val user = snapshot.documents.firstOrNull()?.toObject(User::class.java)
                if (user != null) {
                    db.collection(PROJECTS_COLLECTION).document(projectId)
                        .get()
                        .addOnSuccessListener { projectSnap: DocumentSnapshot ->
                            val project = projectSnap.toObject(Project::class.java)
                            if (project != null && !project.members.contains(user.id)) {
                                db.collection(PROJECTS_COLLECTION).document(projectId)
                                    .update("members", project.members + user.id)
                                    .addOnSuccessListener { onSuccess() }
                                    .addOnFailureListener { onFailure(it) }
                            } else {
                                onSuccess() // Already a member or project not found
                            }
                        }
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun addComment(taskId: String, commentText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.displayName ?: "User"
        val comment = Comment(
            id = System.currentTimeMillis().toString(),
            userId = userId,
            userName = userName,
            text = commentText
        )
        db.collection(TASKS_COLLECTION).document(taskId).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot ->
                val task = snapshot.toObject(Task::class.java) ?: return@addOnSuccessListener
                db.collection(TASKS_COLLECTION).document(taskId)
                    .update("comments", task.comments + comment)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getUsersByIds(ids: List<String>, onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit) {
        if (ids.isEmpty()) {
            onSuccess(emptyList<User>())
            return
        }
        db.collection(USERS_COLLECTION)
            .whereIn("id", ids)
            .get()
            .addOnSuccessListener { snapshot: QuerySnapshot ->
                val users = snapshot.toObjects(User::class.java)
                onSuccess(users)
            }
            .addOnFailureListener { onFailure(it) }
    }
}
