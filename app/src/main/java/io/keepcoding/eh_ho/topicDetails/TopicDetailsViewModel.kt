package io.keepcoding.eh_ho.topicDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.keepcoding.eh_ho.model.Post
import io.keepcoding.eh_ho.repository.Repository
import io.keepcoding.eh_ho.topicDetails.TopicDetailsViewModel.State.PostsReceived

class TopicDetailsViewModel(private val repository: Repository, private val topicId: Int) :
    ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData<State>()

    val state: LiveData<State> = _state

    fun loadPosts() {
        _state.postValue(
            if (_state.value == null) {
                getPosts()
                State.LoadingPosts.Loading
            } else {
                with(_state.value) {
                    when (this) {
                        is PostsReceived -> State.LoadingPosts.LoadingWithPosts(posts)
                        is State.LoadingPosts -> this
                        else -> State.LoadingPosts.Loading
                    }
                }
            }
        )
    }

    fun getPosts() {
        repository.getPosts(topicId){
            it.fold(::onPostsReceived, ::onPostsFailure)
        }
    }

    private fun onPostsReceived(posts: List<Post>) {
        _state.postValue(
            posts.takeUnless { it.isEmpty() }?.let(::PostsReceived) ?: State.NoPosts
        )
    }

    private fun onPostsFailure(throwable: Throwable) {
        _state.postValue(State.NoPosts)
    }

    sealed class State {
        sealed class LoadingPosts : State() {
            object Loading : LoadingPosts()
            data class LoadingWithPosts(val posts: List<Post>) : LoadingPosts()
        }

        data class PostsReceived(val posts: List<Post>) : State()
        object NoPosts : State()
    }

    class TopicDetailsViewModelProviderFactory(
        private val repository: Repository,
        private val topicId: Int
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
            TopicDetailsViewModel::class.java -> TopicDetailsViewModel(repository, topicId) as T
            else -> throw IllegalArgumentException("TopicDetailsViewModelProviderFactory can only create instances of the TopicDetailsViewModel")
        }

    }
}