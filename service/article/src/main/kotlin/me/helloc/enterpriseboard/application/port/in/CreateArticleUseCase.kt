package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface CreateArticleUseCase {
    fun create(title: String, content: String, boardId: Long, writerId: Long): Article
}