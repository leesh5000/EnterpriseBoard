package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface CreateArticleUseCase {
    fun create(command: CreateArticleCommand): Article
}

data class CreateArticleCommand(
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long
)