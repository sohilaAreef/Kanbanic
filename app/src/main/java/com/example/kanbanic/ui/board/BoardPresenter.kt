package com.example.kanbanic.ui.board

import com.example.kanbanic.data.FirebaseRepository
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
        FirebaseRepository.getProjectById(projectId,
            onSuccess = { project ->
                if (project != null) {
                    FirebaseRepository.getTasksByProject(projectId,
                        onSuccess = { tasks ->
                            view?.showBoard(project, tasks)
                            
                            // Fetch real member data and filter out the current user
                            FirebaseRepository.getUsersByIds(project.members,
                                onSuccess = { allMembers ->
                                    val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                                    val otherMembers = allMembers.filter { it.id != currentUserId }
                                    view?.showProjectMembers(otherMembers)
                                },
                                onFailure = { /* Silently fail for members */ }
                            )
                        },
                        onFailure = { error ->
                            view?.showError(error.message ?: "Failed to load tasks")
                        }
                    )
                } else {
                    view?.showError("Project not found")
                }
            },
            onFailure = { error ->
                view?.showError(error.message ?: "Failed to load project")
            }
        )
    }

    override fun updateTaskColumn(taskId: String, toColumnId: String, newIndex: Int) {
        FirebaseRepository.updateTaskColumn(taskId, toColumnId,
            onSuccess = { /* Snapshot listener will trigger loadBoard indirectly or we can reload */ },
            onFailure = { error -> view?.showError(error.message ?: "Failed to update task") }
        )
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
        FirebaseRepository.addTask(projectId, columnId, title, description, priority, importance, dueDate, assigneeId, color,
            onSuccess = { /* Snapshot listener handles update */ },
            onFailure = { error -> view?.showError(error.message ?: "Failed to add task") }
        )
    }

    override fun addColumn(projectId: String, name: String) {
        FirebaseRepository.addColumn(projectId, name,
            onSuccess = { /* Snapshot listener handles update */ },
            onFailure = { error -> view?.showError(error.message ?: "Failed to add column") }
        )
    }

    override fun inviteMember(projectId: String, email: String) {
        FirebaseRepository.inviteMember(projectId, email,
            onSuccess = { /* Success message in UI */ },
            onFailure = { error -> view?.showError(error.message ?: "Failed to invite member") }
        )
    }
    
    override fun addComment(taskId: String, text: String) {
        FirebaseRepository.addComment(taskId, text,
            onSuccess = { /* Success */ },
            onFailure = { error -> view?.showError(error.message ?: "Failed to add comment") }
        )
    }

    override fun updateProjectBackground(projectId: String, color: String) {
        FirebaseRepository.updateProjectBackground(projectId, color,
            onSuccess = { /* Success */ },
            onFailure = { error -> view?.showError(error.message ?: "Failed to update background") }
        )
    }

    override fun updateColumnColor(projectId: String, columnId: String, color: String) {
        FirebaseRepository.updateColumnColor(projectId, columnId, color,
            onSuccess = { /* Success */ },
            onFailure = { error -> view?.showError(error.message ?: "Failed to update column color") }
        )
    }
}
