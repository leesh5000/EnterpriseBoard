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
            content = content,
            modifiedAt = LocalDateTime.now()
        )
    }

    fun delete(): Comment {
        return this.copy(
            deleted = true,
            modifiedAt = LocalDateTime.now()
        )
    }

    fun isRoot(): Boolean {
        return parentCommentId == commentId
    }

    companion object {
        const val NO_PARENT_ID = 0L
        
        fun create(
            commentId: Long,
            content: String,
            parentCommentId: Long = NO_PARENT_ID,
            articleId: Long,
            writerId: Long,
        ): Comment {
            val now = LocalDateTime.now()
            return Comment(
                commentId = commentId,
                content = content,
                parentCommentId = if (parentCommentId == NO_PARENT_ID) commentId else parentCommentId,
                articleId = articleId,
                writerId = writerId,
                deleted = false,
                createdAt = now,
                modifiedAt = now
            )
        }
    }
}
