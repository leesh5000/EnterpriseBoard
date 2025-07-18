package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.GetArticlePageResult
import me.helloc.enterpriseboard.application.port.`in`.GetArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import me.helloc.enterpriseboard.domain.service.PageLimitCalculator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class GetArticleFacade(
    private val articleRepository: ArticleRepository
) : GetArticleUseCase {

    override fun getById(articleId: Long): Article {
        return articleRepository.getById(articleId)
    }

    override fun getByBoardId(boardId: Long): List<Article> {
        return articleRepository.getByBoardId(boardId)
    }

    override fun getByWriterId(writerId: Long): List<Article> {
        return articleRepository.getByWriterId(writerId)
    }

    override fun getPage(boardId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetArticlePageResult {
        val offset = (page - 1) * pageSize
        val limit = PageLimitCalculator.calculate(
            page = page,
            pageSize = pageSize,
            movablePageCount = movablePageCount
        )

        val articles = articleRepository.findAll(
            boardId = boardId,
            offset = offset,
            limit = pageSize
        )

        val limitedTotalCount = articleRepository.countByBoardId(
            boardId = boardId,
            limit = limit
        )

        return GetArticlePageResult(
            articles = articles,
            visibleRangeCount = limitedTotalCount
        )
    }

    override fun getScroll(boardId: Long, pageSize: Long, lastArticleId: Long): List<Article> {
        val articles = if (lastArticleId == Article.EMPTY_ID) {
            articleRepository.findAllScroll(
                boardId = boardId,
                limit = pageSize
            )
        } else {
            articleRepository.findAllScroll(
                boardId = boardId,
                limit = pageSize,
                lastArticleId = lastArticleId
            )
        }

        return articles
    }
}
