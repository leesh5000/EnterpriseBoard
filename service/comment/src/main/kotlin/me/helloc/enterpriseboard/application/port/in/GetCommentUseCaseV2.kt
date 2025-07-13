package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.model.CommentV2

interface GetCommentUseCaseV2 {
    fun getPage(articleId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetCommentPageResultV2
    fun getScroll(articleId: Long, pageSize: Long, lastParentCommentId: Long = Comment.EMPTY_ID, lastCommentId: Long = Comment.EMPTY_ID): List<CommentV2>
}

data class GetCommentPageResultV2(
    val comments: List<CommentV2>,
    val visibleRangeCount: Long
)
