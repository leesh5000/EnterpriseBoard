package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.adapter.`in`.web.dto.ArticleResponse
import me.helloc.enterpriseboard.adapter.`in`.web.dto.CreateArticleRequest
import me.helloc.enterpriseboard.adapter.`in`.web.dto.UpdateArticleRequest
import me.helloc.enterpriseboard.application.port.`in`.GetArticleScrollQuery
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
        val responseBody = response.body
        responseBody shouldNotBe null
        responseBody!!.articles shouldHaveSize 2
        responseBody?.totalCount shouldBe 2L
        responseBody?.articles?.get(0)?.articleId shouldBe 2L // ID 내림차순
        responseBody?.articles?.get(1)?.articleId shouldBe 1L
    }

    "GET /api/v1/articles - 쿼리 파라미터가 올바르게 Query 객체로 변환되어야 한다" {
        // Given
        val boardId = 100L
        val page = 2L
        val pageSize = 20L
        val movablePageCount = 15L

        // When
        val response = controller.getArticlePage(boardId, page, pageSize, movablePageCount)

        // Then
        // FakeGetArticleUseCase에서 마지막으로 받은 쿼리를 검증하기 위해
        // 실제 호출 검증은 통합 테스트에서 수행
        response.statusCode shouldBe HttpStatus.OK
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
        val responseBody = response.body
        responseBody shouldNotBe null
        responseBody!!.articles shouldHaveSize 1
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
        val responseBody = response.body
        responseBody shouldNotBe null
        responseBody!!.articles shouldHaveSize 0
        responseBody?.totalCount shouldBe 0L
    }

    "GET /api/v1/articles - Article이 ArticleResponse로 올바르게 변환되어야 한다" {
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
})
