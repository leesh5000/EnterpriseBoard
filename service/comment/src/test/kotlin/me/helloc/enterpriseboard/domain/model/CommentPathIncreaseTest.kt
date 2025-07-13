package me.helloc.enterpriseboard.domain.model

import org.junit.jupiter.api.Test

class CommentPathIncreaseTest {

    @Test
    fun `increase 함수 동작 분석`() {
        val commentPath = CommentPath("00000")
        
        println("CHARSET: ${CommentPath.CHARSET}")
        println("DEPTH_CHUNK_SIZE: ${CommentPath.DEPTH_CHUNK_SIZE}")
        println("MIN_CHUNK: ${CommentPath.MIN_CHUNK}")
        println("MAX_CHUNK: ${CommentPath.MAX_CHUNK}")
        
        // "00000"에서 increase 시도
        val path1 = "00000"
        val lastChunk1 = path1.substring(path1.length - CommentPath.DEPTH_CHUNK_SIZE)
        println("Path: $path1, Last chunk: '$lastChunk1'")
        println("Last char: '${lastChunk1.last()}'")
        println("Index of last char: ${CommentPath.CHARSET.indexOf(lastChunk1.last())}")
        
        try {
            val result1 = commentPath.increase(path1)
            println("Result: $result1")
        } catch (e: Exception) {
            println("Exception: ${e.message}")
        }
        
        // 단일 문자 증가 테스트
        println("\n=== 단일 문자 증가 테스트 ===")
        val testChars = listOf('0', '1', '9', 'A', 'Z', 'a', 'z')
        for (char in testChars) {
            val index = CommentPath.CHARSET.indexOf(char)
            if (index >= 0 && index < CommentPath.CHARSET.length - 1) {
                val nextChar = CommentPath.CHARSET[index + 1]
                println("'$char' (index: $index) -> '$nextChar' (index: ${index + 1})")
            } else {
                println("'$char' (index: $index) -> 오버플로우")
            }
        }
    }
}