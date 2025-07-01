package me.helloc.common.event.impl

import com.fasterxml.jackson.databind.ObjectMapper
import me.helloc.common.event.*
import me.helloc.common.event.outbox.OutboxEvent
import me.helloc.common.event.outbox.OutboxEventRepository
import me.helloc.common.event.outbox.OutboxEventStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

/**
 * 아웃박스 패턴을 사용하는 이벤트 발행자
 */
@Component
class OutboxEventPublisher(
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper = ObjectMapper()
) : EventPublisher {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    override fun publish(event: DomainEvent, metadata: EventMetadata) {
        try {
            val outboxEvent = createOutboxEvent(event, metadata)
            outboxEventRepository.save(outboxEvent)
            logger.debug("Event stored in outbox: ${event.eventType} for aggregate: ${event.aggregateId}")
        } catch (e: Exception) {
            logger.error("Failed to store event in outbox: ${event.eventType}", e)
            throw e
        }
    }
    
    override fun publishAll(events: List<DomainEvent>, metadata: EventMetadata) {
        events.forEach { event ->
            publish(event, metadata)
        }
    }
    
    override fun publishAsync(event: DomainEvent, metadata: EventMetadata) {
        // 아웃박스 패턴에서는 동기/비동기 구분이 의미가 없음
        // 실제 발행은 별도 프로세서가 담당
        publish(event, metadata)
    }
    
    override fun publishAllAsync(events: List<DomainEvent>, metadata: EventMetadata) {
        publishAll(events, metadata)
    }
    
    private fun createOutboxEvent(event: DomainEvent, metadata: EventMetadata): OutboxEvent {
        return OutboxEvent(
            id = UUID.randomUUID().toString(),
            aggregateId = event.aggregateId,
            eventType = event.eventType,
            eventData = objectMapper.writeValueAsString(event),
            metadata = objectMapper.writeValueAsString(metadata),
            occurredOn = event.occurredOn,
            status = OutboxEventStatus.PENDING
        )
    }
}

/**
 * 인메모리 아웃박스 리포지토리 구현체 (개발/테스트용)
 */
@Component
class InMemoryOutboxEventRepository : OutboxEventRepository {
    
    private val events = mutableMapOf<String, OutboxEvent>()
    
    override fun save(outboxEvent: OutboxEvent): OutboxEvent {
        events[outboxEvent.id] = outboxEvent
        return outboxEvent
    }
    
    override fun findById(id: String): OutboxEvent? {
        return events[id]
    }
    
    override fun findPendingEvents(limit: Int): List<OutboxEvent> {
        return events.values
            .filter { it.status == OutboxEventStatus.PENDING }
            .sortedBy { it.occurredOn }
            .take(limit)
    }
    
    override fun findFailedEvents(maxRetryCount: Int): List<OutboxEvent> {
        return events.values
            .filter { it.status == OutboxEventStatus.FAILED && it.retryCount < maxRetryCount }
            .sortedBy { it.occurredOn }
    }
    
    override fun findByAggregateId(aggregateId: String): List<OutboxEvent> {
        return events.values
            .filter { it.aggregateId == aggregateId }
            .sortedBy { it.occurredOn }
    }
    
    override fun deleteProcessedEventsBefore(cutoffTime: LocalDateTime) {
        val toRemove = events.values
            .filter { it.status == OutboxEventStatus.PROCESSED && it.processedAt?.isBefore(cutoffTime) == true }
            .map { it.id }
        
        toRemove.forEach { events.remove(it) }
    }
    
    override fun updateStatus(id: String, status: OutboxEventStatus) {
        events[id]?.let { event ->
            val updatedEvent = when (status) {
                OutboxEventStatus.PROCESSED -> event.markAsProcessed()
                OutboxEventStatus.FAILED -> event.markAsFailed("Processing failed")
                OutboxEventStatus.RETRYING -> event.markAsRetrying()
                else -> event.copy(status = status)
            }
            events[id] = updatedEvent
        }
    }
}