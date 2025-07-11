package me.helloc.enterpriseboard.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CommentTest : StringSpec({

    "Comment 생성 시 기본값들이 올바르게 설정되어야 한다" {
        // Given
        val commentId = 1L
        val content = "테스트 댓글 내용"
        val articleId = 100L
        val writerId = 200L

        // When
        val comment = Comment.create(
            commentId = commentId,
            content = content,
            articleId = articleId,
            writerId = writerId
        )

        // Then
        comment.commentId shouldBe commentId
        comment.content shouldBe content
        comment.parentCommentId shouldBe commentId // 부모 댓글이 없으면 자기 자신
        comment.articleId shouldBe articleId
        comment.writerId shouldBe writerId
        comment.deleted shouldBe false
        comment.createdAt shouldNotBe null
    }


    "Comment 삭제 시 deleted 플래그가 true로 변경되어야 한다" {
        // Given
        val comment = Comment.create(
            commentId = 1L,
            content = "테스트 내용",
            articleId = 100L,
            writerId = 200L
        )

        // When
        val deletedComment = comment.delete()

        // Then
        deletedComment.deleted shouldBe true
        deletedComment.commentId shouldBe comment.commentId
        deletedComment.content shouldBe comment.content
        deletedComment.articleId shouldBe comment.articleId
        deletedComment.writerId shouldBe comment.writerId
        deletedComment.createdAt shouldBe comment.createdAt
    }

    "루트 댓글인지 확인할 수 있어야 한다" {
        // Given
        val rootComment = Comment.create(
            commentId = 1L,
            content = "루트 댓글",
            articleId = 100L,
            writerId = 200L
        )

        val replyComment = Comment.create(
            commentId = 2L,
            content = "답글",
            parentCommentId = 1L,
            articleId = 100L,
            writerId = 200L
        )

        // When & Then
        rootComment.isRoot() shouldBe true
        replyComment.isRoot() shouldBe false
    }

    "NO_PARENT_ID로 생성된 댓글은 루트 댓글이어야 한다" {
        // Given & When
        val comment = Comment.create(
            commentId = 1L,
            content = "루트 댓글",
            parentCommentId = Comment.NO_PARENT_ID,
            articleId = 100L,
            writerId = 200L
        )

        // Then
        comment.isRoot() shouldBe true
        comment.parentCommentId shouldBe comment.commentId
    }
})
