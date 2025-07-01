package me.helloc.enterpriseboard.hotarticle.domain.repository

import me.helloc.enterpriseboard.hotarticle.domain.model.*
import java.time.LocalDateTime
import java.util.Optional

/**
 * 인기 게시글 도메인 리포지토리 인터페이스
 */
interface HotArticleRepository {
    fun save(hotArticle: HotArticle): HotArticle
    fun findByArticleId(articleId: ArticleId): Optional<HotArticle>
    fun findByBoardId(boardId: BoardId): List<HotArticle>
    fun findTopByBoardIdOrderByScore(boardId: BoardId, limit: Int): List<HotArticle>
    fun findTopByBoardIdOrderByRank(boardId: BoardId, limit: Int): List<HotArticle>
    fun findAllOrderByScore(limit: Int): List<HotArticle>
    fun findAllOrderByRank(limit: Int): List<HotArticle>
    fun findByCalculatedAtBefore(dateTime: LocalDateTime): List<HotArticle>
    fun deleteByArticleId(articleId: ArticleId)
    fun deleteByCalculatedAtBefore(dateTime: LocalDateTime)
    fun existsByArticleId(articleId: ArticleId): Boolean
}