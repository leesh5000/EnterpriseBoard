package me.helloc.common.event.impl

import me.helloc.common.event.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

/**
 * 인메모리 이벤트 발행자 구현체 (개발/테스트용)
 */
@Component
class InMemoryEventPublisher(
    private val eventStore: EventStore? = null
) : EventPublisher {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val handlers = CopyOnWriteArrayList<EventHandler<DomainEvent>>()
    
    override fun publish(event: DomainEvent, metadata: EventMetadata) {
        logger.info("Publishing event: ${event.eventType} for aggregate: ${event.aggregateId}")
        
        // 이벤트 저장
        eventStore?.save(event, metadata)
        
        // 핸들러들에게 이벤트 전달
        handlers.forEach { handler ->
            try {
                if (handler.canHandle(event)) {
                    handler.handle(event, metadata)
                }
            } catch (e: Exception) {
                logger.error("Error handling event ${event.eventType}", e)
            }
        }
    }
    
    override fun publishAll(events: List<DomainEvent>, metadata: EventMetadata) {
        events.forEach { event ->
            publish(event, metadata)
        }
    }
    
    override fun publishAsync(event: DomainEvent, metadata: EventMetadata) {
        CompletableFuture.runAsync {
            publish(event, metadata)
        }
    }
    
    override fun publishAllAsync(events: List<DomainEvent>, metadata: EventMetadata) {
        CompletableFuture.runAsync {
            publishAll(events, metadata)
        }
    }
    
    fun registerHandler(handler: EventHandler<DomainEvent>) {
        handlers.add(handler)
        logger.info("Registered event handler: ${handler::class.simpleName}")
    }
    
    fun unregisterHandler(handler: EventHandler<DomainEvent>) {
        handlers.remove(handler)
        logger.info("Unregistered event handler: ${handler::class.simpleName}")
    }
}