package me.helloc.enterpriseboard.adapter.`in`.web.dto

data class CreateArticleRequest(
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long
)