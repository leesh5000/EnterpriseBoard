package me.helloc.enterpriseboard.domain.model

import java.time.LocalDateTime

sealed class Comment {
    abstract val commentId: Long
    abstract val content: String
    abstract val parentCommentId: Long
    abstract val articleId: Long // shard key
    abstract val writerId: Long
    abstract val deleted: Boolean
    abstract val createdAt: LocalDateTime
    abstract val modifiedAt: LocalDateTime

    abstract fun update(content: String): Comment
    abstract fun delete(): Comment
    abstract fun isRoot(): Boolean
    abstract fun exists(): Boolean
    
    fun isNull(): Boolean = !exists()

    companion object {
        fun create(
            commentId: Long,
            content: String,
            parentCommentId: Long = 0L, // 0L indicates no parent comment
            articleId: Long,
            writerId: Long,
        ): RealComment {
            val now = LocalDateTime.now()
            return RealComment(
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

        fun empty(): NullComment = NullComment
    }
}

data class RealComment(
    override val commentId: Long,
    override val content: String,
    override val parentCommentId: Long,
    override val articleId: Long,
    override val writerId: Long,
    override val deleted: Boolean,
    override val createdAt: LocalDateTime,
    override val modifiedAt: LocalDateTime,
) : Comment() {
    
    override fun update(content: String): RealComment {
        return this.copy(
            content = content, 
            modifiedAt = LocalDateTime.now()
        )
    }

    override fun delete(): RealComment {
        return this.copy(
            deleted = true, 
            modifiedAt = LocalDateTime.now()
        )
    }

    override fun isRoot(): Boolean {
        return parentCommentId == commentId
    }

    override fun exists(): Boolean = true
}

object NullComment : Comment() {
    override val commentId: Long = -1L
    override val content: String = ""
    override val parentCommentId: Long = -1L
    override val articleId: Long = -1L
    override val writerId: Long = -1L
    override val deleted: Boolean = false
    override val createdAt: LocalDateTime = LocalDateTime.MIN
    override val modifiedAt: LocalDateTime = LocalDateTime.MIN

    override fun update(content: String): NullComment = this

    override fun delete(): NullComment = this

    override fun isRoot(): Boolean = false

    override fun exists(): Boolean = false
}