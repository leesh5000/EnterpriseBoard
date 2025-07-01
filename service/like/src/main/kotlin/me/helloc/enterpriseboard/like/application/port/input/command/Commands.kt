package me.helloc.enterpriseboard.like.application.port.input.command

/**
 * 좋아요 명령 객체들
 */

data class AddLikeCommand(
    val articleId: Long,
    val userId: Long
)

data class RemoveLikeCommand(
    val articleId: Long,
    val userId: Long
)

data class ToggleLikeCommand(
    val articleId: Long,
    val userId: Long
)

// Result objects
data class AddLikeResult(
    val likeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: String
)

data class RemoveLikeResult(
    val articleId: Long,
    val userId: Long,
    val removedAt: String
)

data class ToggleLikeResult(
    val articleId: Long,
    val userId: Long,
    val isLiked: Boolean,
    val likeCount: Long,
    val actionTime: String
)