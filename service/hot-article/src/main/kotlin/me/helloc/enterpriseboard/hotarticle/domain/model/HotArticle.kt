package me.helloc.enterpriseboard.hotarticle.domain.model

import java.time.LocalDateTime

/**
 * 인기 게시글 도메인 모델
 */
data class HotArticle(
    val articleId: ArticleId,
    val boardId: BoardId,
    val score: HotScore,
    val viewCount: ViewCount,
    val likeCount: LikeCount,
    val commentCount: CommentCount,
    val calculatedAt: LocalDateTime,
    val rank: HotRank?
) {
    
    fun updateMetrics(
        newViewCount: ViewCount,
        newLikeCount: LikeCount,
        newCommentCount: CommentCount
    ): HotArticle {
        val newScore = calculateScore(newViewCount, newLikeCount, newCommentCount)
        return copy(
            score = newScore,
            viewCount = newViewCount,
            likeCount = newLikeCount,
            commentCount = newCommentCount,
            calculatedAt = LocalDateTime.now()
        )
    }
    
    fun updateRank(newRank: HotRank): HotArticle {
        return copy(rank = newRank)
    }
    
    private fun calculateScore(
        viewCount: ViewCount,
        likeCount: LikeCount,
        commentCount: CommentCount
    ): HotScore {
        // 인기도 점수 계산 알고리즘
        // 조회수 * 1 + 좋아요 * 5 + 댓글 * 3
        val score = (viewCount.value * 1.0) + (likeCount.value * 5.0) + (commentCount.value * 3.0)
        return HotScore(score)
    }
    
    companion object {
        fun create(
            articleId: ArticleId,
            boardId: BoardId,
            viewCount: ViewCount = ViewCount(0),
            likeCount: LikeCount = LikeCount(0),
            commentCount: CommentCount = CommentCount(0)
        ): HotArticle {
            val score = HotScore(
                (viewCount.value * 1.0) + (likeCount.value * 5.0) + (commentCount.value * 3.0)
            )
            
            return HotArticle(
                articleId = articleId,
                boardId = boardId,
                score = score,
                viewCount = viewCount,
                likeCount = likeCount,
                commentCount = commentCount,
                calculatedAt = LocalDateTime.now(),
                rank = null
            )
        }
    }
}