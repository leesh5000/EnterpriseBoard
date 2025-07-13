package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponseV2
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateCommentRequestV2
import me.helloc.enterpriseboard.application.port.`in`.CreateCommentUseCaseV2
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/comments")
class CreateCommentControllerV2(
    private val useCase: CreateCommentUseCaseV2,
) {

    @PostMapping
    fun createComment(@RequestBody request: CreateCommentRequestV2): ResponseEntity<CommentResponseV2> {
        val comment = useCase.create(
            content = request.content,
            parentPath = request.parentPath,
            articleId = request.articleId,
            writerId = request.writerId
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponseV2.from(comment))
    }
}
