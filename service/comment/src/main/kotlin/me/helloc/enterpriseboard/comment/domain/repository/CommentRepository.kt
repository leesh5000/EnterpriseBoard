package me.helloc.enterpriseboard.comment.domain.repository

import me.helloc.enterpriseboard.comment.domain.model.*
import java.util.Optional

/**
 * 댓글 도메인 리포지토리 인터페이스
 */
interface CommentRepository {
    fun save(comment: Comment): Comment
    fun findById(commentId: CommentId): Optional<Comment>
    fun findByArticleId(articleId: ArticleId): List<Comment>
    fun findByWriterId(writerId: WriterId): List<Comment>
    fun findRepliesByParentCommentId(parentCommentId: CommentId): List<Comment>
    fun deleteById(commentId: CommentId)
    fun existsById(commentId: CommentId): Boolean
    fun countByArticleId(articleId: ArticleId): Long
}