package me.helloc.enterpriseboard.comment.application.port.output

import me.helloc.enterpriseboard.comment.domain.event.DomainEvent

/**
 * 댓글 이벤트 발행 포트
 */
interface EventPublisher {
    fun publish(event: DomainEvent)
    fun publishAll(events: List<DomainEvent>)
}