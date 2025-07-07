package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.GetArticlePageResponse
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.application.port.`in`.GetArticlePageQuery
import me.helloc.enterpriseboard.application.port.`in`.GetArticleUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class GetArticlePageController(
    private val useCase: GetArticleUseCase,
) {

    @GetMapping
    fun getArticlePage(
        @RequestParam boardId: Long,
        @RequestParam page: Long,
        @RequestParam pageSize: Long,
        @RequestParam(defaultValue = "10") movablePageCount: Long
    ): ResponseEntity<GetArticlePageResponse> {
        val query = GetArticlePageQuery(
            boardId = boardId,
            page = page,
            pageSize = pageSize,
            movablePageCount = movablePageCount
        )

        val articlePage = useCase.getPage(query)

        val response = GetArticlePageResponse.of(
            articles = articlePage.articles.map { ArticleResponse.from(it) },
            totalCount = articlePage.count
        )

        return ResponseEntity.ok(response)
    }
}