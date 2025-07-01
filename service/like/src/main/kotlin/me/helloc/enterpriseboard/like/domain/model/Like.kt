package me.helloc.enterpriseboard.like.domain.model

import me.helloc.enterpriseboard.like.domain.event.LikeAddedEvent
import me.helloc.enterpriseboard.like.domain.event.LikeRemovedEvent
import me.helloc.enterpriseboard.like.domain.event.DomainEvent
import java.time.LocalDateTime

/**
 * 좋아요 도메인 모델
 */
data class Like(
    val likeId: LikeId,
    val articleId: ArticleId,
    val userId: UserId,
    val createdAt: LocalDateTime,
    private val events: MutableList<DomainEvent> = mutableListOf()
) {
    
    fun getEvents(): List<DomainEvent> = events.toList()
    
    fun clearEvents() = events.clear()
    
    companion object {
        fun create(
            likeId: LikeId,
            articleId: ArticleId,
            userId: UserId
        ): Like {
            val now = LocalDateTime.now()
            val like = Like(
                likeId = likeId,
                articleId = articleId,
                userId = userId,
                createdAt = now
            )
            
            like.events.add(
                LikeAddedEvent(
                    likeId = likeId,
                    articleId = articleId,
                    userId = userId,
                    createdAt = now
                )
            )
            
            return like
        }
        
        fun createRemovedEvent(
            likeId: LikeId,
            articleId: ArticleId,
            userId: UserId
        ): LikeRemovedEvent {
            return LikeRemovedEvent(
                likeId = likeId,
                articleId = articleId,
                userId = userId
            )
        }
    }
}