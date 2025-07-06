package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.`in`.UpdateArticleCommand
import me.helloc.enterpriseboard.application.port.`in`.UpdateArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateArticleFacade(
    private val articleRepository: ArticleRepository
) : UpdateArticleUseCase {

    override fun update(command: UpdateArticleCommand): Article {
        val article = articleRepository.findById(command.articleId)
            ?: throw NoSuchElementException("Article not found with id: ${command.articleId}")
        
        val updatedArticle = article.update(
            title = command.title,
            content = command.content
        )
        
        return articleRepository.save(updatedArticle)
    }
}