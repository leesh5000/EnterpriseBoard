package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment

interface UpdateCommentUseCase {
    fun update(command: UpdateCommentCommand): Comment
}

data class UpdateCommentCommand(
    val commentId: Long,
    val content: String
)