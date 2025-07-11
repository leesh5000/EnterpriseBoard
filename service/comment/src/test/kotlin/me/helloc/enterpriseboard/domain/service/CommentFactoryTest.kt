package me.helloc.enterpriseboard.domain.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.Comment

class CommentFactoryTest : StringSpec({

    "루트 댓글 생성에 성공한다" {
        // Given
        val factory = CommentFactory()

        // When
        val rootComment = factory.createRootComment(
            content = "루트 댓글",
            articleId = 100L,
            writerId = 1L
        )

        // Then
        rootComment.content shouldBe "루트 댓글"
        rootComment.articleId shouldBe 100L
        rootComment.writerId shouldBe 1L
        rootComment.isRoot() shouldBe true
        rootComment.deleted shouldBe false
    }

    "유효한 부모 댓글로 자식 댓글 생성에 성공한다" {
        // Given
        val factory = CommentFactory()
        val parentComment = Comment.create(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 100L,
            writerId = 1L
        )

        // When
        val childComment = factory.createChildComment(
            content = "자식 댓글",
            articleId = 100L,
            writerId = 2L,
            parent = parentComment
        )

        // Then
        childComment.content shouldBe "자식 댓글"
        childComment.parentCommentId shouldBe 1L
        childComment.isRoot() shouldBe false
    }

    "2depth 댓글을 부모로 자식 댓글 생성 시 NO_ROOT_COMMENT_REPLY 예외가 발생한다" {
        // Given
        val factory = CommentFactory()
        val childComment = Comment.create(
            commentId = 2L,
            content = "2depth 댓글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 1L
        )

        // When & Then
        val exception = shouldThrow<BusinessException> {
            factory.createChildComment(
                content = "3depth 댓글",
                articleId = 100L,
                writerId = 2L,
                parent = childComment
            )
        }

        exception.errorCode shouldBe ErrorCode.NO_ROOT_COMMENT_REPLY
    }

    "삭제된 루트 댓글을 부모로 자식 댓글 생성 시 DELETED_COMMENT_REPLY 예외가 발생한다" {
        // Given
        val factory = CommentFactory()
        val deletedRootComment = Comment.create(
            commentId = 1L,
            content = "삭제된 루트 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete()

        // When & Thenz
        val exception = shouldThrow<BusinessException> {
            factory.createChildComment(
                content = "자식 댓글",
                articleId = 100L,
                writerId = 2L,
                parent = deletedRootComment
            )
        }

        exception.errorCode shouldBe ErrorCode.DELETED_COMMENT_REPLY
    }


    "다양한 루트 댓글로 자식 댓글 생성 테스트" {
        // Given
        val factory = CommentFactory()

        // 루트 댓글들
        val rootComment1 = Comment.create(commentId = 1L, content = "루트1", articleId = 100L, writerId = 1L)
        val rootComment2 = Comment.create(commentId = 5L, content = "루트2", articleId = 100L, writerId = 1L)
        val rootComment3 = Comment.create(commentId = 10L, content = "루트3", articleId = 100L, writerId = 1L)

        // 자식 댓글들 (2depth)
        val childComment1 = Comment.create(commentId = 2L, content = "자식1", parentCommentId = 1L, articleId = 100L, writerId = 1L)
        val childComment2 = Comment.create(commentId = 6L, content = "자식2", parentCommentId = 5L, articleId = 100L, writerId = 1L)
        val childComment3 = Comment.create(commentId = 11L, content = "자식3", parentCommentId = 10L, articleId = 100L, writerId = 1L)

        // When & Then - 루트 댓글로 자식 댓글 생성 성공
        factory.createChildComment("자식1-1", 100L, 2L, rootComment1)
        factory.createChildComment("자식2-1", 100L, 2L, rootComment2)
        factory.createChildComment("자식3-1", 100L, 2L, rootComment3)

        // When & Then - 2depth 댓글로 3depth 생성 시 예외 발생
        shouldThrow<BusinessException> { factory.createChildComment("자식1-2", 100L, 2L, childComment1) }
        shouldThrow<BusinessException> { factory.createChildComment("자식2-2", 100L, 2L, childComment2) }
        shouldThrow<BusinessException> { factory.createChildComment("자식3-2", 100L, 2L, childComment3) }
    }

    "루트 댓글 여부에 따른 자식 댓글 생성 테스트" {
        // Given
        val factory = CommentFactory()
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

        // 루트 댓글로 자식 댓글 생성 성공
        val newChild = factory.createChildComment("새 자식", 1L, 2L, rootComment)
        newChild.parentCommentId shouldBe 100L

        // 2depth 댓글로 3depth 생성 시 예외 발생
        shouldThrow<BusinessException> {
            factory.createChildComment("새 자식", 1L, 2L, childComment)
        }
    }

    "삭제 상태와 루트 댓글 조건 복합 테스트" {
        // Given
        val factory = CommentFactory()

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
        // 유효한 루트 댓글로 자식 댓글 생성 성공
        factory.createChildComment("새 자식", 100L, 2L, validRootComment)

        // 삭제된 루트 댓글로 자식 댓글 생성 시 예외
        val deletedRootException = shouldThrow<BusinessException> {
            factory.createChildComment("새 자식", 100L, 2L, deletedRootComment)
        }
        deletedRootException.errorCode shouldBe ErrorCode.DELETED_COMMENT_REPLY

        // 2depth 댓글로 3depth 생성 시 예외 (루트 아님)
        val validChildException = shouldThrow<BusinessException> {
            factory.createChildComment("새 자식", 100L, 2L, validChildComment)
        }
        validChildException.errorCode shouldBe ErrorCode.NO_ROOT_COMMENT_REPLY

        // 삭제된 2depth 댓글로 3depth 생성 시 예외 (루트 아님)
        val deletedChildException = shouldThrow<BusinessException> {
            factory.createChildComment("새 자식", 100L, 2L, deletedChildComment)
        }
        deletedChildException.errorCode shouldBe ErrorCode.NO_ROOT_COMMENT_REPLY
    }
})
