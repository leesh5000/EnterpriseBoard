package me.helloc.enterpriseboard.view.domain.model

/**
 * 조회수 도메인 값 객체들
 */

@JvmInline
value class ViewId(val value: Long) {
    init {
        require(value > 0) { "View ID must be positive" }
    }
}

@JvmInline
value class ArticleId(val value: Long) {
    init {
        require(value > 0) { "Article ID must be positive" }
    }
}

@JvmInline
value class UserId(val value: Long) {
    init {
        require(value > 0) { "User ID must be positive" }
    }
}

@JvmInline
value class IpAddress(val value: String) {
    init {
        require(value.isNotBlank()) { "IP address cannot be blank" }
        require(isValidIpAddress(value)) { "Invalid IP address format" }
    }
    
    private fun isValidIpAddress(ip: String): Boolean {
        // 간단한 IP 주소 유효성 검사 (IPv4)
        val parts = ip.split(".")
        if (parts.size != 4) return false
        return parts.all { part ->
            try {
                val num = part.toInt()
                num in 0..255
            } catch (e: NumberFormatException) {
                false
            }
        }
    }
}

@JvmInline
value class UserAgent(val value: String) {
    init {
        require(value.isNotBlank()) { "User agent cannot be blank" }
        require(value.length <= 500) { "User agent cannot exceed 500 characters" }
    }
}