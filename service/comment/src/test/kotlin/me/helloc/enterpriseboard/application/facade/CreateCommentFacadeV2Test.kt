package me.helloc.enterpriseboard.application.facade

import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.CommentPath
import me.helloc.enterpriseboard.domain.model.CommentV2
import me.helloc.enterpriseboard.domain.service.CommentFactoryV2
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateCommentFacadeV2Test {

    private lateinit var fakeRepository: FakeCommentRepositoryV2
    private lateinit var commentFactory: CommentFactoryV2
    private lateinit var createCommentFacade: CreateCommentFacadeV2

    @BeforeEach
    fun setUp() {
        fakeRepository = FakeCommentRepositoryV2()
        commentFactory = CommentFactoryV2()
        createCommentFacade = CreateCommentFacadeV2(fakeRepository, commentFactory)
    }

    @Nested
    inner class `루트 댓글 생성` {

        @Test
        fun `create - parentPath가 빈 문자열일 때 루트 댓글 생성`() {
            // given
            val content = "루트 댓글 내용"
            val parentPath = ""
            val articleId = 1L
            val writerId = 100L

            // when
            val createdComment = createCommentFacade.create(content, parentPath, articleId, writerId)

            // then
            assertNotNull(createdComment.commentId)
            assertEquals(content, createdComment.content)
            assertEquals(articleId, createdComment.articleId)
            assertEquals(writerId, createdComment.writerId)
            assertEquals(1, createdComment.commentPath.getDepth())  // 루트 댓글은 depth 1
            assertTrue(createdComment.isRoot())  // depth 1이므로 isRoot()=true
            assertFalse(createdComment.deleted)
        }

        @Test
        fun `create - 루트 댓글이 repository에 저장됨`() {
            // given
            val content = "저장 테스트"
            val articleId = 1L
            val writerId = 100L

            // when
            val createdComment = createCommentFacade.create(content, "", articleId, writerId)

            // then
            val savedComment = fakeRepository.findById(createdComment.commentId)
            assertTrue(savedComment.isPresent)
            assertEquals(createdComment.commentId, savedComment.get().commentId)
        }

        @Test
        fun `create - 여러 루트 댓글 생성 시 각각 다른 ID를 가짐`() {
            // given
            val articleId = 1L
            val writerId = 100L

            // when
            val comment1 = createCommentFacade.create("첫 번째 댓글", "", articleId, writerId)
            val comment2 = createCommentFacade.create("두 번째 댓글", "", articleId, writerId)

            // then
            assertTrue(comment1.commentId != comment2.commentId)
            assertEquals(2, fakeRepository.getAllComments().size)
        }
    }

    @Nested
    inner class `자식 댓글 생성` {

        @Test
        fun `create - 유효한 부모 경로로 자식 댓글 생성`() {
            // given
            val parentComment = createAndSaveRootComment()
            val content = "자식 댓글 내용"
            val parentPath = parentComment.commentPath.path
            val articleId = 1L
            val writerId = 200L

            // when
            val childComment = createCommentFacade.create(content, parentPath, articleId, writerId)

            // then
            assertEquals(content, childComment.content)
            assertEquals(articleId, childComment.articleId)
            assertEquals(writerId, childComment.writerId)
            assertEquals("0000000000", childComment.commentPath.path)  // 첫 번째 자식
            assertFalse(childComment.isRoot())  // "00000" 경로는 depth=1이므로 isRoot()=false
            assertFalse(childComment.deleted)
        }

        @Test
        fun `create - 기존 자식이 있을 때 형제 댓글 생성`() {
            // given
            val parentComment = createAndSaveRootComment()
            val firstChild = createAndSaveChildComment(parentComment)

            val content = "두 번째 자식 댓글"
            val parentPath = parentComment.commentPath.path
            val articleId = 1L
            val writerId = 300L

            // when
            val secondChild = createCommentFacade.create(content, parentPath, articleId, writerId)

            // then
            assertEquals("0000000001", secondChild.commentPath.path)  // 두 번째 자식
            assertFalse(secondChild.isRoot())  // "00001" 경로도 depth=1이므로 isRoot()=false
        }

        @Test
        fun `create - 자식 댓글이 repository에 저장됨`() {
            // given
            val parentComment = createAndSaveRootComment()
            val content = "자식 댓글"
            val parentPath = parentComment.commentPath.path

            // when
            val childComment = createCommentFacade.create(content, parentPath, 1L, 200L)

            // then
            val savedChild = fakeRepository.findById(childComment.commentId)
            assertTrue(savedChild.isPresent)
            assertEquals(childComment.commentId, savedChild.get().commentId)

            // 총 댓글 수 확인 (부모 1개 + 자식 1개)
            assertEquals(2, fakeRepository.getAllComments().size)
        }
    }

    @Nested
    inner class `댓글 생성 정책 검증` {

        @Test
        fun `create - 루트가 아닌 댓글에 답글 시 예외 발생`() {
            // given
            val rootComment = createAndSaveRootComment()
            val childComment = createAndSaveChildComment(rootComment)

            val content = "답글"
            val parentPath = childComment.commentPath.path  // 자식 댓글의 경로 (루트 아님)

            // when & then
            val exception = assertThrows<BusinessException> {
                createCommentFacade.create(content, parentPath, 1L, 300L)
            }

            assertEquals(ErrorCode.NO_ROOT_COMMENT_REPLY, exception.errorCode)
        }

        @Test
        fun `create - 삭제된 댓글에 답글 시 예외 발생`() {
            // given
            val rootComment = createAndSaveRootComment()
            val deletedComment = rootComment.delete()
            fakeRepository.save(deletedComment)  // 삭제된 상태로 저장

            val content = "삭제된 댓글에 답글"
            val parentPath = deletedComment.commentPath.path

            // when & then
            val exception = assertThrows<BusinessException> {
                createCommentFacade.create(content, parentPath, 1L, 400L)
            }

            assertEquals(ErrorCode.DELETED_COMMENT_REPLY, exception.errorCode)
        }
    }

    @Nested
    inner class `예외 상황 처리` {

        @Test
        fun `create - 존재하지 않는 부모 경로로 요청 시 예외 발생`() {
            // given
            val nonExistentPath = "99999"
            val content = "존재하지 않는 부모"

            // when & then
            assertThrows<RuntimeException> {
                createCommentFacade.create(content, nonExistentPath, 1L, 500L)
            }
        }

        @Test
        fun `create - 빈 내용으로도 댓글 생성 가능`() {
            // given
            val content = ""
            val parentPath = ""
            val articleId = 1L
            val writerId = 100L

            // when
            val comment = createCommentFacade.create(content, parentPath, articleId, writerId)

            // then
            assertEquals("", comment.content)
            assertTrue(comment.isRoot())  // 빈 경로는 depth=0이므로 isRoot()=true
        }
    }

    @Nested
    inner class `descendantsLastPath 동작 검증` {

        @Test
        fun `create - getDescendantsLastPath 호출 및 결과 활용 확인`() {
            // given
            val rootComment = createAndSaveRootComment()
            val firstChild = createAndSaveChildComment(rootComment)

            // 첫 번째 자식이 이미 있는 상태에서 두 번째 자식 생성
            val content = "두 번째 자식"
            val parentPath = rootComment.commentPath.path

            // when
            val secondChild = createCommentFacade.create(content, parentPath, 1L, 300L)

            // then
            // getDescendantsLastPath가 "00000"을 반환했고, 그것을 기반으로 "00001"이 생성되어야 함
            assertEquals("0000000001", secondChild.commentPath.path)
        }

        @Test
        fun `create - 자식이 없을 때 첫 번째 자식 생성`() {
            // given
            val rootComment = createAndSaveRootComment()
            val content = "첫 번째 자식"
            val parentPath = rootComment.commentPath.path

            // when
            val firstChild = createCommentFacade.create(content, parentPath, 1L, 200L)

            // then
            // getDescendantsLastPath가 빈 문자열을 반환하고, 첫 번째 자식으로 "00000" 생성
            assertEquals("0000000000", firstChild.commentPath.path)
        }
    }

    // 테스트 헬퍼 메서드들
    private fun createAndSaveRootComment(): CommentV2 {
        val rootComment = CommentV2(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 1L,
            writerId = 100L,
            commentPath = CommentPath("00000"),  // depth 1 (루트)
            deleted = false
        )
        return fakeRepository.save(rootComment)
    }

    private fun createAndSaveChildComment(parent: CommentV2): CommentV2 {
        val childComment = CommentV2(
            commentId = 2L,
            content = "자식 댓글",
            articleId = 1L,
            writerId = 200L,
            commentPath = CommentPath("0000000000"),  // depth 2 (자식)
            deleted = false
        )
        return fakeRepository.save(childComment)
    }
}
