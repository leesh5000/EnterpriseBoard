package me.helloc.common.test

import java.time.LocalDateTime
import kotlin.random.Random

/**
 * 테스트용 픽스처 생성 유틸리티
 */
object TestFixtures {
    
    /**
     * 랜덤 ID 생성
     */
    fun randomId(): Long = Random.nextLong(1, Long.MAX_VALUE)
    
    /**
     * 랜덤 문자열 생성
     */
    fun randomString(length: Int = 10): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
    
    /**
     * 랜덤 이메일 생성
     */
    fun randomEmail(): String = "${randomString(8)}@${randomString(5)}.com"
    
    /**
     * 테스트용 시간 생성
     */
    fun testTime(): LocalDateTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0)
    
    /**
     * 게시글 제목 생성
     */
    fun articleTitle(): String = "Test Article ${randomString(5)}"
    
    /**
     * 게시글 내용 생성
     */
    fun articleContent(): String = "This is test content for article. ${randomString(50)}"
    
    /**
     * 댓글 내용 생성
     */
    fun commentContent(): String = "This is test comment. ${randomString(20)}"
    
    /**
     * IP 주소 생성
     */
    fun randomIpAddress(): String {
        return "${Random.nextInt(1, 255)}.${Random.nextInt(0, 255)}.${Random.nextInt(0, 255)}.${Random.nextInt(1, 255)}"
    }
    
    /**
     * User Agent 생성
     */
    fun randomUserAgent(): String {
        val userAgents = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
        )
        return userAgents.random()
    }
}