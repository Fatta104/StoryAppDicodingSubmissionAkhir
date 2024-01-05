package com.fattanaufal.storyapp.ui

import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.fattanaufal.storyapp.R
import com.fattanaufal.storyapp.databinding.DetailActivityBinding
import com.fattanaufal.storyapp.local.StoryEntity
import com.fattanaufal.storyapp.ui.story.StoryViewModel
import com.fattanaufal.storyapp.ui.story.StoryViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class DetailActivity : AppCompatActivity() {
    private lateinit var detailBinding: DetailActivityBinding
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = DetailActivityBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)
        setSupportActionBar(detailBinding.appBarList)

        storyViewModel = obtainViewModel(this@DetailActivity)
        geocoder = Geocoder(this)

        val drawable = CircularProgressDrawable(this)
        drawable.setColorSchemeColors(R.color.white)
        drawable.centerRadius = 30f
        drawable.strokeWidth = 5f
        drawable.start()


        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(ITEM_STORY, StoryEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(ITEM_STORY)
        }

        if (story != null) {
            loadAllData(story, drawable)
        }else{
            Toast.makeText(this, "Error Load Data", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    private fun obtainViewModel(appCompatActivity: AppCompatActivity): StoryViewModel {
        val factory = StoryViewModelFactory.getInstance(appCompatActivity.applicationContext)
        return ViewModelProvider(
            appCompatActivity, factory
        )[StoryViewModel::class.java]
    }

    private fun loadAllData(story: StoryEntity, drawable: CircularProgressDrawable){
        Glide.with(this)
            .load(story.photoUrl) // URL Gambar
            .placeholder(drawable)
            .transition(
                DrawableTransitionOptions.withCrossFade(
                    DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                )
            )
            .into(detailBinding.imgStory)
        detailBinding.tvSender.text = story.name
        detailBinding.appBarList.subtitle = story.name
        detailBinding.tvDescription.text = story.description
        detailBinding.createdAt.text = changeDateFormat(story.createdAt)
        detailBinding.location.text = story.cityAndProvince
    }

    private fun changeDateFormat(dateString: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("id", "ID"))
        val date = format.parse(dateString) as Date
        var outputFormat = SimpleDateFormat("dd/MM/yy, HH:mm:ss", Locale("id", "ID"))
        outputFormat.timeZone = TimeZone.getTimeZone("GMT+7")

        val createdAt = Date(date.time + (7 * 60 * 60 * 1000))
        val today = Date()
        return if (isDateDifferenceOneDay(today, createdAt)) {
            outputFormat = SimpleDateFormat("HH:mm:ss", Locale("id", "ID"))
            "Yesterday, ${outputFormat.format(Date(date.time + (7 * 60 * 60 * 1000)))}"
        } else {
            outputFormat.format(Date(date.time + (7 * 60 * 60 * 1000)))
        }
    }

    private fun isDateDifferenceOneDay(date1: Date, date2: Date): Boolean {
        val difference = date1.time - date2.time
        val oneDayInMillis = 24 * 60 * 60 * 1000 // Satu hari dalam milidetik
        return Math.abs(difference).toInt() >= oneDayInMillis
    }



    companion object{
        val ITEM_STORY = "STORY"

        @Volatile
        private var instance: DetailActivity? = null

        fun getInstance(): DetailActivity =
            instance ?: synchronized(this) {
                instance ?: DetailActivity()
            }.also { instance = it }
    }
}