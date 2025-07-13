package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.domain.model.CommentPath
import me.helloc.enterpriseboard.domain.model.CommentV2
import me.helloc.enterpriseboard.domain.service.CommentDeletionServiceV2
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeleteCommentFacadeV2Test {

    private lateinit var fakeRepository: FakeCommentRepositoryV2
    private lateinit var commentDeletionService: CommentDeletionServiceV2
    private lateinit var deleteCommentFacade: DeleteCommentFacadeV2

    @BeforeEach
    fun setUp() {
        fakeRepository = FakeCommentRepositoryV2()
        commentDeletionService = CommentDeletionServiceV2(fakeRepository)
        deleteCommentFacade = DeleteCommentFacadeV2(fakeRepository, commentDeletionService)
    }

    @Nested
    inner class `정상 삭제 테스트` {

        @Test
        fun `delete - 존재하는 댓글 정상 삭제`() {
            // given
            val comment = createAndSaveComment()
            val commentId = comment.commentId
            
            // when
            deleteCommentFacade.delete(commentId)
            
            // then
            assertTrue(fakeRepository.findById(commentId).isEmpty)  // 물리적 삭제됨
        }

        @Test
        fun `delete - 자식이 있는 댓글은 논리적 삭제`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)
            
            // when
            deleteCommentFacade.delete(rootComment.commentId)
            
            // then
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)  // 논리적 삭제됨
            
            val savedChildComment = fakeRepository.findById(childComment.commentId).get()
            assertFalse(savedChildComment.deleted)  // 자식은 영향 없음
        }

        @Test
        fun `delete - 자식이 없는 댓글은 물리적 삭제`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)
            
            // when
            deleteCommentFacade.delete(childComment.commentId)
            
            // then
            assertTrue(fakeRepository.findById(childComment.commentId).isEmpty)  // 물리적 삭제됨
            assertTrue(fakeRepository.findById(rootComment.commentId).isPresent)  // 부모는 남아있음
        }

        @Test
        fun `delete - 여러 댓글 순차 삭제`() {
            // given
            val comment1 = createAndSaveComment(commentId = 1L, content = "첫 번째 댓글")
            val comment2 = createAndSaveComment(commentId = 2L, content = "두 번째 댓글")
            val comment3 = createAndSaveComment(commentId = 3L, content = "세 번째 댓글")
            
            // when
            deleteCommentFacade.delete(comment1.commentId)
            deleteCommentFacade.delete(comment3.commentId)
            
            // then
            assertTrue(fakeRepository.findById(comment1.commentId).isEmpty)   // 삭제됨
            assertTrue(fakeRepository.findById(comment2.commentId).isPresent) // 남아있음
            assertTrue(fakeRepository.findById(comment3.commentId).isEmpty)   // 삭제됨
        }
    }

    @Nested
    inner class `이미 삭제된 댓글 테스트` {

        @Test
        fun `delete - 이미 삭제된 댓글은 처리하지 않음`() {
            // given
            val comment = createAndSaveComment().delete()
            fakeRepository.save(comment)
            val initialDeletedState = comment.deleted
            
            // when
            deleteCommentFacade.delete(comment.commentId)
            
            // then
            val savedComment = fakeRepository.findById(comment.commentId).get()
            assertEquals(initialDeletedState, savedComment.deleted)  // 상태 변화 없음
            assertTrue(savedComment.deleted)  // 여전히 삭제 상태
        }

        @Test
        fun `delete - 이미 삭제된 부모와 활성 자식이 있는 경우`() {
            // given
            val rootComment = createAndSaveRootComment().delete()
            fakeRepository.save(rootComment)
            val childComment = createAndSaveChildComment(rootComment)
            
            // when
            deleteCommentFacade.delete(rootComment.commentId)
            
            // then
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)  // 여전히 삭제 상태
            
            val savedChildComment = fakeRepository.findById(childComment.commentId).get()
            assertFalse(savedChildComment.deleted)  // 자식은 영향 없음
        }

        @Test
        fun `delete - 삭제된 댓글도 repository에서 조회됨`() {
            // given
            val comment = createAndSaveComment().delete()
            fakeRepository.save(comment)
            
            // when
            deleteCommentFacade.delete(comment.commentId)
            
            // then
            assertTrue(fakeRepository.findById(comment.commentId).isPresent)  // 여전히 조회 가능
            assertTrue(fakeRepository.findById(comment.commentId).get().deleted)  // 삭제 상태 유지
        }
    }

    @Nested
    inner class `존재하지 않는 댓글 테스트` {

        @Test
        fun `delete - 존재하지 않는 댓글 ID로 요청 시 예외 없이 처리`() {
            // given
            val nonExistentCommentId = 999L
            
            // when & then
            // 예외가 발생하지 않아야 함
            deleteCommentFacade.delete(nonExistentCommentId)
            
            // repository 상태는 변경되지 않음
            assertEquals(0, fakeRepository.getAllComments().size)
        }

        @Test
        fun `delete - 음수 댓글 ID로 요청 시 예외 없이 처리`() {
            // given
            val negativeCommentId = -1L
            
            // when & then
            deleteCommentFacade.delete(negativeCommentId)
            
            assertEquals(0, fakeRepository.getAllComments().size)
        }

        @Test
        fun `delete - 0번 댓글 ID로 요청 시 예외 없이 처리`() {
            // given
            val zeroCommentId = 0L
            
            // when & then
            deleteCommentFacade.delete(zeroCommentId)
            
            assertEquals(0, fakeRepository.getAllComments().size)
        }
    }

    @Nested
    inner class `서비스 연동 테스트` {

        @Test
        fun `delete - CommentDeletionServiceV2와 올바른 협력`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)
            val initialCommentCount = fakeRepository.getAllComments().size
            
            // when
            deleteCommentFacade.delete(rootComment.commentId)
            
            // then
            val finalCommentCount = fakeRepository.getAllComments().size
            assertEquals(initialCommentCount, finalCommentCount)  // 논리적 삭제이므로 개수 유지
            
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)  // 삭제 플래그 설정됨
        }

        @Test
        fun `delete - 재귀적 삭제 정책 확인`() {
            // given
            val rootComment = createAndSaveRootComment().delete()
            fakeRepository.save(rootComment)
            val childComment = createAndSaveChildComment(rootComment)
            val initialCommentCount = fakeRepository.getAllComments().size
            
            // when
            deleteCommentFacade.delete(childComment.commentId)
            
            // then
            val finalCommentCount = fakeRepository.getAllComments().size
            assertEquals(initialCommentCount - 2, finalCommentCount)  // 부모와 자식 모두 물리적 삭제됨
            
            assertTrue(fakeRepository.findById(childComment.commentId).isEmpty)
            assertTrue(fakeRepository.findById(rootComment.commentId).isEmpty)
        }

        @Test
        fun `delete - 복잡한 댓글 트리에서의 삭제 정책`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment1 = createAndSaveChildComment(rootComment, "00000")
            val childComment2 = createAndSaveChildComment(rootComment, "00001")
            val childComment3 = createAndSaveChildComment(rootComment, "00002")
            
            // when
            deleteCommentFacade.delete(rootComment.commentId)
            
            // then
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)  // 루트는 논리적 삭제
            
            // 자식들은 영향받지 않음
            assertFalse(fakeRepository.findById(childComment1.commentId).get().deleted)
            assertFalse(fakeRepository.findById(childComment2.commentId).get().deleted)
            assertFalse(fakeRepository.findById(childComment3.commentId).get().deleted)
        }
    }

    @Nested
    inner class `멱등성 테스트` {

        @Test
        fun `delete - 같은 댓글을 여러 번 삭제해도 안전함`() {
            // given
            val comment = createAndSaveComment()
            val commentId = comment.commentId
            
            // when
            deleteCommentFacade.delete(commentId)
            deleteCommentFacade.delete(commentId)  // 두 번째 삭제
            deleteCommentFacade.delete(commentId)  // 세 번째 삭제
            
            // then
            assertTrue(fakeRepository.findById(commentId).isEmpty)  // 첫 번째 삭제로 이미 없어짐
        }

        @Test
        fun `delete - 삭제된 댓글을 다시 삭제해도 안전함`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)
            
            // 먼저 논리적 삭제
            deleteCommentFacade.delete(rootComment.commentId)
            assertTrue(fakeRepository.findById(rootComment.commentId).get().deleted)
            
            // when - 다시 삭제 시도
            deleteCommentFacade.delete(rootComment.commentId)
            
            // then
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)  // 여전히 삭제 상태
            assertTrue(fakeRepository.findById(childComment.commentId).isPresent)  // 자식은 여전히 존재
        }
    }

    // 테스트 헬퍼 메서드들
    private fun createAndSaveComment(
        commentId: Long = 1L,
        content: String = "테스트 댓글",
        path: String = ""
    ): CommentV2 {
        val comment = CommentV2(
            commentId = commentId,
            content = content,
            articleId = 1L,
            writerId = 100L,
            commentPath = CommentPath(path),
            deleted = false
        )
        return fakeRepository.save(comment)
    }

    private fun createAndSaveRootComment(): CommentV2 {
        return createAndSaveComment(
            commentId = 1L,
            content = "루트 댓글",
            path = "00000"
        )
    }

    private fun createAndSaveChildComment(parent: CommentV2, path: String = "0000000000"): CommentV2 {
        return createAndSaveComment(
            commentId = (fakeRepository.getAllComments().size + 2).toLong(),
            content = "자식 댓글",
            path = path
        )
    }
}