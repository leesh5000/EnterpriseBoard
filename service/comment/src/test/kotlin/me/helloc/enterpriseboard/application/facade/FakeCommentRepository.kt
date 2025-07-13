package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment

class FakeCommentRepository : CommentRepository {
    private val storage = mutableMapOf<Long, Comment>()

    override fun save(comment: Comment): Comment {
        storage[comment.commentId] = comment
        return comment
    }

    override fun findById(commentId: Long): java.util.Optional<Comment> {
        return java.util.Optional.ofNullable(storage[commentId])
    }

    override fun getById(commentId: Long): Comment {
        return storage[commentId] ?:
            throw ErrorCode.COMMENT_NOT_FOUND.toException("commentId" to commentId)
    }


    override fun deleteById(commentId: Long) {
        storage.remove(commentId)
    }

    override fun existsById(commentId: Long): Boolean {
        return storage.containsKey(commentId)
    }


    override fun countBy(articleId: Long, parentCommentId: Long, limit: Long): Long {
        return storage.values
            .filter { it.articleId == articleId && it.parentCommentId == parentCommentId }
            .take(limit.toInt())
            .count()
            .toLong()
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
        lastParentCommentId: Long,
        lastCommentId: Long,
        limit: Long,
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
