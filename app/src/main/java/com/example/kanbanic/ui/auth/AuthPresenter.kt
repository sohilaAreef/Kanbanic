package com.example.kanbanic.ui.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class AuthPresenter : AuthContract.Presenter {
    private var view: AuthContract.View? = null
    private val auth = FirebaseAuth.getInstance()

    override fun attachView(view: AuthContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loginWithEmail(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            view?.showError("Please enter your email and password")
            return
        }

        view?.showLoading()
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                view?.hideLoading()
                if (task.isSuccessful) {
                    view?.navigateToDashboard()
                } else {
                    view?.showError(task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    override fun signInWithGoogleIdToken(idToken: String) {
        view?.showLoading()
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                view?.hideLoading()
                if (task.isSuccessful) {
                    view?.navigateToDashboard()
                } else {
                    view?.showError(task.exception?.localizedMessage ?: "Google sign-in failed")
                }
            }
    }

    override fun signUp(name: String, email: String, pass: String, confirmPass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            view?.showError("Please fill in all fields")
            return
        }

        if (!email.contains("@") || !email.contains(".")) {
            view?.showError("Please enter a valid email")
            return
        }

        if (pass.length < 6) {
            view?.showError("Password must be at least 6 characters")
            return
        }

        if (pass != confirmPass) {
            view?.showError("Passwords do not match")
            return
        }

        view?.showLoading()
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    task.result?.user?.updateProfile(profileUpdate)

                    view?.hideLoading()
                    view?.navigateToDashboard()
                } else {
                    view?.hideLoading()
                    view?.showError(task.exception?.localizedMessage ?: "Sign up failed")
                }
            }
    }
}