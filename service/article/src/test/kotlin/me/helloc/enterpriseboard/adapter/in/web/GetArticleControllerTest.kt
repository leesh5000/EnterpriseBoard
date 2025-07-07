package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class GetArticleControllerTest : StringSpec({

    lateinit var fakeGetUseCase: FakeGetArticleUseCase
    lateinit var controller: GetArticleController

    beforeEach {
        fakeGetUseCase = FakeGetArticleUseCase()
        controller = GetArticleController(
            useCase = fakeGetUseCase
        )
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
        response.body shouldNotBe null
        response.body?.articleId shouldBe articleId
        response.body?.title shouldBe article.title
        response.body?.content shouldBe article.content
        response.body?.boardId shouldBe article.boardId
        response.body?.writerId shouldBe article.writerId
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

    "GET /api/v1/articles/{articleId} - Article이 올바른 Response로 변환되어야 한다" {
        // Given
        val articleId = 789L
        val now = LocalDateTime.now()
        val article = Article(
            articleId = articleId,
            title = "도메인 제목",
            content = "도메인 내용",
            boardId = 111L,
            writerId = 222L,
            createdAt = now.minusHours(1),
            modifiedAt = now
        )
        fakeGetUseCase.addArticle(article)

        // When
        val response = controller.getArticle(articleId)

        // Then
        val responseBody = response.body!!
        responseBody.articleId shouldBe article.articleId
        responseBody.title shouldBe article.title
        responseBody.content shouldBe article.content
        responseBody.boardId shouldBe article.boardId
        responseBody.writerId shouldBe article.writerId
        responseBody.createdAt shouldBe article.createdAt
        responseBody.modifiedAt shouldBe article.modifiedAt
    }
})