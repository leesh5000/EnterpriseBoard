package me.helloc.enterpriseboard.application.facade

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.helloc.enterpriseboard.domain.model.Article
import me.helloc.enterpriseboard.application.facade.DeleteArticleFacade

class DeleteArticleFacadeTest : StringSpec({

    lateinit var fakeRepository: FakeArticleRepository
    lateinit var deleteArticleFacade: DeleteArticleFacade

    beforeEach {
        fakeRepository = FakeArticleRepository()
        deleteArticleFacade = DeleteArticleFacade(fakeRepository)
    }

    "존재하는 Article을 삭제할 수 있어야 한다" {
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
        deleteArticleFacade.delete(1L)

        // Then
        fakeRepository.existsById(1L) shouldBe false
        fakeRepository.findById(1L) shouldBe null
    }

    "존재하지 않는 Article을 삭제하려고 하면 예외가 발생해야 한다" {
        // Given
        // Repository가 비어있음

        // When & Then
        val exception = shouldThrow<NoSuchElementException> {
            deleteArticleFacade.delete(999L)
        }
        exception.message shouldBe "Article not found with id: 999"
    }

    "여러 Article 중 특정 Article만 삭제되어야 한다" {
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
            writerId = 200L
        )
        val article3 = Article.create(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 101L,
            writerId = 201L
        )
        fakeRepository.save(article1)
        fakeRepository.save(article2)
        fakeRepository.save(article3)

        // When
        deleteArticleFacade.delete(2L)

        // Then
        fakeRepository.existsById(1L) shouldBe true
        fakeRepository.existsById(2L) shouldBe false
        fakeRepository.existsById(3L) shouldBe true
        fakeRepository.getAll().size shouldBe 2
    }

    "삭제된 Article을 다시 삭제하려고 하면 예외가 발생해야 한다" {
        // Given
        val article = Article.create(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(article)
        deleteArticleFacade.delete(1L)

        // When & Then
        val exception = shouldThrow<NoSuchElementException> {
            deleteArticleFacade.delete(1L)
        }
        exception.message shouldBe "Article not found with id: 1"
    }

    "삭제 후 같은 ID로 새로운 Article을 저장할 수 있어야 한다" {
        // Given
        val originalArticle = Article.create(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(originalArticle)
        deleteArticleFacade.delete(1L)

        val newArticle = Article.create(
            articleId = 1L,
            title = "새로운 제목",
            content = "새로운 내용",
            boardId = 101L,
            writerId = 201L
        )

        // When
        fakeRepository.save(newArticle)

        // Then
        val savedArticle = fakeRepository.findById(1L)
        savedArticle shouldBe newArticle
        savedArticle?.title shouldBe "새로운 제목"
        savedArticle?.boardId shouldBe 101L
    }
})