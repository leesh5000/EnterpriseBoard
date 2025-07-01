package me.helloc.enterpriseboard.comment.domain.event

import me.helloc.enterpriseboard.comment.domain.model.*
import java.time.LocalDateTime

/**
 * 댓글 도메인 이벤트들
 */
interface DomainEvent {
    val occurredOn: LocalDateTime
}

data class CommentCreatedEvent(
    val commentId: CommentId,
    val articleId: ArticleId,
    val content: CommentContent,
    val writerId: WriterId,
    val parentCommentId: CommentId?,
    val createdAt: LocalDateTime,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent

data class CommentUpdatedEvent(
    val commentId: CommentId,
    val articleId: ArticleId,
    val content: CommentContent,
    val updatedAt: LocalDateTime,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent

data class CommentDeletedEvent(
    val commentId: CommentId,
    val articleId: ArticleId,
    override val occurredOn: LocalDateTime = LocalDateTime.now()
) : DomainEvent