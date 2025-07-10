package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment

class FakeCommentRepository : CommentRepository {
    private val storage = mutableMapOf<Long, Comment>()

    override fun save(comment: Comment): Comment {
        storage[comment.commentId] = comment
        return comment
    }

    override fun findById(commentId: Long): Comment {
        return storage[commentId] ?: Comment.empty()
    }

    override fun findByArticleId(articleId: Long): List<Comment> {
        return storage.values.filter { it.articleId == articleId }
    }

    override fun findByWriterId(writerId: Long): List<Comment> {
        return storage.values.filter { it.writerId == writerId }
    }

    override fun deleteById(commentId: Long) {
        storage.remove(commentId)
    }

    override fun existsById(commentId: Long): Boolean {
        return storage.containsKey(commentId)
    }

    override fun findAll(
        articleId: Long,
        offset: Long,
        limit: Long,
    ): List<Comment> {
        return storage.values
            .filter { it.articleId == articleId }
            .sortedByDescending { it.commentId }
            .drop(offset.toInt())
            .take(limit.toInt())
    }

    override fun countByArticleId(articleId: Long, limit: Long): Long {
        return storage.values
            .filter { it.articleId == articleId }
            .take(limit.toInt())
            .count()
            .toLong()
    }

    override fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long,
    ): List<Comment> {
        return storage.values
            .filter { it.articleId == articleId }
            .sortedByDescending { it.commentId }
            .take(limit.toInt())
    }

    override fun findAllInfiniteScroll(
        articleId: Long,
        limit: Long,
        lastCommentId: Long,
    ): List<Comment> {
        return storage.values
            .filter { it.articleId == articleId && it.commentId < lastCommentId }
            .sortedByDescending { it.commentId }
            .take(limit.toInt())
    }

    // 테스트를 위한 헬퍼 메서드
    fun clear() {
        storage.clear()
    }

    fun getAll(): List<Comment> {
        return storage.values.toList()
    }
}