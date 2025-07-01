package me.helloc.enterpriseboard.article.service

import jakarta.transaction.Transactional
import me.helloc.common.snowflake.Snowflake
import me.helloc.enterpriseboard.article.domain.Article
import me.helloc.enterpriseboard.article.domain.repository.ArticleRepository
import me.helloc.enterpriseboard.article.service.dto.request.ArticleCreateRequest
import me.helloc.enterpriseboard.article.service.dto.request.ArticleUpdateRequest
import me.helloc.enterpriseboard.article.service.dto.response.ArticleResponse
import org.springframework.stereotype.Service

@Service
class ArticleService(
    private val repository: ArticleRepository,
) {

    private val snowflake = Snowflake()

    @Transactional
    fun createArticle(request: ArticleCreateRequest): ArticleResponse {
        val article: Article = repository.save(request.toEntity(snowflake.nextId()))
        return ArticleResponse.from(article)
    }

    fun getArticle(articleId: Long): ArticleResponse {
        val article = repository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article not found with id: $articleId") }
        return ArticleResponse.from(article)
    }

    fun getAllArticles(): List<ArticleResponse> {
        return repository.findAll().map { ArticleResponse.from(it) }
    }

    fun getArticlesByBoard(boardId: Long): List<ArticleResponse> {
        return repository.findByBoardId(boardId).map { ArticleResponse.from(it) }
    }

    fun getArticlesByWriter(writerId: Long): List<ArticleResponse> {
        return repository.findByWriterId(writerId).map { ArticleResponse.from(it) }
    }

    @Transactional
    fun updateArticle(
        articleId: Long,
        request: ArticleUpdateRequest
    ): ArticleResponse {
        val article = repository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article not found with id: $articleId") }
        article.update(request.title, request.content)
        val updatedArticle = repository.save(article)
        return ArticleResponse.from(updatedArticle)
    }

    @Transactional
    fun deleteArticle(articleId: Long) {
        if (!repository.existsById(articleId)) {
            throw IllegalArgumentException("Article not found with id: $articleId")
        }
        repository.deleteById(articleId)
    }
}
