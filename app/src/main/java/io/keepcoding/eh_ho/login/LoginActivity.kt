package io.keepcoding.eh_ho.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.databinding.ActivityLoginBinding
import io.keepcoding.eh_ho.di.DIProvider
import io.keepcoding.eh_ho.topics.TopicsActivity

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val signInFragment: SignInFragment by lazy { SignInFragment.newInstance() }
    private val signUpFragment: SignUpFragment by lazy { SignUpFragment.newInstance() }
    private val vm: LoginViewModel by viewModels { DIProvider.loginViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        vm.state.observe(this) {
            when (it) {
                is LoginViewModel.State.SignIn -> moveTo(signInFragment)
                is LoginViewModel.State.SigningIn -> { }
                is LoginViewModel.State.SignedIn -> navigateToTopics()
                is LoginViewModel.State.SignUp -> moveTo(signUpFragment)
                is LoginViewModel.State.SigningUp -> { }
                is LoginViewModel.State.SignedUp -> navigateToTopics()
            }
        }
        vm.loading.observe(this) {
            binding.viewLoading.root.isVisible = it
        }
    }

    private fun moveTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    private fun navigateToTopics() {
        startActivity(TopicsActivity.createIntent(this))
        finish()
    }
}