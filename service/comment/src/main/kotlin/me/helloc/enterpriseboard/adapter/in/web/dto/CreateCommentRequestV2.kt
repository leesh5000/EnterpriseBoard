package me.helloc.enterpriseboard.adapter.`in`.web.dto

data class CreateCommentRequestV2(
    val content: String,
    val parentPath: String = "",
    val articleId: Long,
    val writerId: Long
)
