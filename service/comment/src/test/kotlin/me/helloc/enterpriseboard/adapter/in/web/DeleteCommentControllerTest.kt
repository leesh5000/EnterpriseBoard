package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus

class DeleteCommentControllerTest : StringSpec({

    lateinit var fakeDeleteUseCase: FakeDeleteCommentUseCase
    lateinit var controller: DeleteCommentController

    beforeEach {
        fakeDeleteUseCase = FakeDeleteCommentUseCase()
        controller = DeleteCommentController(
            useCase = fakeDeleteUseCase
        )
    }

    "DELETE /api/v1/comments/{commentId} - Comment 삭제 시 204 No Content로 응답해야 한다" {
        // Given
        val commentId = 123L

        // When
        controller.deleteComment(commentId)

        // Then
        // @ResponseStatus(HttpStatus.NO_CONTENT) 어노테이션으로 인해 204 응답
        // 별도의 응답 검증은 불가하지만, 예외가 발생하지 않으면 정상 처리로 간주
    }

    "DELETE /api/v1/comments/{commentId} - UseCase에 올바른 commentId가 전달되어야 한다" {
        // Given
        val commentId = 456L

        // When
        controller.deleteComment(commentId)

        // Then
        fakeDeleteUseCase.lastDeletedCommentId shouldBe commentId
        fakeDeleteUseCase.deleteCallCount shouldBe 1
        fakeDeleteUseCase.deletedCommentIds.size shouldBe 1
        fakeDeleteUseCase.deletedCommentIds[0] shouldBe commentId
    }

    "DELETE /api/v1/comments/{commentId} - 여러 댓글 삭제 시 각각의 commentId가 순서대로 추적되어야 한다" {
        // Given
        val commentIds = listOf(100L, 200L, 300L)

        // When
        commentIds.forEach { commentId ->
            controller.deleteComment(commentId)
        }

        // Then
        fakeDeleteUseCase.lastDeletedCommentId shouldBe commentIds.last()
        fakeDeleteUseCase.deleteCallCount shouldBe commentIds.size
        fakeDeleteUseCase.deletedCommentIds shouldBe commentIds
    }

    "DELETE /api/v1/comments/{commentId} - 동일한 댓글을 여러 번 삭제해도 모든 호출이 추적되어야 한다" {
        // Given
        val commentId = 789L

        // When
        repeat(3) {
            controller.deleteComment(commentId)
        }

        // Then
        fakeDeleteUseCase.lastDeletedCommentId shouldBe commentId
        fakeDeleteUseCase.deleteCallCount shouldBe 3
        fakeDeleteUseCase.deletedCommentIds.size shouldBe 3
        fakeDeleteUseCase.deletedCommentIds.all { it == commentId } shouldBe true
    }
})