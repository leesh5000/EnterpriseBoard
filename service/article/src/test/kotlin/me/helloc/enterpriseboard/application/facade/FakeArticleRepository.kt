package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.application.port.out.ArticleRepository
import me.helloc.enterpriseboard.domain.model.Article

class FakeArticleRepository : ArticleRepository {
    private val storage = mutableMapOf<Long, Article>()

    override fun save(article: Article): Article {
        storage[article.articleId] = article
        return article
    }

    override fun findById(articleId: Long): Article? {
        return storage[articleId]
    }

    override fun findByBoardId(boardId: Long): List<Article> {
        return storage.values.filter { it.boardId == boardId }
    }

    override fun findByWriterId(writerId: Long): List<Article> {
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

    // 테스트를 위한 헬퍼 메서드
    fun clear() {
        storage.clear()
    }

    fun getAll(): List<Article> {
        return storage.values.toList()
    }
}
