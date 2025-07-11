package me.helloc.enterpriseboard.domain.service

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.helloc.enterpriseboard.application.facade.FakeCommentRepository
import me.helloc.enterpriseboard.domain.model.Comment

class CommentDeletionServiceTest : StringSpec({

    "자식 댓글이 있는 댓글 삭제 시 논리 삭제된다" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val parentComment = Comment.create(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 100L,
            writerId = 1L
        )
        val childComment = Comment.create(
            commentId = 2L,
            content = "자식 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )
        
        repository.save(parentComment)
        repository.save(childComment)

        // When
        service.deleteComment(parentComment)

        // Then
        val deletedParent = repository.getById(1L)
        deletedParent.deleted shouldBe true
        deletedParent.content shouldBe "부모 댓글" // 내용은 유지
        
        val existingChild = repository.getById(2L)
        existingChild.deleted shouldBe false // 자식은 삭제되지 않음
        repository.existsById(1L) shouldBe true // DB에서 물리적으로 삭제되지 않음
    }

    "자식 댓글이 없는 댓글 삭제 시 물리 삭제된다" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val comment = Comment.create(
            commentId = 1L,
            content = "단독 댓글",
            articleId = 100L,
            writerId = 1L
        )
        repository.save(comment)

        // When
        service.deleteComment(comment)

        // Then
        repository.existsById(1L) shouldBe false // 물리적으로 삭제됨
    }

    "자식 댓글이 없는 대댓글 삭제 시 재귀적으로 부모까지 삭제된다" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val parentComment = Comment.create(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete() // 이미 논리 삭제된 상태
        
        val childComment = Comment.create(
            commentId = 2L,
            content = "자식 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )
        
        repository.save(parentComment)
        repository.save(childComment)

        // When
        service.deleteComment(childComment)

        // Then
        repository.existsById(2L) shouldBe false // 자식 댓글 물리 삭제
        repository.existsById(1L) shouldBe false // 부모 댓글도 재귀적으로 삭제
    }

    "삭제되지 않은 부모 댓글이 있는 자식 댓글 삭제 시 부모는 유지된다" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val parentComment = Comment.create(
            commentId = 1L,
            content = "활성 부모 댓글",
            articleId = 100L,
            writerId = 1L
        ) // 활성 상태
        
        val childComment = Comment.create(
            commentId = 2L,
            content = "자식 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )
        
        repository.save(parentComment)
        repository.save(childComment)

        // When
        service.deleteComment(childComment)

        // Then
        repository.existsById(2L) shouldBe false // 자식 댓글만 삭제
        repository.existsById(1L) shouldBe true  // 활성 부모는 유지
        val parent = repository.getById(1L)
        parent.deleted shouldBe false
    }

    "다른 자식 댓글이 있는 부모 댓글이 논리 삭제된 경우 재귀 삭제되지 않는다" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val parentComment = Comment.create(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete() // 논리 삭제된 상태
        
        val childComment1 = Comment.create(
            commentId = 2L,
            content = "자식 댓글 1",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )
        
        val childComment2 = Comment.create(
            commentId = 3L,
            content = "자식 댓글 2",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 3L
        )
        
        repository.save(parentComment)
        repository.save(childComment1)
        repository.save(childComment2)

        // When
        service.deleteComment(childComment1)

        // Then
        repository.existsById(2L) shouldBe false // 자식1만 삭제
        repository.existsById(1L) shouldBe true  // 부모는 유지 (다른 자식이 있음)
        repository.existsById(3L) shouldBe true  // 자식2는 유지
    }

    "루트 댓글의 자식 댓글 삭제 시 부모 루트 댓글 재귀 삭제 검사를 하지 않는다" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val rootComment = Comment.create(
            commentId = 1L,
            content = "루트 댓글",
            articleId = 100L,
            writerId = 1L
        ) // 루트 댓글 (parentCommentId == commentId)
        
        val childComment = Comment.create(
            commentId = 2L,
            content = "자식 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )
        
        repository.save(rootComment)
        repository.save(childComment)

        // When
        service.deleteComment(childComment)

        // Then
        repository.existsById(2L) shouldBe false // 자식 댓글 삭제
        repository.existsById(1L) shouldBe true  // 루트 댓글은 유지
        val root = repository.getById(1L)
        root.deleted shouldBe false
    }

    "3단계 깊이의 댓글 구조에서 재귀 삭제 테스트" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val grandParent = Comment.create(
            commentId = 1L,
            content = "조부모",
            articleId = 100L,
            writerId = 1L
        ).delete() // 논리 삭제됨
        
        val parent = Comment.create(
            commentId = 2L,
            content = "부모",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        ).delete() // 논리 삭제됨
        
        val child = Comment.create(
            commentId = 3L,
            content = "자식",
            parentCommentId = 2L,
            articleId = 100L,
            writerId = 3L
        )
        
        repository.save(grandParent)
        repository.save(parent)
        repository.save(child)

        // When
        service.deleteComment(child)

        // Then
        repository.existsById(3L) shouldBe false // 자식 삭제
        repository.existsById(2L) shouldBe false // 부모도 재귀 삭제
        repository.existsById(1L) shouldBe false // 조부모도 재귀 삭제
    }

    "hasChildren 메서드가 정확히 자식 댓글 존재 여부를 확인한다" {
        // Given
        val repository = FakeCommentRepository()
        val service = CommentDeletionService(repository)

        val parentWithChild = Comment.create(
            commentId = 1L,
            content = "자식이 있는 부모",
            articleId = 100L,
            writerId = 1L
        )
        val child = Comment.create(
            commentId = 2L,
            content = "자식",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )
        val parentWithoutChild = Comment.create(
            commentId = 3L,
            content = "자식이 없는 부모",
            articleId = 100L,
            writerId = 3L
        )
        
        repository.save(parentWithChild)
        repository.save(child)
        repository.save(parentWithoutChild)

        // When & Then
        // hasChildren는 private이므로 deleteComment 동작으로 간접 확인
        service.deleteComment(parentWithChild) // 논리 삭제되어야 함
        service.deleteComment(parentWithoutChild) // 물리 삭제되어야 함
        
        repository.existsById(1L) shouldBe true // 논리 삭제 (자식이 있음)
        val logicallyDeleted = repository.getById(1L)
        logicallyDeleted.deleted shouldBe true
        
        repository.existsById(3L) shouldBe false // 물리 삭제 (자식이 없음)
    }
})