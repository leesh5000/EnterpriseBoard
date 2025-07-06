package me.helloc.enterpriseboard.adapter.out.persistence

import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class ArticleJpaAdapter(
    private val articleJpaRepository: ArticleJpaRepository
) : ArticleRepository {

    @Transactional
    override fun save(article: Article): Article {
        val entity = if (articleJpaRepository.existsById(article.articleId)) {
            val existingEntity = articleJpaRepository.findById(article.articleId).get()
            existingEntity.updateFrom(article)
            existingEntity
        } else {
            ArticleJpaEntity.from(article)
        }

        return articleJpaRepository.save(entity).toDomain()
    }

    override fun findById(articleId: Long): Article? {
        return articleJpaRepository.findById(articleId)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByBoardId(boardId: Long): List<Article> {
        return articleJpaRepository.findByBoardId(boardId)
            .map { it.toDomain() }
    }

    override fun findByWriterId(writerId: Long): List<Article> {
        return articleJpaRepository.findByWriterId(writerId)
            .map { it.toDomain() }
    }

    @Transactional
    override fun deleteById(articleId: Long) {
        articleJpaRepository.deleteById(articleId)
    }

    override fun existsById(articleId: Long): Boolean {
        return articleJpaRepository.existsById(articleId)
    }

    override fun findAll(
        boardId: Long,
        offset: Long,
        limit: Long,
    ): List<Article> {
        return articleJpaRepository.findAllByBoardId(boardId, offset, limit)
            .map { it.toDomain() }
    }

    override fun countByBoardId(boardId: Long, limit: Long): Long {
        return articleJpaRepository.countByBoardId(boardId, limit)
    }
}
