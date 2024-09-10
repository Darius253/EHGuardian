package com.example.ehguardian.data.models

import com.google.gson.annotations.SerializedName



data class HealthNewsModel(

	@field:SerializedName("news")
	val news: List<NewsItem?>? = null,

)



data class NewsItem(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("short_description")
	val shortDescription: String,


	@field:SerializedName("top_image")
	val topImage: String,


	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("url")
	val url: String
)
