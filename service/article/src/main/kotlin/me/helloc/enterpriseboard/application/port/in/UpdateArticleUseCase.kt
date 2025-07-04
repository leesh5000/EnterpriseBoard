package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface UpdateArticleUseCase {
    fun update(command: UpdateArticleCommand): Article
}

data class UpdateArticleCommand(
    val articleId: Long,
    val title: String,
    val content: String
)