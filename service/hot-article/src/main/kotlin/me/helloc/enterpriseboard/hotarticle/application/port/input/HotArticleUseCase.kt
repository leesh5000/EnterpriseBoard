package me.helloc.enterpriseboard.hotarticle.application.port.input

import me.helloc.enterpriseboard.hotarticle.application.port.input.command.*
import me.helloc.enterpriseboard.hotarticle.application.port.input.query.*

/**
 * 인기 게시글 유스케이스 인터페이스
 */
interface HotArticleUseCase {
    // Command operations
    fun updateHotArticleMetrics(command: UpdateHotArticleMetricsCommand): UpdateHotArticleMetricsResult
    fun updateRankings(command: UpdateRankingsCommand): UpdateRankingsResult
    fun cleanupOldData(command: CleanupOldDataCommand): CleanupOldDataResult
    
    // Query operations
    fun getHotArticlesByBoard(query: GetHotArticlesByBoardQuery): GetHotArticlesByBoardResult
    fun getGlobalHotArticles(query: GetGlobalHotArticlesQuery): GetGlobalHotArticlesResult
    fun getHotArticleByArticleId(query: GetHotArticleByArticleIdQuery): GetHotArticleByArticleIdResult
}