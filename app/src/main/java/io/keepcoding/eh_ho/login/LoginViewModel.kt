package io.keepcoding.eh_ho.login

import android.util.Patterns
import androidx.lifecycle.*
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.model.LogIn
import io.keepcoding.eh_ho.model.LogUp
import io.keepcoding.eh_ho.repository.Repository
import java.util.regex.Pattern

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
    private val usernamePattern: Pattern = Regex("[a-zA-Z0-9]{5,}").toPattern()
    private val passwordPattern: Pattern =
        Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!¡%*#¿?&])[A-Za-z\\d@\$!¡%*#¿?&]{8,}\$").toPattern()

    private val _state: MutableLiveData<State> =
        MutableLiveData<State>().apply { postValue(State.SignIn) }

    private val _mesagge: MutableLiveData<Int> = MutableLiveData<Int>()

    private val _signInData = MutableLiveData<SignInData>().apply { postValue(SignInData("", "")) }
    private val _signInValidationError =
        MutableLiveData<SignInValidationError>().apply { postValue(SignInValidationError()) }

    private val _signUpData =
        MutableLiveData<SignUpData>().apply { postValue(SignUpData("", "", "", "")) }
    private val _signUpValidationError =
        MutableLiveData<SignUpValidationError>().apply { postValue(SignUpValidationError()) }

    val state: LiveData<State> = _state

    val signInData: LiveData<SignInData> = _signInData
    val signInValidationError: LiveData<SignInValidationError> = _signInValidationError

    val signUpData: LiveData<SignUpData> = _signUpData
    val signUpValidationError: LiveData<SignUpValidationError> = _signUpValidationError

    val signInEnabled: LiveData<Boolean> =
        Transformations.map(_signInData) { it?.isValid() ?: false }
    val signUpEnabled: LiveData<Boolean> =
        Transformations.map(_signUpData) { it?.isValid() ?: false }
    val message: LiveData<Int> = _mesagge
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
            if (isSignInDataValid(it)) {
                _state.postValue(State.SigningIn)
                repository.signIn(it.userName, it.password) { it ->
                    if (it is LogIn.Success) {
                        _state.postValue(State.SignedIn)
                    } else {
                        _state.postValue(State.SignIn)
                        _mesagge.postValue(R.string.incorrect_username_or_password)
                    }
                }
            }
        }
    }

    private fun isSignInDataValid(signInData: SignInData): Boolean {
        val validationError = SignInValidationError()
        with(signInData) {
            validationError.isUsernameValid = usernamePattern.matcher(userName).matches()
            validationError.isPasswordValid = passwordPattern.matcher(password).matches()
        }
        _signInValidationError.postValue(validationError)
        with(validationError) {
            return isUsernameValid && isPasswordValid
        }
    }

    fun signUp() {
        signUpData.value?.takeIf { it.isValid() }?.let {
            if (isSignUpDataValid(it)) {
                _state.postValue(State.SigningUp)
                repository.signup(it.userName, it.email, it.password) {
                    if (it is LogUp.Success) {
                        _state.postValue(State.SignIn)
                        _mesagge.postValue(R.string.please_log_in_message)
                    } else {
                        _state.postValue(State.SignUp)
                        _mesagge.postValue(R.string.error_signing_up)
                    }
                }
            }
        }
    }

    private fun isSignUpDataValid(signUpData: SignUpData): Boolean {
        val validationError = SignUpValidationError()
        with(signUpData) {
            validationError.isEmailValid = emailPattern.matcher(email).matches()
            validationError.isUsernameValid = usernamePattern.matcher(userName).matches()
            validationError.isPasswordValid = passwordPattern.matcher(password).matches()
        }
        _signUpValidationError.postValue(validationError)
        with(validationError) {
            return isEmailValid && isUsernameValid && isPasswordValid
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

    data class SignInValidationError(
        var isUsernameValid: Boolean = true,
        var isPasswordValid: Boolean = true
    )

    data class SignUpData(
        val email: String,
        val userName: String,
        val password: String,
        val confirmPassword: String,
    )

    data class SignUpValidationError(
        var isEmailValid: Boolean = true,
        var isUsernameValid: Boolean = true,
        var isPasswordValid: Boolean = true
    )

    class LoginViewModelProviderFactory(private val repository: Repository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
            LoginViewModel::class.java -> LoginViewModel(repository) as T
            else -> throw IllegalArgumentException("LoginViewModelFactory can only create instances of the LoginViewModel")
        }
    }
}

private fun LoginViewModel.SignInData.isValid(): Boolean =
    userName.isNotBlank() && password.isNotBlank()

private fun LoginViewModel.SignUpData.isValid(): Boolean = userName.isNotBlank() &&
        email.isNotBlank() &&
        password == confirmPassword &&
        password.isNotBlank()