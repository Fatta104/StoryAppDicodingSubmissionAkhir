package com.fattanaufal.storyapp

import com.fattanaufal.storyapp.local.StoryEntity

object DataDummy {
    fun generateDummyQuoteResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryEntity(
                i.toString(),
                "author + $i",
                "quote $i",
                "https://story-api.dicoding.dev/images/stories/photos-1698856990408_QW9Xyisr.jpg",
                "2023-10-31",
                -6.2536942,
                106.7168694,
                "City $i"
            )
            items.add(quote)
        }
        return items
    }
}