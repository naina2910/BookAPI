package com.naina.bookapi.model

data class Book(
    val title: String,
    val authors: String,
    val publishedDate: String,
    val description: String,
    val categories: String,
    val thumbnail: String,
    val buy: String,
    val perview: String,
    val price: String,
    val pageCount: Int,
    val mUrl: String
)