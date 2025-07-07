package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface GetArticleUseCase {
    fun getById(articleId: Long): Article?
    fun getByBoardId(boardId: Long): List<Article>
    fun getByWriterId(writerId: Long): List<Article>
    fun getPage(query: GetArticlePageQuery): GetArticlePageResult
    fun getScroll(query: GetArticleScrollQuery): List<Article>
}

data class GetArticlePageQuery(
    val boardId: Long,
    val page: Long,
    val pageSize: Long,
    val movablePageCount: Long
)

data class GetArticleScrollQuery(
    val boardId: Long,
    val pageSize: Long,
    val lastArticleId: Long
)

data class GetArticlePageResult(
    val articles: List<Article>,
    val count: Long
)
