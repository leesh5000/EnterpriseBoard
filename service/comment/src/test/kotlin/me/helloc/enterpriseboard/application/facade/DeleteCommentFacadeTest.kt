package me.helloc.enterpriseboard.application.facade

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import me.helloc.enterpriseboard.application.port.out.CommentRepository
import me.helloc.enterpriseboard.domain.model.Comment
import me.helloc.enterpriseboard.domain.service.CommentDeletionService

class FakeCommentDeletionService : CommentDeletionService(FakeCommentRepository()) {
    val deletedComments = mutableListOf<Comment>()

    override fun deleteComment(comment: Comment) {
        deletedComments.add(comment)
    }
}

class DeleteCommentFacadeTest : StringSpec({

    "존재하는 활성 댓글 삭제에 성공한다" {
        // Given
        val repository = FakeCommentRepository()
        val deletionService = FakeCommentDeletionService()
        val facade = DeleteCommentFacade(repository, deletionService)

        val comment = Comment.create(
            commentId = 1L,
            content = "테스트 댓글",
            articleId = 100L,
            writerId = 1L
        )
        repository.save(comment)

        // When
        facade.delete(1L)

        // Then
        deletionService.deletedComments.size shouldBe 1
        deletionService.deletedComments[0] shouldBe comment
    }

    "이미 삭제된 댓글 삭제 시도 시 CommentDeletionService를 호출하지 않는다" {
        // Given
        val repository = FakeCommentRepository()
        val deletionService = FakeCommentDeletionService()
        val facade = DeleteCommentFacade(repository, deletionService)

        val deletedComment = Comment.create(
            commentId = 1L,
            content = "삭제된 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete()
        repository.save(deletedComment)

        // When
        facade.delete(1L)

        // Then
        deletionService.deletedComments.size shouldBe 0
    }

    "존재하지 않는 댓글 삭제 시도 시 CommentDeletionService를 호출하지 않는다" {
        // Given
        val repository = FakeCommentRepository()
        val deletionService = FakeCommentDeletionService()
        val facade = DeleteCommentFacade(repository, deletionService)

        // When
        facade.delete(999L) // 존재하지 않는 댓글 ID

        // Then
        deletionService.deletedComments.size shouldBe 0
    }

    "여러 댓글 중 활성 댓글만 삭제 서비스를 호출한다" {
        // Given
        val repository = FakeCommentRepository()
        val deletionService = FakeCommentDeletionService()
        val facade = DeleteCommentFacade(repository, deletionService)

        val activeComment = Comment.create(
            commentId = 1L,
            content = "활성 댓글",
            articleId = 100L,
            writerId = 1L
        )
        val deletedComment = Comment.create(
            commentId = 2L,
            content = "삭제된 댓글",
            articleId = 100L,
            writerId = 1L
        ).delete()
        
        repository.save(activeComment)
        repository.save(deletedComment)

        // When
        facade.delete(1L) // 활성 댓글 삭제
        facade.delete(2L) // 삭제된 댓글 삭제 시도

        // Then
        deletionService.deletedComments.size shouldBe 1
        deletionService.deletedComments[0] shouldBe activeComment
    }

    "댓글 삭제 시 Optional.empty()인 경우 CommentDeletionService를 호출하지 않는다" {
        // Given
        val repository = FakeCommentRepository()
        val deletionService = FakeCommentDeletionService()
        val facade = DeleteCommentFacade(repository, deletionService)

        // When
        facade.delete(1L) // 존재하지 않는 댓글

        // Then
        deletionService.deletedComments.size shouldBe 0
    }
})