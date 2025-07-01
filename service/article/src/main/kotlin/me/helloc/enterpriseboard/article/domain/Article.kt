package me.helloc.enterpriseboard.article.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "article")
@Entity
class Article {
    @Id
    var articleId: Long? = null
        private set
    
    var title: String? = null
        private set
    
    var content: String? = null
        private set
    
    var boardId: Long? = null // shard key
        private set
    
    var writerId: Long? = null
        private set
    
    var createdAt: LocalDateTime? = null
        private set
    
    var modifiedAt: LocalDateTime? = null
        private set

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