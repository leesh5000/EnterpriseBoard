package me.helloc.enterpriseboard.articleread.application.port.input.command

import java.time.LocalDateTime

/**
 * 게시글 읽기 모델 명령 객체들
 */

data class CreateArticleReadModelCommand(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val boardName: String,
    val writerId: Long,
    val writerNickname: String,
    val createdAt: LocalDateTime,
    val tags: List<String> = emptyList()
)

data class UpdateArticleReadModelCommand(
    val articleId: Long,
    val title: String,
    val content: String,
    val modifiedAt: LocalDateTime,
    val tags: List<String> = emptyList()
)

data class UpdateViewCountCommand(
    val articleId: Long,
    val viewCount: Long
)

data class UpdateLikeCountCommand(
    val articleId: Long,
    val likeCount: Long
)

data class UpdateCommentCountCommand(
    val articleId: Long,
    val commentCount: Long
)

data class UpdateHotStatusCommand(
    val articleId: Long,
    val isHot: Boolean,
    val hotRank: Int?
)

data class DeleteArticleReadModelCommand(
    val articleId: Long
)

// Result objects
data class CreateArticleReadModelResult(
    val articleId: Long,
    val created: Boolean
)

data class UpdateArticleReadModelResult(
    val articleId: Long,
    val updated: Boolean
)

data class UpdateViewCountResult(
    val articleId: Long,
    val viewCount: Long
)

data class UpdateLikeCountResult(
    val articleId: Long,
    val likeCount: Long
)

data class UpdateCommentCountResult(
    val articleId: Long,
    val commentCount: Long
)

data class UpdateHotStatusResult(
    val articleId: Long,
    val isHot: Boolean,
    val hotRank: Int?
)

data class DeleteArticleReadModelResult(
    val articleId: Long,
    val deleted: Boolean
)