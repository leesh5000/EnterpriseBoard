package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment

interface CreateCommentUseCase {
    fun create(command: CreateCommentCommand): Comment
}

data class CreateCommentCommand(
    val content: String,
    val parentCommentId: Long = 0L,
    val articleId: Long,
    val writerId: Long
)