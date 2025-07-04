package me.helloc.enterpriseboard.application.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
})