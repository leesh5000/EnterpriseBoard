package me.helloc.enterpriseboard.adapter.out.persistence

import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.stereotype.Repository

@Repository
class CommentJpaAdapter(
    private val commentJpaRepository: CommentJpaRepository
) : CommentRepository {

    override fun save(comment: Comment): Comment {
        val entity = CommentJpaEntity.from(comment)
        val savedEntity = commentJpaRepository.save(entity)
        return savedEntity.toDomainModel()
    }

    override fun findById(commentId: Long): Comment {
        return commentJpaRepository.findById(commentId)
            .map { it.toDomainModel() }
            .orElse(Comment.nullComment())
    }

    override fun findByArticleId(articleId: Long): List<Comment> {
        return commentJpaRepository.findByArticleIdOrderByCommentIdDesc(articleId)
            .map { it.toDomainModel() }
    }

    override fun findByWriterId(writerId: Long): List<Comment> {
        return commentJpaRepository.findByWriterIdOrderByCommentIdDesc(writerId)
            .map { it.toDomainModel() }
    }

    override fun deleteById(commentId: Long) {
        commentJpaRepository.deleteById(commentId)
    }

    override fun existsById(commentId: Long): Boolean {
        return commentJpaRepository.existsById(commentId)
    }

    override fun findAll(
        articleId: Long,
        offset: Long,
        limit: Long
    ): List<Comment> {
        return commentJpaRepository.findByArticleIdWithPagination(articleId, offset, limit)
            .map { it.toDomainModel() }
    }

    override fun countByArticleId(articleId: Long, limit: Long): Long {
        return commentJpaRepository.countByArticleIdWithLimit(articleId, limit)
    }

    override fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long
    ): List<Comment> {
        return commentJpaRepository.findByArticleIdForInfiniteScroll(articleId, limit)
            .map { it.toDomainModel() }
    }

    override fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long,
        lastCommentId: Long
    ): List<Comment> {
        return commentJpaRepository.findByArticleIdForInfiniteScrollWithCursor(articleId, lastCommentId, limit)
            .map { it.toDomainModel() }
    }
}
