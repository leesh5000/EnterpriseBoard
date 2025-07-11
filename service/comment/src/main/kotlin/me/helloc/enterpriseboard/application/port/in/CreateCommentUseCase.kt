package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment

interface CreateCommentUseCase {
    fun create(content: String, parentCommentId: Long, articleId: Long, writerId: Long): Comment
}

