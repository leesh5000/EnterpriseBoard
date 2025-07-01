package me.helloc.enterpriseboard.article.interfaces.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import me.helloc.enterpriseboard.article.application.port.input.command.CreateArticleCommand
import me.helloc.enterpriseboard.article.application.port.input.command.UpdateArticleCommand
import me.helloc.enterpriseboard.article.application.port.input.query.ArticleView
import java.time.LocalDateTime

/**
 * 웹 레이어 DTO - HTTP 요청/응답을 위한 데이터 구조
 */

data class CreateArticleRequest(
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
    fun toCommand(): CreateArticleCommand {
        return CreateArticleCommand(
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId
        )
    }
}

data class UpdateArticleRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(max = 200, message = "제목은 200자 이하여야 합니다")
    val title: String,
    
    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(max = 10000, message = "내용은 10000자 이하여야 합니다")
    val content: String,
    
    @field:NotNull(message = "요청자 ID는 필수입니다")
    @field:Positive(message = "요청자 ID는 양수여야 합니다")
    val requesterId: Long
) {
    fun toCommand(articleId: Long): UpdateArticleCommand {
        return UpdateArticleCommand(
            articleId = articleId,
            title = title,
            content = content,
            requesterId = requesterId
        )
    }
}

data class ArticleResponse(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
) {
    companion object {
        fun from(articleView: ArticleView): ArticleResponse {
            return ArticleResponse(
                articleId = articleView.articleId,
                title = articleView.title,
                content = articleView.content,
                boardId = articleView.boardId,
                writerId = articleView.writerId,
                createdAt = articleView.createdAt,
                modifiedAt = articleView.modifiedAt
            )
        }
    }
}