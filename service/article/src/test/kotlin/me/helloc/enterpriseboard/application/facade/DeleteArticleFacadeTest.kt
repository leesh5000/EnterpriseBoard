package me.helloc.enterpriseboard.application.facade

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.model.Article


import java.time.LocalDateTime

class DeleteArticleFacadeTest : StringSpec({

    lateinit var fakeRepository: FakeArticleRepository
    lateinit var deleteArticleFacade: DeleteArticleFacade

    beforeEach {
        fakeRepository = FakeArticleRepository()
        deleteArticleFacade = DeleteArticleFacade(fakeRepository)
    }

    "존재하는 Article을 삭제할 수 있어야 한다" {
        // Given
        val article = Article(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeRepository.save(article)

        // When
        deleteArticleFacade.delete(1L)

        // Then
        fakeRepository.existsById(1L) shouldBe false
        shouldThrow<BusinessException> {
            fakeRepository.getById(1L)
        }
    }

    "여러 Article 중 특정 Article만 삭제되어야 한다" {
        // Given
        val article1 = Article(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article2 = Article(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article3 = Article(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 101L,
            writerId = 201L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
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

    "삭제 후 같은 ID로 새로운 Article을 저장할 수 있어야 한다" {
        // Given
        val originalArticle = Article(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeRepository.save(originalArticle)
        deleteArticleFacade.delete(1L)

        val newArticle = Article(
            articleId = 1L,
            title = "새로운 제목",
            content = "새로운 내용",
            boardId = 101L,
            writerId = 201L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )

        // When
        fakeRepository.save(newArticle)

        // Then
        val savedArticle = fakeRepository.getById(1L)
        savedArticle shouldBe newArticle
        savedArticle?.title shouldBe "새로운 제목"
        savedArticle?.boardId shouldBe 101L
    }
})
