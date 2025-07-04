package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateArticleRequest
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleCommand
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleUseCase
import me.helloc.enterpriseboard.application.port.`in`.DeleteArticleUseCase
import me.helloc.enterpriseboard.application.port.`in`.GetArticleUseCase
import me.helloc.enterpriseboard.application.port.`in`.UpdateArticleCommand
import me.helloc.enterpriseboard.application.port.`in`.UpdateArticleUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class ArticleController(
    private val createArticleUseCase: CreateArticleUseCase,
    private val updateArticleUseCase: UpdateArticleUseCase,
    private val getArticleUseCase: GetArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase
) {

    @PostMapping
    fun createArticle(@RequestBody request: CreateArticleRequest): ResponseEntity<ArticleResponse> {
        val command = CreateArticleCommand(
            title = request.title,
            content = request.content,
            boardId = request.boardId,
            writerId = request.writerId
        )

        val article = createArticleUseCase.create(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(ArticleResponse.from(article))
    }

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

        val article = updateArticleUseCase.update(command)
        return ResponseEntity.ok(ArticleResponse.from(article))
    }

    @GetMapping("/{articleId}")
    fun getArticle(@PathVariable articleId: Long): ResponseEntity<ArticleResponse> {
        val article = getArticleUseCase.getById(articleId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(ArticleResponse.from(article))
    }

    @DeleteMapping("/{articleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteArticle(@PathVariable articleId: Long) {
        deleteArticleUseCase.delete(articleId)
    }
}
