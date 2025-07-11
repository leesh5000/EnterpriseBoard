package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateCommentRequest
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class CreateCommentControllerTest : StringSpec({

    lateinit var fakeCreateUseCase: FakeCreateCommentUseCase
    lateinit var controller: CreateCommentController

    beforeEach {
        fakeCreateUseCase = FakeCreateCommentUseCase()
        controller = CreateCommentController(
            useCase = fakeCreateUseCase
        )
    }

    "POST /api/v1/comments - Comment 생성 시 201 Created와 함께 응답해야 한다" {
        // Given
        val request = CreateCommentRequest(
            content = "새 댓글 내용",
            parentCommentId = Comment.NO_PARENT_ID,
            articleId = 100L,
            writerId = 200L
        )
        val expectedComment = Comment(
            commentId = 123L,
            content = request.content,
            parentCommentId = 123L, // 루트 댓글이므로 자기 자신을 참조
            articleId = request.articleId,
            writerId = request.writerId,
            deleted = false,
            createdAt = LocalDateTime.now()
        )
        fakeCreateUseCase.commentToReturn = expectedComment

        // When
        val response = controller.createComment(request)

        // Then
        response.statusCode shouldBe HttpStatus.CREATED
        response.body shouldNotBe null
        response.body?.commentId shouldBe expectedComment.commentId
        response.body?.content shouldBe expectedComment.content
        response.body?.parentCommentId shouldBe expectedComment.parentCommentId
        response.body?.articleId shouldBe expectedComment.articleId
        response.body?.writerId shouldBe expectedComment.writerId
        response.body?.deleted shouldBe expectedComment.deleted
    }

    "POST /api/v1/comments - UseCase에 올바른 파라미터가 전달되어야 한다" {
        // Given
        val request = CreateCommentRequest(
            content = "테스트 댓글",
            parentCommentId = 50L,
            articleId = 100L,
            writerId = 200L
        )

        // When
        controller.createComment(request)

        // Then
        fakeCreateUseCase.lastContent shouldBe request.content
        fakeCreateUseCase.lastParentCommentId shouldBe request.parentCommentId
        fakeCreateUseCase.lastArticleId shouldBe request.articleId
        fakeCreateUseCase.lastWriterId shouldBe request.writerId
    }

    "POST /api/v1/comments - 생성된 Comment가 올바른 Response로 변환되어야 한다" {
        // Given
        val request = CreateCommentRequest(
            content = "변환 테스트 댓글",
            parentCommentId = Comment.NO_PARENT_ID,
            articleId = 100L,
            writerId = 200L
        )
        val createdTime = LocalDateTime.now()
        val expectedComment = Comment(
            commentId = 456L,
            content = request.content,
            parentCommentId = 456L, // 루트 댓글이므로 자기 자신을 참조
            articleId = request.articleId,
            writerId = request.writerId,
            deleted = false,
            createdAt = createdTime
        )
        fakeCreateUseCase.commentToReturn = expectedComment

        // When
        val response = controller.createComment(request)

        // Then
        val responseBody = response.body!!
        responseBody.commentId shouldBe expectedComment.commentId
        responseBody.content shouldBe expectedComment.content
        responseBody.parentCommentId shouldBe expectedComment.parentCommentId
        responseBody.articleId shouldBe expectedComment.articleId
        responseBody.writerId shouldBe expectedComment.writerId
        responseBody.deleted shouldBe expectedComment.deleted
        responseBody.createdAt shouldBe expectedComment.createdAt
    }

    "POST /api/v1/comments - 대댓글 생성 시 parentCommentId가 올바르게 처리되어야 한다" {
        // Given
        val parentCommentId = 10L
        val request = CreateCommentRequest(
            content = "대댓글 내용",
            parentCommentId = parentCommentId,
            articleId = 100L,
            writerId = 200L
        )
        val expectedComment = Comment(
            commentId = 789L,
            content = request.content,
            parentCommentId = parentCommentId, // 대댓글이므로 부모 댓글 ID 유지
            articleId = request.articleId,
            writerId = request.writerId,
            deleted = false,
            createdAt = LocalDateTime.now()
        )
        fakeCreateUseCase.commentToReturn = expectedComment

        // When
        val response = controller.createComment(request)

        // Then
        response.statusCode shouldBe HttpStatus.CREATED
        response.body?.parentCommentId shouldBe parentCommentId
        fakeCreateUseCase.lastParentCommentId shouldBe parentCommentId
    }
})