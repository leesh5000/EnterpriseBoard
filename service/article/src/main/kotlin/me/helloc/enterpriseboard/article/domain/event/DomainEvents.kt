package me.helloc.enterpriseboard.article.domain.event

import me.helloc.enterpriseboard.article.domain.model.*
import java.time.LocalDateTime

/**
 * 도메인 이벤트 인터페이스
 */
interface DomainEvent {
    val occurredOn: LocalDateTime
}

/**
 * 게시글 생성 이벤트
 */
data class ArticleCreatedEvent(
    val articleId: ArticleId,
    val title: Title,
    val content: Content,
    val boardId: BoardId,
    val writerId: WriterId,
    val createdAt: LocalDateTime,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent

/**
 * 게시글 수정 이벤트
 */
data class ArticleUpdatedEvent(
    val articleId: ArticleId,
    val title: Title,
    val content: Content,
    val boardId: BoardId,
    val updatedAt: LocalDateTime,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent

/**
 * 게시글 삭제 이벤트
 */
data class ArticleDeletedEvent(
    val articleId: ArticleId,
    val boardId: BoardId,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent