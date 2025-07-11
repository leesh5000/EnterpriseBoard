package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateArticleRequest
import me.helloc.enterpriseboard.domain.model.Article

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class UpdateArticleControllerTest : StringSpec({

    lateinit var fakeUpdateUseCase: FakeUpdateArticleUseCase
    lateinit var controller: UpdateArticleController

    beforeEach {
        fakeUpdateUseCase = FakeUpdateArticleUseCase()
        controller = UpdateArticleController(
            useCase = fakeUpdateUseCase
        )
    }

    "PUT /api/v1/articles/{articleId} - Article 수정 시 200 OK와 함께 응답해야 한다" {
        // Given
        val articleId = 1L
        val request = UpdateArticleRequest(
            title = "수정된 제목",
            content = "수정된 내용"
        )
        val updatedArticle = Article(
            articleId = articleId,
            title = request.title,
            content = request.content,
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now().minusDays(1),
            modifiedAt = LocalDateTime.now()
        )
        fakeUpdateUseCase.articleToReturn = updatedArticle

        // When
        val response = controller.updateArticle(articleId, request)

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.articleId shouldBe articleId
        response.body?.title shouldBe request.title
        response.body?.content shouldBe request.content
        response.body?.boardId shouldBe updatedArticle.boardId
        response.body?.writerId shouldBe updatedArticle.writerId
    }

    "PUT /api/v1/articles/{articleId} - UseCase에 올바른 Command가 전달되어야 한다" {
        // Given
        val articleId = 123L
        val request = UpdateArticleRequest(
            title = "새 제목",
            content = "새 내용"
        )

        // When
        controller.updateArticle(articleId, request)

        // Then
        fakeUpdateUseCase.lastArticleId shouldBe articleId
        fakeUpdateUseCase.lastTitle shouldBe request.title
        fakeUpdateUseCase.lastContent shouldBe request.content
    }

    "PUT /api/v1/articles/{articleId} - 수정된 Article이 올바른 Response로 변환되어야 한다" {
        // Given
        val articleId = 456L
        val request = UpdateArticleRequest(
            title = "변환 테스트 제목",
            content = "변환 테스트 내용"
        )
        val updatedTime = LocalDateTime.now()
        val updatedArticle = Article(
            articleId = articleId,
            title = request.title,
            content = request.content,
            boardId = 100L,
            writerId = 200L,
            createdAt = updatedTime.minusHours(1),
            modifiedAt = updatedTime
        )
        fakeUpdateUseCase.articleToReturn = updatedArticle

        // When
        val response = controller.updateArticle(articleId, request)

        // Then
        val responseBody = response.body!!
        responseBody.articleId shouldBe updatedArticle.articleId
        responseBody.title shouldBe updatedArticle.title
        responseBody.content shouldBe updatedArticle.content
        responseBody.boardId shouldBe updatedArticle.boardId
        responseBody.writerId shouldBe updatedArticle.writerId
        responseBody.createdAt shouldBe updatedArticle.createdAt
        responseBody.modifiedAt shouldBe updatedArticle.modifiedAt
    }
})
