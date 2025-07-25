package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.application.port.`in`.CreateCommentUseCase
import me.helloc.enterpriseboard.application.port.`in`.DeleteCommentUseCase
import me.helloc.enterpriseboard.domain.model.Comment
import java.time.LocalDateTime

class FakeCreateCommentUseCase : CreateCommentUseCase {
    var lastContent: String? = null
    var lastParentCommentId: Long? = null
    var lastArticleId: Long? = null
    var lastWriterId: Long? = null
    var commentToReturn: Comment = createDefaultComment()

    override fun create(content: String, parentCommentId: Long, articleId: Long, writerId: Long): Comment {
        lastContent = content
        lastParentCommentId = parentCommentId
        lastArticleId = articleId
        lastWriterId = writerId
        return commentToReturn
    }

    private fun createDefaultComment() = Comment(
        commentId = 1L,
        content = "테스트 댓글",
        parentCommentId = 1L,
        articleId = 100L,
        writerId = 200L,
        deleted = false,
        createdAt = LocalDateTime.now()
    )
}

class FakeDeleteCommentUseCase : DeleteCommentUseCase {
    var lastDeletedCommentId: Long? = null
    val deletedCommentIds = mutableListOf<Long>()
    var deleteCallCount = 0

    override fun delete(commentId: Long) {
        lastDeletedCommentId = commentId
        deletedCommentIds.add(commentId)
        deleteCallCount++
    }
}