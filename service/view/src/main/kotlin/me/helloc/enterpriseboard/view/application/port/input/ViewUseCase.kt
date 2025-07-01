package me.helloc.enterpriseboard.view.application.port.input

import me.helloc.enterpriseboard.view.application.port.input.command.*
import me.helloc.enterpriseboard.view.application.port.input.query.*

/**
 * 조회수 유스케이스 인터페이스
 */
interface ViewUseCase {
    // Command operations
    fun increaseViewCount(command: IncreaseViewCountCommand): IncreaseViewCountResult
    
    // Query operations
    fun getViewCount(query: GetViewCountQuery): GetViewCountResult
    fun getViewsByArticle(query: GetViewsByArticleQuery): GetViewsByArticleResult
    fun getViewsByUser(query: GetViewsByUserQuery): GetViewsByUserResult
    fun getViewStatistics(query: GetViewStatisticsQuery): GetViewStatisticsResult
}