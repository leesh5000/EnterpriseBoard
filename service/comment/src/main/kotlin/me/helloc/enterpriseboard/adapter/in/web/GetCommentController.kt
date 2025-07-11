package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponse
import me.helloc.enterpriseboard.application.port.`in`.GetCommentUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class GetCommentController(
    private val useCase: GetCommentUseCase,
) {

    @GetMapping("/{commentId}")
    fun getComment(@PathVariable commentId: Long): ResponseEntity<CommentResponse> {
        val comment = useCase.getById(commentId) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(CommentResponse.from(comment))
    }
}