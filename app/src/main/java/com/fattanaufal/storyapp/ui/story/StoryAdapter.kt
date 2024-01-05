package com.fattanaufal.storyapp.ui.story

import android.app.Activity
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.fattanaufal.storyapp.R
import com.fattanaufal.storyapp.databinding.ItemStoryBinding
import com.fattanaufal.storyapp.local.StoryEntity
import com.fattanaufal.storyapp.response.ListStoryItem
import com.fattanaufal.storyapp.ui.DetailActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class StoryAdapter(private val appCompatActivity: AppCompatActivity) :
    PagingDataAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val itemStory = getItem(position)
        if (itemStory != null) {
            holder.bind(itemStory, appCompatActivity)
        }

    }


    class StoryViewHolder(private var binding: ItemStoryBinding, private var context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            itemStory: StoryEntity,
            appCompatActivity: AppCompatActivity,
        ) {
            val drawable = CircularProgressDrawable(context)
            drawable.setColorSchemeColors(R.color.white)
            drawable.centerRadius = 30f
            drawable.strokeWidth = 5f
            drawable.start()

            Glide.with(itemView)
                .load(itemStory.photoUrl)
                .placeholder(drawable)
                .transition(
                    DrawableTransitionOptions.withCrossFade(
                        DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                    )
                )
                .into(binding.imgStory)

            binding.apply {
                tvSender.text = itemStory.name
                tvDescription.text = itemStory.description
                tvLocation.text = itemStory.cityAndProvince
                createdAt.text = changeDateFormat(itemStory.createdAt)
                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imgStory, "imgStory"),
                            Pair(tvSender, "sender"),
                            Pair(tvDescription, "description"),
                            Pair(createdAt, "createdAt"),
                            Pair(tvLocation, "location"),
                        )

                    val intentToDetail = Intent(appCompatActivity, DetailActivity::class.java)
                    intentToDetail.putExtra(DetailActivity.ITEM_STORY, itemStory)
                    itemView.context.startActivity(intentToDetail, optionsCompat.toBundle())
                }
            }

        }

        private fun changeDateFormat(dateString: String): String {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            var outputFormat = SimpleDateFormat("dd/MM/yy, HH:mm:ss", Locale.getDefault())

            val date = format.parse(dateString) as Date
            val createdAt = if (Locale.getDefault().country != "US") {
                outputFormat.timeZone = TimeZone.getTimeZone("GMT+7")
                Date(date.time + (7 * 60 * 60 * 1000))
            } else {
                outputFormat = SimpleDateFormat("MM/dd/yy, HH:mm:ss", Locale.getDefault())
                date
            }

            val today = Date()

            return if (isDateDifferenceOneDay(today, createdAt)) {
                outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                "Yesterday, ${outputFormat.format(createdAt)}"
            } else {
                outputFormat.format(createdAt)
            }
        }

        private fun isDateDifferenceOneDay(date1: Date, date2: Date): Boolean {
            val difference = date1.time - date2.time
            val oneDayInMillis = 24 * 60 * 60 * 1000 // Satu hari dalam milidetik
            return Math.abs(difference).toInt() >= oneDayInMillis
        }

    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }


            override fun areContentsTheSame(
                oldItem: StoryEntity,
                newItem: StoryEntity,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}