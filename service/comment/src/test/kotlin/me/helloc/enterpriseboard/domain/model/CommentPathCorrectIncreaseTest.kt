package me.helloc.enterpriseboard.domain.model

import org.junit.jupiter.api.Test

class CommentPathCorrectIncreaseTest {

    @Test
    fun `increase 함수가 62진수에서 정확히 1씩 증가하는지 테스트`() {
        val commentPath = CommentPath("00000")
        
        // 현재 잘못된 동작: "00000" -> "11111"
        // 올바른 동작이어야 함: "00000" -> "00001"
        val result1 = commentPath.increase("00000")
        assert(result1 == "00001") { "Expected '00001', but got '$result1'" }
        
        // "00001" -> "00002"
        val result2 = commentPath.increase("00001")
        assert(result2 == "00002") { "Expected '00002', but got '$result2'" }
        
        // "00009" -> "0000A" (9 다음은 A)
        val result3 = commentPath.increase("00009")
        assert(result3 == "0000A") { "Expected '0000A', but got '$result3'" }
        
        // "0000Z" -> "0000a" (Z 다음은 a)
        val result4 = commentPath.increase("0000Z")
        assert(result4 == "0000a") { "Expected '0000a', but got '$result4'" }
        
        // "0000z" -> "00010" (z 다음은 자리올림)
        val result5 = commentPath.increase("0000z")
        assert(result5 == "00010") { "Expected '00010', but got '$result5'" }
        
        // "00010" -> "00011"
        val result6 = commentPath.increase("00010")
        assert(result6 == "00011") { "Expected '00011', but got '$result6'" }
    }
    
    @Test
    fun `increase 함수의 올바른 동작 확인`() {
        val commentPath = CommentPath("00000")
        
        // 현재 동작 확인
        val currentResult = commentPath.increase("00000")
        println("현재 동작: '00000' -> '$currentResult'")
        
        // 이제는 "00001"이 나와야 함 (올바른 동작)
        assert(currentResult == "00001") { "구현이 올바르지 않습니다: $currentResult" }
    }
}