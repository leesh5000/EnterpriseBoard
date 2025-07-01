package me.helloc.enterpriseboard.like.application.port.input.query

import java.time.LocalDateTime

/**
 * 좋아요 쿼리 객체들
 */

data class GetLikesByArticleQuery(
    val articleId: Long
)

data class GetLikesByUserQuery(
    val userId: Long
)

data class GetLikeCountQuery(
    val articleId: Long
)

data class IsLikedByUserQuery(
    val articleId: Long,
    val userId: Long
)

// Result objects
data class LikeView(
    val likeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime
)

data class GetLikesByArticleResult(
    val likes: List<LikeView>,
    val totalCount: Long
)

data class GetLikesByUserResult(
    val likes: List<LikeView>
)

data class GetLikeCountResult(
    val articleId: Long,
    val count: Long
)

data class IsLikedByUserResult(
    val articleId: Long,
    val userId: Long,
    val isLiked: Boolean
)