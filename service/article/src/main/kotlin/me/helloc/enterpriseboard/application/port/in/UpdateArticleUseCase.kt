package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface UpdateArticleUseCase {
    fun update(articleId: Long, title: String, content: String): Article
}