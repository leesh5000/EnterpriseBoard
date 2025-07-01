package me.helloc.common.event.outbox

import me.helloc.common.event.DomainEvent
import me.helloc.common.event.EventMetadata
import java.time.LocalDateTime

/**
 * 아웃박스 패턴을 위한 이벤트 모델
 */
data class OutboxEvent(
    val id: String,
    val aggregateId: String,
    val eventType: String,
    val eventData: String, // JSON 직렬화된 이벤트 데이터
    val metadata: String, // JSON 직렬화된 메타데이터
    val occurredOn: LocalDateTime,
    val processedAt: LocalDateTime? = null,
    val status: OutboxEventStatus = OutboxEventStatus.PENDING,
    val retryCount: Int = 0,
    val lastError: String? = null
) {
    
    fun markAsProcessed(): OutboxEvent {
        return copy(
            status = OutboxEventStatus.PROCESSED,
            processedAt = LocalDateTime.now()
        )
    }
    
    fun markAsFailed(error: String): OutboxEvent {
        return copy(
            status = OutboxEventStatus.FAILED,
            retryCount = retryCount + 1,
            lastError = error
        )
    }
    
    fun markAsRetrying(): OutboxEvent {
        return copy(
            status = OutboxEventStatus.RETRYING
        )
    }
}

/**
 * 아웃박스 이벤트 상태
 */
enum class OutboxEventStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    FAILED,
    RETRYING,
    DEAD_LETTER
}

/**
 * 아웃박스 리포지토리 인터페이스
 */
interface OutboxEventRepository {
    fun save(outboxEvent: OutboxEvent): OutboxEvent
    fun findById(id: String): OutboxEvent?
    fun findPendingEvents(limit: Int = 100): List<OutboxEvent>
    fun findFailedEvents(maxRetryCount: Int = 3): List<OutboxEvent>
    fun findByAggregateId(aggregateId: String): List<OutboxEvent>
    fun deleteProcessedEventsBefore(cutoffTime: LocalDateTime)
    fun updateStatus(id: String, status: OutboxEventStatus)
}

/**
 * 아웃박스 이벤트 프로세서
 */
interface OutboxEventProcessor {
    fun processEvents()
    fun retryFailedEvents()
    fun cleanupProcessedEvents(olderThanDays: Long = 7)
}