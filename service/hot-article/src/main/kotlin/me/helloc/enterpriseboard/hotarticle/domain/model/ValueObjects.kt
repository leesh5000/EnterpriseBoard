package me.helloc.enterpriseboard.hotarticle.domain.model

/**
 * 인기 게시글 도메인 값 객체들
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
value class HotScore(val value: Double) {
    init {
        require(value >= 0) { "Hot score must be non-negative" }
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
value class HotRank(val value: Int) {
    init {
        require(value > 0) { "Hot rank must be positive" }
    }
}