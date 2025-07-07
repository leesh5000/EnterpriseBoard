package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class CreateArticleControllerTest : StringSpec({

    lateinit var fakeCreateUseCase: FakeCreateArticleUseCase
    lateinit var controller: CreateArticleController

    beforeEach {
        fakeCreateUseCase = FakeCreateArticleUseCase()
        controller = CreateArticleController(
            useCase = fakeCreateUseCase
        )
    }

    "POST /api/v1/articles - Article 생성 시 201 Created와 함께 응답해야 한다" {
        // Given
        val request = CreateArticleRequest(
            title = "새 게시글 제목",
            content = "새 게시글 내용",
            boardId = 100L,
            writerId = 200L
        )
        val expectedArticle = Article(
            articleId = 123L,
            title = request.title,
            content = request.content,
            boardId = request.boardId,
            writerId = request.writerId,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeCreateUseCase.articleToReturn = expectedArticle

        // When
        val response = controller.createArticle(request)

        // Then
        response.statusCode shouldBe HttpStatus.CREATED
        response.body shouldNotBe null
        response.body?.articleId shouldBe expectedArticle.articleId
        response.body?.title shouldBe expectedArticle.title
        response.body?.content shouldBe expectedArticle.content
        response.body?.boardId shouldBe expectedArticle.boardId
        response.body?.writerId shouldBe expectedArticle.writerId
    }

    "POST /api/v1/articles - UseCase에 올바른 Command가 전달되어야 한다" {
        // Given
        val request = CreateArticleRequest(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )

        // When
        controller.createArticle(request)

        // Then
        fakeCreateUseCase.lastCommand shouldNotBe null
        fakeCreateUseCase.lastCommand?.title shouldBe request.title
        fakeCreateUseCase.lastCommand?.content shouldBe request.content
        fakeCreateUseCase.lastCommand?.boardId shouldBe request.boardId
        fakeCreateUseCase.lastCommand?.writerId shouldBe request.writerId
    }

    "POST /api/v1/articles - 생성된 Article이 올바른 Response로 변환되어야 한다" {
        // Given
        val request = CreateArticleRequest(
            title = "변환 테스트 제목",
            content = "변환 테스트 내용",
            boardId = 100L,
            writerId = 200L
        )
        val createdTime = LocalDateTime.now()
        val expectedArticle = Article(
            articleId = 456L,
            title = request.title,
            content = request.content,
            boardId = request.boardId,
            writerId = request.writerId,
            createdAt = createdTime,
            modifiedAt = createdTime
        )
        fakeCreateUseCase.articleToReturn = expectedArticle

        // When
        val response = controller.createArticle(request)

        // Then
        val responseBody = response.body!!
        responseBody.articleId shouldBe expectedArticle.articleId
        responseBody.title shouldBe expectedArticle.title
        responseBody.content shouldBe expectedArticle.content
        responseBody.boardId shouldBe expectedArticle.boardId
        responseBody.writerId shouldBe expectedArticle.writerId
        responseBody.createdAt shouldBe expectedArticle.createdAt
        responseBody.modifiedAt shouldBe expectedArticle.modifiedAt
    }
})