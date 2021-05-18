package io.keepcoding.eh_ho.login

import androidx.lifecycle.*
import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.repository.Repository

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData<State>().apply { postValue(State.SignIn) }
    private val _signInData = MutableLiveData<SignInData>().apply { postValue(SignInData("", "")) }
    private val _signUpData = MutableLiveData<SignUpData>().apply { postValue(SignUpData("", "", "", "")) }
    val state: LiveData<State> = _state
    val signInData: LiveData<SignInData> = _signInData
    val signUpData: LiveData<SignUpData> = _signUpData
    val signInEnabled: LiveData<Boolean> = Transformations.map(_signInData) { it?.isValid() ?: false }
    val signUpEnabled: LiveData<Boolean> = Transformations.map(_signUpData) { it?.isValid() ?: false }
    val loading: LiveData<Boolean> = Transformations.map(_state) {
        when (it) {
            State.SignIn,
            State.SignedIn,
            State.SignUp,
            State.SignedUp -> false
            State.SigningIn,
            State.SigningUp -> true
        }
    }

    fun onNewSignInUserName(userName: String) {
        onNewSignInData(_signInData.value?.copy(userName = userName))
    }

    fun onNewSignInPassword(password: String) {
        onNewSignInData(_signInData.value?.copy(password = password))
    }

    fun onNewSignUpUserName(userName: String) {
        onNewSignUpData(_signUpData.value?.copy(userName = userName))
    }

    fun onNewSignUpEmail(email: String) {
        onNewSignUpData(_signUpData.value?.copy(email = email))
    }

    fun onNewSignUpPassword(password: String) {
        onNewSignUpData(_signUpData.value?.copy(password = password))
    }

    fun onNewSignUpConfirmPassword(confirmPassword: String) {
        onNewSignUpData(_signUpData.value?.copy(confirmPassword = confirmPassword))
    }

    private fun onNewSignInData(signInData: SignInData?) {
        signInData?.takeUnless { it == _signInData.value }?.let(_signInData::postValue)
    }

    private fun onNewSignUpData(signUpData: SignUpData?) {
        signUpData?.takeUnless { it == _signUpData.value }?.let(_signUpData::postValue)
    }

    fun moveToSignIn() {
        _state.postValue(State.SignIn)
    }

    fun moveToSignUp() {
        _state.postValue(State.SignUp)
    }

    fun signIn() {
        signInData.value?.takeIf { it.isValid() }?.let {
            repository.signIn(it.userName, it.password) {
                if (it is LogIn.Success) {
                    _state.postValue(State.SignedIn)
                } else {
                    //
                }
            }
        }
    }

    fun signUp() {
        signUpData.value?.takeIf { it.isValid() }?.let {
            repository.signup(it.userName, it.email, it.password) {
                //
            }
        }
    }

    sealed class State {
        object SignIn : State()
        object SigningIn : State()
        object SignedIn : State()
        object SignUp : State()
        object SigningUp : State()
        object SignedUp : State()
    }

    data class SignInData(
        val userName: String,
        val password: String,
    )

    data class SignUpData(
        val email: String,
        val userName: String,
        val password: String,
        val confirmPassword: String,
    )

    class LoginViewModelProviderFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(repository) as T
            else -> throw IllegalArgumentException("LoginViewModelFactory can only create instances of the LoginViewModel")
        }
    }
}


private fun LoginViewModel.SignInData.isValid(): Boolean = userName.isNotBlank() && password.isNotBlank()
private fun LoginViewModel.SignUpData.isValid(): Boolean = userName.isNotBlank() &&
        email.isNotBlank() &&
        password == confirmPassword &&
        password.isNotBlank()
