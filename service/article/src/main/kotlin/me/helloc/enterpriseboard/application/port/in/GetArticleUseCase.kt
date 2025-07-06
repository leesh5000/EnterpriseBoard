package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Article

interface GetArticleUseCase {
    fun getById(articleId: Long): Article?
    fun getByBoardId(boardId: Long): List<Article>
    fun getByWriterId(writerId: Long): List<Article>
    fun getPage(query: GetArticlePageQuery): GetArticlePageResult
}

data class GetArticlePageQuery(
    val boardId: Long,
    val page: Long,
    val pageSize: Long,
    val movablePageCount: Long
)

data class GetArticlePageResult(
    val articles: List<Article>,
    val totalCount: Long
)
