package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponse
import me.helloc.enterpriseboard.application.port.`in`.GetCommentUseCase
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments/scroll")
class GetCommentScrollController(
    private val useCase: GetCommentUseCase,
) {

    @GetMapping
    fun getCommentScroll(
        @RequestParam articleId: Long,
        @RequestParam pageSize: Long,
        @RequestParam lastParentCommentId: Long = Comment.EMPTY_ID,
        @RequestParam lastCommentId: Long = Comment.EMPTY_ID
    ): ResponseEntity<List<CommentResponse>> {
        val comments = useCase.getScroll(
            articleId = articleId,
            pageSize = pageSize,
            lastParentCommentId = lastParentCommentId,
            lastCommentId = lastCommentId
        )
        val responses = comments.map { CommentResponse.from(it) }
        return ResponseEntity.ok(responses)
    }
}
