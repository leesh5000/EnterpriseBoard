package me.helloc.enterpriseboard.article.infrastructure.persistence.entity

import jakarta.persistence.*
import me.helloc.enterpriseboard.article.domain.model.*
import java.time.LocalDateTime

/**
 * JPA 엔티티 - 인프라스트럭처 계층에서만 사용
 */
@Entity
@Table(name = "article")
class ArticleJpaEntity(
    @Id
    var articleId: Long = 0,
    
    @Column(name = "title", nullable = false, length = 200)
    var title: String = "",
    
    @Column(name = "content", nullable = false, length = 10000)
    var content: String = "",
    
    @Column(name = "board_id", nullable = false)
    var boardId: Long = 0,
    
    @Column(name = "writer_id", nullable = false)
    var writerId: Long = 0,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "modified_at", nullable = false)
    var modifiedAt: LocalDateTime = LocalDateTime.now()
) {
    
    fun toDomainModel(): Article {
        return Article(
            articleId = ArticleId(articleId),
            title = Title(title),
            content = Content(content),
            boardId = BoardId(boardId),
            writerId = WriterId(writerId),
            createdAt = createdAt,
            modifiedAt = modifiedAt
        )
    }
    
    companion object {
        fun fromDomainModel(article: Article): ArticleJpaEntity {
            return ArticleJpaEntity(
                articleId = article.articleId.value,
                title = article.title.value,
                content = article.content.value,
                boardId = article.boardId.value,
                writerId = article.writerId.value,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt
            )
        }
    }
}