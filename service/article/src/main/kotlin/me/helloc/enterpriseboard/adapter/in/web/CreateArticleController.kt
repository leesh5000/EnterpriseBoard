package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleCommand
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles")
class CreateArticleController(
    private val useCase: CreateArticleUseCase,
) {

    @PostMapping
    fun createArticle(@RequestBody request: CreateArticleRequest): ResponseEntity<ArticleResponse> {
        val command = CreateArticleCommand(
            title = request.title,
            content = request.content,
            boardId = request.boardId,
            writerId = request.writerId
        )

        val article = useCase.create(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(ArticleResponse.from(article))
    }
}