package me.helloc.enterpriseboard.articleread.domain.model

/**
 * 게시글 읽기 모델 값 객체들
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
    }
}

@JvmInline
value class Content(val value: String) {
    init {
        require(value.isNotBlank()) { "Content cannot be blank" }
    }
}

@JvmInline
value class Summary(val value: String) {
    init {
        require(value.length <= 200) { "Summary cannot exceed 200 characters" }
    }
}

@JvmInline
value class BoardName(val value: String) {
    init {
        require(value.isNotBlank()) { "Board name cannot be blank" }
    }
}

@JvmInline
value class WriterNickname(val value: String) {
    init {
        require(value.isNotBlank()) { "Writer nickname cannot be blank" }
    }
}

@JvmInline
value class ViewCount(val value: Long) {
    init {
        require(value >= 0) { "View count must be non-negative" }
    }
}

@JvmInline
value class LikeCount(val value: Long) {
    init {
        require(value >= 0) { "Like count must be non-negative" }
    }
}

@JvmInline
value class CommentCount(val value: Long) {
    init {
        require(value >= 0) { "Comment count must be non-negative" }
    }
}

@JvmInline
value class Tag(val value: String) {
    init {
        require(value.isNotBlank()) { "Tag cannot be blank" }
        require(value.length <= 20) { "Tag cannot exceed 20 characters" }
    }
}

@JvmInline
value class HotRank(val value: Int) {
    init {
        require(value > 0) { "Hot rank must be positive" }
    }
}