package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.application.port.`in`.*
import me.helloc.enterpriseboard.domain.model.Article
import java.time.LocalDateTime

class FakeCreateArticleUseCase : CreateArticleUseCase {
    var lastCommand: CreateArticleCommand? = null
    var articleToReturn: Article = createDefaultArticle()
    
    override fun create(command: CreateArticleCommand): Article {
        lastCommand = command
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
    var lastCommand: UpdateArticleCommand? = null
    var shouldThrowException = false
    var articleToReturn: Article = createDefaultArticle()
    
    override fun update(command: UpdateArticleCommand): Article {
        lastCommand = command
        if (shouldThrowException) {
            throw NoSuchElementException("Article not found with id: ${command.articleId}")
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
    
    override fun getById(articleId: Long): Article? {
        return storage[articleId]
    }
    
    override fun getByBoardId(boardId: Long): List<Article> {
        return storage.values.filter { it.boardId == boardId }
    }
    
    override fun getByWriterId(writerId: Long): List<Article> {
        return storage.values.filter { it.writerId == writerId }
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