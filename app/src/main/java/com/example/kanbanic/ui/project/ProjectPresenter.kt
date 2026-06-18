package com.example.kanbanic.ui.project

import com.example.kanbanic.data.model.Project

class ProjectPresenter : ProjectContract.Presenter {
    private var view: ProjectContract.View? = null

    override fun attachView(view: ProjectContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadProjects() {
        // Simulate fetching projects from Firestore
        val dummyProjects = listOf(
            Project(id = "1", name = "Makarya App", description = "Project management mobile application"),
            Project(id = "2", name = "Freelance UI", description = "UI/UX design for client")
        )
        view?.showProjects(dummyProjects)
    }

    override fun createProject(name: String, description: String) {
        // Logic to save to Firestore would go here
        loadProjects()
    }

    override fun joinProject(inviteCode: String) {
        // Logic to join via invite code
        view?.navigateToProjectBoard("new_project_id")
    }
}
