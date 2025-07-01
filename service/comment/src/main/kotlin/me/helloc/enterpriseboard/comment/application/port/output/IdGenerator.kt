package me.helloc.enterpriseboard.comment.application.port.output

/**
 * 댓글 ID 생성 포트
 */
interface IdGenerator {
    fun generateId(): Long
}