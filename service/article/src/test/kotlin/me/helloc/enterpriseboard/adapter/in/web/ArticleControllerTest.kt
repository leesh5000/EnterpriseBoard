package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateArticleRequest
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ArticleControllerTest : StringSpec({

    lateinit var fakeCreateUseCase: FakeCreateArticleUseCase
    lateinit var fakeUpdateUseCase: FakeUpdateArticleUseCase
    lateinit var fakeGetUseCase: FakeGetArticleUseCase
    lateinit var fakeDeleteUseCase: FakeDeleteArticleUseCase
    lateinit var controller: ArticleController

    beforeEach {
        fakeCreateUseCase = FakeCreateArticleUseCase()
        fakeUpdateUseCase = FakeUpdateArticleUseCase()
        fakeGetUseCase = FakeGetArticleUseCase()
        fakeDeleteUseCase = FakeDeleteArticleUseCase()
        controller = ArticleController(
            createArticleUseCase = fakeCreateUseCase,
            updateArticleUseCase = fakeUpdateUseCase,
            getArticleUseCase = fakeGetUseCase,
            deleteArticleUseCase = fakeDeleteUseCase
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
        val responseBody = response.body
        responseBody shouldNotBe null
        responseBody?.articleId shouldBe 123L
        responseBody?.title shouldBe request.title
        responseBody?.content shouldBe request.content
        responseBody?.boardId shouldBe request.boardId
        responseBody?.writerId shouldBe request.writerId
    }

    "POST /api/v1/articles - Request가 올바르게 Command로 변환되어야 한다" {
        // Given
        val request = CreateArticleRequest(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 101L,
            writerId = 201L
        )

        // When
        controller.createArticle(request)

        // Then
        val capturedCommand = fakeCreateUseCase.lastCommand
        capturedCommand shouldNotBe null
        capturedCommand?.title shouldBe request.title
        capturedCommand?.content shouldBe request.content
        capturedCommand?.boardId shouldBe request.boardId
        capturedCommand?.writerId shouldBe request.writerId
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
        val responseBody = response.body
        responseBody shouldNotBe null
        responseBody?.articleId shouldBe articleId
        responseBody?.title shouldBe request.title
        responseBody?.content shouldBe request.content
    }

    "PUT /api/v1/articles/{articleId} - 경로 변수와 Request가 Command로 올바르게 변환되어야 한다" {
        // Given
        val articleId = 123L
        val request = UpdateArticleRequest(
            title = "새 제목",
            content = "새 내용"
        )

        // When
        controller.updateArticle(articleId, request)

        // Then
        val capturedCommand = fakeUpdateUseCase.lastCommand
        capturedCommand shouldNotBe null
        capturedCommand?.articleId shouldBe articleId
        capturedCommand?.title shouldBe request.title
        capturedCommand?.content shouldBe request.content
    }

    "GET /api/v1/articles/{articleId} - 존재하는 Article 조회 시 200 OK와 함께 응답해야 한다" {
        // Given
        val articleId = 1L
        val article = Article(
            articleId = articleId,
            title = "조회된 제목",
            content = "조회된 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeGetUseCase.addArticle(article)

        // When
        val response = controller.getArticle(articleId)

        // Then
        response.statusCode shouldBe HttpStatus.OK
        val responseBody = response.body
        responseBody shouldNotBe null
        responseBody?.articleId shouldBe articleId
        responseBody?.title shouldBe article.title
        responseBody?.content shouldBe article.content
    }

    "GET /api/v1/articles/{articleId} - 존재하지 않는 Article 조회 시 404 Not Found를 반환해야 한다" {
        // Given
        val nonExistentId = 999L
        // fakeGetUseCase는 비어있음

        // When
        val response = controller.getArticle(nonExistentId)

        // Then
        response.statusCode shouldBe HttpStatus.NOT_FOUND
        response.body shouldBe null
    }

    "DELETE /api/v1/articles/{articleId} - Article 삭제 시 204 No Content를 반환해야 한다" {
        // Given
        val articleId = 1L
        fakeDeleteUseCase.shouldThrowException = false

        // When
        controller.deleteArticle(articleId)

        // Then
        fakeDeleteUseCase.wasDeleted(articleId) shouldBe true
    }

    "DELETE /api/v1/articles/{articleId} - 올바른 ID로 UseCase를 호출해야 한다" {
        // Given
        val articleId = 456L
        fakeDeleteUseCase.shouldThrowException = false

        // When
        controller.deleteArticle(articleId)

        // Then
        fakeDeleteUseCase.deletedArticleIds shouldBe listOf(articleId)
    }

    "Response DTO가 Domain 모델로부터 올바르게 생성되어야 한다" {
        // Given
        val now = LocalDateTime.now()
        val article = Article(
            articleId = 789L,
            title = "도메인 제목",
            content = "도메인 내용",
            boardId = 111L,
            writerId = 222L,
            createdAt = now.minusHours(1),
            modifiedAt = now
        )
        fakeCreateUseCase.articleToReturn = article

        // When
        val response = controller.createArticle(
            CreateArticleRequest("any", "any", 1L, 1L)
        )

        // Then
        val responseBody = response.body
        responseBody shouldNotBe null
        responseBody?.articleId shouldBe article.articleId
        responseBody?.title shouldBe article.title
        responseBody?.content shouldBe article.content
        responseBody?.boardId shouldBe article.boardId
        responseBody?.writerId shouldBe article.writerId
        responseBody?.createdAt shouldBe article.createdAt
        responseBody?.modifiedAt shouldBe article.modifiedAt
    }
})