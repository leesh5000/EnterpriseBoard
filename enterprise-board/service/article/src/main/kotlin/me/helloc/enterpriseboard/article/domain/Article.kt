package me.helloc.enterpriseboard.article.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article")
@Entity
class Article {
    @Id
    private var articleId: Long? = null
    private var title: String? = null
    private var content: String? = null
    private var boardId: Long? = null // shard key
    private var writerId: Long? = null
    private var createdAt: LocalDateTime? = null
    private var modifiedAt: LocalDateTime? = null

    fun update(title: String?, content: String?) {
        this.title = title
        this.content = content
        modifiedAt = LocalDateTime.now()
    }

    companion object {
        fun create(articleId: Long?, title: String?, content: String?, boardId: Long?, writerId: Long?): Article {
            val article = Article()
            article.articleId = articleId
            article.title = title
            article.content = content
            article.boardId = boardId
            article.writerId = writerId
            article.createdAt = LocalDateTime.now()
            article.modifiedAt = article.createdAt
            return article
        }
    }
}