package me.helloc.enterpriseboard.application.service

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

    // 테스트를 위한 헬퍼 메서드
    fun clear() {
        storage.clear()
    }

    fun getAll(): List<Article> {
        return storage.values.toList()
    }
}