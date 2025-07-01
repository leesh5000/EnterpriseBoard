package me.helloc.enterpriseboard.article.application.port.input.query

import java.time.LocalDateTime

/**
 * 쿼리 객체들 - 조회 요청을 위한 입력 데이터
 */

data class GetArticleQuery(
    val articleId: Long
)

data class GetArticlesByBoardQuery(
    val boardId: Long
)

data class GetArticlesByWriterQuery(
    val writerId: Long
)

data class GetAllArticlesQuery(
    val page: Int = 0,
    val size: Int = 20
)

// Result objects
data class ArticleView(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
)

data class GetArticleResult(
    val article: ArticleView
)

data class GetArticlesByBoardResult(
    val articles: List<ArticleView>
)

data class GetArticlesByWriterResult(
    val articles: List<ArticleView>
)

data class GetAllArticlesResult(
    val articles: List<ArticleView>,
    val totalCount: Long,
    val page: Int,
    val size: Int
)