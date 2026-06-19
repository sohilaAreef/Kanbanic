package com.example.kanbanic.ui.auth

interface AuthContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun navigateToDashboard()
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loginWithEmail(email: String, pass: String)
        fun signInWithGoogleIdToken(idToken: String)
        fun signUp(name: String, email: String, pass: String, confirmPass: String)
    }
}