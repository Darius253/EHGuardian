package com.tron.ehguardian.data.models

import com.google.gson.annotations.SerializedName



data class NewsRequest(
	val category: String,
	val location: String,
	val language: String,
	val page: Int
)

data class HealthNewsModel(
	@SerializedName("news")
	val news: List<NewsItem>? = null
)

data class NewsItem(
	@SerializedName("date")
	val date: String? = null,

	@SerializedName("short_description")
	val shortDescription: String? = null,

	@SerializedName("top_image")
	val topImage: String? = null,

	@SerializedName("title")
	val title: String? = null,

	@SerializedName("url")
	val url: String? = null
)
