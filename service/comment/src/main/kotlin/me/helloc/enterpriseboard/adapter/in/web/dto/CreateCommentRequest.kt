package me.helloc.enterpriseboard.adapter.`in`.web.dto

import me.helloc.enterpriseboard.domain.model.Comment

data class CreateCommentRequest(
    val content: String,
    val parentCommentId: Long = Comment.NO_PARENT_ID,
    val articleId: Long,
    val writerId: Long
)
