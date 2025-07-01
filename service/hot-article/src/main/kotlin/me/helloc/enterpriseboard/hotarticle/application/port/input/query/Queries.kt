package me.helloc.enterpriseboard.hotarticle.application.port.input.query

import java.time.LocalDateTime

/**
 * 인기 게시글 쿼리 객체들
 */

data class GetHotArticlesByBoardQuery(
    val boardId: Long,
    val limit: Int = 10
)

data class GetGlobalHotArticlesQuery(
    val limit: Int = 10
)

data class GetHotArticleByArticleIdQuery(
    val articleId: Long
)

// Result objects
data class HotArticleView(
    val articleId: Long,
    val boardId: Long,
    val score: Double,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long,
    val rank: Int?,
    val calculatedAt: LocalDateTime
)

data class GetHotArticlesByBoardResult(
    val boardId: Long,
    val hotArticles: List<HotArticleView>
)

data class GetGlobalHotArticlesResult(
    val hotArticles: List<HotArticleView>
)

data class GetHotArticleByArticleIdResult(
    val hotArticle: HotArticleView?
)