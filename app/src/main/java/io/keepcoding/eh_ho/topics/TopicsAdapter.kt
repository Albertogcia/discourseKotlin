package io.keepcoding.eh_ho.topics

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.databinding.ViewTopicBinding
import io.keepcoding.eh_ho.extensions.inflater
import io.keepcoding.eh_ho.model.Topic

class TopicsAdapter(private val viewHolderFactory: TopicsViewHolderFactory, private val onClickListener: (Topic) -> Unit) :
    ListAdapter<Topic, TopicsAdapter.TopicModelViewHolder>(DIFF) {

    override fun getItemViewType(position: Int): Int =
        viewHolderFactory.getItemViewType(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicModelViewHolder =
        viewHolderFactory.getViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: TopicModelViewHolder, position: Int){
        holder.itemView.setOnClickListener {
            onClickListener(currentList[position])
        }
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size

    interface TopicViewHolderFactory {
        fun getItemViewType(topic: Topic): Int
        fun getViewHolder(parent: ViewGroup, type: Int): TopicModelViewHolder
    }

    abstract class TopicModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(model: Topic)
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Topic>() {
            override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean =
                oldItem == newItem
        }
    }
}