package me.helloc.enterpriseboard.comment.application.port.input.query

import java.time.LocalDateTime

/**
 * 댓글 쿼리 객체들
 */

data class GetCommentQuery(
    val commentId: Long
)

data class GetCommentsByArticleQuery(
    val articleId: Long
)

data class GetCommentsByWriterQuery(
    val writerId: Long
)

// Result objects
data class CommentView(
    val commentId: Long,
    val articleId: Long,
    val content: String,
    val writerId: Long,
    val parentCommentId: Long?,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val replies: List<CommentView> = emptyList() // 대댓글 목록
)

data class GetCommentResult(
    val comment: CommentView
)

data class GetCommentsByArticleResult(
    val comments: List<CommentView>
)

data class GetCommentsByWriterResult(
    val comments: List<CommentView>
)