package me.helloc.enterpriseboard.article.infrastructure.persistence

import me.helloc.enterpriseboard.article.domain.model.*
import me.helloc.enterpriseboard.article.domain.repository.ArticleRepository
import me.helloc.enterpriseboard.article.infrastructure.persistence.entity.ArticleJpaEntity
import me.helloc.enterpriseboard.article.infrastructure.persistence.jpa.ArticleJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

/**
 * 리포지토리 어댑터 - 도메인 모델과 JPA 엔티티 간 변환
 */
@Repository
class ArticleRepositoryImpl(
    private val articleJpaRepository: ArticleJpaRepository
): ArticleRepository {

    override fun save(article: Article): Article {
        val jpaEntity = ArticleJpaEntity.fromDomainModel(article)
        val savedEntity = articleJpaRepository.save(jpaEntity)
        return savedEntity.toDomainModel()
    }

    override fun findById(articleId: ArticleId): Optional<Article> {
        val jpaEntity = articleJpaRepository.findById(articleId.value)
        return if (jpaEntity.isPresent) {
            Optional.of(jpaEntity.get().toDomainModel())
        } else {
            Optional.empty()
        }
    }

    override fun findAll(): List<Article> {
        return articleJpaRepository.findAll()
            .map { it.toDomainModel() }
    }

    override fun findByBoardId(boardId: BoardId): List<Article> {
        return articleJpaRepository.findByBoardId(boardId.value)
            .map { it.toDomainModel() }
    }

    override fun findByWriterId(writerId: WriterId): List<Article> {
        return articleJpaRepository.findByWriterId(writerId.value)
            .map { it.toDomainModel() }
    }

    override fun deleteById(articleId: ArticleId) {
        articleJpaRepository.deleteById(articleId.value)
    }

    override fun existsById(articleId: ArticleId): Boolean {
        return articleJpaRepository.existsById(articleId.value)
    }

    override fun countTodayArticlesByWriterInBoard(boardId: BoardId, writerId: WriterId): Int {
        return articleJpaRepository.countTodayArticlesByWriterInBoard(
            boardId.value,
            writerId.value,
            LocalDateTime.now()
        )
    }

    override fun existsByBoardIdAndTitle(boardId: BoardId, title: Title, excludeArticleId: ArticleId?): Boolean {
        return articleJpaRepository.existsByBoardIdAndTitleExcludingId(
            boardId.value,
            title.value,
            excludeArticleId?.value
        )
    }
}
