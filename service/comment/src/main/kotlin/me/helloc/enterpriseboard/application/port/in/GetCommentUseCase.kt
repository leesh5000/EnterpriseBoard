package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment

interface GetCommentUseCase {
    fun getById(commentId: Long): Comment?
    fun getByArticleId(articleId: Long): List<Comment>
    fun getByWriterId(writerId: Long): List<Comment>
    fun getPage(articleId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetCommentPageResult
    fun getScroll(articleId: Long, pageSize: Long, lastCommentId: Long): List<Comment>
}


data class GetCommentPageResult(
    val comments: List<Comment>,
    val limitedTotalCount: Long
)