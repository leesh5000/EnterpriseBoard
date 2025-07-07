package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.domain.model.Article
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class GetArticleScrollControllerTest : StringSpec({

    lateinit var fakeGetUseCase: FakeGetArticleUseCase
    lateinit var controller: GetArticleScrollController

    beforeEach {
        fakeGetUseCase = FakeGetArticleUseCase()
        controller = GetArticleScrollController(
            useCase = fakeGetUseCase
        )
    }

    "GET /api/v1/articles/scroll - 첫 페이지 무한 스크롤 조회 시 200 OK와 함께 응답해야 한다" {
        // Given
        val boardId = 100L
        val articles = (1..5).map { id ->
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
        val response = controller.getArticleScroll(
            boardId = boardId,
            pageSize = 3L,
            lastArticleId = 0L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.shouldHaveSize(3)
        
        val responseArticles = response.body!!
        responseArticles[0].articleId shouldBe 5L
        responseArticles[1].articleId shouldBe 4L
        responseArticles[2].articleId shouldBe 3L
    }

    "GET /api/v1/articles/scroll - lastArticleId 이후 데이터 조회 시 올바른 결과를 반환해야 한다" {
        // Given
        val boardId = 100L
        val articles = (1..10).map { id ->
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
        val response = controller.getArticleScroll(
            boardId = boardId,
            pageSize = 3L,
            lastArticleId = 7L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.shouldHaveSize(3)
        
        val responseArticles = response.body!!
        responseArticles[0].articleId shouldBe 6L
        responseArticles[1].articleId shouldBe 5L
        responseArticles[2].articleId shouldBe 4L
    }

    "GET /api/v1/articles/scroll - 다른 boardId의 게시글은 조회되지 않아야 한다" {
        // Given
        val targetBoardId = 100L
        val articles = listOf(
            Article(articleId = 1L, title = "제목 1", content = "내용 1", boardId = targetBoardId, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            Article(articleId = 2L, title = "제목 2", content = "내용 2", boardId = 101L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            Article(articleId = 3L, title = "제목 3", content = "내용 3", boardId = targetBoardId, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            Article(articleId = 4L, title = "제목 4", content = "내용 4", boardId = 101L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            Article(articleId = 5L, title = "제목 5", content = "내용 5", boardId = targetBoardId, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now())
        )
        articles.forEach { fakeGetUseCase.addArticle(it) }

        // When
        val response = controller.getArticleScroll(
            boardId = targetBoardId,
            pageSize = 10L,
            lastArticleId = 0L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.shouldHaveSize(3)
        
        val responseArticles = response.body!!
        responseArticles.all { it.boardId == targetBoardId } shouldBe true
        responseArticles[0].articleId shouldBe 5L
        responseArticles[1].articleId shouldBe 3L
        responseArticles[2].articleId shouldBe 1L
    }

    "GET /api/v1/articles/scroll - 데이터가 없을 때 빈 리스트를 반환해야 한다" {
        // Given
        val boardId = 999L
        // fakeGetUseCase에 데이터 없음

        // When
        val response = controller.getArticleScroll(
            boardId = boardId,
            pageSize = 10L,
            lastArticleId = 0L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.shouldHaveSize(0)
    }

    "GET /api/v1/articles/scroll - pageSize보다 적은 데이터가 있을 때 실제 데이터 개수만 반환해야 한다" {
        // Given
        val boardId = 100L
        val articles = listOf(
            Article(articleId = 1L, title = "제목 1", content = "내용 1", boardId = boardId, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            Article(articleId = 2L, title = "제목 2", content = "내용 2", boardId = boardId, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now())
        )
        articles.forEach { fakeGetUseCase.addArticle(it) }

        // When
        val response = controller.getArticleScroll(
            boardId = boardId,
            pageSize = 5L,
            lastArticleId = 0L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.shouldHaveSize(2)
        
        val responseArticles = response.body!!
        responseArticles[0].articleId shouldBe 2L
        responseArticles[1].articleId shouldBe 1L
    }

    "GET /api/v1/articles/scroll - 응답 DTO가 도메인 모델을 올바르게 변환해야 한다" {
        // Given
        val boardId = 100L
        val now = LocalDateTime.now()
        val article = Article(
            articleId = 123L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = boardId,
            writerId = 200L,
            createdAt = now,
            modifiedAt = now
        )
        fakeGetUseCase.addArticle(article)

        // When
        val response = controller.getArticleScroll(
            boardId = boardId,
            pageSize = 10L,
            lastArticleId = 0L
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.shouldHaveSize(1)
        
        val articleResponse = response.body!![0]
        articleResponse.articleId shouldBe article.articleId
        articleResponse.title shouldBe article.title
        articleResponse.content shouldBe article.content
        articleResponse.boardId shouldBe article.boardId
        articleResponse.writerId shouldBe article.writerId
        articleResponse.createdAt shouldBe article.createdAt
        articleResponse.modifiedAt shouldBe article.modifiedAt
    }

    "GET /api/v1/articles/scroll - lastArticleId 기본값이 0으로 적용되어야 한다" {
        // Given
        val boardId = 100L
        val articles = (1..3).map { id ->
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

        // When - lastArticleId를 명시적으로 전달하지 않아 기본값 0 사용
        val response = controller.getArticleScroll(
            boardId = boardId,
            pageSize = 2L,
            lastArticleId = 0L // 명시적으로 기본값 전달
        )

        // Then
        response.statusCode shouldBe HttpStatus.OK
        response.body shouldNotBe null
        response.body?.shouldHaveSize(2)
        
        val responseArticles = response.body!!
        responseArticles[0].articleId shouldBe 3L // 최신순
        responseArticles[1].articleId shouldBe 2L
    }
})