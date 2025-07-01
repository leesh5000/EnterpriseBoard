package me.helloc.enterpriseboard.article.application.port.output

import me.helloc.enterpriseboard.article.domain.event.DomainEvent

/**
 * 출력 포트 - 이벤트 발행을 위한 인터페이스
 */
interface EventPublisher {
    fun publish(event: DomainEvent)
    fun publishAll(events: List<DomainEvent>)
}