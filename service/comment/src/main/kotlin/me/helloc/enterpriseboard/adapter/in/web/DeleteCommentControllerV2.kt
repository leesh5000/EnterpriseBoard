package me.helloc.enterpriseboard.adapter.`in`.web

import me.helloc.enterpriseboard.application.port.`in`.DeleteCommentUseCaseV2
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/comments")
class DeleteCommentControllerV2(
    private val useCase: DeleteCommentUseCaseV2,
) {

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(@PathVariable commentId: Long) {
        useCase.delete(commentId)
    }
}
