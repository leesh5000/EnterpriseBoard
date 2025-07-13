package me.helloc.enterpriseboard.domain.model

import java.time.LocalDateTime

data class CommentV2(
    val commentId: Long,
    val content: String,
    val articleId: Long, // shard key
    val writerId: Long,
    val commentPath: CommentPath,
    val deleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {

    fun delete(): CommentV2 {
        return this.copy(
            deleted = true
        )
    }

    fun isRoot(): Boolean {
        return commentPath.isRoot()
    }

    companion object {
        const val EMPTY_ID = 0L
    }
}
