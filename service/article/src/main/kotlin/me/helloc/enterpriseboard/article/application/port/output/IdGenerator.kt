package me.helloc.enterpriseboard.article.application.port.output

/**
 * 출력 포트 - ID 생성을 위한 인터페이스
 */
interface IdGenerator {
    fun generateId(): Long
}