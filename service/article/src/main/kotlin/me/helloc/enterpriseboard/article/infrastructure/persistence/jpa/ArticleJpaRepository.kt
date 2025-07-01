package me.helloc.enterpriseboard.article.infrastructure.persistence.jpa

import me.helloc.enterpriseboard.article.infrastructure.persistence.entity.ArticleJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface ArticleJpaRepository: JpaRepository<ArticleJpaEntity, Long> {
    fun findByBoardId(boardId: Long): List<ArticleJpaEntity>
    fun findByWriterId(writerId: Long): List<ArticleJpaEntity>
    
    @Query("SELECT COUNT(a) FROM ArticleJpaEntity a WHERE a.boardId = :boardId AND a.writerId = :writerId AND DATE(a.createdAt) = DATE(:today)")
    fun countTodayArticlesByWriterInBoard(
        @Param("boardId") boardId: Long,
        @Param("writerId") writerId: Long,
        @Param("today") today: LocalDateTime
    ): Int
    
    @Query("SELECT COUNT(a) > 0 FROM ArticleJpaEntity a WHERE a.boardId = :boardId AND a.title = :title AND (:excludeArticleId IS NULL OR a.articleId != :excludeArticleId)")
    fun existsByBoardIdAndTitleExcludingId(
        @Param("boardId") boardId: Long,
        @Param("title") title: String,
        @Param("excludeArticleId") excludeArticleId: Long?
    ): Boolean
}