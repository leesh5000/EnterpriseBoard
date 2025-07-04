package me.helloc.enterpriseboard.application.service

import me.helloc.enterpriseboard.application.port.`in`.GetArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetArticleService(
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
}