package me.helloc.enterpriseboard.application.facade

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.application.port.`in`.GetArticlePageQuery
import me.helloc.enterpriseboard.application.port.`in`.GetArticleScrollQuery
import me.helloc.enterpriseboard.domain.model.Article
import me.helloc.enterpriseboard.domain.model.NullArticle
import me.helloc.enterpriseboard.domain.model.RealArticle
import java.time.LocalDateTime

class GetArticleFacadeTest : StringSpec({

    lateinit var fakeRepository: FakeArticleRepository
    lateinit var getArticleFacade: GetArticleFacade

    beforeEach {
        fakeRepository = FakeArticleRepository()
        getArticleFacade = GetArticleFacade(fakeRepository)
    }

    "ID로 Article을 조회할 수 있어야 한다" {
        // Given
        val article = RealArticle(
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
        val foundArticle = getArticleFacade.getById(1L)

        // Then
        foundArticle shouldNotBe null
        foundArticle shouldBe article
    }

    "존재하지 않는 ID로 조회하면 null을 반환해야 한다" {
        // Given
        // Repository가 비어있음

        // When
        val foundArticle = getArticleFacade.getById(999L)

        // Then
        foundArticle shouldBe NullArticle
    }

    "Board ID로 Article 목록을 조회할 수 있어야 한다" {
        // Given
        val article1 = RealArticle(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article2 = RealArticle(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 201L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article3 = RealArticle(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 101L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeRepository.save(article1)
        fakeRepository.save(article2)
        fakeRepository.save(article3)

        // When
        val articles = getArticleFacade.getByBoardId(100L)

        // Then
        articles shouldHaveSize 2
        articles shouldContainExactlyInAnyOrder listOf(article1, article2)
    }

    "Board ID로 조회 시 해당하는 Article이 없으면 빈 리스트를 반환해야 한다" {
        // Given
        val article = RealArticle(
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
        val articles = getArticleFacade.getByBoardId(999L)

        // Then
        articles.shouldBeEmpty()
    }

    "Writer ID로 Article 목록을 조회할 수 있어야 한다" {
        // Given
        val article1 = RealArticle(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article2 = RealArticle(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 101L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article3 = RealArticle(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 100L,
            writerId = 201L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeRepository.save(article1)
        fakeRepository.save(article2)
        fakeRepository.save(article3)

        // When
        val articles = getArticleFacade.getByWriterId(200L)

        // Then
        articles shouldHaveSize 2
        articles shouldContainExactlyInAnyOrder listOf(article1, article2)
    }

    "Writer ID로 조회 시 해당하는 Article이 없으면 빈 리스트를 반환해야 한다" {
        // Given
        val article = RealArticle(
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
        val articles = getArticleFacade.getByWriterId(999L)

        // Then
        articles.shouldBeEmpty()
    }

    "Repository가 비어있을 때 모든 조회는 null 또는 빈 리스트를 반환해야 한다" {
        // Given
        // Repository가 비어있음

        // When & Then
        getArticleFacade.getById(1L) shouldBe NullArticle
        getArticleFacade.getByBoardId(100L).shouldBeEmpty()
        getArticleFacade.getByWriterId(200L).shouldBeEmpty()
    }

    "페이지 조회 시 올바른 Article 목록과 totalCount를 반환해야 한다" {
        // Given
        val article1 = RealArticle(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article2 = RealArticle(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 201L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article3 = RealArticle(
            articleId = 3L,
            title = "세 번째 제목",
            content = "세 번째 내용",
            boardId = 100L,
            writerId = 202L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
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
        val result = getArticleFacade.getPage(query)

        // Then
        result.articles shouldHaveSize 2
        result.articles shouldContainExactlyInAnyOrder listOf(article3, article2) // ID 내림차순 정렬
        result.count shouldBe 3L // 전체 카운트는 limit에 의해 제한됨
    }

    "첫 번째 페이지 조회 시 offset이 0이어야 한다" {
        // Given
        val article = RealArticle(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeRepository.save(article)

        val query = GetArticlePageQuery(
            boardId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // When
        val result = getArticleFacade.getPage(query)

        // Then
        result.articles shouldHaveSize 1
        result.articles[0] shouldBe article
    }

    "두 번째 페이지 조회 시 올바른 offset이 적용되어야 한다" {
        // Given
        val articles = (1..5).map { id ->
            RealArticle(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = 100L,
                writerId = 200L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
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
        val result = getArticleFacade.getPage(query)

        // Then
        result.articles shouldHaveSize 2
        // ID 내림차순으로 정렬되므로: 5, 4, 3, 2, 1
        // 두 번째 페이지(offset=2, limit=2)는 3, 2를 반환
        result.articles[0].articleId shouldBe 3L
        result.articles[1].articleId shouldBe 2L
    }

    "존재하지 않는 boardId로 페이지 조회 시 빈 결과를 반환해야 한다" {
        // Given
        val article = RealArticle(
            articleId = 1L,
            title = "테스트 제목",
            content = "테스트 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        fakeRepository.save(article)

        val query = GetArticlePageQuery(
            boardId = 999L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )

        // When
        val result = getArticleFacade.getPage(query)

        // Then
        result.articles.shouldBeEmpty()
        result.count shouldBe 0L
    }

    "페이지 크기보다 적은 데이터가 있을 때 실제 데이터 개수만 반환해야 한다" {
        // Given
        val article1 = RealArticle(
            articleId = 1L,
            title = "첫 번째 제목",
            content = "첫 번째 내용",
            boardId = 100L,
            writerId = 200L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
        )
        val article2 = RealArticle(
            articleId = 2L,
            title = "두 번째 제목",
            content = "두 번째 내용",
            boardId = 100L,
            writerId = 201L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now()
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
        val result = getArticleFacade.getPage(query)

        // Then
        result.articles shouldHaveSize 2
        result.articles shouldContainExactlyInAnyOrder listOf(article2, article1) // ID 내림차순
    }

    "무한 스크롤: lastArticleId가 0일 때 처음부터 데이터를 조회해야 한다" {
        // Given
        val articles = (1..5).map { id ->
            RealArticle(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = 100L,
                writerId = 200L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )
        }
        articles.forEach { fakeRepository.save(it) }

        val query = GetArticleScrollQuery(
            boardId = 100L,
            pageSize = 3L,
            lastArticleId = 0L
        )

        // When
        val result = getArticleFacade.getScroll(query)

        // Then
        result shouldHaveSize 3
        // ID 내림차순으로 정렬되므로: 5, 4, 3
        result[0].articleId shouldBe 5L
        result[1].articleId shouldBe 4L
        result[2].articleId shouldBe 3L
    }

    "무한 스크롤: lastArticleId가 주어졌을 때 해당 ID보다 작은 데이터를 조회해야 한다" {
        // Given
        val articles = (1..10).map { id ->
            RealArticle(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = 100L,
                writerId = 200L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )
        }
        articles.forEach { fakeRepository.save(it) }

        val query = GetArticleScrollQuery(
            boardId = 100L,
            pageSize = 3L,
            lastArticleId = 7L
        )

        // When
        val result = getArticleFacade.getScroll(query)

        // Then
        result shouldHaveSize 3
        // lastArticleId=7보다 작은 ID들을 내림차순으로: 6, 5, 4
        result[0].articleId shouldBe 6L
        result[1].articleId shouldBe 5L
        result[2].articleId shouldBe 4L
    }

    "무한 스크롤: 다른 boardId의 데이터는 조회되지 않아야 한다" {
        // Given
        val articles = listOf(
            RealArticle(articleId = 1L, title = "제목 1", content = "내용 1", boardId = 100L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            RealArticle(articleId = 2L, title = "제목 2", content = "내용 2", boardId = 101L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            RealArticle(articleId = 3L, title = "제목 3", content = "내용 3", boardId = 100L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            RealArticle(articleId = 4L, title = "제목 4", content = "내용 4", boardId = 101L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            RealArticle(articleId = 5L, title = "제목 5", content = "내용 5", boardId = 100L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now())
        )
        articles.forEach { fakeRepository.save(it) }

        val query = GetArticleScrollQuery(
            boardId = 100L,
            pageSize = 10L,
            lastArticleId = 0L
        )

        // When
        val result = getArticleFacade.getScroll(query)

        // Then
        result shouldHaveSize 3
        result.all { it.boardId == 100L } shouldBe true
        result[0].articleId shouldBe 5L
        result[1].articleId shouldBe 3L
        result[2].articleId shouldBe 1L
    }

    "무한 스크롤: 요청한 pageSize보다 적은 데이터가 있을 때 실제 데이터 개수만 반환해야 한다" {
        // Given
        val articles = listOf(
            RealArticle(articleId = 1L, title = "제목 1", content = "내용 1", boardId = 100L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now()),
            RealArticle(articleId = 2L, title = "제목 2", content = "내용 2", boardId = 100L, writerId = 200L, createdAt = LocalDateTime.now(), modifiedAt = LocalDateTime.now())
        )
        articles.forEach { fakeRepository.save(it) }

        val query = GetArticleScrollQuery(
            boardId = 100L,
            pageSize = 5L,
            lastArticleId = 0L
        )

        // When
        val result = getArticleFacade.getScroll(query)

        // Then
        result shouldHaveSize 2
        result[0].articleId shouldBe 2L
        result[1].articleId shouldBe 1L
    }

    "무한 스크롤: lastArticleId 이후에 데이터가 없으면 빈 리스트를 반환해야 한다" {
        // Given
        val articles = (1..3).map { id ->
            RealArticle(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = 100L,
                writerId = 200L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )
        }
        articles.forEach { fakeRepository.save(it) }

        val query = GetArticleScrollQuery(
            boardId = 100L,
            pageSize = 5L,
            lastArticleId = 1L  // 가장 작은 ID
        )

        // When
        val result = getArticleFacade.getScroll(query)

        // Then
        result.shouldBeEmpty()
    }

    "무한 스크롤: 존재하지 않는 boardId로 조회하면 빈 리스트를 반환해야 한다" {
        // Given
        val articles = (1..3).map { id ->
            RealArticle(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = 100L,
                writerId = 200L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )
        }
        articles.forEach { fakeRepository.save(it) }

        val query = GetArticleScrollQuery(
            boardId = 999L,  // 존재하지 않는 boardId
            pageSize = 5L,
            lastArticleId = 0L
        )

        // When
        val result = getArticleFacade.getScroll(query)

        // Then
        result.shouldBeEmpty()
    }

    "무한 스크롤: 마지막 페이지 도달 시나리오 테스트" {
        // Given
        val articles = (1..10).map { id ->
            RealArticle(
                articleId = id.toLong(),
                title = "제목 $id",
                content = "내용 $id",
                boardId = 100L,
                writerId = 200L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )
        }
        articles.forEach { fakeRepository.save(it) }

        // When - 첫 번째 스크롤
        val firstScroll = getArticleFacade.getScroll(
            GetArticleScrollQuery(boardId = 100L, pageSize = 4L, lastArticleId = 0L)
        )

        // Then
        firstScroll shouldHaveSize 4
        firstScroll.map { it.articleId } shouldBe listOf(10L, 9L, 8L, 7L)

        // When - 두 번째 스크롤
        val secondScroll = getArticleFacade.getScroll(
            GetArticleScrollQuery(boardId = 100L, pageSize = 4L, lastArticleId = 7L)
        )

        // Then
        secondScroll shouldHaveSize 4
        secondScroll.map { it.articleId } shouldBe listOf(6L, 5L, 4L, 3L)

        // When - 세 번째 스크롤 (마지막)
        val thirdScroll = getArticleFacade.getScroll(
            GetArticleScrollQuery(boardId = 100L, pageSize = 4L, lastArticleId = 3L)
        )

        // Then
        thirdScroll shouldHaveSize 2  // 남은 데이터는 2개뿐
        thirdScroll.map { it.articleId } shouldBe listOf(2L, 1L)

        // When - 네 번째 스크롤 (더 이상 데이터 없음)
        val fourthScroll = getArticleFacade.getScroll(
            GetArticleScrollQuery(boardId = 100L, pageSize = 4L, lastArticleId = 1L)
        )

        // Then
        fourthScroll.shouldBeEmpty()
    }
})
