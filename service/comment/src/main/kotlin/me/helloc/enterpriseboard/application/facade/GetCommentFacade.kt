package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.GetCommentPageResult
import me.helloc.enterpriseboard.application.port.`in`.GetCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.service.PageLimitCalculator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class GetCommentFacade(
    private val repository: CommentRepository
): GetCommentUseCase {

    override fun getPage(
        articleId: Long,
        page: Long,
        pageSize: Long,
        movablePageCount: Long,
    ): GetCommentPageResult {
        val offset = (page - 1) * pageSize
        val limit = PageLimitCalculator.calculate(
            page = page,
            pageSize = pageSize,
            movablePageCount = movablePageCount
        )

        val comments = repository.findAll(
            articleId = articleId,
            offset = offset,
            limit = pageSize
        )

        val limitedTotalCount = repository.countByArticleId(
            articleId = articleId,
            limit = limit
        )

        return GetCommentPageResult(
            comments = comments,
            visibleRangeCount = limitedTotalCount
        )
    }

    override fun getScroll(
        articleId: Long,
        pageSize: Long,
        lastParentCommentId: Long,
        lastCommentId: Long,
    ): List<Comment> {
        return if (lastCommentId == Comment.EMPTY_ID || lastParentCommentId == Comment.EMPTY_ID) {
            repository.findAllInfiniteScroll(
                articleId = articleId,
                limit = pageSize
            )
        } else {
            repository.findAllInfiniteScroll(
                articleId = articleId,
                lastParentCommentId = lastParentCommentId,
                lastCommentId = lastCommentId,
                limit = pageSize
            )
        }
    }
}
