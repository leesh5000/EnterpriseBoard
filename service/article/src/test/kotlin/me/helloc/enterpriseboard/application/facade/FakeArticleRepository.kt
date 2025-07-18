package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article
import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode

class FakeArticleRepository : ArticleRepository {
    private val storage = mutableMapOf<Long, Article>()

    override fun save(article: Article): Article {
        storage[article.articleId] = article
        return article
    }

    override fun getById(articleId: Long): Article {
        return storage[articleId] ?: throw ErrorCode.NOT_FOUND_ARTICLE.toException(
            "articleId" to articleId
        )
    }

    override fun getByBoardId(boardId: Long): List<Article> {
        return storage.values.filter { it.boardId == boardId }
    }

    override fun getByWriterId(writerId: Long): List<Article> {
        return storage.values.filter { it.writerId == writerId }
    }

    override fun deleteById(articleId: Long) {
        storage.remove(articleId)
    }

    override fun existsById(articleId: Long): Boolean {
        return storage.containsKey(articleId)
    }

    override fun findAll(
        boardId: Long,
        offset: Long,
        limit: Long,
    ): List<Article> {
        return storage.values
            .filter { it.boardId == boardId }
            .sortedByDescending { it.articleId }
            .drop(offset.toInt())
            .take(limit.toInt())
    }

    override fun countByBoardId(boardId: Long, limit: Long): Long {
        return storage.values
            .filter { it.boardId == boardId }
            .take(limit.toInt())
            .count()
            .toLong()
    }

    override fun findAllScroll(
        boardId: Long,
        limit: Long,
    ): List<Article> {
        return storage.values
            .filter { it.boardId == boardId }
            .sortedByDescending { it.articleId }
            .take(limit.toInt())
    }

    override fun findAllScroll(
        boardId: Long,
        limit: Long,
        lastArticleId: Long,
    ): List<Article> {
        return storage.values
            .filter { it.boardId == boardId && it.articleId < lastArticleId }
            .sortedByDescending { it.articleId }
            .take(limit.toInt())
    }

    // 테스트를 위한 헬퍼 메서드
    fun clear() {
        storage.clear()
    }

    fun getAll(): List<Article> {
        return storage.values.toList()
    }
}
