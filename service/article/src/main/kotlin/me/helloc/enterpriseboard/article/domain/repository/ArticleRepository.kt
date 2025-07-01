package me.helloc.enterpriseboard.article.domain.repository

import me.helloc.enterpriseboard.article.domain.model.*
import java.util.Optional

/**
 * 도메인 리포지토리 인터페이스 - 순수 도메인 모델 사용
 */
interface ArticleRepository {
    fun save(article: Article): Article
    fun findById(articleId: ArticleId): Optional<Article>
    fun findAll(): List<Article>
    fun findByBoardId(boardId: BoardId): List<Article>
    fun findByWriterId(writerId: WriterId): List<Article>
    fun deleteById(articleId: ArticleId)
    fun existsById(articleId: ArticleId): Boolean
    
    // 도메인 서비스에서 필요한 추가 메서드들
    fun countTodayArticlesByWriterInBoard(boardId: BoardId, writerId: WriterId): Int
    fun existsByBoardIdAndTitle(boardId: BoardId, title: Title, excludeArticleId: ArticleId? = null): Boolean
}
