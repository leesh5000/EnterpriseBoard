package me.helloc.enterpriseboard.articleread.domain.repository

import me.helloc.enterpriseboard.articleread.domain.model.*
import java.util.Optional

/**
 * 게시글 읽기 모델 리포지토리 인터페이스 - CQRS Read Side
 */
interface ArticleReadModelRepository {
    fun save(articleReadModel: ArticleReadModel): ArticleReadModel
    fun findById(articleId: ArticleId): Optional<ArticleReadModel>
    fun findByBoardId(boardId: BoardId): List<ArticleReadModel>
    fun findByBoardIdWithPaging(boardId: BoardId, page: Int, size: Int): List<ArticleReadModel>
    fun findByWriterId(writerId: WriterId): List<ArticleReadModel>
    fun findByWriterIdWithPaging(writerId: WriterId, page: Int, size: Int): List<ArticleReadModel>
    fun findHotArticles(limit: Int): List<ArticleReadModel>
    fun findHotArticlesByBoard(boardId: BoardId, limit: Int): List<ArticleReadModel>
    fun findByTitleContaining(keyword: String, page: Int, size: Int): List<ArticleReadModel>
    fun findByContentContaining(keyword: String, page: Int, size: Int): List<ArticleReadModel>
    fun findByTitleOrContentContaining(keyword: String, page: Int, size: Int): List<ArticleReadModel>
    fun findByTag(tag: Tag, page: Int, size: Int): List<ArticleReadModel>
    fun findRecentArticles(limit: Int): List<ArticleReadModel>
    fun findRecentArticlesByBoard(boardId: BoardId, limit: Int): List<ArticleReadModel>
    fun countByBoardId(boardId: BoardId): Long
    fun countByWriterId(writerId: WriterId): Long
    fun deleteById(articleId: ArticleId)
    fun existsById(articleId: ArticleId): Boolean
}