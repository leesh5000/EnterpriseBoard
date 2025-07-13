package me.helloc.enterpriseboard.adapter.out.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.helloc.enterpriseboard.domain.model.Article
import java.time.LocalDateTime

@Table(name = "article")
@Entity
open class ArticleJpaEntity(
    @Id
    val articleId: Long,
    var title: String,
    var content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime
) {
    companion object {
        fun from(article: Article): ArticleJpaEntity {
            return ArticleJpaEntity(
                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt
            )
        }
    }

    fun toDomain(): Article {
        return Article.create(
            articleId = this.articleId,
            title = this.title,
            content = this.content,
            boardId = this.boardId,
            writerId = this.writerId,
        )
    }

    fun updateFrom(article: Article) {
        this.title = article.title
        this.content = article.content
        this.modifiedAt = article.modifiedAt
    }
}
