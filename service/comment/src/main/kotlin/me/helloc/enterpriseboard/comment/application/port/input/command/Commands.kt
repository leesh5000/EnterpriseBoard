package me.helloc.enterpriseboard.comment.application.port.input.command

/**
 * 댓글 명령 객체들
 */

data class CreateCommentCommand(
    val articleId: Long,
    val content: String,
    val writerId: Long,
    val parentCommentId: Long? = null
)

data class UpdateCommentCommand(
    val commentId: Long,
    val content: String,
    val requesterId: Long
)

data class DeleteCommentCommand(
    val commentId: Long,
    val requesterId: Long
)

// Result objects
data class CreateCommentResult(
    val commentId: Long,
    val articleId: Long,
    val content: String,
    val writerId: Long,
    val parentCommentId: Long?,
    val createdAt: String
)

data class UpdateCommentResult(
    val commentId: Long,
    val content: String,
    val modifiedAt: String
)

data class DeleteCommentResult(
    val commentId: Long,
    val deletedAt: String
)