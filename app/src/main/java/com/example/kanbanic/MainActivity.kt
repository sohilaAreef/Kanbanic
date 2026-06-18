package com.example.kanbanic

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.kanbanic.data.model.Project
import com.example.kanbanic.data.model.Task
import com.example.kanbanic.ui.auth.AuthContract
import com.example.kanbanic.ui.auth.AuthPresenter
import com.example.kanbanic.ui.auth.AuthScreen
import com.example.kanbanic.ui.board.AddTaskDialog
import com.example.kanbanic.ui.board.BoardContract
import com.example.kanbanic.ui.board.BoardPresenter
import com.example.kanbanic.ui.board.BoardScreen
import com.example.kanbanic.ui.board.TaskDetailBottomSheet
import com.example.kanbanic.ui.project.*
import com.example.kanbanic.ui.theme.KanbanicTheme

class MainActivity : ComponentActivity(), AuthContract.View, ProjectContract.View, BoardContract.View {

    private val authPresenter = AuthPresenter()
    private val projectPresenter = ProjectPresenter()
    private val boardPresenter = BoardPresenter()

    private var currentScreenState by mutableStateOf<AppScreen>(AppScreen.Auth)
    private var projectsState by mutableStateOf<List<Project>>(emptyList())
    private var boardState by mutableStateOf<Pair<Project, List<Task>>?>(null)
    
    // UI States for Dialogs and Overlays
    private var selectedTask by mutableStateOf<Task?>(null)
    private var showAddTaskDialogByColumnId by mutableStateOf<String?>(null)
    private var isShowCreateProjectDialog by mutableStateOf(false)
    private var isShowJoinProjectDialog by mutableStateOf(false)
    private var isShowAddColumnDialog by mutableStateOf(false)
    private var isShowInviteMemberDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authPresenter.attachView(this)
        projectPresenter.attachView(this)
        boardPresenter.attachView(this)

        enableEdgeToEdge()
        setContent {
            KanbanicTheme {
                when (val screen = currentScreenState) {
                    is AppScreen.Auth -> AuthScreen(authPresenter)
                    is AppScreen.ProjectList -> ProjectScreen(
                        projects = projectsState,
                        onProjectClick = { boardPresenter.loadBoard(it) },
                        onCreateProject = { isShowCreateProjectDialog = true },
                        onJoinProject = { isShowJoinProjectDialog = true }
                    )
                    is AppScreen.Board -> {
                        boardState?.let { (project, tasks) ->
                            BoardScreen(
                                project = project,
                                tasks = tasks,
                                onAddTask = { colId -> showAddTaskDialog(colId) },
                                onAddColumn = { isShowAddColumnDialog = true },
                                onInviteMember = { isShowInviteMemberDialog = true },
                                onTaskClick = { showTaskDetails(it) },
                                onBack = { currentScreenState = AppScreen.ProjectList }
                            )
                        }
                    }
                }

                // --- Overlays ---

                selectedTask?.let { task ->
                    TaskDetailBottomSheet(
                        task = task,
                        onDismiss = { selectedTask = null },
                        onAddComment = { text ->
                            boardPresenter.addComment(task.id, text)
                            Toast.makeText(this, "Comment sent (Simulation)", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                showAddTaskDialogByColumnId?.let { columnId ->
                    AddTaskDialog(
                        onDismiss = { showAddTaskDialogByColumnId = null },
                        onConfirm = { title, desc ->
                            boardState?.first?.id?.let { projectId ->
                                boardPresenter.addTask(projectId, columnId, title, desc)
                            }
                            showAddTaskDialogByColumnId = null
                        }
                    )
                }

                if (isShowCreateProjectDialog) {
                    CreateProjectDialog(
                        onDismiss = { isShowCreateProjectDialog = false },
                        onConfirm = { name, desc ->
                            projectPresenter.createProject(name, desc)
                            isShowCreateProjectDialog = false
                        }
                    )
                }

                if (isShowJoinProjectDialog) {
                    JoinProjectDialog(
                        onDismiss = { isShowJoinProjectDialog = false },
                        onConfirm = { code ->
                            projectPresenter.joinProject(code)
                            isShowJoinProjectDialog = false
                        }
                    )
                }

                if (isShowAddColumnDialog) {
                    AddColumnDialog(
                        onDismiss = { isShowAddColumnDialog = false },
                        onConfirm = { name ->
                            boardState?.first?.id?.let { projectId ->
                                boardPresenter.addColumn(projectId, name)
                            }
                            isShowAddColumnDialog = false
                        }
                    )
                }

                if (isShowInviteMemberDialog) {
                    InviteTeamDialog(
                        onDismiss = { isShowInviteMemberDialog = false },
                        onConfirm = { email ->
                            boardState?.first?.id?.let { projectId ->
                                boardPresenter.inviteMember(projectId, email)
                            }
                            isShowInviteMemberDialog = false
                            Toast.makeText(this, "Invite sent to $email", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // --- View Implementations ---
    override fun showLoading() {}
    override fun hideLoading() {}
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Auth
    override fun navigateToDashboard() {
        currentScreenState = AppScreen.ProjectList
        projectPresenter.loadProjects()
    }

    // Project
    override fun showProjects(projects: List<Project>) {
        projectsState = projects
    }
    override fun navigateToProjectBoard(projectId: String) {
        boardPresenter.loadBoard(projectId)
    }
    override fun showCreateProjectDialog() { isShowCreateProjectDialog = true }
    override fun showJoinProjectDialog() { isShowJoinProjectDialog = true }

    // Board
    override fun showBoard(project: Project, tasks: List<Task>) {
        boardState = project to tasks
        currentScreenState = AppScreen.Board(project.id)
    }
    override fun showAddTaskDialog(columnId: String) { showAddTaskDialogByColumnId = columnId }
    override fun showTaskDetails(task: Task) { selectedTask = task }
    override fun showAddColumnDialog() { isShowAddColumnDialog = true }
    override fun showInviteMemberDialog() { isShowInviteMemberDialog = true }

    override fun onDestroy() {
        super.onDestroy()
        authPresenter.detachView()
        projectPresenter.detachView()
        boardPresenter.detachView()
    }
}

sealed class AppScreen {
    object Auth : AppScreen()
    object ProjectList : AppScreen()
    data class Board(val projectId: String) : AppScreen()
}
