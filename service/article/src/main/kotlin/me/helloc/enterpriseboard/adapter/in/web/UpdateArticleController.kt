package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.application.port.`in`.UpdateArticleCommand
import me.helloc.enterpriseboard.application.port.`in`.UpdateArticleUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class UpdateArticleController(
    private val useCase: UpdateArticleUseCase,
) {

    @PutMapping("/{articleId}")
    fun updateArticle(
        @PathVariable articleId: Long,
        @RequestBody request: UpdateArticleRequest
    ): ResponseEntity<ArticleResponse> {
        val command = UpdateArticleCommand(
            articleId = articleId,
            title = request.title,
            content = request.content
        )

        val article = useCase.update(command)
        return ResponseEntity.ok(ArticleResponse.from(article))
    }
}