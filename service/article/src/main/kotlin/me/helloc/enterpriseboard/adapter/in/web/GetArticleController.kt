package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.application.port.`in`.GetArticleUseCase
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class GetArticleController(
    private val useCase: GetArticleUseCase,
) {

    @GetMapping("/{articleId}")
    fun getArticle(@PathVariable articleId: Long): ResponseEntity<ArticleResponse> {
        val article = useCase.getById(articleId)
        if (article.isNull()) {
            throw ErrorCode.NOT_FOUND_ARTICLE.toException("articleId" to articleId)
        }
        return ResponseEntity.ok(ArticleResponse.from(article))
    }
}
