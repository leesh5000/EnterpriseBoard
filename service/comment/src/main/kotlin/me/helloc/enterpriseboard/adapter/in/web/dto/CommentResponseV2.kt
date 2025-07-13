package me.helloc.enterpriseboard.adapter.`in`.web.dto

import me.helloc.enterpriseboard.domain.model.CommentV2
import java.time.LocalDateTime

data class CommentResponseV2(
    val commentId: Long,
    val content: String,
    val path: String,
    val articleId: Long,
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(comment: CommentV2): CommentResponseV2 {
            return CommentResponseV2(
                commentId = comment.commentId,
                content = comment.content,
                path = comment.commentPath.path,
                articleId = comment.articleId,
                writerId = comment.writerId,
                deleted = comment.deleted,
                createdAt = comment.createdAt
            )
        }
    }
}
