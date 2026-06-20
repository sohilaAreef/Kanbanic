package com.example.kanbanic.ui.project

import com.example.kanbanic.data.FirebaseRepository

class ProjectPresenter : ProjectContract.Presenter {
    private var view: ProjectContract.View? = null

    override fun attachView(view: ProjectContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadProjects() {
        view?.showLoading()
        FirebaseRepository.getProjects(
            onSuccess = { projects ->
                view?.hideLoading()
                view?.showProjects(projects)
            },
            onFailure = { error ->
                view?.hideLoading()
                view?.showError(error.message ?: "Failed to load projects")
            }
        )
    }

    override fun createProject(name: String, description: String) {
        view?.showLoading()
        FirebaseRepository.addProject(
            name, description,
            onSuccess = {
                view?.hideLoading()
                loadProjects()
            },
            onFailure = { error ->
                view?.hideLoading()
                view?.showError(error.message ?: "Failed to create project")
            }
        )
    }

    override fun joinProject(inviteCode: String) {
        // logic for joining project via invite code
    }
}
