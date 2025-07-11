package me.helloc.enterpriseboard.application.facade

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.domain.model.Article

class UpdateArticleFacadeTest : StringSpec({

    lateinit var fakeRepository: FakeArticleRepository
    lateinit var updateArticleFacade: UpdateArticleFacade

    beforeEach {
        fakeRepository = FakeArticleRepository()
        updateArticleFacade = UpdateArticleFacade(fakeRepository)
    }

    "기존 Article의 제목과 내용을 업데이트할 수 있어야 한다" {
        // Given
        val existingArticle = Article.create(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(existingArticle)

        val articleId = 1L
        val title = "수정된 제목"
        val content = "수정된 내용"

        // When
        val updatedArticle = updateArticleFacade.update(articleId, title, content)

        // Then
        updatedArticle.title shouldBe "수정된 제목"
        updatedArticle.content shouldBe "수정된 내용"
        updatedArticle.articleId shouldBe existingArticle.articleId
        updatedArticle.boardId shouldBe existingArticle.boardId
        updatedArticle.writerId shouldBe existingArticle.writerId
    }

    "Article 업데이트 시 수정 시간이 변경되어야 한다" {
        // Given
        val existingArticle = Article.create(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(existingArticle)

        val articleId = 1L
        val title = "수정된 제목"
        val content = "수정된 내용"

        // When
        Thread.sleep(1) // 시간 차이를 만들기 위해 잠시 대기
        val updatedArticle = updateArticleFacade.update(articleId, title, content)

        // Then
        updatedArticle.modifiedAt shouldNotBe existingArticle.modifiedAt
        updatedArticle.createdAt shouldBe existingArticle.createdAt
    }

    "업데이트된 Article이 Repository에 저장되어야 한다" {
        // Given
        val existingArticle = Article.create(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(existingArticle)

        val articleId = 1L
        val title = "수정된 제목"
        val content = "수정된 내용"

        // When
        val updatedArticle = updateArticleFacade.update(articleId, title, content)

        // Then
        val savedArticle = fakeRepository.findById(1L)
        savedArticle shouldNotBe null
        savedArticle?.title shouldBe "수정된 제목"
        savedArticle?.content shouldBe "수정된 내용"
    }

    "존재하지 않는 Article을 업데이트하려고 하면 NullArticle이 반환되어야 한다" {
        // Given
        val articleId = 999L
        val title = "수정된 제목"
        val content = "수정된 내용"

        // When
        val result = updateArticleFacade.update(articleId, title, content)

        // Then
        result== null shouldBe true
        result.articleId shouldBe -1L
    }

    "빈 문자열로 Article을 업데이트할 수 있어야 한다" {
        // Given
        val existingArticle = Article.create(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L
        )
        fakeRepository.save(existingArticle)

        val articleId = 1L
        val title = ""
        val content = ""

        // When
        val updatedArticle = updateArticleFacade.update(articleId, title, content)

        // Then
        updatedArticle.title shouldBe ""
        updatedArticle.content shouldBe ""
    }

    "여러 Article 중 특정 Article만 업데이트되어야 한다" {
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
        fakeRepository.save(article1)
        fakeRepository.save(article2)

        val articleId = 1L
        val title = "수정된 첫 번째 제목"
        val content = "수정된 첫 번째 내용"

        // When
        updateArticleFacade.update(articleId, title, content)

        // Then
        val updatedArticle1 = fakeRepository.findById(1L)
        val unchangedArticle2 = fakeRepository.findById(2L)

        updatedArticle1.title shouldBe "수정된 첫 번째 제목"
        updatedArticle1.content shouldBe "수정된 첫 번째 내용"
    }
})
