package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface GetArticleUseCase {
    fun getById(articleId: Long): Article?
    fun getByBoardId(boardId: Long): List<Article>
    fun getByWriterId(writerId: Long): List<Article>
}