package me.helloc.enterpriseboard.view.domain.event

import me.helloc.enterpriseboard.view.domain.model.*
import java.time.LocalDateTime

/**
 * 조회수 도메인 이벤트들
 */
interface DomainEvent {
    val occurredOn: LocalDateTime
}

data class ViewCountIncreasedEvent(
    val viewId: ViewId,
    val articleId: ArticleId,
    val userId: UserId?,
    val ipAddress: IpAddress,
    val createdAt: LocalDateTime,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent