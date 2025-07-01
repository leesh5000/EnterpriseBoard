package me.helloc.enterpriseboard.articleread.application.port.input

import me.helloc.enterpriseboard.articleread.application.port.input.command.*
import me.helloc.enterpriseboard.articleread.application.port.input.query.*

/**
 * 게시글 읽기 유스케이스 인터페이스 - CQRS Query Side
 */
interface ArticleReadUseCase {
    // Command operations (Event-driven updates)
    fun createArticleReadModel(command: CreateArticleReadModelCommand): CreateArticleReadModelResult
    fun updateArticleReadModel(command: UpdateArticleReadModelCommand): UpdateArticleReadModelResult
    fun updateViewCount(command: UpdateViewCountCommand): UpdateViewCountResult
    fun updateLikeCount(command: UpdateLikeCountCommand): UpdateLikeCountResult
    fun updateCommentCount(command: UpdateCommentCountCommand): UpdateCommentCountResult
    fun updateHotStatus(command: UpdateHotStatusCommand): UpdateHotStatusResult
    fun deleteArticleReadModel(command: DeleteArticleReadModelCommand): DeleteArticleReadModelResult
    
    // Query operations
    fun getArticle(query: GetArticleQuery): GetArticleResult
    fun getArticlesByBoard(query: GetArticlesByBoardQuery): GetArticlesByBoardResult
    fun getArticlesByWriter(query: GetArticlesByWriterQuery): GetArticlesByWriterResult
    fun getHotArticles(query: GetHotArticlesQuery): GetHotArticlesResult
    fun searchArticles(query: SearchArticlesQuery): SearchArticlesResult
    fun getRecentArticles(query: GetRecentArticlesQuery): GetRecentArticlesResult
}