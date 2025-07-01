package me.helloc.enterpriseboard.article.service.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ArticleUpdateRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(max = 200, message = "제목은 200자 이하여야 합니다")
    val title: String,
    
    @field:NotBlank(message = "내용은 필수입니다")
    @field:Size(max = 10000, message = "내용은 10000자 이하여야 합니다")
    val content: String
)
