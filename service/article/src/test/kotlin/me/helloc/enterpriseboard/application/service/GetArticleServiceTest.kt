package me.helloc.enterpriseboard.application.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.application.port.`in`.GetArticlePageQuery
import me.helloc.enterpriseboard.domain.model.Article

class GetArticleServiceTest : StringSpec({

    lateinit var fakeRepository: FakeArticleRepository
    lateinit var getArticleService: GetArticleService

    beforeEach {
        fakeRepository = FakeArticleRepository()
        getArticleService = GetArticleService(fakeRepository)
    }

    "ID로 Article을 조회할 수 있어야 한다" {
        // Given
        val article = Article.create(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(article)

        // When
        val foundArticle = getArticleService.getById(1L)

        // Then
        foundArticle shouldNotBe null
        foundArticle shouldBe article
    }

    "존재하지 않는 ID로 조회하면 null을 반환해야 한다" {
        // Given
        // Repository가 비어있음

        // When
        val foundArticle = getArticleService.getById(999L)

        // Then
        foundArticle shouldBe null
    }

    "Board ID로 Article 목록을 조회할 수 있어야 한다" {
        // Given
        val article1 = Article.create(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L
        )
        val article2 = Article.create(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 201L
        )
        val article3 = Article.create(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 101L,
            writerId = 200L
        )
        fakeRepository.save(article1)
        fakeRepository.save(article2)
        fakeRepository.save(article3)

        // When
        val articles = getArticleService.getByBoardId(100L)

        // Then
        articles shouldHaveSize 2
        articles shouldContainExactlyInAnyOrder listOf(article1, article2)
    }

    "Board ID로 조회 시 해당하는 Article이 없으면 빈 리스트를 반환해야 한다" {
        // Given
        val article = Article.create(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(article)

        // When
        val articles = getArticleService.getByBoardId(999L)

        // Then
        articles.shouldBeEmpty()
    }

    "Writer ID로 Article 목록을 조회할 수 있어야 한다" {
        // Given
        val article1 = Article.create(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L
        )
        val article2 = Article.create(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 101L,
            writerId = 200L
        )
        val article3 = Article.create(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 100L,
            writerId = 201L
        )
        fakeRepository.save(article1)
        fakeRepository.save(article2)
        fakeRepository.save(article3)

        // When
        val articles = getArticleService.getByWriterId(200L)

        // Then
        articles shouldHaveSize 2
        articles shouldContainExactlyInAnyOrder listOf(article1, article2)
    }

    "Writer ID로 조회 시 해당하는 Article이 없으면 빈 리스트를 반환해야 한다" {
        // Given
        val article = Article.create(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(article)

        // When
        val articles = getArticleService.getByWriterId(999L)

        // Then
        articles.shouldBeEmpty()
    }

    "Repository가 비어있을 때 모든 조회는 null 또는 빈 리스트를 반환해야 한다" {
        // Given
        // Repository가 비어있음

        // When & Then
        getArticleService.getById(1L) shouldBe null
        getArticleService.getByBoardId(100L).shouldBeEmpty()
        getArticleService.getByWriterId(200L).shouldBeEmpty()
    }

    "페이지 조회 시 올바른 Article 목록과 totalCount를 반환해야 한다" {
        // Given
        val article1 = Article.create(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L
        )
        val article2 = Article.create(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 201L
        )
        val article3 = Article.create(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 100L,
            writerId = 202L
        )
        fakeRepository.save(article1)
        fakeRepository.save(article2)
        fakeRepository.save(article3)

        val query = GetArticlePageQuery(
            boardId = 100L,
            page = 1L,
            pageSize = 2L,
            movablePageCount = 10L
        )

        // When
        val result = getArticleService.getPage(query)

        // Then
        result.articles shouldHaveSize 2
        result.articles shouldContainExactlyInAnyOrder listOf(article3, article2) // ID 내림차순 정렬
        result.totalCount shouldBe 3L // 전체 카운트는 limit에 의해 제한됨
    }

    "첫 번째 페이지 조회 시 offset이 0이어야 한다" {
        // Given
        val article = Article.create(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(article)

        val query = GetArticlePageQuery(
            boardId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // When
        val result = getArticleService.getPage(query)

        // Then
        result.articles shouldHaveSize 1
        result.articles[0] shouldBe article
    }

    "두 번째 페이지 조회 시 올바른 offset이 적용되어야 한다" {
        // Given
        val articles = (1..5).map { id ->
            Article.create(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = 100L,
                writerId = 200L
            )
        }
        articles.forEach { fakeRepository.save(it) }

        val query = GetArticlePageQuery(
            boardId = 100L,
            page = 2L,
            pageSize = 2L,
            movablePageCount = 10L
        )

        // When
        val result = getArticleService.getPage(query)

        // Then
        result.articles shouldHaveSize 2
        // ID 내림차순으로 정렬되므로: 5, 4, 3, 2, 1
        // 두 번째 페이지(offset=2, limit=2)는 3, 2를 반환
        result.articles[0].articleId shouldBe 3L
        result.articles[1].articleId shouldBe 2L
    }

    "존재하지 않는 boardId로 페이지 조회 시 빈 결과를 반환해야 한다" {
        // Given
        val article = Article.create(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(article)

        val query = GetArticlePageQuery(
            boardId = 999L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // When
        val result = getArticleService.getPage(query)

        // Then
        result.articles.shouldBeEmpty()
        result.totalCount shouldBe 0L
    }

    "페이지 크기보다 적은 데이터가 있을 때 실제 데이터 개수만 반환해야 한다" {
        // Given
        val article1 = Article.create(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L
        )
        val article2 = Article.create(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 201L
        )
        fakeRepository.save(article1)
        fakeRepository.save(article2)

        val query = GetArticlePageQuery(
            boardId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // When
        val result = getArticleService.getPage(query)

        // Then
        result.articles shouldHaveSize 2
        result.articles shouldContainExactlyInAnyOrder listOf(article2, article1) // ID 내림차순
    }
})