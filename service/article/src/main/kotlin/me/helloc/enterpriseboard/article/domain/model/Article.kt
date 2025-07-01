package me.helloc.enterpriseboard.article.domain.model

import me.helloc.enterpriseboard.article.domain.event.ArticleCreatedEvent
import me.helloc.enterpriseboard.article.domain.event.ArticleUpdatedEvent
import me.helloc.enterpriseboard.article.domain.event.DomainEvent
import java.time.LocalDateTime

/**
 * 순수 도메인 모델 - 비즈니스 로직과 불변성을 보장
 */
data class Article(
    val articleId: ArticleId,
    val title: Title,
    val content: Content,
    val boardId: BoardId,
    val writerId: WriterId,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    private val events: MutableList<DomainEvent> = mutableListOf()
) {
    fun update(newTitle: Title, newContent: Content): Article {
        val updatedArticle = copy(
            title = newTitle,
            content = newContent,
            modifiedAt = LocalDateTime.now()
        )
        
        updatedArticle.events.add(
            ArticleUpdatedEvent(
                articleId = articleId,
                title = newTitle,
                content = newContent,
                boardId = boardId,
                updatedAt = updatedArticle.modifiedAt
            )
        )
        
        return updatedArticle
    }
    
    fun getEvents(): List<DomainEvent> = events.toList()
    
    fun clearEvents() = events.clear()
    
    companion object {
        fun create(
            articleId: ArticleId,
            title: Title,
            content: Content,
            boardId: BoardId,
            writerId: WriterId
        ): Article {
            val now = LocalDateTime.now()
            val article = Article(
                articleId = articleId,
                title = title,
                content = content,
                boardId = boardId,
                writerId = writerId,
                createdAt = now,
                modifiedAt = now
            )
            
            article.events.add(
                ArticleCreatedEvent(
                    articleId = articleId,
                    title = title,
                    content = content,
                    boardId = boardId,
                    writerId = writerId,
                    createdAt = now
                )
            )
            
            return article
        }
    }
}