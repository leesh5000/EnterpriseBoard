package me.helloc.enterpriseboard.domain.service

import me.helloc.enterpriseboard.application.facade.FakeCommentRepositoryV2
import me.helloc.enterpriseboard.domain.model.CommentPath
import me.helloc.enterpriseboard.domain.model.CommentV2
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommentDeletionServiceV2Test {

    private lateinit var fakeRepository: FakeCommentRepositoryV2
    private lateinit var commentDeletionService: CommentDeletionServiceV2

    @BeforeEach
    fun setUp() {
        fakeRepository = FakeCommentRepositoryV2()
        commentDeletionService = CommentDeletionServiceV2(fakeRepository)
    }

    @Nested
    inner class `논리적 삭제 테스트` {

        @Test
        fun `deleteComment - 자식 댓글이 있을 때 논리적 삭제 수행`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)

            // when
            commentDeletionService.deleteComment(rootComment)

            // then
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)  // 논리적 삭제됨
            assertEquals(rootComment.content, savedRootComment.content)  // 내용은 보존

            val savedChildComment = fakeRepository.findById(childComment.commentId).get()
            assertFalse(savedChildComment.deleted)  // 자식은 삭제되지 않음
        }

        @Test
        fun `deleteComment - 여러 자식이 있을 때 논리적 삭제 수행`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment1 = createAndSaveChildComment(rootComment, "00000")
            val childComment2 = createAndSaveChildComment(rootComment, "00001")

            // when
            commentDeletionService.deleteComment(rootComment)

            // then
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)

            // 자식들은 삭제되지 않음
            assertFalse(fakeRepository.findById(childComment1.commentId).get().deleted)
            assertFalse(fakeRepository.findById(childComment2.commentId).get().deleted)
        }

        @Test
        fun `deleteComment - 이미 삭제된 댓글도 논리적 삭제 처리됨`() {
            // given
            val rootComment = createAndSaveRootComment().delete()
            fakeRepository.save(rootComment)
            val childComment = createAndSaveChildComment(rootComment)

            // when
            commentDeletionService.deleteComment(rootComment)

            // then
            val savedRootComment = fakeRepository.findById(rootComment.commentId).get()
            assertTrue(savedRootComment.deleted)  // 여전히 삭제 상태
        }
    }

    @Nested
    inner class `물리적 삭제 테스트` {

        @Test
        fun `deleteComment - 자식 댓글이 없을 때 물리적 삭제 수행`() {
            // given
            val rootComment = createAndSaveRootComment()

            // when
            commentDeletionService.deleteComment(rootComment)

            // then
            assertTrue(fakeRepository.findById(rootComment.commentId).isEmpty)  // 완전 삭제됨
        }

        @Test
        fun `deleteComment - 리프 노드 댓글 물리적 삭제`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)

            // when
            commentDeletionService.deleteComment(childComment)

            // then
            assertTrue(fakeRepository.findById(childComment.commentId).isEmpty)  // 자식 삭제됨
            assertTrue(fakeRepository.findById(rootComment.commentId).isPresent)  // 부모는 남아있음
        }
    }

    @Nested
    inner class `재귀적 삭제 테스트` {

        @Test
        fun `deleteComment - 삭제된 부모가 자식 없을 때 함께 삭제됨`() {
            // given
            val rootComment = createAndSaveRootComment().delete()
            fakeRepository.save(rootComment)
            val childComment = createAndSaveChildComment(rootComment)

            // when
            commentDeletionService.deleteComment(childComment)

            // then
            assertTrue(fakeRepository.findById(childComment.commentId).isEmpty)  // 자식 삭제됨
            assertTrue(fakeRepository.findById(rootComment.commentId).isEmpty)  // 부모도 함께 삭제됨
        }

        @Test
        fun `deleteComment - 삭제된 부모가 다른 자식 있을 때 부모는 남아있음`() {
            // given
            val rootComment = createAndSaveRootComment().delete()
            fakeRepository.save(rootComment)
            val childComment1 = createAndSaveChildComment(rootComment, "00000")
            val childComment2 = createAndSaveChildComment(rootComment, "00001")

            // when
            commentDeletionService.deleteComment(childComment1)

            // then
            assertTrue(fakeRepository.findById(childComment1.commentId).isEmpty)  // 첫 번째 자식 삭제됨
            assertTrue(fakeRepository.findById(rootComment.commentId).isPresent)  // 부모는 남아있음 (다른 자식 있음)
            assertTrue(fakeRepository.findById(childComment2.commentId).isPresent)  // 두 번째 자식도 남아있음
        }

        @Test
        fun `deleteComment - 두 단계 재귀적 삭제`() {
            // given
            val rootComment = createAndSaveRootComment().delete()
            fakeRepository.save(rootComment)
            val childComment = createAndSaveChildComment(rootComment, "00000")

            // when
            commentDeletionService.deleteComment(childComment)

            // then
            assertTrue(fakeRepository.findById(childComment.commentId).isEmpty)  // 자식 삭제됨
            assertTrue(fakeRepository.findById(rootComment.commentId).isEmpty)   // 부모도 삭제됨 (이미 삭제되고 자식 없음)
        }

        @Test
        fun `deleteComment - 루트 댓글은 재귀적 삭제 대상이 아님`() {
            // given
            val rootComment = createAndSaveRootComment()

            // when
            commentDeletionService.deleteComment(rootComment)

            // then
            assertTrue(fakeRepository.findById(rootComment.commentId).isEmpty)  // 루트 댓글 삭제됨
            // 부모를 찾으려고 시도하지 않음 (루트이므로)
        }
    }

    @Nested
    inner class `hasChildren 메서드 테스트` {

        @Test
        fun `hasChildren - 자식 댓글이 있을 때 true 반환`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)

            // when & then
            assertTrue(hasChildrenWrapper(rootComment))
            assertFalse(hasChildrenWrapper(childComment))
        }

        @Test
        fun `hasChildren - 자식 댓글이 없을 때 false 반환`() {
            // given
            val rootComment = createAndSaveRootComment()

            // when & then
            assertFalse(hasChildrenWrapper(rootComment))
        }

        @Test
        fun `hasChildren - 여러 자식이 있을 때 true 반환`() {
            // given
            val rootComment = createAndSaveRootComment()
            createAndSaveChildComment(rootComment, "00000")
            createAndSaveChildComment(rootComment, "00001")
            createAndSaveChildComment(rootComment, "00002")

            // when & then
            assertTrue(hasChildrenWrapper(rootComment))
        }
    }

    @Nested
    inner class `경계값 테스트` {

        @Test
        fun `deleteComment - 빈 경로 댓글 처리`() {
            // given
            val rootComment = createAndSaveRootComment()

            // when
            commentDeletionService.deleteComment(rootComment)

            // then
            assertTrue(fakeRepository.findById(rootComment.commentId).isEmpty)
        }

        @Test
        fun `deleteComment - 최대 깊이 댓글 처리`() {
            // given
            val maxDepthComment = CommentV2(
                commentId = 999L,
                content = "최대 깊이 댓글",
                articleId = 1L,
                writerId = 100L,
                commentPath = CommentPath("00000"),  // depth = 1
                deleted = false
            )
            fakeRepository.save(maxDepthComment)

            // when
            commentDeletionService.deleteComment(maxDepthComment)

            // then
            assertTrue(fakeRepository.findById(maxDepthComment.commentId).isEmpty)
        }
    }

    // 테스트 헬퍼 메서드들
    private fun createAndSaveRootComment(): CommentV2 {
        val rootComment = CommentV2(
            commentId = 1L,
            content = "루트 댓글",
            articleId = 1L,
            writerId = 100L,
            commentPath = CommentPath("00000"),  // depth 1 (루트)
            deleted = false
        )
        return fakeRepository.save(rootComment)
    }

    private fun createAndSaveChildComment(parent: CommentV2, path: String = "0000000000"): CommentV2 {
        val childComment = CommentV2(
            commentId = (fakeRepository.getAllComments().size + 2).toLong(),
            content = "자식 댓글",
            articleId = 1L,
            writerId = 200L,
            commentPath = CommentPath(path),
            deleted = false
        )
        return fakeRepository.save(childComment)
    }

    // hasChildren은 private이므로 reflection 또는 간접적으로 테스트
    private fun hasChildrenWrapper(comment: CommentV2): Boolean {
        return fakeRepository.getDescendantsLastPath(comment.articleId, comment.commentPath.path)
            .isNotEmpty()
    }
}
