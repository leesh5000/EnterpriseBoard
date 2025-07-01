package me.helloc.enterpriseboard.like.domain.model

/**
 * 좋아요 도메인 값 객체들
 */

@JvmInline
value class LikeId(val value: Long) {
    init {
        require(value > 0) { "Like ID must be positive" }
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