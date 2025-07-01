package me.helloc.common.event

import java.time.LocalDateTime
import java.util.*

/**
 * 공통 도메인 이벤트 인터페이스
 */
interface DomainEvent {
    val eventId: String
    val occurredOn: LocalDateTime
    val eventType: String
    val aggregateId: String
    val version: Int
}

/**
 * 기본 도메인 이벤트 구현체
 */
abstract class BaseDomainEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val occurredOn: LocalDateTime = LocalDateTime.now(),
    override val aggregateId: String,
    override val version: Int = 1
) : DomainEvent {
    override val eventType: String = this::class.simpleName ?: "UnknownEvent"
}

/**
 * 이벤트 메타데이터
 */
data class EventMetadata(
    val correlationId: String = UUID.randomUUID().toString(),
    val causationId: String? = null,
    val userId: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val source: String? = null,
    val version: String = "1.0"
)