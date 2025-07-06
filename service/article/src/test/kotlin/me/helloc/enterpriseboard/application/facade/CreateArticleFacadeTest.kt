package me.helloc.enterpriseboard.application.facade

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.application.port.`in`.CreateArticleCommand
import me.helloc.enterpriseboard.application.facade.CreateArticleFacade

class CreateArticleFacadeTest : StringSpec({

    lateinit var fakeRepository: FakeArticleRepository
    lateinit var createArticleFacade: CreateArticleFacade

    beforeEach {
        fakeRepository = FakeArticleRepository()
        createArticleFacade = CreateArticleFacade(fakeRepository)
    }

    "Article 생성 시 정상적으로 저장되어야 한다" {
        // Given
        val command = CreateArticleCommand(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )

        // When
        val createdArticle = createArticleFacade.create(command)

        // Then
        createdArticle.title shouldBe command.title
        createdArticle.content shouldBe command.content
        createdArticle.boardId shouldBe command.boardId
        createdArticle.writerId shouldBe command.writerId
    }

    "Article 생성 시 Snowflake ID가 할당되어야 한다" {
        // Given
        val command = CreateArticleCommand(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )

        // When
        val createdArticle = createArticleFacade.create(command)

        // Then
        createdArticle.articleId shouldNotBe 0L
        createdArticle.articleId shouldNotBe null
    }

    "생성된 Article이 Repository에 저장되어야 한다" {
        // Given
        val command = CreateArticleCommand(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )

        // When
        val createdArticle = createArticleFacade.create(command)

        // Then
        val savedArticle = fakeRepository.findById(createdArticle.articleId)
        savedArticle shouldNotBe null
        savedArticle shouldBe createdArticle
    }

    "여러 Article을 생성하면 각각 다른 ID를 가져야 한다" {
        // Given
        val command1 = CreateArticleCommand(
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L
        )
        val command2 = CreateArticleCommand(
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 200L
        )

        // When
        val article1 = createArticleFacade.create(command1)
        val article2 = createArticleFacade.create(command2)

        // Then
        article1.articleId shouldNotBe article2.articleId
    }

    "빈 제목과 내용으로도 Article을 생성할 수 있어야 한다" {
        // Given
        val command = CreateArticleCommand(
            title = "",
            content = "",
            boardId = 100L,
            writerId = 200L
        )

        // When
        val createdArticle = createArticleFacade.create(command)

        // Then
        createdArticle.title shouldBe ""
        createdArticle.content shouldBe ""
        val savedArticle = fakeRepository.findById(createdArticle.articleId)
        savedArticle shouldNotBe null
    }

    "생성 시간과 수정 시간이 동일하게 설정되어야 한다" {
        // Given
        val command = CreateArticleCommand(
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )

        // When
        val createdArticle = createArticleFacade.create(command)

        // Then
        createdArticle.createdAt shouldBe createdArticle.modifiedAt
    }
})