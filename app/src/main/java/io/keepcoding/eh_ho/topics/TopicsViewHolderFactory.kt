package io.keepcoding.eh_ho.topics

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.model.Topic

class TopicsViewHolderFactory() : TopicsAdapter.TopicViewHolderFactory {

    override fun getItemViewType(topic: Topic): Int = when (topic.pinned) {
        false -> ITEM_TYPE_NORMAL
        true -> ITEM_TYPE_PINNED
    }

    override fun getViewHolder(
        parent: ViewGroup,
        type: Int
    ): TopicsAdapter.TopicModelViewHolder = when (type) {
        ITEM_TYPE_NORMAL -> NormalTopicViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_topic, parent, false)
        )
        ITEM_TYPE_PINNED -> PinnedTopicViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_pinned_topic, parent, false)
        )
        else -> throw IllegalArgumentException("Type '$type' is not supported yet")
    }

    companion object {
        const val ITEM_TYPE_NORMAL = 0
        const val ITEM_TYPE_PINNED = 1
    }

}

private class NormalTopicViewHolder(view: View) :
    TopicsAdapter.TopicModelViewHolder(view) {
    private val topicTitleTextView: TextView = view.findViewById(R.id.topicTitleTextView)
    private val topicLikesTextView: TextView = view.findViewById(R.id.topicLikesTextView)
    private val topicPostsTextView: TextView = view.findViewById(R.id.topicPostsTextView)
    private val topicLastPosterTextView: TextView = view.findViewById(R.id.topicLastPosterTextView)
    override fun bind(model: Topic) {
        topicTitleTextView.text = model.title
        topicLikesTextView.text = "${model.likeCount}"
        topicPostsTextView.text = "${model.postCount}"
        topicLastPosterTextView.text = model.lastPosterUsername
    }
}

private class PinnedTopicViewHolder(view: View) :
    TopicsAdapter.TopicModelViewHolder(view) {
    private val pinnedTopicTitleTextView: TextView =
        view.findViewById(R.id.pinnedTopicTitleTextView)
    private val pinnedTopicDescriptionTextView: TextView =
        view.findViewById(R.id.pinnedTopicDescriptionTextView)

    override fun bind(model: Topic) {
        pinnedTopicTitleTextView.text = model.title
        pinnedTopicDescriptionTextView.text = model.excerpt ?: ""
    }
}