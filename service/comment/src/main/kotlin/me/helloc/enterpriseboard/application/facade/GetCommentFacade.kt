package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.GetCommentPageQuery
import me.helloc.enterpriseboard.application.port.`in`.GetCommentPageResult
import me.helloc.enterpriseboard.application.port.`in`.GetCommentScrollQuery
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

    override fun getPage(query: GetCommentPageQuery): GetCommentPageResult {
        val offset = (query.page - 1) * query.pageSize
        val limit = PageLimitCalculator.calculate(
            page = query.page,
            pageSize = query.pageSize,
            movablePageCount = query.movablePageCount
        )

        val comments = commentRepository.findAll(
            articleId = query.articleId,
            offset = offset,
            limit = query.pageSize
        )

        val count = commentRepository.countByArticleId(
            articleId = query.articleId,
            limit = limit
        )

        return GetCommentPageResult(
            comments = comments,
            count = count
        )
    }

    override fun getScroll(query: GetCommentScrollQuery): List<Comment> {
        val comments = if (query.lastCommentId == 0L) {
            commentRepository.findAllInfiniteScroll(
                articleId = query.articleId,
                limit = query.pageSize
            )
        } else {
            commentRepository.findAllInfiniteScroll(
                articleId = query.articleId,
                limit = query.pageSize,
                lastCommentId = query.lastCommentId
            )
        }

        return comments
    }
}