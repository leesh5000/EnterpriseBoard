package me.helloc.enterpriseboard.article.service.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import me.helloc.enterpriseboard.article.domain.Article

data class ArticleCreateRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(max = 200, message = "제목은 200자 이하여야 합니다")
    val title: String,
    
    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(max = 10000, message = "내용은 10000자 이하여야 합니다")
    val content: String,
    
    @field:NotNull(message = "게시판 ID는 필수입니다")
    @field:Positive(message = "게시판 ID는 양수여야 합니다")
    val boardId: Long,
    
    @field:NotNull(message = "작성자 ID는 필수입니다")
    @field:Positive(message = "작성자 ID는 양수여야 합니다")
    val writerId: Long
) {
    fun toEntity(articleId: Long): Article {
        return Article.create(
            articleId = articleId,
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId
        )
    }
}
