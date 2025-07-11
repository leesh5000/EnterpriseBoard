package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface GetArticleUseCase {
    fun getById(articleId: Long): Article
    fun getByBoardId(boardId: Long): List<Article>
    fun getByWriterId(writerId: Long): List<Article>
    fun getPage(boardId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetArticlePageResult
    fun getScroll(boardId: Long, pageSize: Long, lastArticleId: Long): List<Article>
}

data class GetArticlePageResult(
    val articles: List<Article>,
    val limitedTotalCount: Long
)
