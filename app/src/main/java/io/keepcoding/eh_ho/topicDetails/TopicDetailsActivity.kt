package io.keepcoding.eh_ho.topicDetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import io.keepcoding.eh_ho.databinding.ActivityTopicDetailsBinding
import io.keepcoding.eh_ho.di.DIProvider
import io.keepcoding.eh_ho.model.Post

class TopicDetailsActivity : AppCompatActivity() {

    private val binding: ActivityTopicDetailsBinding by lazy {
        ActivityTopicDetailsBinding.inflate(
            layoutInflater
        )
    }

    private val topicDetailsAdapter: TopicDetailsAdapter by lazy { TopicDetailsAdapter() }

    private var topicId: Int = 0

    private val vm: TopicDetailsViewModel by viewModels { DIProvider.getTopicDetailsModelProviderFactory(topicId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.postsRecyclerView.apply {
            adapter = topicDetailsAdapter
            addItemDecoration(DividerItemDecoration(this@TopicDetailsActivity, LinearLayout.VERTICAL))
        }
        topicId = intent.getIntExtra(TOPIC_ID, 0)
        vm.state.observe(this) {
            when (it) {
                is TopicDetailsViewModel.State.LoadingPosts -> renderLoading(it)
                is TopicDetailsViewModel.State.PostsReceived -> addPosts(it.posts)
                is TopicDetailsViewModel.State.NoPosts -> renderEmptyState()
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            vm.getPosts()
        }
    }

    override fun onResume() {
        super.onResume()
        vm.loadPosts()
    }

    private fun renderEmptyState() {
        binding.viewLoading.root.isVisible = false
        binding.noPostsTextView.isVisible = true
    }

    private fun renderLoading(loadingState: TopicDetailsViewModel.State.LoadingPosts) {
        (loadingState as? TopicDetailsViewModel.State.LoadingPosts.LoadingWithPosts)?.let {
            addPosts(it.posts)
        }
        (loadingState as? TopicDetailsViewModel.State.LoadingPosts.Loading)?.let {
            binding.noPostsTextView.isVisible = false
            binding.viewLoading.root.isVisible = true
        }
    }

    private fun addPosts(posts: List<Post>) {
        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
        binding.viewLoading.root.isVisible = false
        binding.noPostsTextView.isVisible = false
        topicDetailsAdapter.submitList(posts)
    }

    companion object {
        private const val TOPIC_ID = "topicId"

        @JvmStatic
        fun createIntent(context: Context, topicId: Int): Intent =
            Intent(context, TopicDetailsActivity::class.java).apply {
                putExtra(
                    TOPIC_ID, topicId
                )
            }
    }

}