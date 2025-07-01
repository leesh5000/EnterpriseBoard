package me.helloc.enterpriseboard.like.domain.event

import me.helloc.enterpriseboard.like.domain.model.*
import java.time.LocalDateTime

/**
 * 좋아요 도메인 이벤트들
 */
interface DomainEvent {
    val occurredOn: LocalDateTime
}

data class LikeAddedEvent(
    val likeId: LikeId,
    val articleId: ArticleId,
    val userId: UserId,
    val createdAt: LocalDateTime,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent

data class LikeRemovedEvent(
    val likeId: LikeId,
    val articleId: ArticleId,
    val userId: UserId,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent