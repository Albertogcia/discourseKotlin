package io.keepcoding.eh_ho.topics

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.keepcoding.eh_ho.model.Topic
import io.keepcoding.eh_ho.repository.Repository
import io.keepcoding.eh_ho.topics.TopicsViewModel.State.TopicsReceived

class TopicsViewModel(private val repository: Repository) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData<State>()

    val state: LiveData<State> = _state

    fun loadTopics() {
        _state.postValue(
            if (_state.value == null) {
                getTopics()
                State.LoadingTopics.Loading
            } else {
                with(_state.value) {
                    when (this) {
                        is TopicsReceived -> State.LoadingTopics.LoadingWithTopics(topics)
                        is State.LoadingTopics -> this
                        else -> State.LoadingTopics.Loading
                    }
                }
            }
        )
    }

    fun getTopics(){
        repository.getTopics {
            it.fold(::onTopicsReceived, ::onTopicsFailure)
        }
    }

    private fun onTopicsReceived(topics: List<Topic>) {
        _state.postValue(
            topics.takeUnless { it.isEmpty() }?.let(::TopicsReceived) ?: State.NoTopics
        )
    }

    private fun onTopicsFailure(throwable: Throwable) {
        _state.postValue(State.NoTopics)
    }

    sealed class State {
        sealed class LoadingTopics : State() {
            object Loading : LoadingTopics()
            data class LoadingWithTopics(val topics: List<Topic>) : LoadingTopics()
        }

        data class TopicsReceived(val topics: List<Topic>) : State()
        object NoTopics : State()
    }

    class TopicsViewModelProviderFactory(private val repository: Repository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
            TopicsViewModel::class.java -> TopicsViewModel(repository) as T
            else -> throw IllegalArgumentException("TopicsViewModelFactory can only create instances of the TopicsViewModel")
        }

    }
}