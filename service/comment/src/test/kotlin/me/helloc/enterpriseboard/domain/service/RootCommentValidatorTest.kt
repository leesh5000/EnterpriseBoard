package me.helloc.enterpriseboard.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment

class RootCommentValidatorTest : StringSpec({

    "루트 댓글 검증에 성공한다" {
        // Given
        val validator = RootCommentValidator()
        val rootComment = Comment.create(
            commentId = 1L,
            content = "루트 댓글",
            articleId = 100L,
            writerId = 1L
        )

        // When & Then (예외가 발생하지 않아야 함)
        validator.validate(rootComment)
    }

    "삭제되지 않은 루트 댓글 검증에 성공한다" {
        // Given
        val validator = RootCommentValidator()
        val rootComment = Comment.create(
            commentId = 1L,
            content = "삭제되지 않은 루트 댓글",
            articleId = 100L,
            writerId = 1L
        )

        // When & Then (예외가 발생하지 않아야 함)
        validator.validate(rootComment)
        rootComment.deleted shouldBe false
    }

    "2depth 댓글 검증 시 NOT_ROOT_COMMENT 예외가 발생한다" {
        // Given
        val validator = RootCommentValidator()
        val childComment = Comment.create(
            commentId = 2L,
            content = "2depth 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 1L
        )

        // When & Then
        val exception = shouldThrow<BusinessException> {
            validator.validate(childComment)
        }

        exception.errorCode shouldBe ErrorCode.NOT_ROOT_COMMENT
        exception.message shouldBe "ID 2는 최상위 댓글이 아닙니다."
    }

    "삭제된 루트 댓글 검증 시 COMMENT_DELETED 예외가 발생한다" {
        // Given
        val validator = RootCommentValidator()
        val deletedRootComment = Comment.create(
            commentId = 1L,
            content = "삭제된 루트 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete()

        // When & Then
        val exception = shouldThrow<BusinessException> {
            validator.validate(deletedRootComment)
        }

        exception.errorCode shouldBe ErrorCode.COMMENT_DELETED
        exception.message shouldBe "ID 1에 해당하는 댓글은 이미 삭제되었습니다."
    }

    "null 댓글 검증 시 ROOT_COMMENT_NOT_FOUND 예외가 발생한다" {
        // Given
        val validator = RootCommentValidator()
        val nullComment: Comment? = null
        
        // When & Then
        val exception = shouldThrow<BusinessException> {
            validator.validate(nullComment)
        }
        
        exception.errorCode shouldBe ErrorCode.ROOT_COMMENT_NOT_FOUND
        exception.message shouldBe "ID {parentCommentId}에 해당하는 최상위 댓글을 찾을 수 없습니다."
    }

    "다양한 commentId와 parentCommentId 조합 테스트" {
        // Given
        val validator = RootCommentValidator()

        // 루트 댓글들 (commentId == parentCommentId)
        val rootComment1 = Comment.create(commentId = 1L, content = "루트1", articleId = 100L, writerId = 1L)
        val rootComment2 = Comment.create(commentId = 5L, content = "루트2", articleId = 100L, writerId = 1L)
        val rootComment3 = Comment.create(commentId = 10L, content = "루트3", articleId = 100L, writerId = 1L)

        // 자식 댓글들 (commentId != parentCommentId)
        val childComment1 = Comment.create(commentId = 2L, content = "자식1", parentCommentId = 1L, articleId = 100L, writerId = 1L)
        val childComment2 = Comment.create(commentId = 6L, content = "자식2", parentCommentId = 5L, articleId = 100L, writerId = 1L)
        val childComment3 = Comment.create(commentId = 11L, content = "자식3", parentCommentId = 10L, articleId = 100L, writerId = 1L)

        // When & Then - 루트 댓글들은 검증 통과
        validator.validate(rootComment1)
        validator.validate(rootComment2)
        validator.validate(rootComment3)

        // When & Then - 자식 댓글들은 예외 발생
        shouldThrow<BusinessException> { validator.validate(childComment1) }
        shouldThrow<BusinessException> { validator.validate(childComment2) }
        shouldThrow<BusinessException> { validator.validate(childComment3) }
    }

    "루트 댓글 여부 확인 로직 테스트" {
        // Given
        val validator = RootCommentValidator()
        val rootComment = Comment.create(
            commentId = 100L,
            content = "루트 댓글",
            articleId = 1L,
            writerId = 1L
        )
        val childComment = Comment.create(
            commentId = 200L,
            content = "자식 댓글",
            parentCommentId = 100L,
            articleId = 1L,
            writerId = 1L
        )

        // When & Then
        rootComment.isRoot() shouldBe true
        childComment.isRoot() shouldBe false

        // 루트 댓글은 검증 통과
        validator.validate(rootComment)

        // 자식 댓글은 예외 발생
        shouldThrow<BusinessException> { validator.validate(childComment) }
    }

    "삭제 상태와 루트 댓글 조건 복합 테스트" {
        // Given
        val validator = RootCommentValidator()

        // 삭제되지 않은 루트 댓글 - 통과
        val validRootComment = Comment.create(
            commentId = 1L,
            content = "유효한 루트 댓글",
            articleId = 100L,
            writerId = 1L
        )

        // 삭제된 루트 댓글 - 실패 (삭제됨)
        val deletedRootComment = Comment.create(
            commentId = 2L,
            content = "삭제된 루트 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete()

        // 삭제되지 않은 자식 댓글 - 실패 (루트 아님)
        val validChildComment = Comment.create(
            commentId = 3L,
            content = "유효한 자식 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 1L
        )

        // 삭제된 자식 댓글 - 실패 (루트 아님, 우선 조건)
        val deletedChildComment = Comment.create(
            commentId = 4L,
            content = "삭제된 자식 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 1L
        ).delete()

        // When & Then
        validator.validate(validRootComment) // 성공

        val deletedRootException = shouldThrow<BusinessException> {
            validator.validate(deletedRootComment)
        }
        deletedRootException.errorCode shouldBe ErrorCode.COMMENT_DELETED

        val validChildException = shouldThrow<BusinessException> {
            validator.validate(validChildComment)
        }
        validChildException.errorCode shouldBe ErrorCode.NOT_ROOT_COMMENT

        val deletedChildException = shouldThrow<BusinessException> {
            validator.validate(deletedChildComment)
        }
        deletedChildException.errorCode shouldBe ErrorCode.NOT_ROOT_COMMENT
    }
})
