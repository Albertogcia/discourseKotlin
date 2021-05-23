package io.keepcoding.eh_ho.topicDetails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.databinding.ActivityTopicDetailsBinding
import io.keepcoding.eh_ho.di.DIProvider
import io.keepcoding.eh_ho.model.Post
import org.w3c.dom.Text

class TopicDetailsActivity : AppCompatActivity() {

    private val binding: ActivityTopicDetailsBinding by lazy {
        ActivityTopicDetailsBinding.inflate(
            layoutInflater
        )
    }

    private val topicDetailsAdapter: TopicDetailsAdapter by lazy { TopicDetailsAdapter() }

    private var topicId: Int = 0

    private val vm: TopicDetailsViewModel by viewModels {
        DIProvider.getTopicDetailsModelProviderFactory(
            topicId
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.postsRecyclerView.apply {
            adapter = topicDetailsAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@TopicDetailsActivity,
                    LinearLayout.VERTICAL
                )
            )
        }
        topicId = intent.getIntExtra(TOPIC_ID, 0)
        vm.state.observe(this) {
            when (it) {
                is TopicDetailsViewModel.State.LoadingPosts -> renderLoading(it)
                is TopicDetailsViewModel.State.PostsReceived -> addPosts(it.posts)
                is TopicDetailsViewModel.State.NoPosts -> renderEmptyState()
            }
        }
        vm.createPostError.observe(this){
            it?.let { Toast.makeText(this, getString(it), Toast.LENGTH_LONG).show() }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            vm.getPosts()
        }

        binding.addPostFloatingButton.setOnClickListener(::showNewPostDialog)
    }

    override fun onResume() {
        super.onResume()
        vm.loadPosts()
    }

    private fun showNewPostDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.new_post_dialog_title))
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_new_post, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.postTextEditText)
        builder.setView(dialogLayout)
        builder.setNegativeButton(getString(R.string.cancel_button), null)
        builder.setPositiveButton(getString(R.string.create_post_button)) { _, _ ->
            createNewPost(editText.text.toString())
        }
        builder.show()
    }

    private fun createNewPost(string: String){
        vm.createNewPost(string)
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