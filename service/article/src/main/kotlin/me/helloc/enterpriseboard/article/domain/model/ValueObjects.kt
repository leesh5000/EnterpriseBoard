package me.helloc.enterpriseboard.article.domain.model

/**
 * 도메인 값 객체들 - 불변성과 유효성 검사 보장
 */

@JvmInline
value class ArticleId(val value: Long) {
    init {
        require(value > 0) { "Article ID must be positive" }
    }
}

@JvmInline
value class BoardId(val value: Long) {
    init {
        require(value > 0) { "Board ID must be positive" }
    }
}

@JvmInline
value class WriterId(val value: Long) {
    init {
        require(value > 0) { "Writer ID must be positive" }
    }
}

@JvmInline
value class Title(val value: String) {
    init {
        require(value.isNotBlank()) { "Title cannot be blank" }
        require(value.length <= 200) { "Title cannot exceed 200 characters" }
    }
}

@JvmInline
value class Content(val value: String) {
    init {
        require(value.isNotBlank()) { "Content cannot be blank" }
        require(value.length <= 10000) { "Content cannot exceed 10000 characters" }
    }
}