package me.helloc.enterpriseboard.hotarticle.application.service

import me.helloc.enterpriseboard.hotarticle.application.port.input.HotArticleUseCase
import me.helloc.enterpriseboard.hotarticle.application.port.input.command.*
import me.helloc.enterpriseboard.hotarticle.application.port.input.query.*
import me.helloc.enterpriseboard.hotarticle.domain.model.*
import me.helloc.enterpriseboard.hotarticle.domain.repository.HotArticleRepository
import me.helloc.enterpriseboard.hotarticle.domain.service.HotArticleDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 인기 게시글 애플리케이션 서비스
 */
@Service
@Transactional
class HotArticleApplicationService(
    private val hotArticleRepository: HotArticleRepository,
    private val hotArticleDomainService: HotArticleDomainService
) : HotArticleUseCase {

    override fun updateHotArticleMetrics(command: UpdateHotArticleMetricsCommand): UpdateHotArticleMetricsResult {
        val articleId = ArticleId(command.articleId)
        val boardId = BoardId(command.boardId)
        val viewCount = ViewCount(command.viewCount)
        val likeCount = LikeCount(command.likeCount)
        val commentCount = CommentCount(command.commentCount)
        
        // 인기 게시글 임계값 확인
        if (!hotArticleDomainService.meetsHotThreshold(viewCount, likeCount, commentCount)) {
            return UpdateHotArticleMetricsResult(
                articleId = command.articleId,
                score = 0.0,
                rank = null,
                updated = false
            )
        }
        
        // 업데이트 필요성 확인
        if (!hotArticleDomainService.shouldUpdateHotScore(articleId, viewCount, likeCount, commentCount)) {
            val existing = hotArticleRepository.findByArticleId(articleId).get()
            return UpdateHotArticleMetricsResult(
                articleId = command.articleId,
                score = existing.score.value,
                rank = existing.rank?.value,
                updated = false
            )
        }
        
        // 기존 인기 게시글 조회 또는 새로 생성
        val hotArticle = hotArticleRepository.findByArticleId(articleId)
            .map { it.updateMetrics(viewCount, likeCount, commentCount) }
            .orElse(HotArticle.create(articleId, boardId, viewCount, likeCount, commentCount))
        
        val savedHotArticle = hotArticleRepository.save(hotArticle)
        
        // 게시판별 랭킹 업데이트
        hotArticleDomainService.updateRankings(boardId)
        
        return UpdateHotArticleMetricsResult(
            articleId = command.articleId,
            score = savedHotArticle.score.value,
            rank = savedHotArticle.rank?.value,
            updated = true
        )
    }

    override fun updateRankings(command: UpdateRankingsCommand): UpdateRankingsResult {
        return if (command.boardId != null) {
            // 특정 게시판 랭킹 업데이트
            val boardId = BoardId(command.boardId)
            hotArticleDomainService.updateRankings(boardId)
            val updatedCount = hotArticleRepository.findByBoardId(boardId).size
            UpdateRankingsResult(boardId = command.boardId, updatedCount = updatedCount)
        } else {
            // 전체 랭킹 업데이트
            hotArticleDomainService.updateGlobalRankings()
            val updatedCount = hotArticleRepository.findAllOrderByScore(100).size
            UpdateRankingsResult(boardId = null, updatedCount = updatedCount)
        }
    }

    override fun cleanupOldData(command: CleanupOldDataCommand): CleanupOldDataResult {
        val oldDataCount = hotArticleRepository.findByCalculatedAtBefore(
            java.time.LocalDateTime.now().minusDays(command.daysOld)
        ).size
        
        hotArticleDomainService.cleanupOldData(command.daysOld)
        
        return CleanupOldDataResult(deletedCount = oldDataCount)
    }

    @Transactional(readOnly = true)
    override fun getHotArticlesByBoard(query: GetHotArticlesByBoardQuery): GetHotArticlesByBoardResult {
        val boardId = BoardId(query.boardId)
        val hotArticles = hotArticleRepository.findTopByBoardIdOrderByRank(boardId, query.limit)
        
        val hotArticleViews = hotArticles.map { hotArticle ->
            HotArticleView(
                articleId = hotArticle.articleId.value,
                boardId = hotArticle.boardId.value,
                score = hotArticle.score.value,
                viewCount = hotArticle.viewCount.value,
                likeCount = hotArticle.likeCount.value,
                commentCount = hotArticle.commentCount.value,
                rank = hotArticle.rank?.value,
                calculatedAt = hotArticle.calculatedAt
            )
        }
        
        return GetHotArticlesByBoardResult(
            boardId = query.boardId,
            hotArticles = hotArticleViews
        )
    }

    @Transactional(readOnly = true)
    override fun getGlobalHotArticles(query: GetGlobalHotArticlesQuery): GetGlobalHotArticlesResult {
        val hotArticles = hotArticleRepository.findAllOrderByRank(query.limit)
        
        val hotArticleViews = hotArticles.map { hotArticle ->
            HotArticleView(
                articleId = hotArticle.articleId.value,
                boardId = hotArticle.boardId.value,
                score = hotArticle.score.value,
                viewCount = hotArticle.viewCount.value,
                likeCount = hotArticle.likeCount.value,
                commentCount = hotArticle.commentCount.value,
                rank = hotArticle.rank?.value,
                calculatedAt = hotArticle.calculatedAt
            )
        }
        
        return GetGlobalHotArticlesResult(hotArticles = hotArticleViews)
    }

    @Transactional(readOnly = true)
    override fun getHotArticleByArticleId(query: GetHotArticleByArticleIdQuery): GetHotArticleByArticleIdResult {
        val articleId = ArticleId(query.articleId)
        val hotArticle = hotArticleRepository.findByArticleId(articleId).orElse(null)
        
        val hotArticleView = hotArticle?.let {
            HotArticleView(
                articleId = it.articleId.value,
                boardId = it.boardId.value,
                score = it.score.value,
                viewCount = it.viewCount.value,
                likeCount = it.likeCount.value,
                commentCount = it.commentCount.value,
                rank = it.rank?.value,
                calculatedAt = it.calculatedAt
            )
        }
        
        return GetHotArticleByArticleIdResult(hotArticle = hotArticleView)
    }
}