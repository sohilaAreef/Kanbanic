package com.example.kanbanic.ui.project

import com.example.kanbanic.data.model.Project

interface ProjectContract {
    interface View {
        fun showProjects(projects: List<Project>)
        fun navigateToProjectBoard(projectId: String)
        fun showCreateProjectDialog()
        fun showJoinProjectDialog()
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadProjects()
        fun createProject(name: String, description: String)
        fun joinProject(inviteCode: String)
    }
}
