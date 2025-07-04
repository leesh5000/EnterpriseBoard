package me.helloc.enterpriseboard.application.service

import me.helloc.enterpriseboard.application.port.`in`.DeleteArticleUseCase
import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteArticleService(
    private val articleRepository: ArticleRepository
) : DeleteArticleUseCase {

    override fun delete(articleId: Long) {
        if (!articleRepository.existsById(articleId)) {
            throw NoSuchElementException("Article not found with id: $articleId")
        }
        articleRepository.deleteById(articleId)
    }
}