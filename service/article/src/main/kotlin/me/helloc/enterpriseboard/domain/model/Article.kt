package me.helloc.enterpriseboard.domain.model

import java.time.LocalDateTime

data class Article(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long, // shard key
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    fun update(title: String, content: String): Article {
        return this.copy(
            title = title, content = content, modifiedAt = LocalDateTime.now()
        )
    }

    companion object {
        fun create(
            articleId: Long,
            title: String,
            content: String,
            boardId: Long,
            writerId: Long,
        ): Article {
            val now = LocalDateTime.now()
            return Article(
                articleId = articleId,
                title = title,
                content = content,
                boardId = boardId,
                writerId = writerId,
                createdAt = now,
                modifiedAt = now
            )
        }
    }
}
