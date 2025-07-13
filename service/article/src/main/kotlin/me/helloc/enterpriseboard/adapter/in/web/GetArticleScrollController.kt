package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.application.port.`in`.GetArticleUseCase
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class GetArticleScrollController(
    private val useCase: GetArticleUseCase,
) {

    @GetMapping("/scroll")
    fun getArticleScroll(
        @RequestParam boardId: Long,
        @RequestParam pageSize: Long,
        @RequestParam lastArticleId: Long = Article.EMPTY_ID
    ): ResponseEntity<List<ArticleResponse>> {

        val articles = useCase.getScroll(boardId, pageSize, lastArticleId)
        val responses = articles.map { ArticleResponse.from(it) }
        return ResponseEntity.ok(responses)
    }
}
