package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.adapter.`in`.web.dto.CommentResponse
import me.helloc.enterpriseboard.adapter.`in`.web.dto.GetCommentPageResponse
import me.helloc.enterpriseboard.application.port.`in`.GetCommentUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comments")
class GetCommentPageController(
    private val useCase: GetCommentUseCase,
) {

    @GetMapping
    fun getArticlePage(
        @RequestParam boardId: Long,
        @RequestParam page: Long,
        @RequestParam pageSize: Long,
        @RequestParam(defaultValue = "10") movablePageCount: Long
    ): ResponseEntity<GetCommentPageResponse> {
        val commentPage = useCase.getPage(boardId, page, pageSize, movablePageCount)

        val response = GetCommentPageResponse.of(
            comments = commentPage.comments.map { CommentResponse.from(it) },
            totalCount = commentPage.limitedTotalCount
        )

        return ResponseEntity.ok(response)
    }
}
