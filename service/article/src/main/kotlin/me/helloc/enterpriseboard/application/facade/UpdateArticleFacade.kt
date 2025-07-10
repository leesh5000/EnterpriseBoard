package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.UpdateArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UpdateArticleFacade(
    private val articleRepository: ArticleRepository
) : UpdateArticleUseCase {

    override fun update(articleId: Long, title: String, content: String): Article {
        val article = articleRepository.findById(articleId)
        val updatedArticle = article.update(
            title = title,
            content = content
        )

        return articleRepository.save(updatedArticle)
    }
}
