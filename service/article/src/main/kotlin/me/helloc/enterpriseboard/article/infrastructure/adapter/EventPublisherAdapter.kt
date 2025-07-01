package me.helloc.enterpriseboard.article.infrastructure.adapter

import me.helloc.enterpriseboard.article.application.port.output.EventPublisher
import me.helloc.enterpriseboard.article.domain.event.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 이벤트 발행 어댑터 - 현재는 로깅만 수행, 추후 메시지 브로커 연동 가능
 */
@Component
class EventPublisherAdapter : EventPublisher {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    override fun publish(event: DomainEvent) {
        logger.info("Publishing event: $event")
        // TODO: 실제 이벤트 발행 로직 구현 (예: Kafka, RabbitMQ 등)
    }
    
    override fun publishAll(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }
}