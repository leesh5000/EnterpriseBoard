package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.GetArticlePageQuery
import me.helloc.enterpriseboard.application.port.`in`.GetArticlePageResult
import me.helloc.enterpriseboard.application.port.`in`.GetArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import me.helloc.enterpriseboard.domain.service.PageLimitCalculator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetArticleFacade(
    private val articleRepository: ArticleRepository
) : GetArticleUseCase {

    override fun getById(articleId: Long): Article? {
        return articleRepository.findById(articleId)
    }

    override fun getByBoardId(boardId: Long): List<Article> {
        return articleRepository.findByBoardId(boardId)
    }

    override fun getByWriterId(writerId: Long): List<Article> {
        return articleRepository.findByWriterId(writerId)
    }

    override fun getPage(query: GetArticlePageQuery): GetArticlePageResult {
        val offset = (query.page - 1) * query.pageSize
        val limit = PageLimitCalculator.calculate(
            page = query.page,
            pageSize = query.pageSize,
            movablePageCount = query.movablePageCount
        )

        val articles = articleRepository.findAll(
            boardId = query.boardId,
            offset = offset,
            limit = query.pageSize
        )

        val totalCount = articleRepository.countByBoardId(
            boardId = query.boardId,
            limit = limit
        )

        return GetArticlePageResult(
            articles = articles,
            totalCount = totalCount
        )
    }
}
