package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.application.port.`in`.*
import me.helloc.enterpriseboard.domain.model.Comment
import java.time.LocalDateTime

class FakeCreateCommentUseCase : CreateCommentUseCase {
    var lastCommand: CreateCommentCommand? = null
    var commentToReturn: Comment = createDefaultComment()

    override fun create(command: CreateCommentCommand): Comment {
        lastCommand = command
        return commentToReturn
    }

    private fun createDefaultComment() = Comment(
        commentId = 1L,
        content = "테스트 댓글",
        parentCommentId = 1L,
        articleId = 100L,
        writerId = 200L,
        deleted = false,
        createdAt = LocalDateTime.now(),
        modifiedAt = LocalDateTime.now()
    )
}

class FakeUpdateCommentUseCase : UpdateCommentUseCase {
    var lastCommand: UpdateCommentCommand? = null
    var shouldThrowException = false
    var commentToReturn: Comment = createDefaultComment()

    override fun update(command: UpdateCommentCommand): Comment {
        lastCommand = command
        if (shouldThrowException) {
            throw NoSuchElementException("Comment not found with id: ${command.commentId}")
        }
        return commentToReturn
    }

    private fun createDefaultComment() = Comment(
        commentId = 1L,
        content = "수정된 댓글",
        parentCommentId = 1L,
        articleId = 100L,
        writerId = 200L,
        deleted = false,
        createdAt = LocalDateTime.now().minusDays(1),
        modifiedAt = LocalDateTime.now()
    )
}

class FakeGetCommentUseCase : GetCommentUseCase {
    private val storage = mutableMapOf<Long, Comment>()

    fun addComment(comment: Comment) {
        storage[comment.commentId] = comment
    }

    fun clear() {
        storage.clear()
    }

    override fun getById(commentId: Long): Comment? {
        return storage[commentId]
    }

    override fun getByArticleId(articleId: Long): List<Comment> {
        return storage.values.filter { it.articleId == articleId }
    }

    override fun getByWriterId(writerId: Long): List<Comment> {
        return storage.values.filter { it.writerId == writerId }
    }

    override fun getPage(query: GetCommentPageQuery): GetCommentPageResult {
        val offset = (query.page - 1) * query.pageSize
        val comments = storage.values
            .filter { it.articleId == query.articleId }
            .sortedByDescending { it.commentId }
            .drop(offset.toInt())
            .take(query.pageSize.toInt())

        val totalCount = storage.values
            .filter { it.articleId == query.articleId }
            .count()
            .toLong()

        return GetCommentPageResult(
            comments = comments,
            count = totalCount
        )
    }

    override fun getScroll(query: GetCommentScrollQuery): List<Comment> {
        return if (query.lastCommentId == 0L) {
            storage.values
                .filter { it.articleId == query.articleId }
                .sortedByDescending { it.commentId }
                .take(query.pageSize.toInt())
        } else {
            storage.values
                .filter { it.articleId == query.articleId && it.commentId < query.lastCommentId }
                .sortedByDescending { it.commentId }
                .take(query.pageSize.toInt())
        }
    }
}

class FakeDeleteCommentUseCase : DeleteCommentUseCase {
    var deletedCommentIds = mutableListOf<Long>()
    var shouldThrowException = false

    override fun delete(commentId: Long) {
        if (shouldThrowException) {
            throw NoSuchElementException("Comment not found with id: $commentId")
        }
        deletedCommentIds.add(commentId)
    }

    fun wasDeleted(commentId: Long): Boolean {
        return commentId in deletedCommentIds
    }

    fun reset() {
        deletedCommentIds.clear()
        shouldThrowException = false
    }
}