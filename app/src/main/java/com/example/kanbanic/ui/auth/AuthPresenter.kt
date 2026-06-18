package com.example.kanbanic.ui.auth

class AuthPresenter : AuthContract.Presenter {
    private var view: AuthContract.View? = null

    override fun attachView(view: AuthContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loginWithEmail(email: String, pass: String) {
        view?.showLoading()
        // Simulate network call
        // In real implementation, Firebase Auth would be used here
        view?.hideLoading()
        view?.navigateToDashboard()
    }

    override fun loginWithGoogle() {
        view?.showLoading()
        // Simulate Google Sign-In
        view?.hideLoading()
        view?.navigateToDashboard()
    }

    override fun signUp(email: String, pass: String) {
        view?.showLoading()
        // Simulate signup
        view?.hideLoading()
        view?.navigateToDashboard()
    }
}
