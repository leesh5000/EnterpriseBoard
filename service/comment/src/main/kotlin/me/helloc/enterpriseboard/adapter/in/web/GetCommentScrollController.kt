package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponse
import me.helloc.enterpriseboard.application.port.`in`.GetCommentUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class GetCommentScrollController(
    private val useCase: GetCommentUseCase,
) {

    @GetMapping("/scroll")
    fun getCommentScroll(
        @RequestParam articleId: Long,
        @RequestParam pageSize: Long,
        @RequestParam(defaultValue = "0") lastCommentId: Long
    ): ResponseEntity<List<CommentResponse>> {

        val comments = useCase.getScroll(articleId, pageSize, lastCommentId)
        val responses = comments.map { CommentResponse.from(it) }
        return ResponseEntity.ok(responses)
    }
}