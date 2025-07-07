package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateCommentRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponse
import me.helloc.enterpriseboard.application.port.`in`.UpdateCommentCommand
import me.helloc.enterpriseboard.application.port.`in`.UpdateCommentUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class UpdateCommentController(
    private val useCase: UpdateCommentUseCase,
) {

    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long,
        @RequestBody request: UpdateCommentRequest
    ): ResponseEntity<CommentResponse> {
        val command = UpdateCommentCommand(
            commentId = commentId,
            content = request.content
        )

        val comment = useCase.update(command)
        return ResponseEntity.ok(CommentResponse.from(comment))
    }
}