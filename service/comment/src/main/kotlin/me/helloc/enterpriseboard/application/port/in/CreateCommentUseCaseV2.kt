package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.CommentV2

interface CreateCommentUseCaseV2 {
    fun create(content: String, parentPath: String, articleId: Long, writerId: Long): CommentV2
}

