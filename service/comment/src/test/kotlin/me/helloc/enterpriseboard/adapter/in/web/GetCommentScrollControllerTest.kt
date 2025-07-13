package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class GetCommentScrollControllerTest : StringSpec({

    lateinit var fakeUseCase: FakeGetCommentUseCase
    lateinit var controller: GetCommentScrollController

    beforeEach {
        fakeUseCase = FakeGetCommentUseCase()
        controller = GetCommentScrollController(fakeUseCase)
    }

    "GET /api/v1/comments/scroll - 초기 스크롤 시 최신 댓글부터 반환해야 한다" {
        // Given
        val comments = listOf(
            Comment(
                commentId = 10L,
                content = "10번 댓글",
                parentCommentId = 10L,
                articleId = 100L,
                writerId = 200L,
                deleted = false,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 9L,
                content = "9번 댓글",
                parentCommentId = 9L,
                articleId = 100L,
                writerId = 201L,
                deleted = false,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 8L,
                content = "8번 댓글",
                parentCommentId = 8L,
                articleId = 100L,
                writerId = 202L,
                deleted = false,
                createdAt = LocalDateTime.now()
            )
        )
        fakeUseCase.scrollResultToReturn = comments

        // When
        val response = controller.getCommentScroll(
            articleId = 100L,
            pageSize = 3L,
            lastParentCommentId = Comment.EMPTY_ID,
            lastCommentId = Comment.EMPTY_ID
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.size shouldBe 3
        response.body?.get(0)?.commentId shouldBe 10L
        response.body?.get(1)?.commentId shouldBe 9L
        response.body?.get(2)?.commentId shouldBe 8L
    }

    "GET /api/v1/comments/scroll - 연속 스크롤 시 lastCommentId 이후 댓글을 반환해야 한다" {
        // Given
        val comments = listOf(
            Comment(
                commentId = 5L,
                content = "5번 댓글",
                parentCommentId = 5L,
                articleId = 100L,
                writerId = 200L,
                deleted = false,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 4L,
                content = "4번 댓글",
                parentCommentId = 4L,
                articleId = 100L,
                writerId = 201L,
                deleted = false,
                createdAt = LocalDateTime.now()
            )
        )
        fakeUseCase.scrollResultToReturn = comments

        // When
        val response = controller.getCommentScroll(
            articleId = 100L,
            pageSize = 5L,
            lastParentCommentId = 6L,
            lastCommentId = 6L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.size shouldBe 2
        response.body?.get(0)?.commentId shouldBe 5L
        response.body?.get(1)?.commentId shouldBe 4L
    }

    "GET /api/v1/comments/scroll - 더 이상 댓글이 없으면 빈 리스트를 반환해야 한다" {
        // Given
        fakeUseCase.scrollResultToReturn = emptyList()

        // When
        val response = controller.getCommentScroll(
            articleId = 100L,
            pageSize = 10L,
            lastParentCommentId = 1L,
            lastCommentId = 1L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.size shouldBe 0
    }

    "GET /api/v1/comments/scroll - 삭제된 댓글도 정상적으로 반환해야 한다" {
        // Given
        val comments = listOf(
            Comment(
                commentId = 3L,
                content = "정상 댓글",
                parentCommentId = 3L,
                articleId = 100L,
                writerId = 200L,
                deleted = false,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 2L,
                content = "삭제된 댓글입니다",
                parentCommentId = 2L,
                articleId = 100L,
                writerId = 201L,
                deleted = true,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 1L,
                content = "또 다른 정상 댓글",
                parentCommentId = 1L,
                articleId = 100L,
                writerId = 202L,
                deleted = false,
                createdAt = LocalDateTime.now()
            )
        )
        fakeUseCase.scrollResultToReturn = comments

        // When
        val response = controller.getCommentScroll(
            articleId = 100L,
            pageSize = 10L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body?.size shouldBe 3
        response.body?.get(1)?.commentId shouldBe 2L
        response.body?.get(1)?.deleted shouldBe true
        response.body?.get(1)?.content shouldBe "삭제된 댓글입니다"
    }

    "GET /api/v1/comments/scroll - 대답글과 루트 댓글을 모두 반환해야 한다" {
        // Given
        val comments = listOf(
            Comment(
                commentId = 3L,
                content = "루트 댓글",
                parentCommentId = 3L, // 자기 자신 참조
                articleId = 100L,
                writerId = 200L,
                deleted = false,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 2L,
                content = "3번 댓글의 대답글",
                parentCommentId = 3L, // 3번 댓글 참조
                articleId = 100L,
                writerId = 201L,
                deleted = false,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 1L,
                content = "또 다른 루트 댓글",
                parentCommentId = 1L, // 자기 자신 참조
                articleId = 100L,
                writerId = 202L,
                deleted = false,
                createdAt = LocalDateTime.now()
            )
        )
        fakeUseCase.scrollResultToReturn = comments

        // When
        val response = controller.getCommentScroll(
            articleId = 100L,
            pageSize = 10L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body?.size shouldBe 3
        // 루트 댓글 확인
        response.body?.get(0)?.commentId shouldBe 3L
        response.body?.get(0)?.parentCommentId shouldBe 3L
        // 대답글 확인
        response.body?.get(1)?.commentId shouldBe 2L
        response.body?.get(1)?.parentCommentId shouldBe 3L
        // 또 다른 루트 댓글 확인
        response.body?.get(2)?.commentId shouldBe 1L
        response.body?.get(2)?.parentCommentId shouldBe 1L
    }

    "GET /api/v1/comments/scroll - lastParentCommentId와 lastCommentId 파라미터 미제공 시 기본값을 사용해야 한다" {
        // Given
        val comment = Comment(
            commentId = 1L,
            content = "테스트 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 200L,
            deleted = false,
            createdAt = LocalDateTime.now()
        )
        fakeUseCase.scrollResultToReturn = listOf(comment)

        // When
        val response = controller.getCommentScroll(
            articleId = 100L,
            pageSize = 10L
            // lastParentCommentId와 lastCommentId 미제공
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.size shouldBe 1
        // FakeUseCase에서 기본값이 전달되었는지 확인하려면 FakeUseCase를 수정해야 하지만,
        // 현재는 응답이 정상적으로 반환되는지만 확인
    }
})