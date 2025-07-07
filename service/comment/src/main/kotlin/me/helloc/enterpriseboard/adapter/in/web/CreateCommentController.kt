package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateCommentRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponse
import me.helloc.enterpriseboard.application.port.`in`.CreateCommentCommand
import me.helloc.enterpriseboard.application.port.`in`.CreateCommentUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class CreateCommentController(
    private val useCase: CreateCommentUseCase,
) {

    @PostMapping
    fun createComment(@RequestBody request: CreateCommentRequest): ResponseEntity<CommentResponse> {
        val command = CreateCommentCommand(
            content = request.content,
            parentCommentId = request.parentCommentId,
            articleId = request.articleId,
            writerId = request.writerId
        )

        val comment = useCase.create(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(comment))
    }
}