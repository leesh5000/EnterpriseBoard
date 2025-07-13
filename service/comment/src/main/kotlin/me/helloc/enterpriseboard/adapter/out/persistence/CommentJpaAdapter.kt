package me.helloc.enterpriseboard.adapter.out.persistence

import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class CommentJpaAdapter(
    private val commentJpaRepository: CommentJpaRepository
) : CommentRepository {

    override fun save(comment: Comment): Comment {
        val entity = CommentJpaEntity.from(comment)
        val savedEntity = commentJpaRepository.save(entity)
        return savedEntity.toDomainModel()
    }

    override fun findById(commentId: Long): Optional<Comment> {
        return commentJpaRepository.findById(commentId)
            .map { it.toDomainModel() }
    }

    override fun getById(commentId: Long): Comment {
        return commentJpaRepository.findById(commentId)
            .map { it.toDomainModel() }
            .orElseThrow {
                ErrorCode.COMMENT_NOT_FOUND.toException("commentId" to commentId)
            }
    }

    override fun deleteById(commentId: Long) {
        commentJpaRepository.deleteById(commentId)
    }

    override fun existsById(commentId: Long): Boolean {
        return commentJpaRepository.existsById(commentId)
    }

    override fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long {
        return commentJpaRepository.countBy(articleId, parentCommentId, limit)
    }

    override fun findAll(
        articleId: Long,
        offset: Long,
        limit: Long,
    ): List<Comment> {
        return commentJpaRepository.findAll(articleId, offset, limit)
            .map { it.toDomainModel() }
    }

    override fun countByArticleId(articleId: Long, limit: Long): Long {
        return commentJpaRepository.count(articleId, limit)
    }

    override fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long,
    ): List<Comment> {
        return commentJpaRepository.findAllInfiniteScroll(articleId, limit)
            .map { it.toDomainModel() }
    }

    override fun findAllInfiniteScroll(
        articleId: Long,
        lastParentCommentId: Long,
        lastCommentId: Long,
        limit: Long,
    ): List<Comment> {
        return commentJpaRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit)
            .map { it.toDomainModel() }
    }
}
