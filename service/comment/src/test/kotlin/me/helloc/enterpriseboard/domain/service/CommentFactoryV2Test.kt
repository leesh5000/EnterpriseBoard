package me.helloc.enterpriseboard.domain.service

import me.helloc.enterpriseboard.domain.exception.BusinessException
import me.helloc.enterpriseboard.domain.exception.ErrorCode
import me.helloc.enterpriseboard.domain.model.CommentPath
import me.helloc.enterpriseboard.domain.model.CommentV2
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CommentFactoryV2Test {

    private val commentFactory = CommentFactoryV2()

    @Nested
    inner class `루트 댓글 생성` {

        @Test
        fun `createRootComment - 유효한 파라미터로 루트 댓글 생성`() {
            // given
            val content = "테스트 댓글 내용"
            val articleId = 1L
            val writerId = 100L

            // when
            val rootComment = commentFactory.createRootComment(
                content = content,
                articleId = articleId,
                writerId = writerId,
                lastPath = ""
            )

            // then
            assertNotNull(rootComment.commentId)
            assertTrue(rootComment.commentId > 0)
            assertEquals(content, rootComment.content)
            assertEquals(articleId, rootComment.articleId)
            assertEquals(writerId, rootComment.writerId)
            assertEquals(1, rootComment.commentPath.getDepth())  // 루트 댓글은 depth 1
            assertTrue(rootComment.isRoot())  // depth 1이므로 isRoot()=true
            assertFalse(rootComment.deleted)
            assertNotNull(rootComment.createdAt)
        }

        @Test
        fun `createRootComment - 빈 문자열 내용으로도 생성 가능`() {
            // given
            val content = ""
            val articleId = 1L
            val writerId = 100L

            // when
            val rootComment = commentFactory.createRootComment(content, articleId, writerId)

            // then
            assertEquals("", rootComment.content)
            assertTrue(rootComment.isRoot())  // depth 1이므로 isRoot()=true
        }
    }

    @Nested
    inner class `자식 댓글 생성` {

        @Test
        fun `createChildComment - 유효한 루트 댓글에 자식 댓글 생성`() {
            // given
            val parentComment = createValidRootComment()
            val content = "답글 내용"
            val articleId = 1L
            val writerId = 200L
            val descendantsLastPath = ""

            // when
            val childComment = commentFactory.createChildComment(
                content = content,
                articleId = articleId,
                writerId = writerId,
                parent = parentComment,
                descendantsLastPath = descendantsLastPath
            )

            // then
            assertNotNull(childComment.commentId)
            assertTrue(childComment.commentId > 0)
            assertEquals(content, childComment.content)
            assertEquals(articleId, childComment.articleId)
            assertEquals(writerId, childComment.writerId)
            assertEquals("00000", childComment.commentPath.path)  // 첫 번째 자식
            assertFalse(childComment.isRoot())
            assertFalse(childComment.deleted)
        }

        @Test
        fun `createChildComment - descendantsLastPath가 있을 때 형제 댓글 생성`() {
            // given
            val parentComment = createValidRootComment()
            val content = "두 번째 답글"
            val articleId = 1L
            val writerId = 300L
            val descendantsLastPath = "00000"  // 기존 첫 번째 자식

            // when
            val childComment = commentFactory.createChildComment(
                content = content,
                articleId = articleId,
                writerId = writerId,
                parent = parentComment,
                descendantsLastPath = descendantsLastPath
            )

            // then
            assertEquals("00001", childComment.commentPath.path)  // 두 번째 자식
            assertFalse(childComment.isRoot())
        }

        @Test
        fun `createChildComment - 부모와 자식의 ID가 다름`() {
            // given
            val parentComment = createValidRootComment()
            val content = "답글"
            val articleId = 1L
            val writerId = 200L

            // when
            val childComment = commentFactory.createChildComment(
                content = content,
                articleId = articleId,
                writerId = writerId,
                parent = parentComment,
                descendantsLastPath = ""
            )

            // then
            assertTrue(parentComment.commentId != childComment.commentId)
        }
    }

    @Nested
    inner class `댓글 생성 정책 검증` {

        @Test
        fun `createChildComment - 루트가 아닌 댓글에 답글 시 예외 발생`() {
            // given
            val nonRootComment = createNonRootComment()
            val content = "답글"
            val articleId = 1L
            val writerId = 200L

            // when & then
            val exception = assertThrows<BusinessException> {
                commentFactory.createChildComment(
                    content = content,
                    articleId = articleId,
                    writerId = writerId,
                    parent = nonRootComment,
                    descendantsLastPath = ""
                )
            }

            assertEquals(ErrorCode.NO_ROOT_COMMENT_REPLY, exception.errorCode)
            assertTrue(exception.message!!.contains(nonRootComment.commentId.toString()))
        }

        @Test
        fun `createChildComment - 삭제된 댓글에 답글 시 예외 발생`() {
            // given
            val deletedComment = createDeletedRootComment()
            val content = "답글"
            val articleId = 1L
            val writerId = 200L

            // when & then
            val exception = assertThrows<BusinessException> {
                commentFactory.createChildComment(
                    content = content,
                    articleId = articleId,
                    writerId = writerId,
                    parent = deletedComment,
                    descendantsLastPath = ""
                )
            }

            assertEquals(ErrorCode.DELETED_COMMENT_REPLY, exception.errorCode)
            assertTrue(exception.message!!.contains(deletedComment.commentId.toString()))
        }
    }

    @Nested
    inner class `경계값 테스트` {

        @Test
        fun `createRootComment - 최대 Long 값으로 ID 생성`() {
            // given
            val content = "경계값 테스트"
            val articleId = Long.MAX_VALUE
            val writerId = Long.MAX_VALUE

            // when
            val rootComment = commentFactory.createRootComment(content, articleId, writerId)

            // then
            assertEquals(Long.MAX_VALUE, rootComment.articleId)
            assertEquals(Long.MAX_VALUE, rootComment.writerId)
        }

        @Test
        fun `createRootComment - 긴 내용으로 댓글 생성`() {
            // given
            val longContent = "a".repeat(1000)
            val articleId = 1L
            val writerId = 100L

            // when
            val rootComment = commentFactory.createRootComment(longContent, articleId, writerId)

            // then
            assertEquals(longContent, rootComment.content)
            assertEquals(1000, rootComment.content.length)
        }
    }

    @Nested
    inner class `시간 검증` {

        @Test
        fun `createRootComment - 생성 시간이 현재 시간 근처`() {
            // given
            val beforeCreate = LocalDateTime.now()
            val content = "시간 테스트"
            val articleId = 1L
            val writerId = 100L

            // when
            val rootComment = commentFactory.createRootComment(content, articleId, writerId)
            val afterCreate = LocalDateTime.now()

            // then
            assertTrue(rootComment.createdAt.isAfter(beforeCreate.minusSeconds(1)))
            assertTrue(rootComment.createdAt.isBefore(afterCreate.plusSeconds(1)))
        }

        @Test
        fun `createChildComment - 생성 시간이 현재 시간 근처`() {
            // given
            val parentComment = createValidRootComment()
            val beforeCreate = LocalDateTime.now()

            // when
            val childComment = commentFactory.createChildComment(
                content = "자식 댓글",
                articleId = 1L,
                writerId = 200L,
                parent = parentComment,
                descendantsLastPath = ""
            )
            val afterCreate = LocalDateTime.now()

            // then
            assertTrue(childComment.createdAt.isAfter(beforeCreate.minusSeconds(1)))
            assertTrue(childComment.createdAt.isBefore(afterCreate.plusSeconds(1)))
        }
    }

    // 테스트 헬퍼 메서드들
    private fun createValidRootComment(): CommentV2 {
        return CommentV2(
            commentId = 1L,
            content = "부모 댓글",
            articleId = 1L,
            writerId = 100L,
            commentPath = CommentPath("00000"),  // depth 1 (루트 댓글)
            deleted = false
        )
    }

    private fun createNonRootComment(): CommentV2 {
        return CommentV2(
            commentId = 2L,
            content = "자식 댓글",
            articleId = 1L,
            writerId = 100L,
            commentPath = CommentPath("0000000000"),  // depth 2 (루트가 아닌 댓글)
            deleted = false
        )
    }

    private fun createDeletedRootComment(): CommentV2 {
        return CommentV2(
            commentId = 3L,
            content = "삭제된 댓글",
            articleId = 1L,
            writerId = 100L,
            commentPath = CommentPath("00000"),  // depth 1 (루트 댓글이지만 삭제됨)
            deleted = true
        )
    }
}
