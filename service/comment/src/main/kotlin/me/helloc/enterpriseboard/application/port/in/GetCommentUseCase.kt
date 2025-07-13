package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment

interface GetCommentUseCase {
    fun getPage(articleId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetCommentPageResult
    fun getScroll(articleId: Long, pageSize: Long, lastParentCommentId: Long = Comment.EMPTY_ID, lastCommentId: Long = Comment.EMPTY_ID): List<Comment>
}

data class GetCommentPageResult(
    val comments: List<Comment>,
    val visibleRangeCount: Long
)
