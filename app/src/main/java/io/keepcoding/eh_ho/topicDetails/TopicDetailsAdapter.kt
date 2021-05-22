package io.keepcoding.eh_ho.topicDetails

import android.os.Build
import android.text.Html
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.databinding.ViewPostBinding
import io.keepcoding.eh_ho.extensions.inflater
import io.keepcoding.eh_ho.model.Post

class TopicDetailsAdapter() :
    ListAdapter<Post, TopicDetailsAdapter.PostViewHolder>(DIFF) {

    class PostViewHolder(
        parent: ViewGroup,
        private val binding: ViewPostBinding = ViewPostBinding.inflate(
            parent.inflater,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.posterUsernameTextView.text = post.name ?: post.username
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.postTextTextView.text =
                    Html.fromHtml(post.cooked, Html.FROM_HTML_MODE_COMPACT)
            } else {
                binding.postTextTextView.text = Html.fromHtml(post.cooked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
        PostViewHolder(parent)

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem
        }
    }
}