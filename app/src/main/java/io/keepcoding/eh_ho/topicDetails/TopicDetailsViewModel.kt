package io.keepcoding.eh_ho.topicDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.model.Post
import io.keepcoding.eh_ho.repository.Repository
import io.keepcoding.eh_ho.topicDetails.TopicDetailsViewModel.State.PostsReceived

class TopicDetailsViewModel(private val repository: Repository, private val topicId: Int) :
    ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData<State>()

    private val _createPostError: MutableLiveData<Int?> = MutableLiveData<Int?>()

    val state: LiveData<State> = _state
    val createPostError: LiveData<Int?> = _createPostError

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
        _createPostError.postValue(null)
    }

    fun getPosts() {
        repository.getPosts(topicId) {
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

    fun createNewPost(text: String) {
        if (text.isBlank() || text.isEmpty()) {
            _createPostError.postValue(R.string.empty_post_text_error)
        } else if (text.length < 20) {
            _createPostError.postValue(R.string.post_text_too_short)
        } else {
            repository.createPost(topicId, text) {
                it.fold(::onPostCreated, ::onPostFailure)
            }
        }
    }

    private fun onPostCreated(sucess: Boolean) {
        _state.postValue(State.LoadingPosts.Loading)
        getPosts()
    }

    private fun onPostFailure(throwable: Throwable) {
        _createPostError.postValue(R.string.create_post_error)
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