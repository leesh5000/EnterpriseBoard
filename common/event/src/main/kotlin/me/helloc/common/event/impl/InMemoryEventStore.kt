package me.helloc.common.event.impl

import me.helloc.common.event.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 인메모리 이벤트 스토어 구현체 (개발/테스트용)
 */
@Component
class InMemoryEventStore : EventStore {
    
    private val events = CopyOnWriteArrayList<StoredEvent>()
    private val eventsByAggregateId = ConcurrentHashMap<String, MutableList<StoredEvent>>()
    private val eventsByType = ConcurrentHashMap<String, MutableList<StoredEvent>>()
    
    override fun save(event: DomainEvent, metadata: EventMetadata) {
        val storedEvent = StoredEvent(event, metadata)
        
        // 전체 이벤트 목록에 추가
        events.add(storedEvent)
        
        // Aggregate ID별 인덱스에 추가
        eventsByAggregateId.computeIfAbsent(event.aggregateId) { 
            CopyOnWriteArrayList() 
        }.add(storedEvent)
        
        // 이벤트 타입별 인덱스에 추가
        eventsByType.computeIfAbsent(event.eventType) { 
            CopyOnWriteArrayList() 
        }.add(storedEvent)
    }
    
    override fun saveAll(events: List<DomainEvent>, metadata: EventMetadata) {
        events.forEach { event ->
            save(event, metadata)
        }
    }
    
    override fun findByAggregateId(aggregateId: String): List<StoredEvent> {
        return eventsByAggregateId[aggregateId]?.toList() ?: emptyList()
    }
    
    override fun findByEventType(eventType: String): List<StoredEvent> {
        return eventsByType[eventType]?.toList() ?: emptyList()
    }
    
    override fun findByTimeRange(
        startTime: LocalDateTime, 
        endTime: LocalDateTime
    ): List<StoredEvent> {
        return events.filter { storedEvent ->
            val eventTime = storedEvent.event.occurredOn
            eventTime.isAfter(startTime) && eventTime.isBefore(endTime)
        }
    }
    
    fun getAllEvents(): List<StoredEvent> {
        return events.toList()
    }
    
    fun getEventCount(): Int {
        return events.size
    }
    
    fun clear() {
        events.clear()
        eventsByAggregateId.clear()
        eventsByType.clear()
    }
}