package me.helloc.enterpriseboard.application.port.out

import me.helloc.enterpriseboard.domain.model.Article

interface ArticleRepository {
    fun save(article: Article): Article
    fun findById(articleId: Long): Article?
    fun findByBoardId(boardId: Long): List<Article>
    fun findByWriterId(writerId: Long): List<Article>
    fun deleteById(articleId: Long)
    fun existsById(articleId: Long): Boolean
    fun findAll(boardId: Long, offset: Long, limit: Long): List<Article>
    fun countByBoardId(boardId: Long, limit: Long): Long
}
