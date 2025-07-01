package me.helloc.enterpriseboard.view.application.port.input.command

/**
 * 조회수 명령 객체들
 */

data class IncreaseViewCountCommand(
    val articleId: Long,
    val userId: Long? = null,
    val ipAddress: String,
    val userAgent: String? = null
)

// Result objects
data class IncreaseViewCountResult(
    val viewId: Long?,
    val articleId: Long,
    val viewCount: Long,
    val increased: Boolean,
    val reason: String? = null
)