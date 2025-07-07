package me.helloc.enterpriseboard.domain.model

import java.time.LocalDateTime

data class Comment(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val articleId: Long, // shard key
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    fun update(content: String): Comment {
        return this.copy(
            content = content, modifiedAt = LocalDateTime.now()
        )
    }

    companion object {
        fun create(
            commentId: Long,
            content: String,
            parentCommentId: Long = 0L, // 0L indicates no parent comment
            articleId: Long,
            writerId: Long,
        ): Comment {
            val now = LocalDateTime.now()
            return Comment(
                commentId = commentId,
                content = content,
                parentCommentId = if (parentCommentId == 0L) commentId else parentCommentId,
                articleId = articleId,
                writerId = writerId,
                deleted = false,
                createdAt = now,
                modifiedAt = now
            )
        }
    }

    fun isRoot(): Boolean {
        return parentCommentId == commentId
    }

    fun delete(): Comment {
        return this.copy(
            deleted = true, modifiedAt = LocalDateTime.now()
        )
    }
}