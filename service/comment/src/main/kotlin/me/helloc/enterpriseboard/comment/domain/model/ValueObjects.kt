package me.helloc.enterpriseboard.comment.domain.model

/**
 * 댓글 도메인 값 객체들
 */

@JvmInline
value class CommentId(val value: Long) {
    init {
        require(value > 0) { "Comment ID must be positive" }
    }
}

@JvmInline
value class ArticleId(val value: Long) {
    init {
        require(value > 0) { "Article ID must be positive" }
    }
}

@JvmInline
value class WriterId(val value: Long) {
    init {
        require(value > 0) { "Writer ID must be positive" }
    }
}

@JvmInline
value class CommentContent(val value: String) {
    init {
        require(value.isNotBlank()) { "Comment content cannot be blank" }
        require(value.length <= 1000) { "Comment content cannot exceed 1000 characters" }
    }
}