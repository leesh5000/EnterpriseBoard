package me.helloc.enterpriseboard.comment.domain.model

import me.helloc.enterpriseboard.comment.domain.event.CommentCreatedEvent
import me.helloc.enterpriseboard.comment.domain.event.CommentDeletedEvent
import me.helloc.enterpriseboard.comment.domain.event.CommentUpdatedEvent
import me.helloc.enterpriseboard.comment.domain.event.DomainEvent
import java.time.LocalDateTime

/**
 * 댓글 도메인 모델
 */
data class Comment(
    val commentId: CommentId,
    val articleId: ArticleId,
    val content: CommentContent,
    val writerId: WriterId,
    val parentCommentId: CommentId?, // 대댓글을 위한 부모 댓글 ID
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    private val events: MutableList<DomainEvent> = mutableListOf()
) {
    
    fun update(newContent: CommentContent): Comment {
        val updatedComment = copy(
            content = newContent,
            modifiedAt = LocalDateTime.now()
        )
        
        updatedComment.events.add(
            CommentUpdatedEvent(
                commentId = commentId,
                articleId = articleId,
                content = newContent,
                updatedAt = updatedComment.modifiedAt
            )
        )
        
        return updatedComment
    }
    
    fun isReply(): Boolean = parentCommentId != null
    
    fun getEvents(): List<DomainEvent> = events.toList()
    
    fun clearEvents() = events.clear()
    
    companion object {
        fun create(
            commentId: CommentId,
            articleId: ArticleId,
            content: CommentContent,
            writerId: WriterId,
            parentCommentId: CommentId? = null
        ): Comment {
            val now = LocalDateTime.now()
            val comment = Comment(
                commentId = commentId,
                articleId = articleId,
                content = content,
                writerId = writerId,
                parentCommentId = parentCommentId,
                createdAt = now,
                modifiedAt = now
            )
            
            comment.events.add(
                CommentCreatedEvent(
                    commentId = commentId,
                    articleId = articleId,
                    content = content,
                    writerId = writerId,
                    parentCommentId = parentCommentId,
                    createdAt = now
                )
            )
            
            return comment
        }
    }
}