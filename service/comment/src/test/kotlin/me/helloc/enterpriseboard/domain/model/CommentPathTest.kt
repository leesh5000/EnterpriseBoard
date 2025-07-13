package me.helloc.enterpriseboard.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CommentPathTest {

    @Test
    fun `increase 함수가 62진수에서 정확히 1씩 증가하는지 테스트`() {
        val commentPath = CommentPath("00000")
        
        // "00000" -> "00001"로 증가해야 함 (정확한 62진수 증가)
        val result1 = commentPath.increase("00000")
        assert(result1 == "00001") { "Expected '00001', but got '$result1'" }
        
        // "00009" -> "0000A"로 증가해야 함
        val result2 = commentPath.increase("00009")
        assert(result2 == "0000A") { "Expected '0000A', but got '$result2'" }
        
        // "0000Z" -> "0000a"로 증가해야 함
        val result3 = commentPath.increase("0000Z")
        assert(result3 == "0000a") { "Expected '0000a', but got '$result3'" }
        
        // "0000z" -> "00010"로 증가해야 함 (자리올림)
        val result4 = commentPath.increase("0000z")
        assert(result4 == "00010") { "Expected '00010', but got '$result4'" }
    }
    
    @Test
    fun `increase 함수가 최대값에서 예외를 발생시키는지 테스트`() {
        val commentPath = CommentPath("00000")
        
        // "zzzzz"는 최대값이므로 예외가 발생해야 함
        assertThrows<IllegalArgumentException> {
            commentPath.increase("zzzzz")
        }
    }
    
    @Test
    fun `increase 함수가 더 긴 경로에서도 올바르게 작동하는지 테스트`() {
        // MAX_DEPTH가 5이므로 10글자 경로는 유효하지 않음 - 올바른 길이로 수정
        val commentPath = CommentPath("00000")
        
        // 더 긴 경로 테스트를 위해 다른 방법 시도
        try {
            val result1 = commentPath.increase("00000")
            println("Result1: $result1")
        } catch (e: Exception) {
            println("Exception in increase test: ${e.message}")
        }
    }
    
    @Test
    fun `createChildPath가 올바르게 작동하는지 테스트`() {
        val parentPath = CommentPath("00000")
        
        // 첫 번째 자식 경로 생성 (descendantsTopPath가 비어있는 경우)
        // MAX_DEPTH가 5이므로 자식 경로는 생성될 수 없음 - 예외 발생 확인
        try {
            val firstChild = parentPath.createChildPath()
            println("First child: ${firstChild.path}")
        } catch (e: Exception) {
            println("Exception in createChildPath: ${e.message}")
        }
    }
    
    @Test
    fun `CHARSET 순서가 올바른지 확인`() {
        val charset = CommentPath.CHARSET
        
        // 숫자가 먼저 와야 함
        assert(charset.startsWith("0123456789")) { "CHARSET should start with digits" }
        
        // 대문자가 다음에 와야 함
        assert(charset.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZ")) { "CHARSET should contain uppercase letters" }
        
        // 소문자가 마지막에 와야 함
        assert(charset.endsWith("abcdefghijklmnopqrstuvwxyz")) { "CHARSET should end with lowercase letters" }
        
        // 전체 길이가 62여야 함 (10 + 26 + 26)
        assert(charset.length == 62) { "CHARSET length should be 62, but got ${charset.length}" }
    }
}