package me.helloc.enterpriseboard.adapter.`in`.web.dto

data class CreateCommentRequest(
    val content: String,
    val parentCommentId: Long = 0L,
    val articleId: Long,
    val writerId: Long
)