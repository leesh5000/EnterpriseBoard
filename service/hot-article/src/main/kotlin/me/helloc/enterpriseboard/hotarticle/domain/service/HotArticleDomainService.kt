package me.helloc.enterpriseboard.hotarticle.domain.service

import me.helloc.enterpriseboard.hotarticle.domain.model.*
import me.helloc.enterpriseboard.hotarticle.domain.repository.HotArticleRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 인기 게시글 도메인 서비스
 */
@Component
class HotArticleDomainService(
    private val hotArticleRepository: HotArticleRepository
) {
    
    /**
     * 게시글의 인기도 점수를 업데이트해야 하는지 확인
     */
    fun shouldUpdateHotScore(
        articleId: ArticleId,
        newViewCount: ViewCount,
        newLikeCount: LikeCount,
        newCommentCount: CommentCount
    ): Boolean {
        val existingHotArticle = hotArticleRepository.findByArticleId(articleId)
        
        if (existingHotArticle.isEmpty) {
            return true // 새로운 인기 게시글이므로 추가
        }
        
        val existing = existingHotArticle.get()
        
        // 메트릭이 변경되었으면 업데이트
        return existing.viewCount != newViewCount ||
               existing.likeCount != newLikeCount ||
               existing.commentCount != newCommentCount
    }
    
    /**
     * 랭킹을 업데이트
     */
    fun updateRankings(boardId: BoardId) {
        val hotArticles = hotArticleRepository.findByBoardId(boardId)
            .sortedByDescending { it.score.value }
        
        hotArticles.forEachIndexed { index, hotArticle ->
            val newRank = HotRank(index + 1)
            val updatedHotArticle = hotArticle.updateRank(newRank)
            hotArticleRepository.save(updatedHotArticle)
        }
    }
    
    /**
     * 전체 랭킹을 업데이트
     */
    fun updateGlobalRankings() {
        val hotArticles = hotArticleRepository.findAllOrderByScore(100) // 상위 100개만
        
        hotArticles.forEachIndexed { index, hotArticle ->
            val newRank = HotRank(index + 1)
            val updatedHotArticle = hotArticle.updateRank(newRank)
            hotArticleRepository.save(updatedHotArticle)
        }
    }
    
    /**
     * 오래된 데이터 정리
     */
    fun cleanupOldData(daysOld: Long) {
        val cutoffDate = LocalDateTime.now().minusDays(daysOld)
        hotArticleRepository.deleteByCalculatedAtBefore(cutoffDate)
    }
    
    /**
     * 인기 게시글 임계값 확인
     */
    fun meetsHotThreshold(
        viewCount: ViewCount,
        likeCount: LikeCount,
        commentCount: CommentCount
    ): Boolean {
        val score = (viewCount.value * 1.0) + (likeCount.value * 5.0) + (commentCount.value * 3.0)
        return score >= 10.0 // 최소 10점 이상이어야 인기 게시글
    }
}