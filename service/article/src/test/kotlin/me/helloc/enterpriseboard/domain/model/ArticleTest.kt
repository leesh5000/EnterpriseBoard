package me.helloc.enterpriseboard.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class ArticleTest : StringSpec({

    "Article 생성 시 필수 정보가 올바르게 설정되어야 한다" {
        // Given
        val articleId = 1L
        val title = "테스트 제목"
        val content = "테스트 내용"
        val boardId = 100L
        val writerId = 200L

        // When
        val article = RealArticle(
            articleId = articleId,
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )

        // Then
        article.articleId shouldBe articleId
        article.title shouldBe title
        article.content shouldBe content
        article.boardId shouldBe boardId
        article.writerId shouldBe writerId
    }

    "Article 생성 시 생성 시간과 수정 시간이 같아야 한다" {
        // Given
        val articleId = 1L
        val title = "테스트 제목"
        val content = "테스트 내용"
        val boardId = 100L
        val writerId = 200L
        val now = LocalDateTime.now()

        // When
        val article = RealArticle(
            articleId = articleId,
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId,
            createdAt = now,
            modifiedAt = now
        )

        // Then
        article.createdAt shouldBe article.modifiedAt
    }

    "Article 업데이트 시 제목과 내용이 변경되어야 한다" {
        // Given
        val originalArticle = RealArticle(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val newTitle = "수정된 제목"
        val newContent = "수정된 내용"

        // When
        val updatedArticle = originalArticle.update(newTitle, newContent)

        // Then
        updatedArticle.title shouldBe newTitle
        updatedArticle.content shouldBe newContent
    }

    "Article 업데이트 시 수정 시간이 변경되어야 한다" {
        // Given
        val originalArticle = RealArticle(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val newTitle = "수정된 제목"
        val newContent = "수정된 내용"

        // When
        Thread.sleep(1) // 시간 차이를 만들기 위해 잠시 대기
        val updatedArticle = originalArticle.update(newTitle, newContent)

        // Then
        updatedArticle.modifiedAt shouldNotBe originalArticle.modifiedAt
    }

    "Article 업데이트 시 다른 필드들은 변경되지 않아야 한다" {
        // Given
        val originalArticle = RealArticle(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val newTitle = "수정된 제목"
        val newContent = "수정된 내용"

        // When
        val updatedArticle = originalArticle.update(newTitle, newContent)

        // Then
        updatedArticle.articleId shouldBe originalArticle.articleId
        updatedArticle.boardId shouldBe originalArticle.boardId
        updatedArticle.writerId shouldBe originalArticle.writerId
        updatedArticle.createdAt shouldBe originalArticle.createdAt
    }

    "Article 업데이트는 불변성을 유지해야 한다" {
        // Given
        val originalArticle = RealArticle(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val newTitle = "수정된 제목"
        val newContent = "수정된 내용"

        // When
        val updatedArticle = originalArticle.update(newTitle, newContent)

        // Then
        updatedArticle shouldNotBe originalArticle
        originalArticle.title shouldBe "원본 제목"
        originalArticle.content shouldBe "원본 내용"
    }

    "빈 문자열로 Article을 생성할 수 있어야 한다" {
        // Given
        val articleId = 1L
        val title = ""
        val content = ""
        val boardId = 100L
        val writerId = 200L

        // When
        val article = RealArticle(
            articleId = articleId,
            title = title,
            content = content,
            boardId = boardId,
            writerId = writerId,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )

        // Then
        article.title shouldBe ""
        article.content shouldBe ""
    }

    "빈 문자열로 Article을 업데이트할 수 있어야 한다" {
        // Given
        val originalArticle = RealArticle(
            articleId = 1L,
            title = "원본 제목",
            content = "원본 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )

        // When
        val updatedArticle = originalArticle.update("", "")

        // Then
        updatedArticle.title shouldBe ""
        updatedArticle.content shouldBe ""
    }

    "같은 값으로 생성된 Article 인스턴스는 동등해야 한다" {
        // Given
        val now = LocalDateTime.now()
        val article1 = RealArticle(
            articleId = 1L,
            title = "제목",
            content = "내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = now,
            modifiedAt = now
        )
        val article2 = RealArticle(
            articleId = 1L,
            title = "제목",
            content = "내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = now,
            modifiedAt = now
        )

        // When & Then
        article1 shouldBe article2
    }

    "다른 값으로 생성된 Article 인스턴스는 동등하지 않아야 한다" {
        // Given
        val now = LocalDateTime.now()
        val article1 = RealArticle(
            articleId = 1L,
            title = "제목1",
            content = "내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = now,
            modifiedAt = now
        )
        val article2 = RealArticle(
            articleId = 1L,
            title = "제목2",
            content = "내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = now,
            modifiedAt = now
        )

        // When & Then
        article1 shouldNotBe article2
    }

})