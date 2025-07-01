package me.helloc.enterpriseboard.hotarticle.application.port.input.command

/**
 * 인기 게시글 명령 객체들
 */

data class UpdateHotArticleMetricsCommand(
    val articleId: Long,
    val boardId: Long,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long
)

data class UpdateRankingsCommand(
    val boardId: Long? = null // null이면 전체 랭킹 업데이트
)

data class CleanupOldDataCommand(
    val daysOld: Long = 30 // 기본 30일 이전 데이터 삭제
)

// Result objects
data class UpdateHotArticleMetricsResult(
    val articleId: Long,
    val score: Double,
    val rank: Int?,
    val updated: Boolean
)

data class UpdateRankingsResult(
    val boardId: Long?,
    val updatedCount: Int
)

data class CleanupOldDataResult(
    val deletedCount: Int
)