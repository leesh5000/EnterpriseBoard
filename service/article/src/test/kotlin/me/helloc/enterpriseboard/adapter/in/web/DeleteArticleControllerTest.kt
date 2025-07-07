package me.helloc.enterpriseboard.adapter.`in`.web

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DeleteArticleControllerTest : StringSpec({

    lateinit var fakeDeleteUseCase: FakeDeleteArticleUseCase
    lateinit var controller: DeleteArticleController

    beforeEach {
        fakeDeleteUseCase = FakeDeleteArticleUseCase()
        controller = DeleteArticleController(
            useCase = fakeDeleteUseCase
        )
    }

    "DELETE /api/v1/articles/{articleId} - Article 삭제 시 UseCase를 호출해야 한다" {
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

    "DELETE /api/v1/articles/{articleId} - 여러 삭제 호출 시 모든 ID가 기록되어야 한다" {
        // Given
        val articleIds = listOf(1L, 2L, 3L)
        fakeDeleteUseCase.shouldThrowException = false

        // When
        articleIds.forEach { controller.deleteArticle(it) }

        // Then
        fakeDeleteUseCase.deletedArticleIds shouldBe articleIds
        articleIds.forEach { 
            fakeDeleteUseCase.wasDeleted(it) shouldBe true 
        }
    }
})