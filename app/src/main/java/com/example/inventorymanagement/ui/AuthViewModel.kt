package com.example.inventorymanagement.ui

import androidx.lifecycle.*
import com.example.inventorymanagement.data.User
import com.example.inventorymanagement.data.UserRepository
import com.example.inventorymanagement.data.UserSession
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthViewModel(
    private val repository: UserRepository,
    private val session: UserSession
) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private val _navigationEvent = MutableLiveData<NavigationEvent?>(null)
    val navigationEvent: LiveData<NavigationEvent?> = _navigationEvent

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = repository.getUserByEmail(email)
            if (user != null && user.passwordHash == hashPassword(password)) {
                session.saveSession(user.email, user.name)
                _authState.value = AuthState.Success
                _navigationEvent.value = NavigationEvent.NavigateToDashboard
            } else {
                _authState.value = AuthState.Error("Invalid email or password")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            if (repository.isEmailExists(email)) {
                _authState.value = AuthState.Error("Email already registered")
                return@launch
            }

            val user = User(name = name, email = email, passwordHash = hashPassword(password))
            repository.insertUser(user)
            session.saveSession(user.email, user.name)
            _authState.value = AuthState.Success
            _navigationEvent.value = NavigationEvent.NavigateToDashboard
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    fun isLoggedIn() = session.isLoggedIn()

    fun logout() {
        session.logout()
        _authState.value = AuthState.Idle
        _navigationEvent.value = NavigationEvent.NavigateToLogin
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class NavigationEvent {
        object NavigateToDashboard : NavigationEvent()
        object NavigateToLogin : NavigationEvent()
    }
}

class AuthViewModelFactory(
    private val repository: UserRepository,
    private val session: UserSession
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, session) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
