package me.helloc.enterpriseboard.adapter.out.persistence

import jakarta.persistence.*
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.model.RealComment
import java.time.LocalDateTime

@Entity
@Table(name = "comment")
class CommentJpaEntity(
    @Id
    val commentId: Long,
    
    @Column(nullable = false, length = 1000)
    val content: String,
    
    @Column(nullable = false)
    val parentCommentId: Long,
    
    @Column(nullable = false)
    val articleId: Long,
    
    @Column(nullable = false)
    val writerId: Long,
    
    @Column(nullable = false)
    val deleted: Boolean,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime,
    
    @Column(nullable = false)
    val modifiedAt: LocalDateTime
) {
    companion object {
        fun from(comment: Comment): CommentJpaEntity {
            return CommentJpaEntity(
                commentId = comment.commentId,
                content = comment.content,
                parentCommentId = comment.parentCommentId,
                articleId = comment.articleId,
                writerId = comment.writerId,
                deleted = comment.deleted,
                createdAt = comment.createdAt,
                modifiedAt = comment.modifiedAt
            )
        }
    }

    fun toDomainModel(): Comment {
        return RealComment(
            commentId = commentId,
            content = content,
            parentCommentId = parentCommentId,
            articleId = articleId,
            writerId = writerId,
            deleted = deleted,
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
}