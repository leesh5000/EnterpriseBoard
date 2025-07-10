package me.helloc.enterpriseboard.application.port.`in`

import me.helloc.enterpriseboard.domain.model.Comment

interface GetCommentUseCase {
    fun getById(commentId: Long): Comment
    fun getByArticleId(articleId: Long): List<Comment>
    fun getByWriterId(writerId: Long): List<Comment>
    fun getPage(query: GetCommentPageQuery): GetCommentPageResult
    fun getScroll(query: GetCommentScrollQuery): List<Comment>
}

data class GetCommentPageQuery(
    val articleId: Long,
    val page: Long,
    val pageSize: Long,
    val movablePageCount: Long
)

data class GetCommentScrollQuery(
    val articleId: Long,
    val pageSize: Long,
    val lastCommentId: Long
)

data class GetCommentPageResult(
    val comments: List<Comment>,
    val count: Long
)