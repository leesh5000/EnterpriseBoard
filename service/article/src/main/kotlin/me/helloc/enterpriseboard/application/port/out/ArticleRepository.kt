package me.helloc.enterpriseboard.application.port.out

import me.helloc.enterpriseboard.domain.model.Article

interface ArticleRepository {
    fun save(article: Article): Article
    fun getById(articleId: Long): Article
    fun getByBoardId(boardId: Long): List<Article>
    fun getByWriterId(writerId: Long): List<Article>
    fun deleteById(articleId: Long)
    fun existsById(articleId: Long): Boolean
    fun findAll(boardId: Long, offset: Long, limit: Long): List<Article>
    fun countByBoardId(boardId: Long, limit: Long): Long
    fun findAllScroll(boardId: Long, limit: Long): List<Article>
    fun findAllScroll(boardId: Long, limit: Long, lastArticleId: Long): List<Article>
}
