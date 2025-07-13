package me.helloc.enterpriseboard.domain.model

import java.time.LocalDateTime

data class CommentV2(
    val commentId: Long,
    val content: String,
    val parentCommentId: Long,
    val articleId: Long, // shard key
    val writerId: Long,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
) {

    fun delete(): CommentV2 {
        return this.copy(
            deleted = true
        )
    }

    fun isRoot(): Boolean {
        return parentCommentId == commentId
    }

    companion object {
        const val EMPTY_ID = 0L

        fun create(
            commentId: Long,
            content: String,
            parentCommentId: Long = EMPTY_ID,
            articleId: Long,
            writerId: Long,
        ): CommentV2 {
            val now = LocalDateTime.now()
            return CommentV2(
                commentId = commentId,
                content = content,
                parentCommentId = if (parentCommentId == EMPTY_ID) commentId else parentCommentId,
                articleId = articleId,
                writerId = writerId,
                deleted = false,
                createdAt = now
            )
        }
    }
}
