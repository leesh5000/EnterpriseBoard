package me.helloc.enterpriseboard.article.domain.service

import me.helloc.enterpriseboard.article.domain.model.*
import me.helloc.enterpriseboard.article.domain.repository.ArticleRepository
import org.springframework.stereotype.Component

/**
 * 도메인 서비스 - 복잡한 비즈니스 로직 처리
 */
@Component
class ArticleDomainService(
    private val articleRepository: ArticleRepository
) {
    
    /**
     * 게시판에 게시글을 작성할 수 있는지 확인
     * 예: 게시판별 일일 작성 제한, 권한 확인 등
     */
    fun canCreateArticleInBoard(boardId: BoardId, writerId: WriterId): Boolean {
        // 비즈니스 규칙 예시
        val todayArticleCount = articleRepository.countTodayArticlesByWriterInBoard(boardId, writerId)
        return todayArticleCount < 10 // 일일 10개 제한
    }
    
    /**
     * 게시글 수정 권한 확인
     */
    fun canUpdateArticle(article: Article, requesterId: WriterId): Boolean {
        return article.writerId == requesterId
    }
    
    /**
     * 게시글 삭제 권한 확인
     */
    fun canDeleteArticle(article: Article, requesterId: WriterId): Boolean {
        return article.writerId == requesterId
    }
    
    /**
     * 중복 제목 확인
     */
    fun isDuplicateTitle(boardId: BoardId, title: Title, excludeArticleId: ArticleId? = null): Boolean {
        return articleRepository.existsByBoardIdAndTitle(boardId, title, excludeArticleId)
    }
}