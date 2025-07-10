package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.DeleteArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class DeleteArticleFacade(
    private val articleRepository: ArticleRepository
) : DeleteArticleUseCase {

    override fun delete(articleId: Long) {
        articleRepository.deleteById(articleId)
    }
}
