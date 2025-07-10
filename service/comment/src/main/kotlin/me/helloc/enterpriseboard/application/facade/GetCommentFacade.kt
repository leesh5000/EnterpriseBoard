package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.GetCommentPageResult
import me.helloc.enterpriseboard.application.port.`in`.GetCommentUseCase
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.service.PageLimitCalculator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetCommentFacade(
    private val commentRepository: CommentRepository
) : GetCommentUseCase {

    override fun getById(commentId: Long): Comment {
        return commentRepository.findById(commentId)
    }

    override fun getByArticleId(articleId: Long): List<Comment> {
        return commentRepository.findByArticleId(articleId)
    }

    override fun getByWriterId(writerId: Long): List<Comment> {
        return commentRepository.findByWriterId(writerId)
    }

    override fun getPage(articleId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetCommentPageResult {
        val offset = (page - 1) * pageSize
        val limit = PageLimitCalculator.calculate(
            page = page,
            pageSize = pageSize,
            movablePageCount = movablePageCount
        )

        val comments = commentRepository.findAll(
            articleId = articleId,
            offset = offset,
            limit = pageSize
        )

        val limitedTotalCount = commentRepository.countByArticleId(
            articleId = articleId,
            limit = limit
        )

        return GetCommentPageResult(
            comments = comments,
            limitedTotalCount = limitedTotalCount
        )
    }

    override fun getScroll(articleId: Long, pageSize: Long, lastCommentId: Long): List<Comment> {
        val comments = if (lastCommentId == 0L) {
            commentRepository.findAllInfiniteScroll(
                articleId = articleId,
                limit = pageSize
            )
        } else {
            commentRepository.findAllInfiniteScroll(
                articleId = articleId,
                limit = pageSize,
                lastCommentId = lastCommentId
            )
        }

        return comments
    }
}