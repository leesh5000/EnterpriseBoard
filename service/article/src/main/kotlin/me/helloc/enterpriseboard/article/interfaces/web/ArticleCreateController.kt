package me.helloc.enterpriseboard.article.interfaces.web

import jakarta.validation.Valid
import me.helloc.enterpriseboard.article.application.port.input.ArticleUseCase
import me.helloc.enterpriseboard.article.application.port.input.command.DeleteArticleCommand
import me.helloc.enterpriseboard.article.application.port.input.query.*
import me.helloc.enterpriseboard.article.interfaces.web.dto.ArticleResponse
import me.helloc.enterpriseboard.article.interfaces.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.article.interfaces.web.dto.UpdateArticleRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 웹 어댑터 - HTTP 요청을 애플리케이션 명령/쿼리로 변환
 */
@RestController
@RequestMapping("/api/v1")
class ArticleController(
    private val articleUseCase: ArticleUseCase
) {

    @PostMapping("/articles")
    fun createArticle(@Valid @RequestBody request: CreateArticleRequest): ResponseEntity<ArticleResponse> {
        val result = articleUseCase.createArticle(request.toCommand())
        val response = ArticleResponse(
            articleId = result.articleId,
            title = result.title,
            content = result.content,
            boardId = result.boardId,
            writerId = result.writerId,
            createdAt = java.time.LocalDateTime.parse(result.createdAt),
            modifiedAt = java.time.LocalDateTime.parse(result.createdAt)
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/articles/{articleId}")
    fun getArticle(@PathVariable articleId: Long): ResponseEntity<ArticleResponse> {
        val result = articleUseCase.getArticle(GetArticleQuery(articleId))
        val response = ArticleResponse.from(result.article)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/articles")
    fun getAllArticles(): ResponseEntity<List<ArticleResponse>> {
        val result = articleUseCase.getAllArticles(GetAllArticlesQuery())
        val responses = result.articles.map { ArticleResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/boards/{boardId}/articles")
    fun getArticlesByBoard(@PathVariable boardId: Long): ResponseEntity<List<ArticleResponse>> {
        val result = articleUseCase.getArticlesByBoard(GetArticlesByBoardQuery(boardId))
        val responses = result.articles.map { ArticleResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @GetMapping("/writers/{writerId}/articles")
    fun getArticlesByWriter(@PathVariable writerId: Long): ResponseEntity<List<ArticleResponse>> {
        val result = articleUseCase.getArticlesByWriter(GetArticlesByWriterQuery(writerId))
        val responses = result.articles.map { ArticleResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @PutMapping("/articles/{articleId}")
    fun updateArticle(
        @PathVariable articleId: Long,
        @Valid @RequestBody request: UpdateArticleRequest
    ): ResponseEntity<ArticleResponse> {
        val result = articleUseCase.updateArticle(request.toCommand(articleId))
        val response = ArticleResponse(
            articleId = result.articleId,
            title = result.title,
            content = result.content,
            boardId = 0, // 업데이트 결과에서는 boardId가 없으므로 별도 조회 필요
            writerId = 0, // 업데이트 결과에서는 writerId가 없으므로 별도 조회 필요
            createdAt = java.time.LocalDateTime.now(), // 임시
            modifiedAt = java.time.LocalDateTime.parse(result.modifiedAt)
        )
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/articles/{articleId}")
    fun deleteArticle(
        @PathVariable articleId: Long,
        @RequestParam requesterId: Long
    ): ResponseEntity<Void> {
        articleUseCase.deleteArticle(DeleteArticleCommand(articleId, requesterId))
        return ResponseEntity.noContent().build()
    }
}
