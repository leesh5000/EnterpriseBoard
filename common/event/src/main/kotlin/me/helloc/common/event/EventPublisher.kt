package me.helloc.common.event

/**
 * 공통 이벤트 발행자 인터페이스
 */
interface EventPublisher {
    fun publish(event: DomainEvent, metadata: EventMetadata = EventMetadata())
    fun publishAll(events: List<DomainEvent>, metadata: EventMetadata = EventMetadata())
    fun publishAsync(event: DomainEvent, metadata: EventMetadata = EventMetadata())
    fun publishAllAsync(events: List<DomainEvent>, metadata: EventMetadata = EventMetadata())
}

/**
 * 이벤트 핸들러 인터페이스
 */
interface EventHandler<T : DomainEvent> {
    fun handle(event: T, metadata: EventMetadata)
    fun canHandle(event: DomainEvent): Boolean
}

/**
 * 이벤트 스토어 인터페이스
 */
interface EventStore {
    fun save(event: DomainEvent, metadata: EventMetadata)
    fun saveAll(events: List<DomainEvent>, metadata: EventMetadata)
    fun findByAggregateId(aggregateId: String): List<StoredEvent>
    fun findByEventType(eventType: String): List<StoredEvent>
    fun findByTimeRange(startTime: java.time.LocalDateTime, endTime: java.time.LocalDateTime): List<StoredEvent>
}

/**
 * 저장된 이벤트
 */
data class StoredEvent(
    val event: DomainEvent,
    val metadata: EventMetadata,
    val storedAt: java.time.LocalDateTime = java.time.LocalDateTime.now()
)