package com.example.kanbanic.ui.project

import com.example.kanbanic.data.MockDataRepository

class ProjectPresenter : ProjectContract.Presenter {
    private var view: ProjectContract.View? = null

    override fun attachView(view: ProjectContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadProjects() {
        view?.showProjects(MockDataRepository.getProjects())
    }

    override fun createProject(name: String, description: String) {
        MockDataRepository.addProject(name, description)
        loadProjects()
    }

    override fun joinProject(inviteCode: String) {
        // Mock join
        view?.navigateToProjectBoard("1")
    }
}
