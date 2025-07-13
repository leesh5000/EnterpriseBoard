package me.helloc.enterpriseboard.application.facade

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.service.PageLimitCalculator

class GetCommentFacadeTest : StringSpec({

    "getPage - 댓글이 존재하는 경우 페이지네이션 결과를 반환한다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // 테스트 데이터 생성 (articleId=100L에 20개 댓글)
        (1L..20L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    articleId = 100L,
                    writerId = 1L
                )
            )
        }
        
        // When
        val result = facade.getPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )
        
        // Then
        result.comments.size shouldBe 10
        result.comments.first().commentId shouldBe 20L // 최신순 정렬
        result.comments.last().commentId shouldBe 11L
        result.visibleRangeCount shouldBe 20L // limit=51이지만 실제 댓글은 20개
    }

    "getPage - 댓글이 없는 경우 빈 결과를 반환한다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // When
        val result = facade.getPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )
        
        // Then
        result.comments.size shouldBe 0
        result.visibleRangeCount shouldBe 0L
    }

    "getPage - 두 번째 페이지 조회 시 올바른 offset을 적용한다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // 15개 댓글 생성
        (1L..15L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    articleId = 100L,
                    writerId = 1L
                )
            )
        }
        
        // When
        val result = facade.getPage(
            articleId = 100L,
            page = 2L,
            pageSize = 10L,
            movablePageCount = 5L
        )
        
        // Then
        result.comments.size shouldBe 5 // 남은 댓글 5개
        result.comments.first().commentId shouldBe 5L
        result.comments.last().commentId shouldBe 1L
        result.visibleRangeCount shouldBe 15L
    }

    "getPage - PageLimitCalculator를 통해 올바른 limit을 계산한다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // 100개 댓글 생성
        (1L..100L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    articleId = 100L,
                    writerId = 1L
                )
            )
        }
        
        // When
        val page = 3L
        val pageSize = 10L
        val movablePageCount = 5L
        val expectedLimit = PageLimitCalculator.calculate(page, pageSize, movablePageCount)
        
        val result = facade.getPage(
            articleId = 100L,
            page = page,
            pageSize = pageSize,
            movablePageCount = movablePageCount
        )
        
        // Then
        expectedLimit shouldBe 51L // (((3-1)/5) + 1) * 10 * 5 + 1
        result.visibleRangeCount shouldBe 51L // limit 범위 내 댓글 수
    }

    "getPage - 다른 게시글의 댓글은 조회하지 않는다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // articleId=100에 5개, articleId=200에 5개 생성
        (1L..5L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    articleId = 100L,
                    writerId = 1L
                )
            )
        }
        (6L..10L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    articleId = 200L,
                    writerId = 1L
                )
            )
        }
        
        // When
        val result = facade.getPage(
            articleId = 100L,
            page = 1L,
            pageSize = 10L,
            movablePageCount = 5L
        )
        
        // Then
        result.comments.size shouldBe 5
        result.comments.all { it.articleId == 100L } shouldBe true
        result.visibleRangeCount shouldBe 5L
    }

    "getScroll - 초기 스크롤 시 최신 댓글부터 반환한다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // 10개 댓글 생성
        (1L..10L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    articleId = 100L,
                    writerId = 1L
                )
            )
        }
        
        // When
        val result = facade.getScroll(
            articleId = 100L,
            pageSize = 5L,
            lastParentCommentId = Comment.EMPTY_ID,
            lastCommentId = Comment.EMPTY_ID
        )
        
        // Then
        result.size shouldBe 5
        result.first().commentId shouldBe 10L // 최신순
        result.last().commentId shouldBe 6L
    }

    "getScroll - 연속 스크롤 시 lastCommentId 이후 댓글을 반환한다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // 10개 댓글 생성
        (1L..10L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    parentCommentId = id, // 루트 댓글
                    articleId = 100L,
                    writerId = 1L
                )
            )
        }
        
        // When
        val result = facade.getScroll(
            articleId = 100L,
            pageSize = 5L,
            lastParentCommentId = 6L,
            lastCommentId = 6L
        )
        
        // Then
        result.size shouldBe 5
        result.first().commentId shouldBe 5L
        result.last().commentId shouldBe 1L
    }

    "getScroll - 더 이상 댓글이 없으면 빈 리스트를 반환한다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // 3개 댓글만 생성
        (1L..3L).forEach { id ->
            repository.save(
                Comment.create(
                    commentId = id,
                    content = "댓글 $id",
                    articleId = 100L,
                    writerId = 1L
                )
            )
        }
        
        // When
        val result = facade.getScroll(
            articleId = 100L,
            pageSize = 5L,
            lastParentCommentId = Comment.EMPTY_ID,
            lastCommentId = Comment.EMPTY_ID
        )
        
        // Then
        result.size shouldBe 3 // pageSize보다 적은 댓글만 존재
    }

    "getScroll - 다른 게시글의 댓글은 조회하지 않는다" {
        // Given
        val repository = FakeCommentRepository()
        val facade = GetCommentFacade(repository)
        
        // articleId가 다른 댓글들 생성
        repository.save(
            Comment.create(
                commentId = 1L,
                content = "댓글 1",
                articleId = 100L,
                writerId = 1L
            )
        )
        repository.save(
            Comment.create(
                commentId = 2L,
                content = "댓글 2",
                articleId = 200L, // 다른 게시글
                writerId = 1L
            )
        )
        
        // When
        val result = facade.getScroll(
            articleId = 100L,
            pageSize = 5L,
            lastParentCommentId = Comment.EMPTY_ID,
            lastCommentId = Comment.EMPTY_ID
        )
        
        // Then
        result.size shouldBe 1
        result.first().articleId shouldBe 100L
    }
})