package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.application.port.`in`.*
import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Article
import java.time.LocalDateTime

class FakeCreateArticleUseCase : CreateArticleUseCase {
    var lastTitle: String? = null
    var lastContent: String? = null
    var lastBoardId: Long? = null
    var lastWriterId: Long? = null
    var articleToReturn: Article = createDefaultArticle()

    override fun create(title: String, content: String, boardId: Long, writerId: Long): Article {
        lastTitle = title
        lastContent = content
        lastBoardId = boardId
        lastWriterId = writerId
        return articleToReturn
    }

    private fun createDefaultArticle() = Article(
        articleId = 1L,
        title = "테스트 제목",
        content = "테스트 내용",
        boardId = 100L,
        writerId = 200L,
        createdAt = LocalDateTime.now(),
        modifiedAt = LocalDateTime.now()
    )
}

class FakeUpdateArticleUseCase : UpdateArticleUseCase {
    var lastArticleId: Long? = null
    var lastTitle: String? = null
    var lastContent: String? = null
    var shouldThrowException = false
    var articleToReturn: Article = createDefaultArticle()

    override fun update(articleId: Long, title: String, content: String): Article {
        lastArticleId = articleId
        lastTitle = title
        lastContent = content
        if (shouldThrowException) {
            throw NoSuchElementException("Article not found with id: $articleId")
        }
        return articleToReturn
    }

    private fun createDefaultArticle() = Article(
        articleId = 1L,
        title = "수정된 제목",
        content = "수정된 내용",
        boardId = 100L,
        writerId = 200L,
        createdAt = LocalDateTime.now().minusDays(1),
        modifiedAt = LocalDateTime.now()
    )
}

class FakeGetArticleUseCase : GetArticleUseCase {
    private val storage = mutableMapOf<Long, Article>()

    fun addArticle(article: Article) {
        storage[article.articleId] = article
    }

    fun clear() {
        storage.clear()
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

    override fun getPage(boardId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetArticlePageResult {
        val offset = (page - 1) * pageSize
        val articles = storage.values
            .filter { it.boardId == boardId }
            .sortedByDescending { it.articleId }
            .drop(offset.toInt())
            .take(pageSize.toInt())

        val totalCount = storage.values
            .filter { it.boardId == boardId }
            .count()
            .toLong()

        return GetArticlePageResult(
            articles = articles,
            limitedTotalCount = totalCount
        )
    }

    override fun getScroll(boardId: Long, pageSize: Long, lastArticleId: Long): List<Article> {
        return if (lastArticleId == 0L) {
            storage.values
                .filter { it.boardId == boardId }
                .sortedByDescending { it.articleId }
                .take(pageSize.toInt())
        } else {
            storage.values
                .filter { it.boardId == boardId && it.articleId < lastArticleId }
                .sortedByDescending { it.articleId }
                .take(pageSize.toInt())
        }
    }
}

class FakeDeleteArticleUseCase : DeleteArticleUseCase {
    var deletedArticleIds = mutableListOf<Long>()
    var shouldThrowException = false

    override fun delete(articleId: Long) {
        if (shouldThrowException) {
            throw NoSuchElementException("Article not found with id: $articleId")
        }
        deletedArticleIds.add(articleId)
    }

    fun wasDeleted(articleId: Long): Boolean {
        return articleId in deletedArticleIds
    }

    fun reset() {
        deletedArticleIds.clear()
        shouldThrowException = false
    }
}
