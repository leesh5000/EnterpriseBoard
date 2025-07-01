package me.helloc.enterpriseboard.view.application.port.input.query

import java.time.LocalDateTime

/**
 * 조회수 쿼리 객체들
 */

data class GetViewCountQuery(
    val articleId: Long
)

data class GetViewsByArticleQuery(
    val articleId: Long
)

data class GetViewsByUserQuery(
    val userId: Long
)

data class GetViewStatisticsQuery(
    val articleId: Long,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
)

// Result objects
data class ViewDetailView(
    val viewId: Long,
    val articleId: Long,
    val userId: Long?,
    val ipAddress: String,
    val userAgent: String?,
    val createdAt: LocalDateTime
)

data class GetViewCountResult(
    val articleId: Long,
    val count: Long
)

data class GetViewsByArticleResult(
    val views: List<ViewDetailView>,
    val totalCount: Long
)

data class GetViewsByUserResult(
    val views: List<ViewDetailView>
)

data class GetViewStatisticsResult(
    val articleId: Long,
    val totalViews: Long,
    val periodViews: Long,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?
)