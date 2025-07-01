package me.helloc.enterpriseboard.articleread.application.port.input.query

import java.time.LocalDateTime

/**
 * 게시글 읽기 쿼리 객체들
 */

data class GetArticleQuery(
    val articleId: Long
)

data class GetArticlesByBoardQuery(
    val boardId: Long,
    val page: Int = 0,
    val size: Int = 20
)

data class GetArticlesByWriterQuery(
    val writerId: Long,
    val page: Int = 0,
    val size: Int = 20
)

data class GetHotArticlesQuery(
    val boardId: Long? = null,
    val limit: Int = 10
)

data class SearchArticlesQuery(
    val keyword: String,
    val searchType: SearchType = SearchType.TITLE_AND_CONTENT,
    val page: Int = 0,
    val size: Int = 20
)

data class GetRecentArticlesQuery(
    val boardId: Long? = null,
    val limit: Int = 10
)

enum class SearchType {
    TITLE_ONLY,
    CONTENT_ONLY,
    TITLE_AND_CONTENT,
    TAG
}

// Result objects
data class ArticleReadView(
    val articleId: Long,
    val title: String,
    val content: String,
    val summary: String,
    val boardId: Long,
    val boardName: String,
    val writerId: Long,
    val writerNickname: String,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long,
    val tags: List<String>,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val isHot: Boolean,
    val hotRank: Int?
)

data class GetArticleResult(
    val article: ArticleReadView
)

data class GetArticlesByBoardResult(
    val articles: List<ArticleReadView>,
    val totalCount: Long,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class GetArticlesByWriterResult(
    val articles: List<ArticleReadView>,
    val totalCount: Long,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class GetHotArticlesResult(
    val articles: List<ArticleReadView>,
    val boardId: Long?
)

data class SearchArticlesResult(
    val articles: List<ArticleReadView>,
    val keyword: String,
    val searchType: SearchType,
    val totalCount: Long,
    val page: Int,
    val size: Int,
    val hasNext: Boolean
)

data class GetRecentArticlesResult(
    val articles: List<ArticleReadView>,
    val boardId: Long?
)