package me.helloc.enterpriseboard.article.service.dto.response

import me.helloc.enterpriseboard.article.domain.Article
import java.time.LocalDateTime

data class ArticleResponse(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {
    companion object {
        fun from(article: Article): ArticleResponse {
            return ArticleResponse(
                articleId = article.articleId ?: throw IllegalArgumentException("Article ID cannot be null"),
                title = article.title ?: throw IllegalArgumentException("Title cannot be null"),
                content = article.content ?: throw IllegalArgumentException("Content cannot be null"),
                boardId = article.boardId ?: throw IllegalArgumentException("Board ID cannot be null"),
                writerId = article.writerId ?: throw IllegalArgumentException("Writer ID cannot be null"),
                createdAt = article.createdAt ?: throw IllegalArgumentException("Created At cannot be null"),
                modifiedAt = article.modifiedAt ?: throw IllegalArgumentException("Modified At cannot be null")
            )
        }
    }
}
