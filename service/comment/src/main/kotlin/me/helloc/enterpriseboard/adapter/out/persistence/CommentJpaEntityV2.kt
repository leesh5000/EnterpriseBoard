package me.helloc.enterpriseboard.adapter.out.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.helloc.enterpriseboard.domain.model.CommentPath
import me.helloc.enterpriseboard.domain.model.CommentV2
import java.time.LocalDateTime

@Table(name = "comment_v2")
@Entity
open class CommentJpaEntityV2(
    @Id
    val commentId: Long,

    @Column(nullable = false, length = 1000)
    val content: String,

    @Column(nullable = false)
    val articleId: Long,

    @Column(nullable = false)
    val writerId: Long,

    @Column(nullable = false)
    val path: String,

    @Column(nullable = false)
    val deleted: Boolean,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(comment: CommentV2): CommentJpaEntityV2 {
            return CommentJpaEntityV2(
                commentId = comment.commentId,
                content = comment.content,
                articleId = comment.articleId,
                writerId = comment.writerId,
                path = comment.commentPath.path,
                deleted = comment.deleted,
                createdAt = comment.createdAt
            )
        }
    }

    fun toDomainModel(): CommentV2 {
        return CommentV2(
            commentId = commentId,
            content = content,
            commentPath = CommentPath(path),
            articleId = articleId,
            writerId = writerId,
            deleted = deleted,
            createdAt = createdAt
        )
    }
}

