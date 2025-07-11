package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment

interface UpdateCommentUseCase {
    fun update(commentId: Long, content: String): Comment?
}

