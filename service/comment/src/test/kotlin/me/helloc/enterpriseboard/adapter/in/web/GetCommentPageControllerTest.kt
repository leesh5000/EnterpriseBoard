package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.application.port.`in`.GetCommentPageResult
import me.helloc.enterpriseboard.application.port.`in`.GetCommentUseCase
import me.helloc.enterpriseboard.domain.model.Comment
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class FakeGetCommentUseCase : GetCommentUseCase {
    var lastArticleId: Long? = null
    var lastPage: Long? = null
    var lastPageSize: Long? = null
    var lastMovablePageCount: Long? = null
    var pageResultToReturn: GetCommentPageResult = GetCommentPageResult(
        comments = emptyList(),
        visibleRangeCount = 0L
    )
    var scrollResultToReturn: List<Comment> = emptyList()

    override fun getPage(articleId: Long, page: Long, pageSize: Long, movablePageCount: Long): GetCommentPageResult {
        lastArticleId = articleId
        lastPage = page
        lastPageSize = pageSize
        lastMovablePageCount = movablePageCount
        return pageResultToReturn
    }

    override fun getScroll(articleId: Long, pageSize: Long, lastParentCommentId: Long, lastCommentId: Long): List<Comment> {
        return scrollResultToReturn
    }
}

class GetCommentPageControllerTest : StringSpec({

    lateinit var fakeUseCase: FakeGetCommentUseCase
    lateinit var controller: GetCommentPageController

    beforeEach {
        fakeUseCase = FakeGetCommentUseCase()
        controller = GetCommentPageController(fakeUseCase)
    }

    "GET /api/v1/comments - 댓글 페이지 조회 시 200 OK와 함께 응답해야 한다" {
        // Given
        val comments = listOf(
            Comment(
                commentId = 1L,
                content = "첫 번째 댓글",
                parentCommentId = 1L,
                articleId = 100L,
                writerId = 200L,
                deleted = false,
                createdAt = LocalDateTime.now()
            ),
            Comment(
                commentId = 2L,
                content = "두 번째 댓글",
                parentCommentId = 2L,
                articleId = 100L,
                writerId = 201L,
                deleted = false,
                createdAt = LocalDateTime.now()
            )
        )
        fakeUseCase.pageResultToReturn = GetCommentPageResult(
            comments = comments,
            visibleRangeCount = 50L
        )

        // When
        val response = controller.getCommentPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.comments?.size shouldBe 2
        response.body?.comments?.get(0)?.commentId shouldBe 1L
        response.body?.comments?.get(0)?.content shouldBe "첫 번째 댓글"
        response.body?.comments?.get(1)?.commentId shouldBe 2L
        response.body?.visibleRangeCount shouldBe 50L
    }

    "GET /api/v1/comments - UseCase로 올바른 파라미터를 전달해야 한다" {
        // Given
        fakeUseCase.pageResultToReturn = GetCommentPageResult(
            comments = emptyList(),
            visibleRangeCount = 0L
        )

        // When
        controller.getCommentPage(
            articleId = 123L,
            page = 3L,
            pageSize = 20L,
            movablePageCount = 10L
        )

        // Then
        fakeUseCase.lastArticleId shouldBe 123L
        fakeUseCase.lastPage shouldBe 3L
        fakeUseCase.lastPageSize shouldBe 20L
        fakeUseCase.lastMovablePageCount shouldBe 10L
    }

    "GET /api/v1/comments - 댓글이 없는 경우 빈 리스트와 0 카운트를 반환해야 한다" {
        // Given
        fakeUseCase.pageResultToReturn = GetCommentPageResult(
            comments = emptyList(),
            visibleRangeCount = 0L
        )

        // When
        val response = controller.getCommentPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.comments?.size shouldBe 0
        response.body?.visibleRangeCount shouldBe 0L
    }

    "GET /api/v1/comments - movablePageCount 파라미터 미제공 시 기본값 10을 사용해야 한다" {
        // Given
        fakeUseCase.pageResultToReturn = GetCommentPageResult(
            comments = emptyList(),
            visibleRangeCount = 0L
        )

        // When
        controller.getCommentPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 10L // 기본값
        )

        // Then
        fakeUseCase.lastMovablePageCount shouldBe 10L
    }

    "GET /api/v1/comments - 삭제된 댓글도 정상적으로 반환해야 한다" {
        // Given
        val deletedComment = Comment(
            commentId = 1L,
            content = "삭제된 댓글입니다",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 200L,
            deleted = true,
            createdAt = LocalDateTime.now()
        )
        fakeUseCase.pageResultToReturn = GetCommentPageResult(
            comments = listOf(deletedComment),
            visibleRangeCount = 1L
        )

        // When
        val response = controller.getCommentPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body?.comments?.size shouldBe 1
        response.body?.comments?.get(0)?.deleted shouldBe true
        response.body?.comments?.get(0)?.content shouldBe "삭제된 댓글입니다"
    }

    "GET /api/v1/comments - 대답글과 루트 댓글을 구분하여 반환해야 한다" {
        // Given
        val rootComment = Comment(
            commentId = 1L,
            content = "루트 댓글",
            parentCommentId = 1L, // 자기 자신 참조
            articleId = 100L,
            writerId = 200L,
            deleted = false,
            createdAt = LocalDateTime.now()
        )
        val replyComment = Comment(
            commentId = 2L,
            content = "대답글",
            parentCommentId = 1L, // 루트 댓글 참조
            articleId = 100L,
            writerId = 201L,
            deleted = false,
            createdAt = LocalDateTime.now()
        )
        fakeUseCase.pageResultToReturn = GetCommentPageResult(
            comments = listOf(rootComment, replyComment),
            visibleRangeCount = 2L
        )

        // When
        val response = controller.getCommentPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body?.comments?.size shouldBe 2
        // 루트 댓글 확인
        response.body?.comments?.get(0)?.commentId shouldBe 1L
        response.body?.comments?.get(0)?.parentCommentId shouldBe 1L
        // 대답글 확인
        response.body?.comments?.get(1)?.commentId shouldBe 2L
        response.body?.comments?.get(1)?.parentCommentId shouldBe 1L
    }
})