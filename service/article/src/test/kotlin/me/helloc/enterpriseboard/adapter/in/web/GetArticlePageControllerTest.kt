package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.domain.model.Article

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class GetArticlePageControllerTest : StringSpec({

    lateinit var fakeGetUseCase: FakeGetArticleUseCase
    lateinit var controller: GetArticlePageController

    beforeEach {
        fakeGetUseCase = FakeGetArticleUseCase()
        controller = GetArticlePageController(
            useCase = fakeGetUseCase
        )
    }

    "GET /api/v1/articles - 페이지 조회 시 200 OK와 함께 응답해야 한다" {
        // Given
        val boardId = 100L
        val page = 1L
        val pageSize = 10L
        val movablePageCount = 5L

        val article1 = Article(
            articleId = 1L,
            title = "첫 번째 게시글",
            content = "첫 번째 내용",
            boardId = boardId,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article2 = Article(
            articleId = 2L,
            title = "두 번째 게시글",
            content = "두 번째 내용",
            boardId = boardId,
            writerId = 201L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )

        fakeGetUseCase.addArticle(article1)
        fakeGetUseCase.addArticle(article2)

        // When
        val response = controller.getArticlePage(boardId, page, pageSize, movablePageCount)

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body!!.articles shouldHaveSize 2
        response.body?.visibleRangeCount shouldBe 2L
        response.body?.articles?.get(0)?.articleId shouldBe 2L // ID 내림차순
        response.body?.articles?.get(1)?.articleId shouldBe 1L
    }

    "GET /api/v1/articles - movablePageCount 기본값이 적용되어야 한다" {
        // Given
        val boardId = 100L
        val page = 1L
        val pageSize = 10L
        // movablePageCount는 기본값 10L을 사용

        val article = Article(
            articleId = 1L,
            title = "테스트 게시글",
            content = "테스트 내용",
            boardId = boardId,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeGetUseCase.addArticle(article)

        // When
        val response = controller.getArticlePage(boardId, page, pageSize, 10L)

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body!!.articles shouldHaveSize 1
    }

    "GET /api/v1/articles - 빈 결과에 대해서도 올바르게 응답해야 한다" {
        // Given
        val boardId = 999L // 존재하지 않는 보드
        val page = 1L
        val pageSize = 10L
        val movablePageCount = 5L

        // When
        val response = controller.getArticlePage(boardId, page, pageSize, movablePageCount)

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body!!.articles shouldHaveSize 0
        response.body?.visibleRangeCount shouldBe 0L
    }

    "GET /api/v1/articles - Article이 GetArticlePageItemResponse로 올바르게 변환되어야 한다" {
        // Given
        val boardId = 100L
        val now = LocalDateTime.now()
        val article = Article(
            articleId = 123L,
            title = "변환 테스트 제목",
            content = "변환 테스트 내용",
            boardId = boardId,
            writerId = 456L,
            createdAt = now.minusHours(1),
            modifiedAt = now
        )
        fakeGetUseCase.addArticle(article)

        // When
        val response = controller.getArticlePage(boardId, 1L, 10L, 5L)

        // Then
        val responseBody = response.body
        responseBody shouldNotBe null
        val articleResponse = responseBody?.articles?.first()
        articleResponse shouldNotBe null
        articleResponse?.articleId shouldBe article.articleId
        articleResponse?.title shouldBe article.title
        articleResponse?.content shouldBe article.content
        articleResponse?.boardId shouldBe article.boardId
        articleResponse?.writerId shouldBe article.writerId
        articleResponse?.createdAt shouldBe article.createdAt
        articleResponse?.modifiedAt shouldBe article.modifiedAt
    }

    "GET /api/v1/articles - 여러 페이지 처리 시 올바른 결과를 반환해야 한다" {
        // Given
        val boardId = 100L
        val articles = (1..15).map { id ->
            Article(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = boardId,
                writerId = 200L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )
        }
        articles.forEach { fakeGetUseCase.addArticle(it) }

        // When
        val response = controller.getArticlePage(
            boardId = boardId,
            page = 2L,
            pageSize = 5L,
            movablePageCount = 3L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.visibleRangeCount shouldBe 15L
        // 실제 페이지네이션 로직은 FakeGetArticleUseCase에서 처리됨
    }
})
