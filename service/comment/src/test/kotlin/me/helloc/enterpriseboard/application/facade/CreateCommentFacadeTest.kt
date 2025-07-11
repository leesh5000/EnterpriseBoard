package me.helloc.enterpriseboard.application.facade

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.service.CommentFactory

class CreateCommentFacadeTest : StringSpec({

    "parentCommentId가 NO_PARENT_ID일 때 루트 댓글 생성에 성공한다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        // When
        val result = facade.create(
            content = "루트 댓글",
            parentCommentId = Comment.NO_PARENT_ID,
            articleId = 100L,
            writerId = 1L
        )

        // Then
        result.content shouldBe "루트 댓글"
        result.parentCommentId shouldBe result.commentId // 루트 댓글은 parentCommentId == commentId
        result.articleId shouldBe 100L
        result.writerId shouldBe 1L
        result.isRoot() shouldBe true
        result.deleted shouldBe false
    }

    "루트 댓글에 대댓글 생성에 성공한다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        val parentComment = Comment.create(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 100L,
            writerId = 1L
        )
        repository.save(parentComment)

        // When
        val result = facade.create(
            content = "새로운 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )

        // Then
        result.content shouldBe "새로운 댓글"
        result.parentCommentId shouldBe 1L
        result.articleId shouldBe 100L
        result.writerId shouldBe 2L
        result.commentId shouldNotBe 0L
        result.deleted shouldBe false
    }

    "대댓글 생성에 성공한다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        val rootComment = Comment.create(
            commentId = 1L,
            content = "루트 댓글",
            articleId = 100L,
            writerId = 1L
        )
        repository.save(rootComment)

        // When
        val result = facade.create(
            content = "대댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )

        // Then
        result.content shouldBe "대댓글"
        result.parentCommentId shouldBe 1L
        result.articleId shouldBe 100L
        result.writerId shouldBe 2L
        result.isRoot() shouldBe false
    }

    "삭제된 댓글에 대댓글 생성 시 예외가 발생한다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        val deletedComment = Comment.create(
            commentId = 1L,
            content = "삭제된 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete()
        repository.save(deletedComment)

        // When & Then
        val exception = shouldThrow<BusinessException> {
            facade.create(
                content = "대댓글 시도",
                parentCommentId = 1L,
                articleId = 100L,
                writerId = 2L
            )
        }

        exception.errorCode shouldBe ErrorCode.DELETED_COMMENT_REPLY
    }

    "3depth 댓글 생성 시 예외가 발생한다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        val childComment = Comment.create(
            commentId = 2L,
            content = "2depth 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 1L
        )
        repository.save(childComment)

        // When & Then
        val exception = shouldThrow<BusinessException> {
            facade.create(
                content = "3depth 댓글 시도",
                parentCommentId = 2L,
                articleId = 100L,
                writerId = 2L
            )
        }

        exception.errorCode shouldBe ErrorCode.NO_ROOT_COMMENT_REPLY
    }

    "존재하지 않는 부모 댓글에 대댓글 생성 시 ROOT_COMMENT_NOT_FOUND 예외가 발생한다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        // When & Then
        val exception = shouldThrow<BusinessException> {
            facade.create(
                content = "대댓글 시도",
                parentCommentId = 999L,
                articleId = 100L,
                writerId = 2L
            )
        }

        exception.errorCode shouldBe ErrorCode.COMMENT_NOT_FOUND
    }

    "생성된 댓글이 Repository에 저장된다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        val parentComment = Comment.create(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 100L,
            writerId = 1L
        )
        repository.save(parentComment)

        // When
        val result = facade.create(
            content = "새로운 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )

        // Then
        val savedComment = repository.getById(result.commentId)
        savedComment shouldNotBe null
        savedComment?.content shouldBe "새로운 댓글"
        savedComment?.parentCommentId shouldBe 1L
        savedComment?.articleId shouldBe 100L
        savedComment?.writerId shouldBe 2L
    }

    "Snowflake ID가 고유하게 생성된다" {
        // Given
        val repository = FakeCommentRepository()
        val validator = CommentFactory()
        val facade = CreateCommentFacade(repository, validator)

        val parentComment = Comment.create(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 100L,
            writerId = 1L
        )
        repository.save(parentComment)

        // When
        val result1 = facade.create(
            content = "댓글1",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )

        val result2 = facade.create(
            content = "댓글2",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 2L
        )

        // Then
        result1.commentId shouldNotBe 0L
        result2.commentId shouldNotBe 0L
        result1.commentId shouldNotBe result2.commentId
    }
})
