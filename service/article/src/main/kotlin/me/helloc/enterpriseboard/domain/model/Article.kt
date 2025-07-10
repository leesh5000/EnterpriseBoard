package me.helloc.enterpriseboard.domain.model

import java.time.LocalDateTime

sealed class Article {

    abstract val articleId: Long
    abstract val title: String
    abstract val content: String
    abstract val boardId: Long
    abstract val writerId: Long
    abstract val createdAt: LocalDateTime
    abstract val modifiedAt: LocalDateTime

    abstract fun update(title: String, content: String): Article
    abstract fun exists(): Boolean

    fun isNull(): Boolean = !exists()

    companion object {
        fun create(
            articleId: Long,
            title: String,
            content: String,
            boardId: Long,
            writerId: Long,
        ): RealArticle {
            val now = LocalDateTime.now()
            return RealArticle(
                articleId = articleId,
                title = title,
                content = content,
                boardId = boardId,
                writerId = writerId,
                createdAt = now,
                modifiedAt = now
            )
        }

        fun empty(): NullArticle = NullArticle
    }
}

data class RealArticle(
    override val articleId: Long,
    override val title: String,
    override val content: String,
    override val boardId: Long,
    override val writerId: Long,
    override val createdAt: LocalDateTime,
    override val modifiedAt: LocalDateTime,
) : Article() {

    override fun update(title: String, content: String): Article {
        return this.copy(
            title = title, content = content, modifiedAt = LocalDateTime.now()
        )
    }

    override fun exists(): Boolean = true
}

object NullArticle : Article() {
    override val articleId: Long = -1L
    override val title: String = ""
    override val content: String = ""
    override val boardId: Long = -1L
    override val writerId: Long = -1L
    override val createdAt: LocalDateTime = LocalDateTime.MIN
    override val modifiedAt: LocalDateTime = LocalDateTime.MIN

    override fun update(title: String, content: String): Article = this
    override fun exists(): Boolean = false
}
