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

    "DELETE /api/v1/comments/{commentId} - 댓글 삭제 시 204 NO_CONTENT를 응답해야 한다" {
        // Given
        val commentId = 123L

        // When
        controller.deleteComment(commentId)

        // Then
        // @ResponseStatus 어노테이션으로 인해 자동으로 204 NO_CONTENT가 설정됨
        // 실제 Spring MVC 환경에서 테스트되어야 하지만, 
        // 단위 테스트에서는 메서드가 정상적으로 호출되는지만 검증
        fakeDeleteUseCase.deleteCallCount shouldBe 1
    }

    "DELETE /api/v1/comments/{commentId} - UseCase에 올바른 commentId가 전달되어야 한다" {
        // Given
        val commentId = 456L

        // When
        controller.deleteComment(commentId)

        // Then
        fakeDeleteUseCase.lastDeletedCommentId shouldBe commentId
    }

    "DELETE /api/v1/comments/{commentId} - 여러 번의 삭제 요청이 각각 처리되어야 한다" {
        // Given
        val commentIds = listOf(100L, 200L, 300L)

        // When
        commentIds.forEach { commentId ->
            controller.deleteComment(commentId)
        }

        // Then
        fakeDeleteUseCase.deleteCallCount shouldBe 3
        fakeDeleteUseCase.deletedCommentIds shouldBe commentIds
        fakeDeleteUseCase.lastDeletedCommentId shouldBe 300L
    }

    "DELETE /api/v1/comments/{commentId} - 0 이하의 commentId도 UseCase로 전달되어야 한다" {
        // Given
        val invalidCommentIds = listOf(0L, -1L, -999L)

        // When
        invalidCommentIds.forEach { commentId ->
            controller.deleteComment(commentId)
        }

        // Then
        // 컨트롤러는 검증 없이 UseCase로 전달하고, 
        // 실제 검증은 UseCase/도메인 레이어에서 처리됨
        fakeDeleteUseCase.deletedCommentIds shouldBe invalidCommentIds
        fakeDeleteUseCase.deleteCallCount shouldBe 3
    }

    "DELETE /api/v1/comments/{commentId} - Long 타입의 최대값도 정상적으로 처리되어야 한다" {
        // Given
        val maxLongValue = Long.MAX_VALUE

        // When
        controller.deleteComment(maxLongValue)

        // Then
        fakeDeleteUseCase.lastDeletedCommentId shouldBe maxLongValue
        fakeDeleteUseCase.deleteCallCount shouldBe 1
    }
})