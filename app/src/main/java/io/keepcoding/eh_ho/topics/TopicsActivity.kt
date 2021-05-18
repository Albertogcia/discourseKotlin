package io.keepcoding.eh_ho.topics

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import io.keepcoding.eh_ho.databinding.ActivityTopicsBinding
import io.keepcoding.eh_ho.di.DIProvider

class TopicsActivity : AppCompatActivity() {

    private val binding: ActivityTopicsBinding by lazy { ActivityTopicsBinding.inflate(layoutInflater) }
    private val topicsAdapter = TopicsAdapter()
    private val vm: TopicsViewModel by viewModels { DIProvider.topicsViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.topics.apply {
            adapter = topicsAdapter
            addItemDecoration(DividerItemDecoration(this@TopicsActivity, LinearLayout.VERTICAL))
        }
        vm.state.observe(this) {
            when (it) {
                is TopicsViewModel.State.LoadingTopics -> renderLoading(it)
                is TopicsViewModel.State.TopicsReceived -> topicsAdapter.submitList(it.topics)
                is TopicsViewModel.State.NoTopics -> renderEmptyState()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.loadTopics()
    }

    private fun renderEmptyState() {
        // Render empty state
    }

    private fun renderLoading(loadingState: TopicsViewModel.State.LoadingTopics) {
        (loadingState as? TopicsViewModel.State.LoadingTopics.LoadingWithTopics)?.let { topicsAdapter.submitList(it.topics) }
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent = Intent(context, TopicsActivity::class.java)
    }
}