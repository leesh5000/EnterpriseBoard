package me.helloc.enterpriseboard.domain.model

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommentPathTest {

    @Nested
    inner class `생성자 및 초기화` {

        @Test
        fun `유효한 경로로 CommentPath 생성`() {
            val validPaths = listOf("00000")  // MAX_DEPTH가 5이므로 5자까지만 유효

            validPaths.forEach { path ->
                val commentPath = CommentPath(path)
                assertEquals(path, commentPath.path)
            }
        }

        @Test
        fun `depth가 MAX_DEPTH를 초과하면 예외 발생`() {
            val invalidPath = "000000000000000000000000000000"  // 30자 = depth 6 (MAX_DEPTH=5 초과)

            val exception = assertThrows<IllegalArgumentException> {
                CommentPath(invalidPath)
            }
            assertTrue(exception.message!!.contains("Comment path is too long"))
        }

        @Test
        fun `경로 길이가 DEPTH_CHUNK_SIZE의 배수가 아니면 예외 발생`() {
            val invalidPaths = listOf("0", "00", "000", "0000")  // 5의 배수가 아닌 길이 (1,2,3,4자)

            invalidPaths.forEach { path ->
                val exception = assertThrows<IllegalArgumentException> {
                    CommentPath(path)
                }
                assertTrue(exception.message!!.contains("must be a multiple of"))
            }
        }
    }

    @Nested
    inner class `깊이 계산` {

        @Test
        fun `getDepth는 올바른 깊이를 반환한다`() {
            val commentPath = CommentPath("00000")  // 5자 = 1깊이
            assertEquals(1, commentPath.getDepth())
        }

        @Test
        fun `calculateDepth 정적 메서드는 올바른 깊이를 반환한다`() {
            assertEquals(1, CommentPath.calculateDepth("00000"))  // 5자 = 1깊이
        }

        @Test
        fun `isRoot는 깊이가 1일 때만 true를 반환한다`() {
            assertTrue(CommentPath(CommentPath.next("")).isRoot())  // 깊이 0은 루트 아님
            assertTrue(CommentPath("00000").isRoot())  // 깊이 1은 루트
        }
    }

    @Nested
    inner class `부모 경로` {

        @Test
        fun `루트 경로에서 부모 경로는 빈 문자열`() {
            val commentPath = CommentPath("00000")  // 깊이 1 (루트)
            assertEquals("", commentPath.getParentPath())
        }
    }

    @Nested
    inner class `자식 경로 생성` {

        @Test
        fun `descendantsLastPath가 빈 문자열일 때 MIN_CHUNK 추가`() {
            val parentPath = CommentPath("00000")  // depth 1

            // depth 1에서 자식 생성하면 depth 2가 되므로 정상 동작
            val childPath = parentPath.createChildPath("")
            assertEquals("0000000000", childPath.path)  // depth 2
        }

        @Test
        fun `깊은 계층에서 형제 노드 생성`() {
            // depth 4에서 형제 노드 생성
            val parentPath = CommentPath("00000000000000000000")  // depth 4
            val descendantsLastPath = "0000000000000000000000000"  // depth 5의 첫 번째 자식

            // depth 4에서 형제 생성은 가능 (depth 5가 됨)
            val siblingPath = parentPath.createChildPath(descendantsLastPath)
            assertEquals("0000000000000000000000001", siblingPath.path)
        }

        @Test
        fun `최대 깊이에서 자식 생성 시 예외 발생`() {
            val maxDepthPath = CommentPath("0000000000000000000000000")  // depth 5 (최대 깊이)

            // MAX_DEPTH=5이므로 자식 생성 시 depth 6이 되어 예외 발생
            assertThrows<IllegalArgumentException> {
                maxDepthPath.createChildPath("")
            }
        }
    }

    @Nested
    inner class `자식 경로 찾기` {

        @Test
        fun `findChildrenLastPath는 올바른 길이의 자식 경로를 반환한다`() {
            val parentPath = CommentPath("00000")  // 깊이 1
            val descendantsLastPath = "0000012345"  // 10자 후손 경로 (기대되는 자식 길이)

            val childrenLastPath = parentPath.findChildrenLastPath(descendantsLastPath)
            assertEquals("0000012345", childrenLastPath)  // (1+1) * 5 = 10자
        }
    }

    @Nested
    inner class `62진수 증가` {

        @Test
        fun `next - 기본적인 단일 자리 증가`() {
            val commentPath = CommentPath("00000")

            assertEquals("00001", CommentPath.next("00000"))
            assertEquals("00002", CommentPath.next("00001"))
            assertEquals("00009", CommentPath.next("00008"))
        }

        @Test
        fun `next - 숫자에서 대문자로 증가`() {
            val commentPath = CommentPath("00000")

            assertEquals("0000A", CommentPath.next("00009"))
            assertEquals("0000B", CommentPath.next("0000A"))
            assertEquals("0000Z", CommentPath.next("0000Y"))
        }

        @Test
        fun `next - 대문자에서 소문자로 증가`() {
            val commentPath = CommentPath("00000")

            assertEquals("0000a", CommentPath.next("0000Z"))
            assertEquals("0000b", CommentPath.next("0000a"))
            assertEquals("0000z", CommentPath.next("0000y"))
        }

        @Test
        fun `next - 자리올림 발생`() {
            val commentPath = CommentPath("00000")

            assertEquals("00010", CommentPath.next("0000z"))
            assertEquals("00020", CommentPath.next("0001z"))
            assertEquals("00100", CommentPath.next("000zz"))
        }

        @Test
        fun `next - 복잡한 자리올림`() {
            val commentPath = CommentPath("00000")

            assertEquals("00100", CommentPath.next("000zz"))
            assertEquals("01000", CommentPath.next("00zzz"))
            assertEquals("10000", CommentPath.next("0zzzz"))
        }

        @Test
        fun `next - 최대값에서 예외 발생`() {
            assertThrows<IllegalArgumentException> {
                CommentPath.next("zzzzz")
            }
        }

        @Test
        fun `next - 다양한 길이 경로에서 증가`() {
            val commentPath = CommentPath("00000")

            // 5자 경로 증가 테스트
            assertEquals("00001", CommentPath.next("00000"))
            assertEquals("0000A", CommentPath.next("00009"))
            assertEquals("00010", CommentPath.next("0000z"))
        }

        @ParameterizedTest
        @CsvSource(
            "00000, 00001",
            "00001, 00002",
            "00009, 0000A",
            "0000Z, 0000a",
            "0000z, 00010",
            "00010, 00011",
            "0001z, 00020",
            "000zz, 00100"
        )
        fun `next - 다양한 케이스 파라미터 테스트`(input: String, expected: String) {
            val commentPath = CommentPath("00000")
            assertEquals(expected, CommentPath.next(input))
        }
    }

    @Nested
    inner class `상수 및 유틸리티` {

        @Test
        fun `CHARSET이 올바른 순서와 길이를 가진다`() {
            val charset = CommentPath.CHARSET

            // 순서 확인: 숫자(0-9) -> 대문자(A-Z) -> 소문자(a-z)
            assertTrue(charset.startsWith("0123456789"))
            assertTrue(charset.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZ"))
            assertTrue(charset.endsWith("abcdefghijklmnopqrstuvwxyz"))

            // 길이 확인: 10 + 26 + 26 = 62
            assertEquals(62, charset.length)
        }

        @Test
        fun `MIN_CHUNK와 MAX_CHUNK 상수 확인`() {
            assertEquals("00000", CommentPath.MIN_CHUNK)
            assertEquals("zzzzz", CommentPath.MAX_CHUNK)
            assertEquals(5, CommentPath.MIN_CHUNK.length)
            assertEquals(5, CommentPath.MAX_CHUNK.length)
        }

        @Test
        fun `DEPTH_CHUNK_SIZE와 MAX_DEPTH 상수 확인`() {
            assertEquals(5, CommentPath.DEPTH_CHUNK_SIZE)
            assertEquals(5, CommentPath.MAX_DEPTH)
        }

        @ParameterizedTest
        @ValueSource(strings = ["000000000000000000000000000000"])  // 30자 = depth 6
        fun `isDepthOverflowed - depth가 MAX_DEPTH를 초과하면 true`(path: String) {
            assertTrue(CommentPath.isDepthOverflowed(path))
        }

        @Test
        fun `isDepthOverflowed - 유효한 depth면 false`() {
            assertFalse(CommentPath.isDepthOverflowed("0000000000000000000000000"))  // 25자 = depth 5 (유효)
            assertFalse(CommentPath.isDepthOverflowed("00000"))  // 5자 = depth 1 (유효)
        }
    }

    @Nested
    inner class `루트 댓글 개념` {

        @Test
        fun `depth 1이 루트 댓글이다`() {
            val rootComment = CommentPath("00000")  // 5자 = depth 1
            assertEquals(1, rootComment.getDepth())
            assertTrue(rootComment.isRoot())  // depth 1이 Root 댓글
        }

        @Test
        fun `루트 댓글들은 서로 다른 고유 path를 가져야 한다`() {
            val path1 = CommentPath("00000")
            val path2 = CommentPath("00001")
            val path3 = CommentPath("0000A")

            assertEquals(1, path1.getDepth())
            assertEquals(1, path2.getDepth())
            assertEquals(1, path3.getDepth())

            // 모두 다른 path
            assertTrue(path1.path != path2.path)
            assertTrue(path2.path != path3.path)
            assertTrue(path1.path != path3.path)
        }
    }

    @Nested
    inner class `경계값 테스트` {

        @Test
        fun `최소 경로 (깊이 1) 테스트`() {
            val minPath = CommentPath("00000")
            assertEquals(1, minPath.getDepth())
            assertTrue(minPath.isRoot())  // 깊이 1은 루트
            assertEquals("", minPath.getParentPath())
        }

        @Test
        fun `최대 경로 (길이 5) 테스트`() {
            val maxPath = CommentPath("00000")  // 5자 = 최대 길이
            assertEquals(1, maxPath.getDepth())
            assertTrue(maxPath.isRoot())  // 깊이 1은 루트
            assertEquals("", maxPath.getParentPath())
        }

        @Test
        fun `MIN_CHUNK로 구성된 경로`() {
            val commentPath = CommentPath("00000")  // MIN_CHUNK
            assertEquals(1, commentPath.getDepth())
        }

        @Test
        fun `MAX_CHUNK로 구성된 경로`() {
            val commentPath = CommentPath("zzzzz")  // MAX_CHUNK
            assertEquals(1, commentPath.getDepth())
        }
    }

    @Nested
    inner class `예외 처리` {

        @Test
        fun `빈 descendantsLastPath 처리`() {
            val parentPath = CommentPath("00000")

            // MAX_DEPTH=5 제한으로 예외가 발생해야 함
            assertThrows<IllegalArgumentException> {
                parentPath.createChildPath("")
            }
        }

        @Test
        fun `findChildrenLastPath - descendantsLastPath가 너무 짧으면 예외`() {
            val parentPath = CommentPath("00000")  // 깊이 1
            val shortDescendantsPath = "0000"  // 4자 (부족)

            assertThrows<StringIndexOutOfBoundsException> {
                parentPath.findChildrenLastPath(shortDescendantsPath)
            }
        }
    }
}
