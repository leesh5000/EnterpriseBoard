package me.helloc.enterpriseboard.article.application.port.input

import me.helloc.enterpriseboard.article.application.port.input.command.*
import me.helloc.enterpriseboard.article.application.port.input.query.*

/**
 * 입력 포트 - 애플리케이션의 유스케이스 인터페이스
 */
interface ArticleUseCase {
    // Command operations
    fun createArticle(command: CreateArticleCommand): CreateArticleResult
    fun updateArticle(command: UpdateArticleCommand): UpdateArticleResult
    fun deleteArticle(command: DeleteArticleCommand): DeleteArticleResult
    
    // Query operations
    fun getArticle(query: GetArticleQuery): GetArticleResult
    fun getArticlesByBoard(query: GetArticlesByBoardQuery): GetArticlesByBoardResult
    fun getArticlesByWriter(query: GetArticlesByWriterQuery): GetArticlesByWriterResult
    fun getAllArticles(query: GetAllArticlesQuery): GetAllArticlesResult
}